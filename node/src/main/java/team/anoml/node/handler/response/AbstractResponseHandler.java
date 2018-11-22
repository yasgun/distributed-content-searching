package team.anoml.node.handler.response;

import team.anoml.node.handler.AbstractHandler;

abstract class AbstractResponseHandler extends AbstractHandler {

    @Override
    public void run() {
        handleResponse();
    }

    protected abstract void handleResponse();

}
