package team.anoml.node.handler.request;

import team.anoml.node.core.FileTableEntry;
import team.anoml.node.util.SystemSettings;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SearchRequestHandler extends AbstractRequestHandler {

    private static Logger logger = Logger.getLogger(SearchRequestHandler.class.getName());

    @Override
    protected void handleRequest() {
        String[] parts = getMessage().split(" ");

        String ipAddress = parts[0];
        int port = Integer.parseInt(parts[1]);
        String fileName = parts[2];
        int hopsCount = Integer.parseInt(parts[3]);

        Collection<FileTableEntry> fileTableEntries = getFileTable().getEntriesByFileNameRegex(fileName);
        StringBuilder fileNamesResponse = new StringBuilder();

        for (FileTableEntry fileTableEntry : getFileTable().getAllEntries()) {
            fileNamesResponse.append(fileTableEntry.getFileName());
        }

        if (!fileTableEntries.isEmpty()) {
            try (DatagramSocket datagramSocket = new DatagramSocket()) {
                String response = String.format(SystemSettings.SEROK_MSG_FORMAT, fileTableEntries.size(), SystemSettings
                        .getNodeIP(), SystemSettings.getTCPPort(), hopsCount + 1, fileNamesResponse);
                sendMessage(datagramSocket, response, new InetSocketAddress(ipAddress, port).getAddress(), port);
                logger.log(Level.INFO, "Sent file names " + fileNamesResponse + " to: ip " + ipAddress + " port: " +
                        port);
            } catch (IOException e) {
                logger.log(Level.WARNING, "Handling SER request failed", e);
            }
        }
    }
}
