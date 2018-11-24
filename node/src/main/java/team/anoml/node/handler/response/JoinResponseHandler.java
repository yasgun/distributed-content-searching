package team.anoml.node.handler.response;

import team.anoml.node.core.ResponseTracker;
import team.anoml.node.util.SystemSettings;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JoinResponseHandler extends AbstractResponseHandler {

    private static Logger logger = Logger.getLogger(JoinResponseHandler.class.getName());

    @Override
    protected void handleResponse() {
        String[] parts = getMessage().split(" ");

        String ipAddress = parts[0];
        int port = Integer.parseInt(parts[1]);
        boolean isTracked = ResponseTracker.getResponseTracker().consumeWaitingResponse(SystemSettings.NBROK_MSG + ":" + ipAddress);

        if (isTracked) {
            getRoutingTable().getEntryByIP(ipAddress).validate();
            logger.log(Level.INFO, "Validated ip: " + ipAddress + " port: " + port + " in routing table");
        } else {
            logger.log(Level.WARNING, "Handling JOIN response failed as no table entry was found");
        }
    }
}
