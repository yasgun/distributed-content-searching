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

    @Override
    protected void sendResponse() {
        Collection<FileTableEntry> fileTableEntries = getFileTable().getEntriesByFileName(fileName);

        if (!fileTableEntries.isEmpty()) {
            StringBuilder fileNamesResponse = new StringBuilder();

            for (FileTableEntry fileTableEntry : getFileTable().getAllEntries()) {
                fileNamesResponse.append(fileTableEntry.getFileName()).append(" ");
            }

            try {
                String response = String.format(SystemSettings.SEROK_MSG_FORMAT, fileTableEntries.size(),
                        nodeIpAddress, SystemSettings.getTCPPort(), hopsCount + 1,
                        fileNamesResponse.toString().trim());

                sendMessage(response, getDestinationIpAddress(), getDestinationPort());
                logger.info("Sent file names: " + fileNamesResponse + " to " + getDestinationIpAddress() + ":" + getDestinationPort());
            } catch (IOException e) {
                logger.error("Handling SER request from " + getDestinationIpAddress() + ":" + getDestinationPort() + "failed", e);
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

                    logger.debug("Executing request sender");
                    sender.send();
                }
            } else {
                try {
                    String response = String.format(SystemSettings.SEROK_MSG_FORMAT, 0,
                            nodeIpAddress, SystemSettings.getTCPPort(), hopsCount + 1, "");

                    sendMessage(response, getDestinationIpAddress(), getDestinationPort());
                    logger.info("Sent no files found SEROK response to " + getDestinationIpAddress() + ":" + getDestinationPort());
                } catch (IOException e) {
                    logger.error("Sending SEROK response to " + getDestinationIpAddress() + ":" + getDestinationPort() + "failed", e);
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
}
