package team.anoml.node.handler.response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.anoml.node.core.ResponseTracker;
import team.anoml.node.util.SystemSettings;

public class JoinResponseHandler extends AbstractResponseHandler {

    private static Logger logger = LogManager.getLogger(JoinResponseHandler.class.getName());

    @Override
    protected void handleResponse() {
        String[] parts = getMessage().split(" ");

        int value = Integer.parseInt(parts[0]);

        if (ResponseTracker.getResponseTracker()
                .consumeWaitingResponse(SystemSettings.JOINOK_MSG, getClientIpAddress(), getClientPort())) {

            if (value == 0) {
                getRoutingTable().getEntry(getClientIpAddress(), getClientPort()).validate();
                logger.info("Validated ip: " + getClientIpAddress() + " port: " + getClientPort() + " in routing table");
            } else {
                logger.info("Validation of ip: " + getClientIpAddress() + " port: " + getClientPort() +
                        " in routing table failed due to error: " + value);
            }
        } else {
            logger.error("Handling JOIN response failed since no response tracker entry was found");
        }
    }
}
