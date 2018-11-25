package team.anoml.node.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.anoml.node.api.NodeServer;
import team.anoml.node.core.ResponseTracker;
import team.anoml.node.core.RoutingTable;
import team.anoml.node.core.RoutingTableEntry;
import team.anoml.node.handler.AbstractHandler;
import team.anoml.node.handler.request.*;
import team.anoml.node.handler.response.*;
import team.anoml.node.task.GossipingTimerTask;
import team.anoml.node.task.HeartbeatTimerTask;
import team.anoml.node.util.SystemSettings;

import java.io.IOException;
import java.net.*;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UDPServer implements NodeServer {

    private static Logger logger = LogManager.getLogger(UDPServer.class.getName());

    private Timer timer = new Timer(true);
    private Executor executor = Executors.newSingleThreadExecutor();

    private boolean listening = false;
    private final int port = SystemSettings.getUDPPort();

    @Override
    public void startServer() {
        if (listening) {
            return;
        }

        try {
            listen();
        } catch (Exception e) {
            logger.warn("UDP listening failed");
        }
    }

    private void listen() throws SocketException {
        listening = true;
        startGossiping();
        startHeartbeat();

        DatagramSocket datagramSocket = new DatagramSocket(port);

        while (listening) {
            try {
                byte[] buffer = new byte[SystemSettings.getMaxMessageCharSize() * 8];
                DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(incoming);

                String clientIp = incoming.getAddress().getHostAddress();
                int clientPort = incoming.getPort();

                byte[] data = incoming.getData();
                String request = new String(data, 0, incoming.getLength());

                String[] incomingResult = request.split(" ", 3);
                String command = incomingResult[1];

                AbstractHandler handler = null;

                switch (command) {
                    case SystemSettings.JOIN_MSG:
                        handler = new JoinRequestHandler();
                        break;
                    case SystemSettings.LEAVE_MSG:
                        handler = new LeaveRequestHandler();
                        break;
                    case SystemSettings.NBR_MSG:
                        handler = new NeighbourRequestHandler();
                        break;
                    case SystemSettings.HB_MSG:
                        handler = new HeartbeatRequestHandler();
                        break;
                    case SystemSettings.SER_MSG:
                        handler = new SearchRequestHandler();
                        break;
                    case SystemSettings.ERROR_MSG:
                        handler = new ErrorResponseHandler();
                        break;
                    case SystemSettings.JOINOK_MSG:
                        handler = new JoinResponseHandler();
                        break;
                    case SystemSettings.LEAVEOK_MSG:
                        handler = new LeaveResponseHandler();
                        break;
                    case SystemSettings.NBROK_MSG:
                        handler = new NeighbourResponseHandler();
                        break;
                    case SystemSettings.HBOK_MSG:
                        handler = new HeartbeatResponseHandler();
                        break;
                    case SystemSettings.SEROK_MSG:
                        handler = new SearchResponseHandler();
                        break;
                }

                if (handler != null) {
                    handler.setMessage(incomingResult[2]);
                    handler.setClientIpAddress(clientIp);
                    handler.setClientPort(clientPort);
                    executor.execute(new Thread(handler));
                }

            } catch (Exception e) {
                logger.error("Error occurred while UDP listening", e);
            }
        }
    }

    private void startGossiping() {
        TimerTask timerTask = new GossipingTimerTask();
        timer.scheduleAtFixedRate(timerTask, SystemSettings.getUDPGossipDelay(), SystemSettings.getUDPGossipPeriod());
    }

    private void startHeartbeat() {
        TimerTask timerTask = new HeartbeatTimerTask();
        timer.scheduleAtFixedRate(timerTask, SystemSettings.getUDPHeartbeatDelay(), SystemSettings.getUDPHeartbeatPeriod());
    }

    @Override
    public void stopServer() {

        timer.cancel();

        if (listening) {
            listening = false;
        }
    }

    @Override
    public void run() {
        startServer();
    }
}
