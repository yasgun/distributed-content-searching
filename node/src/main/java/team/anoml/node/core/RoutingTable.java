package team.anoml.node.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.anoml.node.sender.AbstractSender;
import team.anoml.node.sender.request.NeighbourRequestSender;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class RoutingTable {

    private static Logger logger = LogManager.getLogger(RoutingTable.class.getName());

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

    public void removeRandomEntryAndSendNBRRequest() {
        Random random = new Random();

        int randInt = random.nextInt(getCount());

        int i = 0;

        for (RoutingTableEntry entry : getAllEntries()) {
            if (i == randInt) {
                removeEntry(entry.getIPAddress(), entry.getPort());
                sendRequest(new NeighbourRequestSender(), entry.getIPAddress(), entry.getPort());
                break;
            }
            i++;
        }
    }

    public void sendRandomNBRRequest() {
        Random random = new Random();

        int randInt = random.nextInt(getCount());

        int i = 0;

        for (RoutingTableEntry entry : getAllEntries()) {
            if (i == randInt) {
                sendRequest(new NeighbourRequestSender(), entry.getIPAddress(), entry.getPort());
                break;
            }
            i++;
        }
    }

    private void sendRequest(AbstractSender sender, String ipAddress, int port) {
        sender.setDestinationIpAddress(ipAddress);
        sender.setDestinationPort(port);

        logger.debug("Executing request sender");
        sender.send();
    }
}
