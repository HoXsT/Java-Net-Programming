package udpWork;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class UDPClient {
    private ActiveUsers userList = null;
    private DatagramSocket socket = null;
    private DatagramPacket packet = null;
    private int serverPort = -1;
    private InetAddress serverAddress = null;

    public UDPClient(String address, int port) {
        userList = new ActiveUsers();
        serverPort = port;
        try {
            serverAddress = InetAddress.getByName(address);
            socket = new DatagramSocket();
            socket.setSoTimeout(1000); // Встановлюємо таймаут очікування
        } catch (UnknownHostException | SocketException e) {
            System.out.println("Error: " + e);
        }
    }

    private void clear(byte[] arr) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] = 0;
        }
    }

    public void work(int bufferSize) throws ClassNotFoundException {
        byte[] buffer = new byte[bufferSize];
        try {
            // Відправляємо пустий запит на реєстрацію
            packet = new DatagramPacket(buffer, buffer.length, serverAddress, serverPort);
            socket.send(packet);
            System.out.println("Sending request");

            while (true) {
                packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                // Якщо прийшов пакет з нульовою довжиною буфера ("end"), виходимо з циклу[cite: 1]
                if (packet.getLength() == 0 || new String(packet.getData(), 0, packet.getLength()).equals("end")) {
                    break;
                }

                ObjectInputStream in = new ObjectInputStream(
                        new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));
                User usr = (User) in.readObject();
                userList.add(usr);
                clear(buffer);
            }
        } catch (SocketTimeoutException e) {
            System.out.println("Server is unreachable: " + e);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        } finally {
            if (socket != null) {
                socket.close();
            }
        }

        System.out.println("Registered users: " + userList.size());
        System.out.println(userList);
    }

    public static void main(String[] args) throws ClassNotFoundException {
        (new UDPClient("127.0.0.1", 1501)).work(256);
    }
}