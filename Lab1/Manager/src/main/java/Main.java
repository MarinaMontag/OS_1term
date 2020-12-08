import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        Manager manager=new Manager();
       int result= manager.start();
        System.out.println("Result: " + result);
        System.exit(0);
    }

}
