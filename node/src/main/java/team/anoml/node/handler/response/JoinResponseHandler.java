package team.anoml.node.handler.response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.anoml.node.core.ResponseTracker;
import team.anoml.node.core.RoutingTableEntry;
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
                getRoutingTable().addEntry(new RoutingTableEntry(getClientIpAddress(), getClientPort()));
                logger.info("Added " + getClientIpAddress() + ":" + getClientPort() + " to routing table");
            } else {
                logger.info("Adding " + getClientIpAddress() + ":" + getClientPort() +
                        " to routing table failed due to error: " + value);
            }
        } else {
            logger.error("Handling JOIN response failed since no response tracker entry was found");
        }
    }
}
