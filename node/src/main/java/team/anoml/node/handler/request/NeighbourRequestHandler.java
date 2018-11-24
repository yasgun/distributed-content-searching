package team.anoml.node.handler.request;

import team.anoml.node.core.RoutingTableEntry;
import team.anoml.node.util.SystemSettings;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NeighbourRequestHandler extends AbstractRequestHandler {

    private static Logger logger = Logger.getLogger(JoinRequestHandler.class.getName());

    @Override
    protected void handleRequest() {
        String[] parts = getMessage().split(" ");

        String ipAddress = parts[0];
        int port = Integer.parseInt(parts[1]);

        try (DatagramSocket datagramSocket = new DatagramSocket()) {
            Collection<RoutingTableEntry> routingTableEntries = getRoutingTable().getAllEntries();
            StringBuilder neighborDetails = new StringBuilder();
            int neighborDetailsSize;

            if (routingTableEntries.size() <= 2) {
                neighborDetailsSize = routingTableEntries.size();
                while (routingTableEntries.iterator().hasNext()) {
                    RoutingTableEntry neighbour = routingTableEntries.iterator().next();
                    neighborDetails.append(neighbour.getIP()).append(" ").append(neighbour.getPort()).append(" ");
                }
            } else {
                neighborDetailsSize = 2;
                Random random = new Random();
                int randInt1 = random.nextInt(routingTableEntries.size());
                int randInt2 = random.nextInt(routingTableEntries.size());

                while (randInt1 == randInt2) {
                    randInt2 = random.nextInt(routingTableEntries.size());
                }

                int i = 1;
                RoutingTableEntry neighbour;
                int highestNumber;

                if (randInt1 < randInt2) {
                    highestNumber = randInt2;
                } else {
                    highestNumber = randInt1;
                }
                while (i <= highestNumber) {
                    neighbour = routingTableEntries.iterator().next();
                    if (i == randInt1) {
                        neighborDetails.append(neighbour.getIP()).append(" ").append(neighbour.getPort()).append(" ");
                    }
                    if (i == randInt2) {
                        neighborDetails.append(neighbour.getIP()).append(" ").append(neighbour.getPort());
                    }
                    i++;
                }
            }

            String response = String.format(SystemSettings.NBROK_MSG_FORMAT, neighborDetailsSize, neighborDetails
                    .toString().trim());
            sendResponse(datagramSocket, response, new InetSocketAddress(ipAddress, port).getAddress(), port);
            logger.log(Level.INFO, "Sent neighbor details no of neighbors: " + neighborDetailsSize + " details: " +
                    neighborDetails + " to " + ipAddress + ":" + port);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Handling NBR request failed", e);
        }
    }
}
