/**
 * Utility class for enhancing HTML test reports with Kaspresso step information
 */
class ReportEnhancer {
    
    /**
     * Enhances HTML report files with test step information
     * @param reportDir Directory containing HTML report files
     * @param testsByClass Map of className -> [testMethodName -> steps]
     * @param screenshotsDir Directory for screenshots
     * @param adbPath Path to ADB executable
     */
    static void enhanceReports(File reportDir, Map<String, Map<String, List>> testsByClass, 
                               File screenshotsDir, String adbPath) {
        testsByClass.each { className, tests ->
            def htmlFile = new File(reportDir, "${className}.html")
            if (htmlFile.exists()) {
                def htmlContent = htmlFile.text
                def testDurations = extractTestDurations(htmlContent)
                def unifiedTests = buildUnifiedTestStructure(tests, testDurations)
                
                // Copy screenshots before generating HTML
                ScreenshotHandler.copyScreenshots(unifiedTests, screenshotsDir, adbPath)
                
                // Generate and inject HTML
                def containerId = "kaspresso-steps-unified"
                def stepsHtml = TestReporter.generateUnifiedStepsHtml(unifiedTests, containerId)
                
                // Remove existing Kaspresso steps sections and insert new unified one
                htmlContent = htmlContent.replaceAll(/(?s)<div id="kaspresso-steps[^"]*">.*?<\/div>\s*<script>.*?<\/script>/, '')
                htmlContent = htmlContent.replace('<div id="footer">', stepsHtml + '<div id="footer">')
                htmlFile.text = htmlContent
                
                def totalSteps = unifiedTests.sum { StepProcessor.countAllSteps([it]) }
                println "Enhanced ${htmlFile.name} with unified table containing ${tests.size()} tests and ${totalSteps} total steps"
            }
        }
    }
    
    /**
     * Extracts test durations from HTML report content
     * @param htmlContent The HTML report content
     * @return Map of testMethodName -> duration in milliseconds
     */
    private static Map<String, Integer> extractTestDurations(String htmlContent) {
        def testDurations = [:]
        def patterns = [
            /(\w+)\s+passed\s+\((\d+\.?\d*)s\)/,  // "contextMenuTest passed (12.885s)"
            />(\w+)<\/td>\s*<td[^>]*>passed[^<]*<\/td>\s*<td[^>]*>\((\d+\.?\d*)s\)/,  // HTML table format
            /"(\w+)"[^>]*>passed[^<]*\((\d+\.?\d*)s\)/  // JSON or other formats
        ]
        
        patterns.each { pattern ->
            def matcher = htmlContent =~ pattern
            matcher.each { match ->
                def methodName = match[1]
                def durationSeconds = match[2] as double
                def durationMillis = (durationSeconds * 1000) as int
                testDurations[methodName] = durationMillis
            }
        }
        
        return testDurations
    }
    
    /**
     * Builds unified test structure from parsed test data
     * @param tests Map of testMethodName -> steps
     * @param testDurations Map of testMethodName -> duration in milliseconds
     * @return List of unified test nodes
     */
    private static List buildUnifiedTestStructure(Map<String, List> tests, Map<String, Integer> testDurations) {
        def unifiedTests = []
        
        tests.each { testMethodName, steps ->
            def testName = StepProcessor.formatMethodName(testMethodName)
            def actualSteps = steps
            def testDuration = ''
            
            if (steps.size() > 0) {
                def firstStep = steps[0]
                def firstStepName = firstStep.name
                
                def looksLikeTestName = StepProcessor.looksLikeTestName(firstStepName, testMethodName)
                
                // If first step has children AND looks like a test name, use it
                if (firstStep.children && firstStep.children.size() > 0 && looksLikeTestName) {
                    testName = firstStepName
                    actualSteps = firstStep.children
                    testDuration = firstStep.duration ?: ''
                }
                // If first step looks like a test name but has no children, still use it
                else if (looksLikeTestName) {
                    testName = firstStepName
                    actualSteps = steps
                    testDuration = firstStep.duration ?: ''
                }
            }
            
            // Calculate overall test status
            def testStatus = actualSteps.every { it.status == 'SUCCEED' } ? 'SUCCEED' :
                    actualSteps.any { it.status == 'FAILED' } ? 'FAILED' : 'RUNNING'
            
            // Use duration from test report if available, otherwise calculate from steps
            if (testDurations.containsKey(testMethodName)) {
                def reportDurationMillis = testDurations[testMethodName]
                testDuration = TestReporter.formatDuration(reportDurationMillis)
            } else {
                def calculatedDuration = StepProcessor.calculateTotalDuration(actualSteps)
                if (calculatedDuration) {
                    testDuration = calculatedDuration
                } else if (testDuration) {
                    def millis = TestReporter.parseDurationToMillis(testDuration)
                    testDuration = TestReporter.formatDuration(millis)
                }
            }
            
            def testNode = [
                name: testName,
                status: testStatus,
                duration: testDuration,
                children: actualSteps
            ]
            unifiedTests.add(testNode)
        }
        
        return unifiedTests
    }
}
