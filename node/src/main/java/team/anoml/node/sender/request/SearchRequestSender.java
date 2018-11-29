package team.anoml.node.sender.request;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.anoml.node.util.SystemSettings;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class SearchRequestSender extends AbstractRequestSender {

    private static Logger logger = LogManager.getLogger(SearchRequestSender.class.getName());

    private static ConcurrentHashMap<String, Date> sentRequests = new ConcurrentHashMap<>();

    private String targetIpAddress;
    private int targetPort;
    private String fileName;
    private int hopsCount;
    private String id;

    @Override
    protected void sendRequest() {
        if (sentRequests.containsKey(id)) {
            logger.info("SER message with id: " + id + " ignored since already sent");
        } else {
            sentRequests.put(id, new Date());
            try {
                String response = String.format(SystemSettings.SER_MSG_FORMAT, targetIpAddress, targetPort, fileName, hopsCount, id);
                sendMessage(response, getDestinationIpAddress(), getDestinationPort());
                logger.info("Sending SER for " + fileName + " from " + getDestinationIpAddress() + ":" + getDestinationPort());
            } catch (IOException e) {
                logger.error("Sending SER request to " + getDestinationIpAddress() + ":" + getDestinationPort() + " failed", e);
            }
        }
    }

    public void setTargetIpAddress(String targetIpAddress) {
        this.targetIpAddress = targetIpAddress;
    }

    public void setTargetPort(int targetPort) {
        this.targetPort = targetPort;
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
