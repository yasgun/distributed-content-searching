package team.anoml.node.api;

import team.anoml.node.Node;

public interface NodeServer {

    void start(Node node);

    void stop();

    void listen();
}
