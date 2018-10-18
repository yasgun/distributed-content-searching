package team.anoml.node;

import team.anoml.node.impl.RoutingTable;
import team.anoml.node.impl.UDPServer;
import team.anoml.node.util.SystemSettings;

public class Node {

    private static RoutingTable routingTable;
    private static String ipAddress;
    private static int port;

    private static UDPServer udpServer;

    public static void main(String[] args) {
        routingTable = new RoutingTable();
        ipAddress = SystemSettings.getIPAddress();
        port = SystemSettings.getPort();

        udpServer = new UDPServer(port);

        // Add Bootstrapping logic here
    }

    public RoutingTable getRoutingTable() {
        return routingTable;
    }
}
