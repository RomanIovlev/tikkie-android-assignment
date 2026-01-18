/**
 * Utility class for parsing logcat files and extracting Kaspresso test steps
 */
class LogcatParser {
    
    /**
     * Parses logcat files and extracts test step information
     * @param testResultsDir Directory containing test results with logcat files
     * @return Map of className -> [testMethodName -> steps]
     */
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
    
    /**
     * Extracts step information from logcat content
     * @param logcatContent The logcat file content
     * @return List of step maps with hierarchy
     */
    private static List extractStepsFromLogcat(String logcatContent) {
        def stepStack = []
        def allSteps = []
        def lines = logcatContent.readLines()
        def currentFailedStep = null
        def errorBuffer = []
        def capturingError = false
        
        lines.eachWithIndex { line, index ->
            // Match start of step
            if (line.contains('TEST STEP:') && 
                !line.contains('SUCCEED') && 
                !line.contains('FAILED') && 
                !line.contains('finished')) {
                
                // Reset error capturing when new step starts
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
            // Match end of step
            else if (line.contains('SUCCEED') || line.contains('FAILED')) {
                def status = line.contains('SUCCEED') ? 'SUCCEED' : 'FAILED'
                def durationMatch = line =~ /(?:SUCCEED|FAILED)\. It took (.+?)\./
                if (durationMatch && !stepStack.isEmpty()) {
                    def currentStep = stepStack.pop()
                    currentStep.status = status
                    currentStep.duration = durationMatch[0][1]
                    
                    // If step failed, start capturing error messages
                    if (status == 'FAILED') {
                        currentFailedStep = currentStep
                        capturingError = true
                        errorBuffer = []
                    }
                }
            }
            // Capture error messages after a failed step
            else if (capturingError && currentFailedStep != null) {
                // Stop capturing if we hit a new test step
                if (line.contains('TEST STEP:') && !line.contains('FAILED') && !line.contains('SUCCEED')) {
                    // End of error block
                    if (errorBuffer.size() > 0) {
                        currentFailedStep.error = errorBuffer.join('\n').trim()
                    }
                    capturingError = false
                    currentFailedStep = null
                    errorBuffer = []
                }
                // Look for common error patterns - capture the error message
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
                    // Capture error line (keep original formatting for readability)
                    errorBuffer.add(line)
                }
                // Stop capturing after a reasonable number of lines or when we see non-error content
                else if (errorBuffer.size() > 0) {
                    // If we've captured some error content and hit a non-error line,
                    // check if next few lines might still be part of the error
                    if (errorBuffer.size() > 50) {
                        // Too many lines, stop capturing
                        currentFailedStep.error = errorBuffer.join('\n').trim()
                        capturingError = false
                        currentFailedStep = null
                        errorBuffer = []
                    } else if (!line.trim()) {
                        // Empty line - might be separator, continue if next line is error-related
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
                                // End of error block
                                currentFailedStep.error = errorBuffer.join('\n').trim()
                                capturingError = false
                                currentFailedStep = null
                                errorBuffer = []
                            } else {
                                errorBuffer.add('')
                            }
                        } else {
                            // Last line, save error
                            currentFailedStep.error = errorBuffer.join('\n').trim()
                            capturingError = false
                            currentFailedStep = null
                            errorBuffer = []
                        }
                    }
                }
            }
            // Match screenshot logs
            else if (line.toLowerCase().contains('screenshot') && 
                     (line.contains('.png') || line.contains('.jpg') || line.contains('.jpeg'))) {
                def pathMatch = line =~ /([\/\\].*?\.(png|jpg|jpeg))/
                if (pathMatch && !stepStack.isEmpty()) {
                    stepStack.last().screenshot = pathMatch[0][1]
                }
            }
        }
        
        // Save any remaining error buffer
        if (currentFailedStep != null && errorBuffer.size() > 0) {
            currentFailedStep.error = errorBuffer.join('\n').trim()
        }
        
        return allSteps
    }
}
