/**
 * Utility class for processing and manipulating test steps
 */
class StepProcessor {
    
    /**
     * Deduplicates steps at the same level
     * @param steps List of step maps
     * @return Deduplicated list of steps
     */
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
    
    /**
     * Counts all steps recursively including nested children
     * @param steps List of step maps
     * @return Total count of steps
     */
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
    
    /**
     * Recursively calculates total duration for a single step including all nested steps
     * @param step Step map
     * @return Total duration in milliseconds
     */
    static int calculateTotalDurationRecursive(Map step) {
        def totalMillis = 0
        
        // Add this step's duration
        if (step.duration) {
            def millis = TestReporter.parseDurationToMillis(step.duration)
            if (millis > 0) {
                totalMillis += millis
            }
        }
        
        // Recursively add all children's durations
        if (step.children && step.children.size() > 0) {
            step.children.each { child ->
                totalMillis += calculateTotalDurationRecursive(child)
            }
        }
        
        return totalMillis
    }
    
    /**
     * Calculates total duration for a list of steps
     * @param steps List of step maps
     * @return Formatted duration string or empty string
     */
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
    
    /**
     * Formats method name from camelCase to Title Case
     * @param methodName The camelCase method name
     * @return Formatted title case string
     */
    static String formatMethodName(String methodName) {
        return methodName.replaceAll(/([a-z])([A-Z])/, '$1 $2')
                .split(' ')
                .collect { word -> word.capitalize() }
                .join(' ')
    }
    
    /**
     * Determines if a step name looks like a test name
     * @param stepName The step name to check
     * @param testMethodName The original test method name
     * @return true if step name looks like a test name
     */
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
