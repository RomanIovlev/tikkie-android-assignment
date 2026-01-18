class LogcatParser {
    static Map<String, Map<String, List>> parseLogcatFiles(File testResultsDir) {
        def testsByClass = [:]
        testResultsDir.eachDir { deviceDir ->
            deviceDir.eachFileMatch(~/logcat-.*\.txt/) { logcatFile ->
                def logcatContent = logcatFile.text
                def steps = extractStepsFromLogcat(logcatContent)
                
                if (steps.size() > 0) {
                    def filenameMatch = logcatFile.name =~ /logcat-(.+?)-(.+?)\.txt/
                    if (filenameMatch) {
                        def className = filenameMatch[0][1]
                        def testMethodName = filenameMatch[0][2]
                        if (!testsByClass.containsKey(className)) {
                            testsByClass[className] = [:]
                        }
                        testsByClass[className][testMethodName] = steps
                    }
                }
            }
        }
        
        return testsByClass
    }

    private static List extractStepsFromLogcat(String logcatContent) {
        def stepStack = []
        def allSteps = []
        def lines = logcatContent.readLines()
        def currentFailedStep = null
        def errorBuffer = []
        def capturingError = false
        
        lines.eachWithIndex { line, index ->
            if (line.contains('TEST STEP:') && 
                !line.contains('SUCCEED') && 
                !line.contains('FAILED') && 
                !line.contains('finished')) {

                capturingError = false
                errorBuffer = []
                currentFailedStep = null
                
                def stepMatch = line =~ /TEST STEP: "(?:\d+(?:\.\d+)*\.\s+)?(.+?)"/
                if (stepMatch) {
                    def stepName = stepMatch[0][1]
                    def step = [
                        name: stepName,
                        status: 'RUNNING',
                        duration: '',
                        screenshot: '',
                        error: '',
                        children: []
                    ]
                    
                    if (!stepStack.isEmpty()) {
                        stepStack.last().children.add(step)
                    } else {
                        allSteps.add(step)
                    }
                    stepStack.push(step)
                }
            }
            else if (line.contains('SUCCEED') || line.contains('FAILED')) {
                def status = line.contains('SUCCEED') ? 'SUCCEED' : 'FAILED'
                def durationMatch = line =~ /(?:SUCCEED|FAILED)\. It took (.+?)\./
                if (durationMatch && !stepStack.isEmpty()) {
                    def currentStep = stepStack.pop()
                    currentStep.status = status
                    currentStep.duration = durationMatch[0][1]
                    if (status == 'FAILED') {
                        currentFailedStep = currentStep
                        capturingError = true
                        errorBuffer = []
                    }
                }
            }
            else if (capturingError && currentFailedStep != null) {
                if (line.contains('TEST STEP:') && !line.contains('FAILED') && !line.contains('SUCCEED')) {
                    if (errorBuffer.size() > 0) {
                        currentFailedStep.error = errorBuffer.join('\n').trim()
                    }
                    capturingError = false
                    currentFailedStep = null
                    errorBuffer = []
                }
                else if (line.contains('AssertionFailedError') || 
                         line.contains('Exception') || 
                         line.contains('Error:') ||
                         line.contains("doesn't match") ||
                         line.contains('Expected:') ||
                         line.contains('Got:') ||
                         line.contains('View Details:') ||
                         line.contains('AppCompatTextView') ||
                         line.contains('res-name=') ||
                         (errorBuffer.size() > 0 && line.trim())) {
                    errorBuffer.add(line)
                }
                else if (errorBuffer.size() > 0) {
                    if (errorBuffer.size() > 50) {
                        currentFailedStep.error = errorBuffer.join('\n').trim()
                        capturingError = false
                        currentFailedStep = null
                        errorBuffer = []
                    } else if (!line.trim()) {
                        if (index + 1 < lines.size()) {
                            def nextLine = lines[index + 1]
                            if (nextLine.contains('TEST STEP:') || 
                                nextLine.contains('SUCCEED') ||
                                (!nextLine.contains('AssertionFailedError') && 
                                 !nextLine.contains('Exception') && 
                                 !nextLine.contains('Error:') &&
                                 !nextLine.contains('Expected:') &&
                                 !nextLine.contains('Got:') &&
                                 !nextLine.contains('View Details:'))) {
                                currentFailedStep.error = errorBuffer.join('\n').trim()
                                capturingError = false
                                currentFailedStep = null
                                errorBuffer = []
                            } else {
                                errorBuffer.add('')
                            }
                        } else {
                            currentFailedStep.error = errorBuffer.join('\n').trim()
                            capturingError = false
                            currentFailedStep = null
                            errorBuffer = []
                        }
                    }
                }
            }
            else if (line.toLowerCase().contains('screenshot') && 
                     (line.contains('.png') || line.contains('.jpg') || line.contains('.jpeg'))) {
                def pathMatch = line =~ /([\/\\].*?\.(png|jpg|jpeg))/
                if (pathMatch && !stepStack.isEmpty()) {
                    stepStack.last().screenshot = pathMatch[0][1]
                }
            }
        }
        if (currentFailedStep != null && errorBuffer.size() > 0) {
            currentFailedStep.error = errorBuffer.join('\n').trim()
        }
        return allSteps
    }
}
