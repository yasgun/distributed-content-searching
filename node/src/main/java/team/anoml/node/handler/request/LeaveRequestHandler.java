package team.anoml.node.handler.request;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.anoml.node.sender.response.LeaveResponseSender;

public class LeaveRequestHandler extends AbstractRequestHandler {

    private static Logger logger = LogManager.getLogger(LeaveRequestHandler.class.getName());

    @Override
    protected void handleRequest() {
        String[] parts = getMessage().split(" ");
        String ipAddress = parts[0];
        int port = Integer.parseInt(parts[1]);

        LeaveResponseSender sender = new LeaveResponseSender();
        sender.setDestinationIpAddress(ipAddress);
        sender.setDestinationPort(port);

        logger.debug("Executing LEAVEOK response sender for " + ipAddress + ":" + port);
        sender.send();
    }
}
