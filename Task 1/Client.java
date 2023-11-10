import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.net.Socket;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public class Client {
    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 2250;
        System.out.format("Connecting to the server at %s:%d\n", hostname, port);


        System.setProperty("javax.net.ssl.trustStore", "chrisTruststore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "c3412618");

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, null, new java.security.SecureRandom());
            SSLSocketFactory factory = (SSLSocketFactory) sc.getSocketFactory();
            SSLSocket s = (SSLSocket) factory.createSocket(hostname, port);
            s.setTcpNoDelay(true);
            s.startHandshake();

            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            DataInputStream in = new DataInputStream(s.getInputStream());


            out.write("Be careful. There's no telling what tricks they have planned.".getBytes());


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte data[] = new byte[1024];
            int bytesRead = in.read(data);
            if (bytesRead != -1) {
                baos.write(data, 0, bytesRead);
                System.out.format("Client <- Server: %s\n", new String(data, 0, bytesRead));
            } else {
                System.out.println("No data received from server.");
            }
            
            in.close();
            out.close();
            s.close();
        } catch (Exception e) {
            System.err.println("An error occurred during the communication process:");
            e.printStackTrace();
        }
    }
}
