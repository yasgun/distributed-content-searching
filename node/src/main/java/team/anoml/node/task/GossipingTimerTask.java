package team.anoml.node.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.anoml.node.core.RoutingTable;
import team.anoml.node.core.RoutingTableEntry;
import team.anoml.node.sender.request.LeaveRequestSender;
import team.anoml.node.sender.request.NeighbourRequestSender;
import team.anoml.node.util.SystemSettings;

import java.util.Collection;
import java.util.Random;

public class GossipingTimerTask extends AbstractTimerTask {

    private static Logger logger = LogManager.getLogger(GossipingTimerTask.class.getName());

    private RoutingTable routingTable = RoutingTable.getRoutingTable();

    @Override
    public void run() {
        logger.info("Gossiping round started");

        Collection<RoutingTableEntry> routingTableEntries = RoutingTable.getRoutingTable().getAllEntries();

        if (routingTable.getAllEntries().size() >= SystemSettings.getRoutingTableLimit()) {
            Random random = new Random();

            int randInt1 = random.nextInt(routingTableEntries.size());
            int randInt2 = random.nextInt(routingTableEntries.size());

            while (randInt1 == randInt2) {
                randInt2 = random.nextInt(routingTableEntries.size());
            }

            int i = 1;
            RoutingTableEntry entry;
            int highestNumber;

            if (randInt1 < randInt2) {
                highestNumber = randInt2;
            } else {
                highestNumber = randInt1;
            }

            while (i <= highestNumber) {
                entry = routingTableEntries.iterator().next();
                if (i == randInt1) {
                    routingTable.removeEntry(entry.getIPAddress(), entry.getPort());
                }
                if (i == randInt2) {
                    routingTable.removeEntry(entry.getIPAddress(), entry.getPort());
                }
                if (i == randInt1 || i == randInt2) {
                    sendRequest(new LeaveRequestSender(), entry.getIPAddress(), entry.getPort());
                    sendRequest(new NeighbourRequestSender(), entry.getIPAddress(), entry.getPort());
                }
                i++;
            }
        } else if (routingTable.getAllEntries().size() >= 2) {

            Random random = new Random();

            int randInt1 = random.nextInt(routingTableEntries.size());
            int randInt2 = random.nextInt(routingTableEntries.size());

            while (randInt1 == randInt2) {
                randInt2 = random.nextInt(routingTableEntries.size());
            }

            int i = 1;
            RoutingTableEntry entry;
            int highestNumber;

            if (randInt1 < randInt2) {
                highestNumber = randInt2;
            } else {
                highestNumber = randInt1;
            }

            while (i <= highestNumber) {
                entry = routingTableEntries.iterator().next();
                if (i == randInt1 || i == randInt2) {
                    sendRequest(new NeighbourRequestSender(), entry.getIPAddress(), entry.getPort());
                }
                i++;
            }
        } else if (routingTable.getAllEntries().size() == 1) {

            for (RoutingTableEntry entry : routingTableEntries) {
                sendRequest(new NeighbourRequestSender(), entry.getIPAddress(), entry.getPort());
            }
        }
    }
}
