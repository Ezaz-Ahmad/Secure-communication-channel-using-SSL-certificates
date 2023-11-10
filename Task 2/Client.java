import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.math.BigInteger;
import java.util.Scanner;

public class Client {

    private static final BigInteger p = new BigInteger("26843324785128901513417116316925732553404222774635958566889827638875897488034057311483937952106679349969263344635559824014361082003084666378955119293181493589760568433914965225590417108953050419989070134227723419628577824473314118032274233153025783052801961622734421126267902679329579374349194358077784194270469031354853870366276839801846838189441295222823491922209220269104275973623369762533388107818473597741182444436249629602212031042849817322077272068306803314656118091636825993023615750701053919691509027454922631247161069560588603126188473677951773780331443255125571239837020813185809987900054081998193766130783");
    private static final BigInteger q = new BigInteger("32099542856813384173342268587175419121125318109951071180029235203156006701016705813462636832134766352737899281347488332364159592487062598017241272279463040395670754495791226091538062314217292791624356010545068711473043928742942060483804250055846312919327816890171226888821151760525350852715106071022552490843785007558035198126584000622486207485879314666757048602691366393044672867980260378923380281225930172162175317199949254955750894555741757232827630069316926899631654339461126500283261256951068603749713746041360045748715893136550908214042888503796074493300905351401372023601247328681157412280880665512803624307439");
    private static final BigInteger n = p.multiply(q);
    private static final BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
    private static final BigInteger e = new BigInteger("3");
    private static final BigInteger d = e.modInverse(phi);

    private static String rsaEncrypt(String message) {
        return (new BigInteger(message.getBytes())).modPow(e, n).toString();
    }

    private static String rsaDecrypt(String encrypted) {
        return new String((new BigInteger(encrypted)).modPow(d, n).toByteArray());
    }

    public static byte[] sendReceive(String hostname, int port, byte[] message) {
        try (
                Socket s = new Socket(hostname, port);
                DataOutputStream out = new DataOutputStream(s.getOutputStream());
                DataInputStream in = new DataInputStream(s.getInputStream())
        ) {
            out.write(message);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte buffer[] = new byte[1024];
            baos.write(buffer, 0, in.read(buffer));
            return buffer;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 22500;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the SENG2250 password manager client, you have the following options:");

        while (true) {
            System.out.println("- store <website> <password>");
            System.out.println("- get <website>");
            System.out.println("- end");
            System.out.print(">>> ");

            String[] input = scanner.nextLine().split(" ", 3);

            if (input.length == 0) {
                System.out.println("Invalid input. Please try again.");
                continue;
            }

            switch (input[0]) {
                case "store":
                    if (input.length < 3) {
                        System.out.println("Invalid input. Please provide a website and a password.");
                        break;
                    }
                    String encryptedPassword = rsaEncrypt(input[2]);
                    System.out.println(
                            new String(sendReceive(hostname, port, ("store " + input[1] + " " + encryptedPassword).getBytes()))
                    );
                    break;
                case "get":
                    if (input.length < 2) {
                        System.out.println("Invalid input. Please provide a website.");
                        break;
                    }
                    String response = new String(sendReceive(hostname, port, ("get " + input[1]).getBytes())).trim();
                    String decryptedPassword = rsaDecrypt(response);
                    System.out.println("Your password for " + input[1] + " is " + decryptedPassword);
                    break;
                case "end":
                    System.out.println("bye.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please choose again.");
                    break;
            }
        }
    }
}
