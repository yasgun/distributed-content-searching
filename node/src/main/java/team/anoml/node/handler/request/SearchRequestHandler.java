package team.anoml.node.handler.request;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.anoml.node.core.FileTableEntry;
import team.anoml.node.core.RoutingTableEntry;
import team.anoml.node.util.SystemSettings;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Collection;

public class SearchRequestHandler extends AbstractRequestHandler {

    private static Logger logger = LogManager.getLogger(SearchRequestHandler.class.getName());

    @Override
    protected void handleRequest() {
        String[] parts = getMessage().split(" ");

        String ipAddress = parts[0];
        int port = Integer.parseInt(parts[1]);

        String fileName = parts[2];
        int hopsCount = Integer.parseInt(parts[3]);

        Collection<FileTableEntry> fileTableEntries = getFileTable().getEntriesByFileName(fileName);
        StringBuilder fileNamesResponse = new StringBuilder();

        for (FileTableEntry fileTableEntry : getFileTable().getAllEntries()) {
            fileNamesResponse.append(fileTableEntry.getFileName()).append(" ");
        }

        if (!fileTableEntries.isEmpty()) {
            try (DatagramSocket datagramSocket = new DatagramSocket(SystemSettings.getUDPPort())) {
                String response = String.format(SystemSettings.SEROK_MSG_FORMAT, fileTableEntries.size(),
                        SystemSettings.getNodeIP(), SystemSettings.getTCPPort(), hopsCount + 1,
                        fileNamesResponse.toString().trim());

                sendMessage(datagramSocket, response, new InetSocketAddress(ipAddress, port).getAddress(), port);
                logger.info("Sent file names " + fileNamesResponse + " to: ip " + ipAddress + " port: " + port);
            } catch (IOException e) {
                logger.error("Handling SER request failed", e);
            }

        } else {

            try (DatagramSocket datagramSocket = new DatagramSocket(SystemSettings.getUDPPort())) {
                String request = String.format(SystemSettings.SER_MSG_FORMAT, ipAddress, port, fileName, hopsCount + 1);

                for (RoutingTableEntry routingTableEntry : getRoutingTable().getAllEntries()) {
                    sendMessage(datagramSocket, request, new InetSocketAddress(routingTableEntry.getIP(),
                            routingTableEntry.getPort()).getAddress(), routingTableEntry.getPort());
                    logger.info("Sent SER requests to neighbor: ip " + routingTableEntry.getIP() + " port: " +
                            routingTableEntry.getPort());
                }

            } catch (IOException e) {
                logger.error("Handling SER request failed", e);
            }
        }
    }
}
