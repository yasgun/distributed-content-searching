package team.anoml.node.impl;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import team.anoml.node.api.NodeServer;
import team.anoml.node.util.SystemSettings;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
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

    private void listen() {
        listening = true;

//        serverSocket = new ServerSocket(SystemSettings.getTCPPort());
//
//        while (listening) {
//            try {
//                Socket connectionSocket = serverSocket.accept();
//                BufferedReader in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
//                DataOutputStream out = new DataOutputStream(connectionSocket.getOutputStream());
//
//                String request = in.readLine();
//
//                String[] incomingResult = request.split(" ", 3);
//                String command = incomingResult[1];
//
//                switch (command) {
//                    case SystemSettings.DOWN_MSG:
//                        OutputStream fileOutputStream = new FileOutputStream("filePath");
//                }
//            } catch (Exception e) {
//                logger.log(Level.WARNING, "Error occurred while TCP listening", e);
//            }
//        }

        HttpServer server;
        try {
            server = HttpServer.create(new InetSocketAddress(SystemSettings.getTCPPort()), 0);
            server.createContext("/", new FileHandler());
            server.setExecutor(null); // creates a default executor
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    static class FileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            InputStream inputStream = t.getRequestBody();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String request = bufferedReader.readLine();

            String[] incomingResult = request.split(" ", 3);
            String command = incomingResult[1];

            switch (command) {
                case SystemSettings.DOWN_MSG:
                    File file = new File(SystemSettings.getFilePath() + "/" + incomingResult[2]);
                    byte[] bytes = new byte[(int) file.length()];
                    FileInputStream fileInputStream = new FileInputStream(file);

                    t.sendResponseHeaders(200, file.length());
                    OutputStream os = t.getResponseBody();

                    int count;
                    while ((count = fileInputStream.read(bytes)) > 0) {
                        os.write(bytes, 0, count);
                    }
                    os.close();
                    fileInputStream.close();
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
