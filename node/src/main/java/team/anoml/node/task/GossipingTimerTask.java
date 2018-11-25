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
import java.util.Random;

public class GossipingTimerTask extends AbstractTimerTask {

    private static Logger logger = LogManager.getLogger(GossipingTimerTask.class.getName());

    private RoutingTable routingTable = RoutingTable.getRoutingTable();

    @Override
    public void run() {
        logger.info("Gossiping round started");

        Collection<RoutingTableEntry> routingTableEntries = RoutingTable.getRoutingTable().getAllEntries();

        if (routingTable.getAllEntries().size() >= SystemSettings.getRoutingTableLimit()) {
            Random random = new Random();

            int randInt1 = random.nextInt(routingTableEntries.size());
            int randInt2 = random.nextInt(routingTableEntries.size());

            while (randInt1 == randInt2) {
                randInt2 = random.nextInt(routingTableEntries.size());
            }

            int i = 1;
            RoutingTableEntry entry;
            int highestNumber;

            if (randInt1 < randInt2) {
                highestNumber = randInt2;
            } else {
                highestNumber = randInt1;
            }

            while (i <= highestNumber) {
                entry = routingTableEntries.iterator().next();
                if (i == randInt1) {
                    routingTable.removeEntry(entry.getIP(), entry.getPort());
                }
                if (i == randInt2) {
                    routingTable.removeEntry(entry.getIP(), entry.getPort());
                }
                if (i == randInt1 || i == randInt2) {
                    try (DatagramSocket datagramSocket = new DatagramSocket()) {

                        String response = String.format(SystemSettings.LEAVE_MSG_FORMAT, SystemSettings.getNodeIP(), SystemSettings.getUDPPort());
                        sendRequest(datagramSocket, response, new InetSocketAddress(entry.getIP(), entry.getPort()).getAddress(), entry.getPort());
                        ResponseTracker.getResponseTracker().addWaitingResponse(SystemSettings.LEAVEOK_MSG, entry.getIP(), entry.getPort(), new Date());
                        logger.info("sent LEAVE request to ip: " + entry.getIP() + " port: " + entry.getPort());

                    } catch (IOException e) {
                        logger.info("Sending LEAVE request failed", e);
                    }

                    try (DatagramSocket datagramSocket = new DatagramSocket()) {

                        String response = String.format(SystemSettings.NBR_MSG_FORMAT, SystemSettings.getNodeIP(), SystemSettings.getUDPPort());
                        sendRequest(datagramSocket, response, new InetSocketAddress(entry.getIP(), entry.getPort()).getAddress(), entry.getPort());
                        ResponseTracker.getResponseTracker().addWaitingResponse(SystemSettings.NBROK_MSG, entry.getIP(), entry.getPort(), new Date());
                        logger.info("Requested routing table from ip: " + entry.getIP() + " port: " + entry.getPort());

                    } catch (IOException e) {
                        logger.info("Sending NBR request failed", e);
                    }
                }
                i++;
            }
        } else if (routingTable.getAllEntries().size() >= 2) {

            Random random = new Random();

            int randInt1 = random.nextInt(routingTableEntries.size());
            int randInt2 = random.nextInt(routingTableEntries.size());

            while (randInt1 == randInt2) {
                randInt2 = random.nextInt(routingTableEntries.size());
            }

            int i = 1;
            RoutingTableEntry entry;
            int highestNumber;

            if (randInt1 < randInt2) {
                highestNumber = randInt2;
            } else {
                highestNumber = randInt1;
            }

            while (i <= highestNumber) {
                entry = routingTableEntries.iterator().next();
                if (i == randInt1 || i == randInt2) {
                    try (DatagramSocket datagramSocket = new DatagramSocket()) {

                        String response = String.format(SystemSettings.NBR_MSG_FORMAT, SystemSettings.getNodeIP(), SystemSettings.getUDPPort());
                        sendRequest(datagramSocket, response, new InetSocketAddress(entry.getIP(), entry.getPort()).getAddress(), entry.getPort());
                        ResponseTracker.getResponseTracker().addWaitingResponse(SystemSettings.NBROK_MSG, entry.getIP(), entry.getPort(), new Date());
                        logger.info("Requested routing table from ip: " + entry.getIP() + " port: " + entry.getPort());

                    } catch (IOException e) {
                        logger.info("Sending NBR request failed", e);
                    }
                }
                i++;
            }
        } else if (routingTable.getAllEntries().size() == 1) {

            for (RoutingTableEntry entry : routingTableEntries) {

                try (DatagramSocket datagramSocket = new DatagramSocket()) {
                    String response = String.format(SystemSettings.NBR_MSG_FORMAT, SystemSettings.getNodeIP(), SystemSettings.getUDPPort());
                    sendRequest(datagramSocket, response, new InetSocketAddress(entry.getIP(), entry.getPort()).getAddress(), entry.getPort());
                    ResponseTracker.getResponseTracker().addWaitingResponse(SystemSettings.NBROK_MSG, entry.getIP(), entry.getPort(), new Date());
                    logger.info("Requested routing table from ip: " + entry.getIP() + " port: " + entry.getPort());

                } catch (IOException e) {
                    logger.info("Sending NBR request failed", e);
                }

            }
        }
    }
}
