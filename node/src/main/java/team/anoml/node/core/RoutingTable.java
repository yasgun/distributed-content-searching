package team.anoml.node.core;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class RoutingTable {

    private static RoutingTable routingTable = new RoutingTable();
    private ConcurrentHashMap<String, RoutingTableEntry> entries = new ConcurrentHashMap<>();

    private RoutingTable() {
        //nothing is required here
    }

    public static RoutingTable getRoutingTable() {
        return routingTable;
    }

    public void addEntry(RoutingTableEntry routingTableEntry) {
        entries.putIfAbsent(routingTableEntry.getIPAddress() + ":" + routingTableEntry.getPort(), routingTableEntry);
    }

    public void removeEntry(String ip, int port) {
        entries.remove(ip + ":" + port);
    }

    public Collection<RoutingTableEntry> getAllEntries() {
        return entries.values();
    }

    public RoutingTableEntry getEntry(String ip, int port) {
        return entries.get(ip + ":" + port);
    }

    public int getCount() {
        return entries.size();
    }
}
