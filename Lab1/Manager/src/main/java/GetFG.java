import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Callable;

public class GetFG implements Callable<Integer> {
    private final Socket socket;
   public GetFG(Socket socket) {
        this.socket =socket;
    }

    @Override
    public Integer call() throws IOException {
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out.println(Manager.getX());
        int res=Integer.parseInt(in.readLine());
        socket.close();
        return res;
    }
}
