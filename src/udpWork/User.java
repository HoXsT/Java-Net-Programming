package udpWork;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Objects;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private InetAddress address;
    private int port;

    public User(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public InetAddress getAddress() { return address; }
    public void setAddress(InetAddress address) { this.address = address; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    @Override
    public String toString() {
        return "Host address: " + address.getHostAddress() + " Port: " + port;
    }

    // Перевизначаємо equals, щоб метод contains у списку працював коректно
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return port == user.port && Objects.equals(address, user.address);
    }
}