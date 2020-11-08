import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Gfunction {
    public static String hostName = "localhost";
    public static int portNumber = 4444;

    public static void main(String[] args) throws InterruptedException {
        try {
            Socket pipe = new Socket(hostName, portNumber);
            BufferedReader in = new BufferedReader(new InputStreamReader(pipe.getInputStream()));
            PrintWriter out = new PrintWriter(pipe.getOutputStream(), true);
            Integer x =Integer.parseInt(in.readLine());
            x=function(x);
            out.println(x);
        } catch (UnknownHostException e) {
            System.err.println("Unknown host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName + ".." + e.toString());
            System.exit(1);
        }
    }

    public static Integer function(Integer x) throws InterruptedException {
        Thread.sleep(500);
        x+=6;
        return x;
    }
}
