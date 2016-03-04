import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

public class Main {
    public static void main(String[] args) throws IOException {
        new UDPServer().start();
    }
}

class UDPServer extends Thread {
    private static final int PACKAGE_QUANT = 1000;
    private static final int BUFFER_SIZE = 1000;
    private static final int PORT = 5555;
    private static final int TIMEOUT = 5000;

    private static final int START_MSG = PACKAGE_QUANT + 1;
    private static final int END_MSG = START_MSG + 1;

    protected byte[] buffer;
    protected DatagramSocket socket;
    protected DatagramPacket packet;

    public UDPServer() throws SocketException{
        super("UDP SERVER THREAD");
        buffer = new byte[BUFFER_SIZE];
        packet = new DatagramPacket(buffer, buffer.length);
        socket = new DatagramSocket(PORT);
    }

    protected void sendInfoPacket(int value, InetAddress address) throws IOException {
        buffer = ByteBuffer.allocate(BUFFER_SIZE).putInt(value).array();
        packet = new DatagramPacket(buffer, buffer.length, address, PORT);
        try {
            Thread.sleep(TIMEOUT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        socket.send(packet);
        System.out.println("Successfully sent result to " + address.getHostAddress() + "\n===================");
    }

    public void run() {
        System.out.println("Waiting for request...\n");
        int numberOfReceivedPackages = 0;

        while (true) {
            try {
                socket.receive(packet);
                InetAddress address = packet.getAddress();
                int packageInfo = ByteBuffer.wrap(packet.getData()).getInt();

                if (packageInfo == START_MSG) {
                    System.out.println("Test started with " + address.getHostAddress() + "\n===================");
                    numberOfReceivedPackages = 0;
                } else if (packageInfo == END_MSG) {
                    System.out.println("\nTest finished\n===================");
                    sendInfoPacket(numberOfReceivedPackages, address);
                } else {
                    numberOfReceivedPackages++;
                    System.out.println("Got packet number " + Integer.toString(packageInfo));
                }
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }
}
