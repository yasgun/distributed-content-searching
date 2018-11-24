package team.anoml.node.handler.response;

import team.anoml.node.core.RoutingTableEntry;
import team.anoml.node.handler.request.JoinRequestHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JoinResponseHandler extends AbstractResponseHandler{

    private static Logger logger = Logger.getLogger(JoinRequestHandler.class.getName());

    @Override
    protected void handleResponse() {
        String[] parts = getMessage().split(" ");

        String ipAddress = parts[0];
        int port = Integer.parseInt(parts[1]);

        RoutingTableEntry entry = getRoutingTable().getEntryByIP(ipAddress);
        if (entry != null){
            entry.validate();
            logger.log(Level.INFO, "Validated ip: " + ipAddress + " port: " + port + " in routing table");
        }else{
            logger.log(Level.WARNING, "Handling JOIN response failed as no table entry was found");
        }
    }
}
