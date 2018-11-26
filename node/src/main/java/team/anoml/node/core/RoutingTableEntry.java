package team.anoml.node.core;

public class RoutingTableEntry {

    private String ip;
    private int port;
    private boolean validated = false;

    public RoutingTableEntry(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIP() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public RoutingTableEntry validate() {
        this.validated = true;
        return this;
    }

    public boolean isValidated() {
        return validated;
    }
}
