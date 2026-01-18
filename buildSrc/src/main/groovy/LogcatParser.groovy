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
                        errorSummary: '',
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
                        def fullError = errorBuffer.join('\n').trim()
                        currentFailedStep.error = fullError
                        currentFailedStep.errorSummary = extractErrorSummary(fullError)
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
                        def fullError = errorBuffer.join('\n').trim()
                        currentFailedStep.error = fullError
                        currentFailedStep.errorSummary = extractErrorSummary(fullError)
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
                                def fullError = errorBuffer.join('\n').trim()
                                currentFailedStep.error = fullError
                                currentFailedStep.errorSummary = extractErrorSummary(fullError)
                                capturingError = false
                                currentFailedStep = null
                                errorBuffer = []
                            } else {
                                errorBuffer.add('')
                            }
                        } else {
                            def fullError = errorBuffer.join('\n').trim()
                            currentFailedStep.error = fullError
                            currentFailedStep.errorSummary = extractErrorSummary(fullError)
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
            def fullError = errorBuffer.join('\n').trim()
            currentFailedStep.error = fullError
            currentFailedStep.errorSummary = extractErrorSummary(fullError)
        }
        return allSteps
    }
    
    /**
     * Extracts error summary (Expected and Got lines) from full error text
     * @param errorText Full error text
     * @return Summary string with Expected and Got lines, or empty string if not found
     */
    private static String extractErrorSummary(String errorText) {
        if (!errorText) return ''
        
        def lines = errorText.readLines()
        def expectedLine = ''
        def gotLine = ''
        
        lines.each { line ->
            // Look for "E TestRunner: Expected:" pattern
            // Match: "E TestRunner: Expected: ..." or "E  TestRunner: Expected: ..." (with variable spaces)
            if (line.contains('E') && line.contains('TestRunner:') && line.contains('Expected:')) {
                // Extract everything after "Expected:" and trim multiple spaces
                def match = line =~ /E\s+TestRunner:\s+Expected:\s*(.+)$/
                if (match) {
                    expectedLine = match[0][1].replaceAll(/\s+/, ' ').trim()
                } else {
                    // Try alternative pattern without strict spacing
                    def altMatch = line =~ /.*Expected:\s*(.+)$/
                    if (altMatch) {
                        expectedLine = altMatch[0][1].replaceAll(/\s+/, ' ').trim()
                    }
                }
            }
            // Look for "E TestRunner:      Got:" pattern (with multiple spaces)
            else if (line.contains('E') && line.contains('TestRunner:') && line.contains('Got:')) {
                // Extract everything after "Got:" and trim multiple spaces
                def match = line =~ /E\s+TestRunner:\s+Got:\s*(.+)$/
                if (match) {
                    gotLine = match[0][1].replaceAll(/\s+/, ' ').trim()
                } else {
                    // Try alternative pattern without strict spacing
                    def altMatch = line =~ /.*Got:\s*(.+)$/
                    if (altMatch) {
                        gotLine = altMatch[0][1].replaceAll(/\s+/, ' ').trim()
                    }
                }
            }
        }
        
        if (expectedLine || gotLine) {
            def summary = []
            if (expectedLine) {
                summary.add("Expected: ${expectedLine}")
            }
            if (gotLine) {
                summary.add("Got: ${gotLine}")
            }
            return summary.join('\n')
        }
        
        return ''
    }
}
