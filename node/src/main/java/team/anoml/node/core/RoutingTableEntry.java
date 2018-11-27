package team.anoml.node.core;

public class RoutingTableEntry {

    private String ipAddress;
    private int port;

    public RoutingTableEntry(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public String getIPAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

}
