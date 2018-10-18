package team.anoml.distributed.systems.server;

import team.anoml.distributed.systems.Constants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A class to manage the Bootstrap Server Implementation
 * Taken from CS4262 course CSE Provided by Dilum Bandara and Anura P. Jayasumana
 */
public class BootstrapServer {

    private int port;
    private boolean started = false;
    private ExecutorService executorService;

    public BootstrapServer(int port) {
        this.port = port;
    }

    public void start() {
        if (started) {
            throw new IllegalStateException("Server already running");
        }

        started = true;
        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            try {
                doProcessing();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    private void doProcessing() {
        DatagramSocket sock = null;
        String s;
        List<Neighbour> nodes = new ArrayList<>();

        try {
            sock = new DatagramSocket(port);

            echo("Bootstrap Server created at 55555. Waiting for incoming data...");

            while (started) {
                byte[] buffer = new byte[65536];
                DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
                sock.receive(incoming);

                byte[] data = incoming.getData();
                s = new String(data, 0, incoming.getLength());

                //echo the details of incoming data - client ip : client port - client message
                echo(incoming.getAddress().getHostAddress() + " : " + incoming.getPort() + " - " + s);

                StringTokenizer st = new StringTokenizer(s, " ");

                String length = st.nextToken();
                String command = st.nextToken();

                if (command.equals("REG")) {
                    String reply = "REGOK ";

                    String ip = st.nextToken();
                    int port = Integer.parseInt(st.nextToken());
                    String username = st.nextToken();
                    if (nodes.size() == 0) {
                        reply += "0";
                        nodes.add(new Neighbour(ip, port, username));
                    } else {
                        boolean isOkay = true;
                        for (int i = 0; i < nodes.size(); i++) {
                            if (nodes.get(i).getPort() == port) {
                                if (nodes.get(i).getUsername().equals(username)) {
                                    reply += "9998";
                                } else {
                                    reply += "9997";
                                }
                                isOkay = false;
                            }
                        }
                        if (isOkay) {
                            if (nodes.size() == 1) {
                                reply += "1 " + nodes.get(0).getIp() + " " + nodes.get(0).getPort();
                            } else if (nodes.size() == 2) {
                                reply += "2 " + nodes.get(0).getIp() + " " + nodes.get(0).getPort() + " " +
                                        nodes.get(1).getIp() + " " + nodes.get(1).getPort();
                            } else {
                                Random r = new Random();
                                int Low = 0;
                                int High = nodes.size();
                                int random_1 = r.nextInt(High - Low) + Low;
                                int random_2 = r.nextInt(High - Low) + Low;
                                while (random_1 == random_2) {
                                    random_2 = r.nextInt(High - Low) + Low;
                                }
                                echo(random_1 + " " + random_2);
                                reply += "2 " + nodes.get(random_1).getIp() + " " + nodes.get(random_1).getPort() +
                                        " " + nodes.get(random_2).getIp() + " " + nodes.get(random_2).getPort();
                            }
                            nodes.add(new Neighbour(ip, port, username));
                        }
                    }

                    reply = String.format("%04d", reply.length() + 5) + " " + reply;

                    DatagramPacket dpReply = new DatagramPacket(reply.getBytes(), reply.getBytes().length,
                            incoming.getAddress(), incoming.getPort());
                    sock.send(dpReply);
                } else if (command.equals("UNREG")) {
                    String ip = st.nextToken();
                    int port = Integer.parseInt(st.nextToken());
                    String username = st.nextToken();
                    for (int i = 0; i < nodes.size(); i++) {
                        if (nodes.get(i).getPort() == port) {
                            nodes.remove(i);
                            String reply = "0012 UNROK 0";
                            DatagramPacket dpReply = new DatagramPacket(reply.getBytes(), reply.getBytes().length,
                                    incoming.getAddress(), incoming.getPort());
                            sock.send(dpReply);
                        }
                    }
                } else if (command.equals("ECHO")) {
                    for (int i = 0; i < nodes.size(); i++) {
                        echo(nodes.get(i).getIp() + " " + nodes.get(i).getPort() + " " +
                                nodes.get(i).getUsername());
                    }
                    String reply = "0012 ECHOK 0";
                    DatagramPacket dpReply = new DatagramPacket(reply.getBytes(), reply.getBytes().length,
                            incoming.getAddress(), incoming.getPort());
                    sock.send(dpReply);
                }
            }
        } catch (IOException e) {
            System.err.println("IOException " + e);
        } finally {
            if (sock != null) {
                sock.close();
            }
        }
    }

    public void stop() {
        if (started) {
            started = false;
            executorService.shutdownNow();
        }
    }

    /**
     * simple function to echo data to terminal
     */
    private void echo(String msg) {
        System.out.println(msg);
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isStarted() {
        return started;
    }

    public static void main(String[] args) {
        BootstrapServer server = new BootstrapServer(Constants.BOOTSTRAP_PORT);
        server.start();

        while (server.isStarted()) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {
            }
        }

        server.stop();
    }
}
