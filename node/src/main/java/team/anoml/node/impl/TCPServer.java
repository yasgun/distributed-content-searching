package team.anoml.node.impl;

import team.anoml.node.api.NodeServer;
import team.anoml.node.core.FileTable;
import team.anoml.node.util.SystemSettings;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static spark.Spark.*;

public class TCPServer implements NodeServer {

    private static Logger logger = Logger.getLogger(TCPServer.class.getName());

    @Override
    public void startServer() {
        get("/download/:name", (request, response) -> {

            try (OutputStream outputStream = response.raw().getOutputStream()) {

                File file = new File(SystemSettings.getFilePath() + "/" + request.params("name"));
                byte[] bytes = new byte[(int) file.length()];
                FileInputStream fileInputStream = new FileInputStream(file);

                response.header("Content-Disposition", "attachment; filename=" + request.params("name"));
                response.header("Content-MD5", FileTable.getFileTable().getEntryByFileName(request.params("name")).getMd5());

                int count;
                while ((count = fileInputStream.read(bytes)) > 0) {
                    outputStream.write(bytes, 0, count);
                }

            } catch (Exception e) {
                halt(500, "Error occurred while generating respose");
            }

            return response.raw();
        });

        init();
    }

    @Override
    public void stopServer() {
        logger.log(Level.INFO, "Stopping TCP server...");
        stop();
        logger.log(Level.INFO, "TCP server stopped!");
    }

    @Override
    public void run() {
        logger.log(Level.INFO, "Starting TCP server...");
        port(SystemSettings.getTCPPort());
        startServer();
        logger.log(Level.INFO, "TCP server started");
    }

}
