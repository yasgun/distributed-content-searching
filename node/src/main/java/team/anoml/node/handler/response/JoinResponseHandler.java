package team.anoml.node.handler.response;

import team.anoml.node.core.ResponseTracker;
import team.anoml.node.util.SystemSettings;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JoinResponseHandler extends AbstractResponseHandler {

    private static Logger logger = Logger.getLogger(JoinResponseHandler.class.getName());
    private String ipAddress;
    private int port;

    public void setMessage(String message, String clientIp, int clientPort) {
        super.setMessage(message);
        this.ipAddress = clientIp;
        this.port = clientPort;
    }

    @Override
    protected void handleResponse() {
        String[] parts = getMessage().split(" ");

        int value = Integer.parseInt(parts[0]);

        boolean isTracked = ResponseTracker.getResponseTracker().consumeWaitingResponse(SystemSettings.NBROK_MSG + ":" + ipAddress);

        if (isTracked) {
            if (value == 0){
                getRoutingTable().getEntryByIP(ipAddress).validate();
                logger.log(Level.INFO, "Validated ip: " + ipAddress + " port: " + port + " in routing table");
            }else {
                logger.log(Level.INFO, "Validation of ip: " + ipAddress + " port: " + port + " in routing table failed due to error: "+ value);
            }
        } else {
            logger.log(Level.WARNING, "Handling JOIN response failed as no Response Tracker entry was found");
        }
    }
}
