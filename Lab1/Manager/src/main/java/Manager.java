import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

public class Manager {
    private static ServerSocket server;
    private static int x;
    private static boolean gotNull;
    private static int result;
    public static ExecutorService executor;
    private static String FDir;
    private static String GDir;
    public static List<Future<Integer>> funcResults;

    Manager() throws IOException {
        setX();
        setProjectDir();
        setServer();
        setExecutor();
        setProcesses();
        funcResults = new ArrayList<>(2);
    }

    public static int start() throws IOException, InterruptedException, ExecutionException {
        Cancellation cancellation = new Cancellation();
        cancellation.start();
        startFunctions();
        processingResults();
        return result;
    }

    public static int getX() {
        return x;
    }

    private void setX() {
        System.out.print("Enter x: ");
        Scanner scanner = new Scanner(System.in);
        x = scanner.nextInt();
    }

    private static void setServer() throws IOException {
        server = new ServerSocket(62370);
    }

    private static void setProjectDir() {
        String projectDir = System.getProperty("user.dir");
        projectDir = projectDir.substring(0, projectDir.length() - 7);
        FDir = projectDir + "Ffunc\\my-jar\\ffunc-1.0-SNAPSHOT.jar";
        GDir = projectDir + "Gfunc\\my-jar\\gfunc-1.0-SNAPSHOT.jar";
    }

    private static void setExecutor() {
        executor = Executors.newFixedThreadPool(2);
    }

    private static void setProcesses() throws IOException {
        Process fProcess = Runtime.getRuntime().exec("java -jar " + FDir);
        Process gProcess = Runtime.getRuntime().exec("java -jar " + GDir);
    }

    private static void startFunctions() throws IOException {
        for (int i = 0; i < 2; i++) {
            Socket socket = server.accept();
            funcResults.add(executor.submit(new GetFG(socket)));
        }
    }

    private static boolean handleZero() throws ExecutionException, InterruptedException {
        if ((funcResults.get(0).isDone() && funcResults.get(0).get() == 0 )||
                (funcResults.get(1).isDone() && funcResults.get(1).get() == 0 ))
            return true;
        else return false;
    }

    private static void processingResults() throws ExecutionException, InterruptedException, IOException {

        while(!funcResults.get(0).isDone()||!funcResults.get(1).isDone()) {
            if (handleZero()){
                gotNull = true;
                System.out.println("Got 0 firstly");
                break;
            }
            executor.awaitTermination(100, TimeUnit.MILLISECONDS);
            if (funcResults.get(0).isDone() && funcResults.get(1).isDone())
                break;
        }

        if (!gotNull)
            display();
        executor.shutdown();
        server.close();
    }

    public static void display() throws ExecutionException, InterruptedException, IOException {
        boolean computedF = displayResult(0, "f");
        boolean computedG = displayResult(1, "g");
        if (computedF && computedG) {
            result = funcResults.get(0).get() * funcResults.get(1).get();
        } else if (!gotNull) {
            System.out.println("Result is undefined");
            executor.shutdown();
            server.close();
            System.exit(0);
        }
    }

    private static boolean displayResult(int i, String func) throws ExecutionException, InterruptedException {
        if (funcResults.get(i).isDone()) {
            System.out.println(func + ": " + funcResults.get(i).get());
            if (funcResults.get(i).get() == 0)
                gotNull = true;
            return true;
        } else {
            System.out.println("Function " + func + " hasn't been computed yet");
            return false;
        }
    }
}


