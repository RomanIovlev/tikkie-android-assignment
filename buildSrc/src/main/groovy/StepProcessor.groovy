class StepProcessor {
    static List deduplicateSteps(List steps) {
        def seen = [] as Set
        def deduplicated = []
        
        steps.each { step ->
            def key = step.name
            if (!seen.contains(key)) {
                seen.add(key)
                if (step.children && step.children.size() > 0) {
                    step.children = deduplicateSteps(step.children)
                }
                deduplicated.add(step)
            }
        }
        
        return deduplicated
    }

    static int countAllSteps(List steps) {
        def count = 0
        steps.each { step ->
            count++
            if (step.children) {
                count += countAllSteps(step.children)
            }
        }
        return count
    }

    static int calculateTotalDurationRecursive(Map step) {
        def totalMillis = 0
        if (step.duration) {
            def millis = TestReporter.parseDurationToMillis(step.duration)
            if (millis > 0) {
                totalMillis += millis
            }
        }
        if (step.children && step.children.size() > 0) {
            step.children.each { child ->
                totalMillis += calculateTotalDurationRecursive(child)
            }
        }
        
        return totalMillis
    }

    static String calculateTotalDuration(List steps) {
        if (steps.isEmpty()) return ''
        
        def totalMillis = 0
        def hasValidDuration = false
        
        steps.each { step ->
            def stepMillis = calculateTotalDurationRecursive(step)
            if (stepMillis > 0) {
                totalMillis += stepMillis
                hasValidDuration = true
            }
        }
        
        if (hasValidDuration && totalMillis > 0) {
            return TestReporter.formatDuration(totalMillis)
        }
        
        return ''
    }

    static String formatMethodName(String methodName) {
        return methodName.replaceAll(/([a-z])([A-Z])/, '$1 $2')
                .split(' ')
                .collect { word -> word.capitalize() }
                .join(' ')
    }

    static boolean looksLikeTestName(String stepName, String testMethodName) {
        def containsTest = stepName.toLowerCase().contains('test')
        def looksLikeTestName = stepName != testMethodName &&
                (containsTest ||
                        (stepName.contains(' ') &&
                                stepName.matches(/^[A-Z][a-zA-Z\s]+$/))) &&
                !stepName.toLowerCase().startsWith('open') &&
                !stepName.toLowerCase().startsWith('verify') &&
                !stepName.toLowerCase().startsWith('check') &&
                !stepName.toLowerCase().startsWith('click')
        
        return looksLikeTestName
    }
}
