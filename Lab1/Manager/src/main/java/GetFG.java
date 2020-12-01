import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class GetFG implements Callable<Integer> {
    private final Socket socket;
    private final CountDownLatch countDownLatch;
    public GetFG(Socket socket,CountDownLatch countDownLatch) {
        this.socket =socket;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public Integer call() throws IOException {
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out.println(Manager.getX());
        int res=Integer.parseInt(in.readLine());
        socket.close();
        if(countDownLatch.getCount()>0)
            countDownLatch.countDown();
        return res;
    }
}
