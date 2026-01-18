/**
 * Utility class for handling screenshot operations
 */
class ScreenshotHandler {
    
    /**
     * Finds the ADB executable path
     * @return Path to ADB executable
     */
    static String findAdbPath() {
        def androidHome = System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT")
        if (androidHome) {
            def os = org.gradle.internal.os.OperatingSystem.current()
            def adbPath = new File(androidHome, "platform-tools/adb${os.isWindows() ? '.exe' : ''}")
            if (adbPath.exists()) {
                return adbPath.absolutePath
            }
        }
        
        // Try common locations
        def commonPaths = [
            "${System.getProperty('user.home')}/AppData/Local/Android/Sdk/platform-tools/adb.exe",
            "${System.getProperty('user.home')}/Library/Android/sdk/platform-tools/adb",
            "${System.getProperty('user.home')}/Android/Sdk/platform-tools/adb"
        ]
        
        for (path in commonPaths) {
            def adbFile = new File(path)
            if (adbFile.exists()) {
                return adbFile.absolutePath
            }
        }
        
        return "adb" // Fallback to PATH
    }
    
    /**
     * Copies screenshots from device or local paths to report directory
     * @param steps List of test steps (will be modified in place)
     * @param screenshotsDir Target directory for screenshots
     * @param adbPath Path to ADB executable
     */
    static void copyScreenshots(List steps, File screenshotsDir, String adbPath) {
        screenshotsDir.mkdirs()
        copyScreenshotsRecursive(steps, screenshotsDir, adbPath)
    }
    
    /**
     * Recursively copies screenshots from steps
     */
    private static void copyScreenshotsRecursive(List steps, File screenshotsDir, String adbPath) {
        steps.each { step ->
            if (step.screenshot && step.screenshot != '') {
                try {
                    def screenshotPath = step.screenshot
                    def screenshotFileName = extractFileName(screenshotPath)
                    def targetFile = new File(screenshotsDir, screenshotFileName)
                    
                    // Check if screenshot is on device
                    if (screenshotPath.startsWith('/storage') || screenshotPath.startsWith('/sdcard')) {
                        // Use adb pull to copy from device
                        def adbCommand = "\"${adbPath}\" pull \"${screenshotPath}\" \"${targetFile.absolutePath}\""
                        def process = adbCommand.execute()
                        process.waitFor()
                        if (process.exitValue() == 0 && targetFile.exists()) {
                            step.screenshot = screenshotFileName
                        } else {
                            println "Warning: Failed to pull screenshot ${screenshotPath}"
                        }
                    } else {
                        // Local file path
                        def sourceFile = new File(screenshotPath)
                        if (sourceFile.exists()) {
                            sourceFile.withInputStream { input ->
                                targetFile.withOutputStream { output ->
                                    output << input
                                }
                            }
                            step.screenshot = screenshotFileName
                        }
                    }
                } catch (Exception e) {
                    println "Warning: Error copying screenshot ${step.screenshot}: ${e.message}"
                }
            }
            
            if (step.children && step.children.size() > 0) {
                copyScreenshotsRecursive(step.children, screenshotsDir, adbPath)
            }
        }
    }
    
    /**
     * Extracts filename from a path (handles both / and \ separators)
     */
    private static String extractFileName(String path) {
        if (path.contains('/')) {
            return path.substring(path.lastIndexOf('/') + 1)
        } else if (path.contains('\\')) {
            return path.substring(path.lastIndexOf('\\') + 1)
        }
        return path
    }
}
