package team.anoml.node.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.anoml.node.core.ResponseTracker;
import team.anoml.node.core.RoutingTable;
import team.anoml.node.core.RoutingTableEntry;
import team.anoml.node.util.SystemSettings;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Date;

public class HeartbeatTimerTask extends AbstractTimerTask {

    private static Logger logger = LogManager.getLogger(HeartbeatTimerTask.class.getName());

    @Override
    public void run() {
        Collection<RoutingTableEntry> routingTableEntries = RoutingTable.getRoutingTable().getAllEntries();

        for (RoutingTableEntry entry : routingTableEntries) {
            String ipAddress = entry.getIP();
            int port = entry.getPort();

            if (ResponseTracker.getResponseTracker().consumeWaitingResponse(SystemSettings.HBOK_MSG, ipAddress, port)) {

                RoutingTable.getRoutingTable().removeEntry(ipAddress, port);
                logger.info("Ip: " + ipAddress + " port: " + port + " was removed from RoutingTable since no response to HB");

            } else {

                try (DatagramSocket datagramSocket = new DatagramSocket()) {

                    String response = String.format(SystemSettings.HB_MSG_FORMAT, SystemSettings.getNodeIP(), SystemSettings.getUDPPort());
                    sendRequest(datagramSocket, response, new InetSocketAddress(ipAddress, port).getAddress(), port);
                    ResponseTracker.getResponseTracker().addWaitingResponse(SystemSettings.HBOK_MSG, ipAddress, port, new Date());
                    logger.info("Requested HB from ip: " + ipAddress + " port: " + port);

                } catch (IOException e) {
                    logger.error("Sending HB request failed", e);
                }
            }

        }
    }
}
