package team.anoml.node;

import team.anoml.node.impl.RoutingTable;
import team.anoml.node.impl.RoutingTableEntry;

public class Node {

    private RoutingTable routingTable;
    private String ip_address;

    public static void main(String[] args) {

    }

    public void addEntry(RoutingTableEntry entry) {
        routingTable.addEntry(entry);
    }

    public String getIp_address() {
        return ip_address;
    }
}
