import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.Exec

class TestShardTaskCreator {
    private final Project project
    private final TestShardConfig config
    
    TestShardTaskCreator(Project project, TestShardConfig config) {
        this.project = project
        this.config = config
    }
    
    void createShardTasks() {
        def connectedTestTask = project.tasks.findByName('connectedDebugAndroidTest')
        if (!connectedTestTask) {
            return
        }
        
        // Create individual shard tasks
        for (int i = 0; i < config.shardCount; i++) {
            createShardTask(i, config.getDeviceForShard(i))
        }
        if (!config.hasMultipleDevices()) {
            configureSequentialExecution()
        }
    }
    
    TestShardConfig getConfig() {
        return config
    }
    
    private void createShardTask(int shardIndex, String deviceId) {
        project.tasks.register("testShard${shardIndex}", Exec) { task ->
            task.group = 'verification'
            task.description = deviceId ? 
                "Runs test shard ${shardIndex} of ${config.shardCount - 1} on device ${deviceId}" :
                "Runs test shard ${shardIndex} of ${config.shardCount - 1}"
            
            task.workingDir = project.rootDir
            
            def isWindows = System.getProperty('os.name').toLowerCase().contains('windows')
            def gradleCmd = isWindows ? 'gradlew.bat' : './gradlew'
            
            def commandArgs = [gradleCmd, 'connectedDebugAndroidTest',
                "-Pandroid.testInstrumentationRunnerArguments.numShards=${config.shardCount}",
                "-Pandroid.testInstrumentationRunnerArguments.shardIndex=${shardIndex}"]
            
            task.commandLine = commandArgs

            task.environment = new HashMap(System.getenv())
            if (System.getProperty('java.home')) {
                task.environment['JAVA_HOME'] = System.getProperty('java.home')
            }
            
            if (deviceId) {
                task.environment['ANDROID_SERIAL'] = deviceId
            }
            
            task.ignoreExitValue = false
            
            task.doFirst {
                println "============================================================"
                if (deviceId) {
                    println "[Shard ${shardIndex}/${config.shardCount - 1}] Starting on device: ${deviceId}"
                    // Verify device is still available
                    def currentDevices = DeviceDetector.getAvailableDevices()
                    if (!currentDevices.contains(deviceId)) {
                        throw new GradleException("Device ${deviceId} is no longer available. Please check device connection.")
                    }
                } else {
                    println "[Shard ${shardIndex}/${config.shardCount - 1}] Starting (no specific device assigned)"
                }
                println "============================================================"
            }
            
            task.doLast {
                println "[Shard ${shardIndex}] Completed successfully"
            }
        }
    }
    
    private void configureSequentialExecution() {
        (0..<config.shardCount).each { i ->
            if (i > 0) {
                project.tasks.named("testShard${i}").configure {
                    mustRunAfter project.tasks.named("testShard${i - 1}")
                }
            }
        }
    }
}
