package team.anoml.node.handler.response;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SearchResponseHandler extends AbstractResponseHandler {

    private static Logger logger = Logger.getLogger(SearchResponseHandler.class.getName());

    @Override
    protected void handleResponse() {
        String[] parts = getMessage().split(" ");

        int filesCount = Integer.parseInt(parts[0]);
        String ipAddress = parts[1];
        int port = Integer.parseInt(parts[2]);
        int hopsCount = Integer.parseInt(parts[3]);

        logger.log(Level.INFO, "Received SEROK response from ip: " + ipAddress + " port: " + port + " hops count: " + hopsCount);
        for (int i = 4; i < filesCount * 2; i++) {
            String fileName = parts[i];
            logger.log(Level.INFO, "File name: " + fileName);
        }
    }
}
