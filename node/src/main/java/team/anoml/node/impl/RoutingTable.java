package team.anoml.node.impl;

import java.util.Collection;
import java.util.HashMap;

public class RoutingTable {

    private HashMap<String, RoutingTableEntry> entries = new HashMap<>();

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
        return null;
    }
}
