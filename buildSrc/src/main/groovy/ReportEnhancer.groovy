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
            /(\w+)\s+passed\s+\((\d+\.?\d*)s\)/,
            />(\w+)<\/td>\s*<td[^>]*>passed[^<]*<\/td>\s*<td[^>]*>\((\d+\.?\d*)s\)/,
            /"(\w+)"[^>]*>passed[^<]*\((\d+\.?\d*)s\)/
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
