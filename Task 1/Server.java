import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.net.ServerSocket;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class Server {

    public static void main(String[] args) {
        int port = 2250;
        System.out.format("Listening for a client on port %d\n", port);
        System.setProperty("javax.net.ssl.keyStore", "jillServer.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "c3412618");

        try {

            SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(port);
            SSLSocket socket = (SSLSocket) serverSocket.accept();
            socket.setTcpNoDelay(true);
            socket.startHandshake();

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            System.out.format(
                    "Connected by %s:%d\n",
                    socket.getInetAddress().toString(),
                    socket.getPort()
            );
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte data[] = new byte[1024];
            int bytesRead = in.read(data);
            baos.write(data, 0, bytesRead);
            System.out.format("Client -> Server: %s\n", baos.toString());
            out.write(" I'll do the best I can. Let's get moving.".getBytes());


            in.close();
            out.close();
            socket.close();
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
