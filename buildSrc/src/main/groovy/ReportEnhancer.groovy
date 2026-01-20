class ReportEnhancer {
    static void enhanceReports(File reportDir, Map<String, Map<String, List>> testsByClass, 
                               File screenshotsDir, String adbPath) {
        testsByClass.each { className, tests ->
            def htmlFile = new File(reportDir, "${className}.html")
            if (htmlFile.exists()) {
                def htmlContent = htmlFile.text
                def testDurations = extractTestDurations(htmlContent)
                def unifiedTests = buildUnifiedTestStructure(tests, testDurations)
                ScreenshotHandler.copyScreenshots(unifiedTests, screenshotsDir, adbPath)
                def containerId = "kaspresso-steps-unified"
                def stepsHtml = TestReporter.generateUnifiedStepsHtml(unifiedTests, containerId)
                htmlContent = htmlContent.replaceAll(/(?s)<div id="kaspresso-steps[^"]*">.*?<\/div>\s*<script>.*?<\/script>/, '')
                htmlContent = htmlContent.replace('<div id="footer">', stepsHtml + '<div id="footer">')
                htmlFile.text = htmlContent
                
                def totalSteps = unifiedTests.sum { StepProcessor.countAllSteps([it]) }
                println "Enhanced ${htmlFile.name} with unified table containing ${tests.size()} tests and ${totalSteps} total steps"
            }
        }
    }

    private static Map<String, Integer> extractTestDurations(String htmlContent) {
        def testDurations = [:]
        def patterns = [
            // Pattern for simple method names: testMethod passed (7.463s)
            /(\w+)\s+passed\s+\((\d+\.?\d*)s\)/,
            // Pattern for parameterized test names: test[input: 50, expected: 50] passed (7.463s)
            // Matches test names with brackets and special characters
            /(test\[[^\]]+\])\s+passed\s+\((\d+\.?\d*)s\)/,
            // Pattern for HTML table cells with simple method names: >testMethod</td>...passed...>(7.463s)
            />(\w+)<\/td>\s*<td[^>]*>passed[^<]*<\/td>\s*<td[^>]*>\((\d+\.?\d*)s\)/,
            // Pattern for HTML table cells with parameterized test names
            />(test\[[^\]]+\])<\/td>\s*<td[^>]*>passed[^<]*<\/td>\s*<td[^>]*>\((\d+\.?\d*)s\)/,
            // Pattern for quoted method names in HTML: "testMethod"...passed...(7.463s)
            /"(\w+)"[^>]*>passed[^<]*\((\d+\.?\d*)s\)/,
            // Pattern for quoted parameterized test names
            /"(test\[[^\]]+\])"[^>]*>passed[^<]*\((\d+\.?\d*)s\)/,
            // More general pattern: any text before "passed" that doesn't contain HTML tags
            // This catches edge cases and variations in HTML structure
            />([^<>\s]+(?:\[[^\]]+\])?)\s*<\/td>\s*<td[^>]*>passed[^<]*<\/td>\s*<td[^>]*>\((\d+\.?\d*)s\)/
        ]
        patterns.each { pattern ->
            def matcher = htmlContent =~ pattern
            matcher.each { match ->
                def methodName = match[1]
                def durationSeconds = match[2] as double
                def durationMillis = (durationSeconds * 1000) as int
                // Only store if not already present (first match wins)
                if (!testDurations.containsKey(methodName)) {
                    testDurations[methodName] = durationMillis
                }
            }
        }
        return testDurations
    }
    private static List buildUnifiedTestStructure(Map<String, List> tests, Map<String, Integer> testDurations) {
        def unifiedTests = []
        
        tests.each { testMethodName, steps ->
            // Always start with formatted method name as default
            def testName = StepProcessor.formatMethodName(testMethodName)
            def actualSteps = steps
            def testDuration = ''
            
            // First priority: Find test name from any step that has it (from "in Test Name" pattern in logcat)
            def foundTestName = null
            def findTestNameInSteps = { stepList ->
                stepList.each { step ->
                    if (step.testName && step.testName.trim()) {
                        foundTestName = step.testName.trim()
                        return
                    }
                    if (step.children && step.children.size() > 0) {
                        findTestNameInSteps(step.children)
                    }
                }
            }
            findTestNameInSteps(steps)
            
            if (foundTestName) {
                testName = foundTestName
            }
            // Second priority: Check if first step name looks like a test name
            else if (steps.size() > 0) {
                def firstStep = steps[0]
                def firstStepName = firstStep.name
                def looksLikeTestName = StepProcessor.looksLikeTestName(firstStepName, testMethodName) &&
                                        (firstStepName.toLowerCase().contains('test') || 
                                         firstStepName.matches(/^Test\s+.+/))
                
                if (firstStep.children && firstStep.children.size() > 0 && looksLikeTestName) {
                    testName = firstStepName
                    actualSteps = firstStep.children
                    testDuration = firstStep.duration ?: ''
                }
                else if (looksLikeTestName) {
                    testName = firstStepName
                    actualSteps = steps
                    testDuration = firstStep.duration ?: ''
                }
            }
            def testStatus = actualSteps.every { it.status == 'SUCCEED' } ? 'SUCCEED' :
                    actualSteps.any { it.status == 'FAILED' } ? 'FAILED' : 'RUNNING'

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
