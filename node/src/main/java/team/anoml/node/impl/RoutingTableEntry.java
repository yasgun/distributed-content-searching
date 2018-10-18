package team.anoml.node.impl;

public class RoutingTableEntry {

    private String ip;
    private int port;

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
}
