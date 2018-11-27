package team.anoml.node.handler.request;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.anoml.node.sender.response.NeighbourResponseSender;

public class NeighbourRequestHandler extends AbstractRequestHandler {

    private static Logger logger = LogManager.getLogger(NeighbourRequestHandler.class.getName());

    @Override
    protected void handleRequest() {
        String[] parts = getMessage().split(" ");
        String ipAddress = parts[0];
        int port = Integer.parseInt(parts[1]);

        NeighbourResponseSender sender = new NeighbourResponseSender();
        sender.setDestinationIpAddress(ipAddress);
        sender.setDestinationPort(port);

        logger.debug("Executing NBROK response sender");
        sender.send();
    }
}
