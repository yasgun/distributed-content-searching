package team.anoml.node.impl;

import team.anoml.node.api.NodeServer;
import team.anoml.node.handler.AbstractHandler;
import team.anoml.node.handler.request.*;
import team.anoml.node.handler.response.*;
import team.anoml.node.task.GossipingTimerTask;
import team.anoml.node.task.HeartbeatTimerTask;
import team.anoml.node.util.SystemSettings;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPServer implements NodeServer {

    private static Logger logger = Logger.getLogger(UDPServer.class.getName());

    private Timer timer = new Timer(true);
    private Executor executor = Executors.newSingleThreadExecutor();

    private static boolean listening = false;
    private final int port = SystemSettings.getUDPPort();

    @Override
    public void startServer() {
        if (listening) {
            return;
        }

        try {
            Runtime.getRuntime().addShutdownHook(new Thread(this::stopServer));
            listen();
        } catch (Exception e) {
            logger.log(Level.WARNING, "UDP listening failed");
        }
    }

    private void listen() throws SocketException {
        listening = true;
        startGossiping();
        startHeartbeat();

        DatagramSocket datagramSocket = new DatagramSocket(port);

        while (listening) {
            try {
                byte[] buffer = new byte[65536];
                DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(incoming);

                byte[] data = incoming.getData();
                String request = new String(data, 0, incoming.getLength());

                String[] incomingResult = request.split(" ", 3);
                String command = incomingResult[1];

                switch (command) {
                    case SystemSettings.JOIN_MSG:
                        AbstractHandler joinRequestHandler = new JoinRequestHandler();
                        joinRequestHandler.setMessage(incomingResult[2]);
                        executor.execute(new Thread(joinRequestHandler));
                        break;
                    case SystemSettings.LEAVE_MSG:
                        AbstractHandler leaveRequestHandler = new LeaveRequestHandler();
                        leaveRequestHandler.setMessage(incomingResult[2]);
                        executor.execute(new Thread(leaveRequestHandler));
                        break;
                    case SystemSettings.NBR_MSG:
                        AbstractHandler neighbourRequestHandler = new NeighbourRequestHandler();
                        neighbourRequestHandler.setMessage(incomingResult[2]);
                        executor.execute(new Thread(neighbourRequestHandler));
                        break;
                    case SystemSettings.HB_MSG:
                        AbstractHandler heartbeatRequestHandler = new HeartbeatRequestHandler();
                        heartbeatRequestHandler.setMessage(incomingResult[2]);
                        executor.execute(new Thread(heartbeatRequestHandler));
                        break;
                    case SystemSettings.SEARCH_MSG:
                        AbstractHandler searchRequestHandler = new SearchRequestHandler();
                        searchRequestHandler.setMessage(incomingResult[2]);
                        executor.execute(new Thread(searchRequestHandler));
                        break;
                    case SystemSettings.ERROR_MSG:
                        AbstractHandler errorResponseHandler = new ErrorResponseHandler();
                        errorResponseHandler.setMessage(incomingResult[2]);
                        executor.execute(new Thread(errorResponseHandler));
                        break;
                    case SystemSettings.JOINOK_MSG:
                        AbstractHandler joinResponseHandler = new JoinResponseHandler();
                        joinResponseHandler.setMessage(incomingResult[2]);
                        executor.execute(new Thread(joinResponseHandler));
                        break;
                    case SystemSettings.LEAVEOK_MSG:
                        AbstractHandler leaveResponseHandler = new LeaveResponseHandler();
                        leaveResponseHandler.setMessage(incomingResult[2]);
                        executor.execute(new Thread(leaveResponseHandler));
                        break;
                    case SystemSettings.NBROK_MSG:
                        AbstractHandler neighbourResponseHandler = new NeighbourResponseHandler();
                        neighbourResponseHandler.setMessage(incomingResult[2]);
                        executor.execute(new Thread(neighbourResponseHandler));
                        break;
                    case SystemSettings.HBOK_MSG:
                        AbstractHandler heartbeatResponseHandler = new HeartbeatResponseHandler();
                        heartbeatResponseHandler.setMessage(incomingResult[2]);
                        executor.execute(new Thread(heartbeatResponseHandler));
                        break;
                    case SystemSettings.SEARCHOK_MSG:
                        AbstractHandler searchResponseHandler = new SearchResponseHandler();
                        searchResponseHandler.setMessage(incomingResult[2]);
                        executor.execute(new Thread(searchResponseHandler));
                        break;
                }
            } catch (IOException e) {
                logger.log(Level.WARNING, "Error occurred while listening", e);
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
            try {
                Thread.sleep(SystemSettings.getUDPShutdownGracePeriod());
            } catch (InterruptedException ignored) {
            }
        }
    }

    @Override
    public void run() {
        startServer();
    }

    public static String getHealthStatus() {
        if (listening) {
            return "GREEN";
        } else {
            return "RED";
        }
    }
}
