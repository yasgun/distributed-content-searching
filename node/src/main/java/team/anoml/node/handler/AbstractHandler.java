package team.anoml.node.handler;

import team.anoml.node.core.RoutingTable;

public abstract class AbstractHandler implements Runnable {

    private RoutingTable routingTable = RoutingTable.getRoutingTable();
    private String message;

    public void setMessage(String message) {
        this.message = message;
    }

    protected String getMessage() {
        return message;
    }

    protected RoutingTable getRoutingTable() {
        return routingTable;
    }
}
