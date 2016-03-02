import java.io.*;
import java.math.BigInteger;
import java.net.*;

public class Main {
    public static void main(String[] args) throws IOException {
        new UDPServer().start();
    }
}

class UDPServer extends Thread {
    protected byte[] buffer;
    protected DatagramSocket socket;
    protected DatagramPacket packet;
    protected int numberOfPackages;

    private static final int BUFFER_SIZE = 1000;
    private static final int PORT = 5555;

    public UDPServer() {
        super("UDP SERVER THREAD");

        buffer = new byte[BUFFER_SIZE];
        packet = new DatagramPacket(buffer, buffer.length);
        numberOfPackages = 0;

        try {
            socket = new DatagramSocket(PORT);
            socket.setBroadcast(true);
        } catch (SocketException e) {
            System.err.println(e);
        }
    }

    protected void sendInfoPacket(InetAddress address) {
        buffer = BigInteger.valueOf(numberOfPackages).toByteArray();
        packet = new DatagramPacket(buffer, buffer.length, address, PORT);

        try {
            socket.send(packet);
            System.out.println("Successful sent to " + packet.getAddress().getHostAddress() + "\n===================");
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    protected boolean isActive(){
        return (numberOfPackages != 100); //for testing
    }

    public void run() {
        System.out.println("Waiting for request...\n");
        while (true) {
            try {
                socket.receive(packet);
                String message = packet.toString();
                InetAddress address = packet.getAddress();

                if (message.equals("start_test")) {
                    System.out.println("Starting the test with " + address.getHostAddress()+"\n===================");
                    while (isActive()) {
                        try {
                            socket.receive(packet);
                            numberOfPackages++;
                        } catch (IOException e) {
                            System.err.println("Packet has missed");
                        }
                    }
                }

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    System.err.println(e);
                }

                sendInfoPacket(address);
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }
}
