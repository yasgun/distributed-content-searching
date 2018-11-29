package team.anoml.node.handler.request;

import team.anoml.node.sender.response.SearchResponseSender;

public class SearchRequestHandler extends AbstractRequestHandler {

    @Override
    protected void handleRequest() {
        String[] parts = getMessage().split(" ");

        String ipAddress = parts[0];
        int port = Integer.parseInt(parts[1]);

        String fileName = parts[2];
        int hopsCount = Integer.parseInt(parts[3]);
        String id = parts[4];

        SearchResponseSender sender = new SearchResponseSender();
        sender.setDestinationIpAddress(ipAddress);
        sender.setDestinationPort(port);
        sender.setFileName(fileName);
        sender.setHopsCount(hopsCount);
        sender.setId(id);
        sender.send();
    }
}
