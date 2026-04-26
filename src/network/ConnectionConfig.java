package ZeroCarbonFootprintTracker.src.network;

/**
 * Holds the host and port for the GreenPrint Discount Server connection.
 * Using this class means host and port are never hardcoded in the GUI.
 */
public class ConnectionConfig {
 
    private String host;
    private int port;
 
    /**
     * Constructs a ConnectionConfig with the given host and port.
     *
     * @param host the server hostname (e.g. "localhost")
     * @param port the server port (e.g. 6000)
     */
    public ConnectionConfig(String host, int port) {
        this.host = host;
        this.port = port;
    }
 
    /**
     * Gets the server host.
     * @return host string
     */
    public String getHost() {
        return host;
    }
 
    /**
     * Sets the server host.
     * @param host the hostname to connect to
     */
    public void setHost(String host) {
        this.host = host;
    }
 
    /**
     * Gets the server port.
     * @return port number
     */
    public int getPort() {
        return port;
    }
 
    /**
     * Sets the server port.
     * @param port the port number to connect to
     */
    public void setPort(int port) {
        this.port = port;
    }
 
    @Override
    public String toString() {
        return host + ":" + port;
    }
}
