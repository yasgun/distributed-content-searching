package team.anoml.node.handler.response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SearchResponseHandler extends AbstractResponseHandler {

    private static Logger logger = LogManager.getLogger(SearchResponseHandler.class.getName());

    @Override
    protected void handleResponse() {
        String[] parts = getMessage().split(" ");

        int filesCount = Integer.parseInt(parts[0]);

        String ipAddress = parts[1];
        int port = Integer.parseInt(parts[2]);
        int hopsCount = Integer.parseInt(parts[3]);

        logger.info("Received SEROK response from " + ipAddress + ":" + port + " hops count" + hopsCount);

        for (int i = 4; i < filesCount * 2; i++) {
            String fileName = parts[i];
            System.out.println("File name: " + fileName + " at " + ipAddress + ":" + port);
        }
    }
}
