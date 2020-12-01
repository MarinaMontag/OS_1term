import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import spos.lab1.demo.IntOps;

public class Ffunction {
    public static String hostName = "localhost";
    public static int portNumber = 4444;

    public static void main(String[] args) throws InterruptedException {
        try{
            Socket socket=new Socket(hostName,portNumber);
            BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out=new PrintWriter(socket.getOutputStream(),true);
            Integer x =Integer.parseInt(in.readLine());
            x=function(x);
            out.println(x);
            in.close();
            out.close();
            socket.close();
        } catch (UnknownHostException e) {
            System.err.println("Unknown host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName + ".." + e.toString());
            System.exit(1);
        }
    }
    public static Integer function(Integer x) throws InterruptedException {
        return new IntOps().funcF(x);
    }
}
