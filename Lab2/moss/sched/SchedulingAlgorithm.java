 // Run() is called from Scheduling.main() and is where
// the scheduling algorithm written by the user resides.
// User modification should occur within the Run() function.

import java.util.Vector;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SchedulingAlgorithm {

  public static Results Run(int runtime, Vector processVector, Results result) {
    int i = 0;
    int comptime = 0;
    int currentProcess = 0;
    int previousProcess = 0;
    int size = processVector.size();
    int completed = 0;
    String resultsFile = "Summary-Processes";

    result.schedulingType = "Batch (Nonpreemptive)";
    result.schedulingName = "Shortest Job First";

    List<sProcess>processList=getSortedProcesses(processVector);

    try {
      PrintStream out = new PrintStream(new FileOutputStream(resultsFile));
      sProcess process =processList.get(currentProcess);
      outPrintData(out, currentProcess,"registered...",process);
      while (comptime < runtime) {
        if (process.cpudone == process.cputime){
          completed++;
          outPrintData(out, currentProcess,"completed...",process);
         if (completed == size) {
            result.compuTime = comptime;
            out.close();
            return result;
          }
          for (i = size - 1; i >= 0; i--) {
            process =processList.get(i);
            if (process.cpudone < process.cputime) {
              currentProcess = i;
            }
          }
          process =processList.get(currentProcess);
          outPrintData(out, currentProcess,"registered...",process);
        }
        if (process.ioblocking == process.ionext) {
          outPrintData(out, currentProcess,"I/O blocked...",process);
          process.numblocked++;
          process.ionext = 0; 
          previousProcess = currentProcess;
          for (i = size - 1; i >= 0; i--) {
            process =processList.get(i);
            if (process.cpudone < process.cputime && previousProcess != i) {
              currentProcess = i;
            }
          }
           process =processList.get(currentProcess);
          outPrintData(out, currentProcess,"registered...",process);
        }        
        process.cpudone++;       
        if (process.ioblocking > 0) {
          process.ionext++;
        }
        comptime++;
      }
      out.close();
    } catch (IOException e) { /* Handle exceptions */ }
    result.compuTime = comptime;
    return result;
  }

  private static List<sProcess>getSortedProcesses(Vector processVector){
    int size = processVector.size();
    List<sProcess>processList=new ArrayList<>(size);
    for(int i=0;i<size;i++)
      processList.add((sProcess)processVector.elementAt(i));
    Collections.sort(processList);
    return processList;
  }

  private static void outPrintData(PrintStream out, int currentProcess, String status, sProcess process){
     out.println("Process: " + currentProcess + " "+ status+ " ("+ process.cputime + " " + process.ioblocking + " " + process.cpudone + ")");
  }
}
