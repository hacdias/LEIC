package pt.tecnico.sauron.silo.domain;

public class Options {
    private final Integer instance;
    private final Integer totalInstances;
    private final String host;
    private final Integer basePort;
    private final Integer updateFrequency;
    private final String storageFile;

    public Options(Integer instance, Integer totalInstances, String host, Integer basePort, String storageFile, Integer updateFrequency) {
        this.totalInstances = totalInstances;
        this.host = host;
        this.storageFile = storageFile;
        this.updateFrequency = updateFrequency;

        // Convert from 1-based to 0-based instance numbers.
        this.instance = instance - 1;
        this.basePort = basePort + 1;
    }

    public Integer getInstance() {
        return instance;
    }

    public Integer getTotalInstances() {
        return totalInstances;
    }

    public String getHost() {
        return host;
    }

    public Integer getBasePort() {
        return basePort;
    }

    public Integer getUpdateFrequency() {
        return updateFrequency;
    }

    public String getInstanceTarget (Integer n) {
        return getHost() + ":" + Integer.toString(getBasePort() + n);
    }

    public String getStorageFile() {
        return storageFile;
    }

    @Override
    public String toString() {
        return "Options{" +
            "instance=" + instance +
            ", totalInstances=" + totalInstances +
            ", host='" + host + '\'' +
            ", basePort=" + basePort +
            ", updateFrequency=" + updateFrequency +
            ", storageFile='" + storageFile + '\'' +
            '}';
    }
}
