import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

public class Manager {
    private static ServerSocket server;
    public static int portNumber = 4444;
    private static int x;
    private static int result;
    private static ExecutorService executor;
    private static Process Fprocess;
    private static Process Gprocess;
    private static  String projectDir;
    private static String[] jarfiles;
    private static List<Future<Integer>> funcResults;
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        try {
            setProjectDir();

            setX();

            server = new ServerSocket(portNumber);
            executor = Executors.newFixedThreadPool(2);
            jarfiles= new String[]{"Ffunc\\my-jar\\ffunc-1.0-SNAPSHOT.jar", "Gfunc\\my-jar\\gfunc-1.0-SNAPSHOT.jar"};
            funcResults= new ArrayList<>(2);

            start(0,Fprocess,Gprocess);
            start(1,Gprocess,Fprocess);

            while (!funcResults.get(0).isDone() || !funcResults.get(1).isDone()) {
                if(executor.isShutdown())
                    break;
            }

            if (!executor.isShutdown()) {
                int f = funcResults.get(0).get();
                int g = funcResults.get(1).get();
                System.out.println("f: "+f);
                System.out.println("g: "+g);
                result = f * g;
                executor.shutdown();
            }

            System.out.println("Result: " + result);
        }finally {
            server.close();
        }

    }

    private static void setProjectDir()
    {
        projectDir=System.getProperty("user.dir");
        projectDir=projectDir.substring(0,projectDir.length()-7);
    }

    private static void setX(){
        System.out.println("Enter x: ");
        Scanner scanner = new Scanner(System.in);
        x = scanner.nextInt();
    }

    public static void start(int i, Process process1,Process process2) throws IOException {
        Socket pipe = server.accept();
        funcResults.add(executor.submit(new GetFG(projectDir+jarfiles[i],pipe,process1,process2)));
    }


    static class GetFG implements Callable<Integer> {
        String jar;
        Socket pipe;
        Process process1;
        Process process2;
        public GetFG(String jar,Socket pipe, Process process1, Process process2) {
            this.jar = jar;
            this.pipe=pipe;
            this.process1=process1;
            this.process2=process2;
        }

        @Override
        public Integer call() throws Exception {
            PrintWriter out = new PrintWriter(pipe.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(pipe.getInputStream()));
            process1 = Runtime.getRuntime().exec("java -jar "+jar);
            out.println(x);
            int res=Integer.parseInt(in.readLine());
            if (res == 0) {
                pipe.close();
                process1.destroyForcibly();
                process2.destroyForcibly();
                executor.shutdownNow();
                return null;
            }
            pipe.close();
            return res;
        }
    }
}
