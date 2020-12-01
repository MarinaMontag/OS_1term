import java.util.Scanner;

public class Cancellation extends Thread{
    @Override
    public void run() {
        System.out.println("If you want to stop application press 0");
        Scanner scanner=new Scanner(System.in);
        if(scanner.nextInt()==0)
           System.exit(0);
    }
}
