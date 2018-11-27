package team.anoml.node.handler;

import team.anoml.node.core.RoutingTable;
import team.anoml.node.util.SystemSettings;

public abstract class AbstractHandler implements Runnable {

    protected static final String nodeIpAddress = SystemSettings.getNodeIP();
    protected static final int nodePort = SystemSettings.getUDPPort();

    private String clientIpAddress;
    private int clientPort;
    private String message;

    private RoutingTable routingTable = RoutingTable.getRoutingTable();

    public void setMessage(String message) {
        this.message = message;
    }

    public void setClientIpAddress(String clientIpAddress) {
        this.clientIpAddress = clientIpAddress;
    }

    public void setClientPort(int clientPort) {
        this.clientPort = clientPort;
    }

    protected String getMessage() {
        return message;
    }

    protected RoutingTable getRoutingTable() {
        return routingTable;
    }

    protected String getClientIpAddress() {
        return clientIpAddress;
    }

    protected int getClientPort() {
        return clientPort;
    }

}
