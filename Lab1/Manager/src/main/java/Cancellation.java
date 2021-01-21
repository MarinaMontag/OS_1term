import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class Cancellation extends Thread{
    @Override
    public void run() {
        System.out.println("If you want to stop application press 0");
        Scanner scanner=new Scanner(System.in);
        if(scanner.nextInt()==0) {
            try {
                Manager.display();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
