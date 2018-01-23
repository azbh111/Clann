package lol.clann.api;


public final class security {

    private final String QQ;
    private final String plugin;
    private final String key;

    public security(String QQ, String plugin, String key) {
        this.QQ = QQ;
        this.plugin = plugin;
        this.key = key;
    }

    /**
     * @return the QQ
     */
    public String getQQ() {
        return QQ;
    }
    /**
     * @return the plugin
     */
    public String getPlugin() {
        return plugin;
    }
    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }
}
