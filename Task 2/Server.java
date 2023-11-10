import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.HashMap;

public class Server {

    private static HashMap<String, String> passwordStore = new HashMap<>();

    public static boolean handleMessage(Socket conn, byte[] data) {
        try (
                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                DataInputStream in = new DataInputStream(conn.getInputStream())
        ) {
            String message = new String(data).trim();

            if (message.equals("end")) {
                out.write("end okay".getBytes());
                return true;
            } else if (message.startsWith("store ")) {
                String[] parts = message.split(" ", 3);
                if (parts.length == 3) {
                    passwordStore.put(parts[1], parts[2]);
                    out.write("Password successfully stored.".getBytes());
                    return false;
                } else {
                    out.write("Error storing password.".getBytes());
                    return false;
                }
            } else if (message.startsWith("get ")) {
                String website = message.split(" ")[1];
                String encryptedPassword = passwordStore.get(website); 
                if (encryptedPassword != null) {
                    out.write(encryptedPassword.getBytes());
                } else {
                    out.write("Password not found.".getBytes());
                }
                return false;
            }

            System.out.println("Client -> Server: " + message);
            out.write(data);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 22500;
        boolean end = false;
        System.out.format("Listening for a client on port %d\n", port);

        try (
                ServerSocket serverSocket = new ServerSocket(port)
        ) {
            do {
                Socket socket = serverSocket.accept();
                System.out.format(
                        "Connected by %s:%d\n",
                        socket.getInetAddress().toString(),
                        socket.getPort()
                );
                DataInputStream in = new DataInputStream(socket.getInputStream());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte data[] = new byte[1024];
                baos.write(data, 0, in.read(data));
                end = handleMessage(socket, data);
                in.close();
                socket.close();
            } while (!end);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
