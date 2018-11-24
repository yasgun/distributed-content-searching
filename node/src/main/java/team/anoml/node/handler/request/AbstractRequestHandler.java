package team.anoml.node.handler.request;

import team.anoml.node.handler.AbstractHandler;

abstract class AbstractRequestHandler extends AbstractHandler {

    @Override
    public void run() {
        handleRequest();
    }

    protected abstract void handleRequest();
}
