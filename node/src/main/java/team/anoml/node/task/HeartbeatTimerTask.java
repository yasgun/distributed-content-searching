package team.anoml.node.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.anoml.node.core.ResponseTracker;
import team.anoml.node.core.RoutingTable;
import team.anoml.node.core.RoutingTableEntry;
import team.anoml.node.sender.request.HeartbeatRequestSender;
import team.anoml.node.util.SystemSettings;

import java.util.Collection;

public class HeartbeatTimerTask extends AbstractTimerTask {

    private static Logger logger = LogManager.getLogger(HeartbeatTimerTask.class.getName());

    @Override
    public void run() {
        Collection<RoutingTableEntry> routingTableEntries = RoutingTable.getRoutingTable().getAllEntries();

        for (RoutingTableEntry entry : routingTableEntries) {
            String ipAddress = entry.getIPAddress();
            int port = entry.getPort();

            if (!ResponseTracker.getResponseTracker().consumeWaitingResponse(SystemSettings.HBOK_MSG, ipAddress, port)) {

                RoutingTable.getRoutingTable().removeEntry(ipAddress, port);
                logger.info(ipAddress + ":" + port + " was removed from routing table since no response to HB");

            } else {
                sendRequest(new HeartbeatRequestSender(), ipAddress, port);
            }

        }
    }
}
