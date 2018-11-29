package team.anoml.node.sender.request;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.anoml.node.core.ResponseTracker;
import team.anoml.node.util.SystemSettings;

import java.io.IOException;
import java.util.Date;

public class NeighbourRequestSender extends AbstractRequestSender {

    private static Logger logger = LogManager.getLogger(NeighbourRequestSender.class.getName());

    @Override
    protected void sendRequest() {
        try {

            String request = String.format(SystemSettings.NBR_MSG_FORMAT, nodeIpAddress, nodePort);
            ResponseTracker.getResponseTracker().addWaitingResponse(SystemSettings.NBROK_MSG, getDestinationIpAddress(), getDestinationPort(), new Date());
            sendMessage(request, getDestinationIpAddress(), getDestinationPort());
            logger.info("Sent message: [" + request + "] from " + getDestinationIpAddress() + ":" + getDestinationPort());

        } catch (IOException e) {
            logger.error("Sending request to " + getDestinationIpAddress() + ":" + getDestinationPort() + " failed", e);        }
    }
}
