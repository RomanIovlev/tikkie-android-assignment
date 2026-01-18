class TestShardConfig {
    List<String> availableDevices
    int deviceCount
    int shardCount
    
    TestShardConfig(List<String> availableDevices, int requestedShards) {
        this.availableDevices = availableDevices
        this.deviceCount = availableDevices.size()
        this.shardCount = calculateShardCount(requestedShards)
    }
    
    private int calculateShardCount(int requestedShards) {
        int calculatedShards
        
        if (requestedShards > 0) {
            calculatedShards = requestedShards
        } else if (deviceCount > 0) {
            calculatedShards = Math.min(deviceCount, 4)
        } else {
            calculatedShards = 4
        }
        if (deviceCount == 1 && requestedShards <= 0) {
            println "INFO: Single device detected. Using 1 shard to avoid conflicts."
            calculatedShards = 1
        } else if (deviceCount > 0 && calculatedShards > deviceCount) {
            println "WARNING: Requested ${calculatedShards} shards but only ${deviceCount} device(s) available. Using ${deviceCount} shards."
            calculatedShards = deviceCount
        }
        
        return calculatedShards
    }
    
    String getDeviceForShard(int shardIndex) {
        if (deviceCount > 0) {
            return availableDevices[shardIndex % deviceCount]
        }
        return null
    }
    
    boolean hasMultipleDevices() {
        return deviceCount > 1
    }
    
    void printDeviceInfo() {
        if (deviceCount == 0) {
            println "WARNING: No Android devices detected. Please connect a device or start an emulator."
            println "Run 'adb devices' to verify device connection."
        } else {
            println "Detected ${deviceCount} device(s): ${availableDevices.join(', ')}"
        }
    }
}
