/**
 * Utility class for opening files in the default browser
 */
class BrowserOpener {
    
    /**
     * Opens a file in the default browser
     * @param file The file to open
     * @return true if successful, false otherwise
     */
    static boolean openInBrowser(File file) {
        if (!file.exists()) {
            return false
        }
        
        try {
            def os = org.gradle.internal.os.OperatingSystem.current()
            def command
            
            if (os.isWindows()) {
                command = ['cmd', '/c', 'start', '', file.absolutePath]
            } else if (os.isMacOsX()) {
                command = ['open', file.absolutePath]
            } else {
                command = ['xdg-open', file.absolutePath]
            }
            
            def process = new ProcessBuilder(command).start()
            process.waitFor()
            return true
        } catch (Exception e) {
            println "Warning: Could not open report automatically: ${e.message}"
            return false
        }
    }
    
    /**
     * Prints instructions for manually opening the report
     * @param reportFile The report file path
     */
    static void printManualOpenInstructions(File reportFile) {
        println ""
        println "═══════════════════════════════════════════════════════════════"
        println "⚠️  Test report not found!"
        println "═══════════════════════════════════════════════════════════════"
        println ""
        println "Expected location: ${reportFile.absolutePath}"
        println ""
        println "To generate the report, run tests first:"
        println "  PowerShell: .\\gradlew.bat connectedDebugAndroidTest"
        println "  CMD:        gradlew.bat connectedDebugAndroidTest"
        println "  Linux/Mac:  ./gradlew connectedDebugAndroidTest"
        println ""
        println "Or run tests from Android Studio, then run this task again."
        println ""
    }
}
