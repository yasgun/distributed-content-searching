package team.anoml.node.core;

import java.util.Collection;
import java.util.HashMap;
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
