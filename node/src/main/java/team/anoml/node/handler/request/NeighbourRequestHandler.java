package team.anoml.node.handler.request;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.anoml.node.core.RoutingTableEntry;
import team.anoml.node.util.SystemSettings;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Random;

public class NeighbourRequestHandler extends AbstractRequestHandler {

    private static Logger logger = LogManager.getLogger(NeighbourRequestHandler.class.getName());

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

                for (RoutingTableEntry entry : routingTableEntries) {
                    neighborDetails.append(entry.getIP()).append(" ").append(entry.getPort()).append(" ");
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
                        neighborDetails.append(neighbour.getIP()).append(" ").append(neighbour.getPort()).append(" ");
                    }
                    i++;
                }
            }

            String response = String.format(SystemSettings.NBROK_MSG_FORMAT, neighborDetailsSize, neighborDetails.toString().trim());

            sendMessage(datagramSocket, response, new InetSocketAddress(ipAddress, port).getAddress(), port);
            logger.info("Sent neighbor details: " + neighborDetails + " to " + ipAddress + ":" + port);
        } catch (IOException e) {
            logger.error("Handling NBR request failed", e);
        }
    }
}
