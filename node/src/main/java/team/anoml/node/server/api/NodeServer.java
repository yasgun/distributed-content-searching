package team.anoml.node.server.api;

public interface NodeServer extends Runnable {

    void startServer();

    void stopServer();
}
