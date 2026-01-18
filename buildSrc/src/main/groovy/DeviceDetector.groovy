import org.gradle.internal.os.OperatingSystem

class DeviceDetector {
    static List<String> getAvailableDevices() {
        def adbPath = ScreenshotHandler.findAdbPath()
        def devices = []
        
        try {
            def process = [adbPath, 'devices'].execute()
            def output = new StringBuffer()
            def error = new StringBuffer()
            process.consumeProcessOutput(output, error)
            process.waitFor()
            
            if (process.exitValue() == 0) {
                def lines = output.toString().split('\n')
                lines.each { line ->
                    def trimmed = line.trim()
                    // Skip header line and empty lines
                    if (trimmed && !trimmed.startsWith('List of devices') && trimmed != '') {
                        // Format: "device_id    device"
                        def parts = trimmed.split('\\s+')
                        if (parts.length >= 2 && parts[1] == 'device') {
                            devices.add(parts[0])
                        }
                    }
                }
            } else {
                println "Warning: Failed to detect devices: ${error.toString()}"
            }
        } catch (Exception e) {
            println "Warning: Error detecting devices: ${e.message}"
        }
        
        return devices
    }
    
    static int getDeviceCount() {
        return getAvailableDevices().size()
    }
}
