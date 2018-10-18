package team.anoml.node.impl;

public class RoutingTableEntry {

    private String ip;
    private int port;
    private boolean validated;

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

    public void validate(){
        this.validated = true;
    }
}
