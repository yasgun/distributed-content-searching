package team.anoml.node.impl;

import team.anoml.node.api.NodeServer;
import team.anoml.node.util.SystemSettings;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPServer implements NodeServer {

    private static Logger logger = Logger.getLogger(UDPServer.class.getName());

    private ServerSocket serverSocket;

    private boolean listening = false;

    @Override
    public void startServer() {
        if (listening) {
            return;
        }

        try {
            listen();
        } catch (Exception e) {
            logger.log(Level.WARNING, "TCP listening failed");
        }
    }

    private void listen() throws IOException {
        listening = true;

        serverSocket = new ServerSocket(SystemSettings.getTCPPort());

        while (listening) {
            try {
                Socket connectionSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                DataOutputStream out = new DataOutputStream(connectionSocket.getOutputStream());

                String request = in.readLine();

                String[] incomingResult = request.split(" ", 3);
                String command = incomingResult[1];

                switch (command) {
                    case SystemSettings.DOWN_MSG:
                        break;
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error occurred while TCP listening", e);
            }
        }
    }

    @Override
    public void stopServer() {
        if (listening) {
            listening = false;
        }
    }

    @Override
    public void run() {

    }
}
