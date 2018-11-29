package team.anoml.node.sender.response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.anoml.node.core.FileTableEntry;
import team.anoml.node.core.RoutingTableEntry;
import team.anoml.node.sender.request.SearchRequestSender;
import team.anoml.node.util.SystemSettings;

import java.io.IOException;
import java.util.Collection;

public class SearchResponseSender extends AbstractResponseSender {

    private static Logger logger = LogManager.getLogger(SearchResponseSender.class.getName());

    private String fileName;
    private int hopsCount;
    private String id;

    @Override
    protected void sendResponse() {
        Collection<FileTableEntry> fileTableEntries = getFileTable().getEntriesByFileName(fileName.replaceAll("_", " "));

        if (!fileTableEntries.isEmpty()) {
            StringBuilder fileNamesResponse = new StringBuilder();

            for (FileTableEntry fileTableEntry : fileTableEntries) {
                fileNamesResponse.append(fileTableEntry.getFileName().replaceAll(" ", "_")).append(" ");
            }

            try {
                String response = String.format(SystemSettings.SEROK_MSG_FORMAT, fileTableEntries.size(),
                        nodeIpAddress, SystemSettings.getTCPPort(), hopsCount + 1,
                        fileNamesResponse.toString().trim());

                sendMessage(response, getDestinationIpAddress(), getDestinationPort());
                logger.info("Sent message: [" + response + "] from " + getDestinationIpAddress() + ":" + getDestinationPort());
            } catch (IOException e) {
                logger.error("Sending response to " + getDestinationIpAddress() + ":" + getDestinationPort() + " failed", e);
            }

        } else {
            if (hopsCount < SystemSettings.getUDPSearchMaxHopCount()) {
                for (RoutingTableEntry entry : getRoutingTable().getAllEntries()) {
                    SearchRequestSender sender = new SearchRequestSender();

                    sender.setDestinationIpAddress(entry.getIPAddress());
                    sender.setDestinationPort(entry.getPort());
                    sender.setTargetIpAddress(getDestinationIpAddress());
                    sender.setTargetPort(getDestinationPort());
                    sender.setFileName(fileName);
                    sender.setHopsCount(hopsCount + 1);
                    sender.setId(id);
                    sender.send();
                }
            } else {
                try {
                    String response = String.format(SystemSettings.SEROK_MSG_FORMAT, 0,
                            nodeIpAddress, SystemSettings.getTCPPort(), hopsCount + 1, "");

                    sendMessage(response, getDestinationIpAddress(), getDestinationPort());
                    logger.info("Sent message: [" + response + "] from " + getDestinationIpAddress() + ":" + getDestinationPort());

                } catch (IOException e) {
                    logger.error("Sending response to " + getDestinationIpAddress() + ":" + getDestinationPort() + " failed", e);
                }
            }
        }
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setHopsCount(int hopsCount) {
        this.hopsCount = hopsCount;
    }

    public void setId(String id) {
        this.id = id;
    }
}
