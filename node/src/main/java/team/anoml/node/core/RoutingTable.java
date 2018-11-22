package team.anoml.node.core;

import java.util.Collection;
import java.util.HashMap;

public class RoutingTable {

    private static RoutingTable routingTable = new RoutingTable();
    private HashMap<String, RoutingTableEntry> entries = new HashMap<>();

    private RoutingTable() {
        //nothing is required here
    }

    public static RoutingTable getRoutingTable() {
        return routingTable;
    }

    public void addEntry(RoutingTableEntry routingTableEntry) {
        entries.put(routingTableEntry.getIP(), routingTableEntry);
    }

    public void removeEntry(String ip) {
        entries.remove(ip);
    }

    public Collection<RoutingTableEntry> getAllEntries() {
        return entries.values();
    }

    public RoutingTableEntry getEntryByIP(String ip) {
        return entries.get(ip);
    }
}
