package team.anoml.node.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.anoml.node.core.RoutingTable;
import team.anoml.node.util.SystemSettings;

public abstract class AbstractHandler implements Runnable {

    private static Logger logger = LogManager.getLogger(AbstractHandler.class.getName());


    protected static final String nodeIpAddress = SystemSettings.getNodeIP();
    protected static final int nodePort = SystemSettings.getUDPPort();

    private String clientIpAddress;
    private int clientPort;
    private String message;

    private RoutingTable routingTable = RoutingTable.getRoutingTable();

    public void setMessage(String message) {
        logger.debug("Message received: " + message);
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
