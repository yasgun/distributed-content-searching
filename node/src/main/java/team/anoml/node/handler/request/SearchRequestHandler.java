package team.anoml.node.handler.request;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.anoml.node.sender.response.SearchResponseSender;

public class SearchRequestHandler extends AbstractRequestHandler {

    private static Logger logger = LogManager.getLogger(SearchRequestHandler.class.getName());

    @Override
    protected void handleRequest() {
        String[] parts = getMessage().split(" ");

        String ipAddress = parts[0];
        int port = Integer.parseInt(parts[1]);

        String fileName = parts[2];
        int hopsCount = Integer.parseInt(parts[3]);

        SearchResponseSender sender = new SearchResponseSender();
        sender.setDestinationIpAddress(ipAddress);
        sender.setDestinationPort(port);
        sender.setFileName(fileName);
        sender.setHopsCount(hopsCount);

        logger.debug("Executing SEROK response sender for " + ipAddress + ":" + port);
        sender.send();
    }
}
