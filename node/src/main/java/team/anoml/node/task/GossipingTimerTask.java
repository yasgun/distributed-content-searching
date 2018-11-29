package team.anoml.node.task;

import team.anoml.node.core.RoutingTable;
import team.anoml.node.core.RoutingTableEntry;
import team.anoml.node.sender.request.NeighbourRequestSender;
import team.anoml.node.util.SystemSettings;

import java.util.Collection;

public class GossipingTimerTask extends AbstractTimerTask {

    private RoutingTable routingTable = RoutingTable.getRoutingTable();

    @Override
    public void run() {
        Collection<RoutingTableEntry> routingTableEntries = RoutingTable.getRoutingTable().getAllEntries();

        if (routingTable.getCount() >= SystemSettings.getRoutingTableLimit()) {
            routingTable.removeRandomEntryAndSendNBRRequest();
            routingTable.removeRandomEntryAndSendNBRRequest();

        } else if (routingTable.getAllEntries().size() >= 2) {
            routingTable.sendRandomNBRRequest();
            routingTable.sendRandomNBRRequest();

        } else if (routingTable.getAllEntries().size() == 1) {

            for (RoutingTableEntry entry : routingTableEntries) {
                sendRequest(new NeighbourRequestSender(), entry.getIPAddress(), entry.getPort());
            }
        }
    }
}
