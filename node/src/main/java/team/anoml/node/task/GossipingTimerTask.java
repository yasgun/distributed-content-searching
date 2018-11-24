package team.anoml.node.task;

import team.anoml.node.core.ResponseTracker;
import team.anoml.node.core.RoutingTable;
import team.anoml.node.core.RoutingTableEntry;
import team.anoml.node.util.SystemSettings;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Date;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GossipingTimerTask extends TimerTask {

    private static Logger logger = Logger.getLogger(GossipingTimerTask.class.getName());

    @Override
    public void run() {
        logger.log(Level.INFO, "Gossiping round started");

        Collection<RoutingTableEntry> routingTableEntries = RoutingTable.getRoutingTable().getAllEntries();

        for (RoutingTableEntry entry : routingTableEntries) {
            String ipAddress = entry.getIP();
            int port = entry.getPort();

            try (DatagramSocket datagramSocket = new DatagramSocket()) {
                String response = String.format(SystemSettings.NBR_MSG_FORMAT, ipAddress, 0);
                sendRequest(datagramSocket, response, new InetSocketAddress(ipAddress, port).getAddress(), port);
                ResponseTracker.getResponseTracker().addWaitingResponse(SystemSettings.NBROK_MSG + ":" + ipAddress, new Date());
                logger.log(Level.INFO, "Requested routing table from ip: " + ipAddress + " port: " + port);
            } catch (IOException e) {
                logger.log(Level.WARNING, "Sending NBR request failed", e);
            }
        }
    }

    private void sendRequest(DatagramSocket datagramSocket, String response, InetAddress address, int port) throws IOException {
        String lengthText = "0000" + (response.length() + 5);
        lengthText = lengthText.substring(lengthText.length() - 4);
        response = lengthText + " " + response;

        DatagramPacket datagramPacket = new DatagramPacket(response.getBytes(), response.length(), address, port);

        for (int i = 0; i < SystemSettings.getRequestTryCount(); i++) {
            datagramSocket.send(datagramPacket);
            try {
                Thread.sleep(SystemSettings.getRequestTryDelay());
            } catch (InterruptedException ignored) {
            }
        }

    }
}
