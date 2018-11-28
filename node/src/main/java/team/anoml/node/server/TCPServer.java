package team.anoml.node.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.anoml.node.server.api.NodeServer;
import team.anoml.node.core.FileTable;
import team.anoml.node.util.SystemSettings;

import java.io.*;

import static spark.Spark.*;

public class TCPServer implements NodeServer {

    private static Logger logger = LogManager.getLogger(TCPServer.class.getName());

    @Override
    public void startServer() {
        get("/download/:name", (request, response) -> {

            try (OutputStream outputStream = response.raw().getOutputStream()) {

                File file = new File(SystemSettings.getFilePath() + "/" + request.params("name"));
                byte[] bytes = new byte[(int) file.length()];
                FileInputStream fileInputStream = new FileInputStream(file);

                response.header("Content-Disposition", "attachment; filename=" + request.params("name"));
                response.header("Content-SHA", FileTable.getFileTable().getEntryByFileName(request.params("name")).getSHA());

                int count;
                while ((count = fileInputStream.read(bytes)) > 0) {
                    outputStream.write(bytes, 0, count);
                }

            } catch (Exception e) {
                halt(500, "Error occurred while generating response");
            }

            return response.raw();
        });

        init();
    }

    @Override
    public void stopServer() {
        logger.info("Stopping TCP server...");
        stop();
        logger.info("TCP server stopped!");
    }

    @Override
    public void run() {
        logger.info("Starting TCP server...");
        port(SystemSettings.getTCPPort());
        startServer();
        logger.info("TCP server started");
    }
}
