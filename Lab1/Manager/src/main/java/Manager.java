import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

import static com.sun.java.accessibility.util.AWTEventMonitor.addKeyListener;

public class Manager {
    private static ServerSocket server;
    private static int x;
    private static int result;;
    public static ExecutorService executor;
    private static String FDir;
    private static String GDir;
    public static List<Future<Integer>> funcResults;
    private static Cancellation cancellation;

    Manager() throws IOException {
        setX();
        setProjectDir();
        setServer();
        setExecutor();
        setProcesses();
        funcResults=new ArrayList<>();
    }

    public static int getX() {
        return x;
    }

    private void setX() {
        System.out.print("Enter x: ");
        Scanner scanner=new Scanner(System.in);
        x=scanner.nextInt();
    }

    public static void start() throws IOException, InterruptedException, ExecutionException {
        Cancellation cancellation=new Cancellation();
        cancellation.start();
        CountDownLatch countDownLatch= new CountDownLatch(1);
        startFunctions(countDownLatch);
        countDownLatch.await();
        handleZero();
        processingResults();
        shutdown();
        closeServer();
        System.exit(0);
    }

    private static void shutdown(){
        executor.shutdown();
    }

    private static void setServer() throws IOException {
        server =new ServerSocket(4444);
    }

    private static void setProjectDir()
    {
        String projectDir = System.getProperty("user.dir");
        projectDir = projectDir.substring(0, projectDir.length()-7);
        FDir= projectDir +"Ffunc\\my-jar\\ffunc-1.0-SNAPSHOT.jar";
        GDir= projectDir +"Gfunc\\my-jar\\gfunc-1.0-SNAPSHOT.jar";
    }

    private static void setExecutor() {
        executor =  Executors.newFixedThreadPool(2);
    }

    private static void setProcesses() throws IOException {
        Process fProcess = Runtime.getRuntime().exec("java -jar " + FDir);
        Process gProcess = Runtime.getRuntime().exec("java -jar " + GDir);
    }

    private static void startFunctions(CountDownLatch countDownLatch) throws IOException {
        for(int i=0;i<2;i++){
            Socket socket= server.accept();
            funcResults.add(executor.submit(new GetFG(socket,countDownLatch)));
        }
    }

    private static void handleZero() throws ExecutionException, InterruptedException {
        if(funcResults.get(0).get()==0){
            System.out.println("Got Null");
            System.out.println("Result: " + result);
            System.exit(0);
        }
    }

    private static void processingResults() throws ExecutionException, InterruptedException {
        System.out.println("Got results");
        System.out.println("f: "+funcResults.get(0).get());
        System.out.println("g: "+funcResults.get(1).get());
        result = funcResults.get(0).get() * funcResults.get(1).get();
        System.out.println("Result: " + result);
    }

    private static void closeServer() throws IOException {
        server.close();
    }

}
