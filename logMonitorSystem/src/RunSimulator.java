

/**
 * The main class to run the log monitoring system simulator.
 */
public class RunSimulator {

  public static void main(String[] args) {

    // if the query is empty
    if (args.length == 0) {
      System.out.println("Please run the program with the expected arguments and try again");

    }
    // to generate log files by specifying the path wherein the directory of log files will be created
    else if (args.length == 1) {
      String path = args[0];
      LogMonitorSystem obj = new LogMonitorSystem();
      obj.generateLog(path);


    }
    // to fetch information from the Logs directory by using information inputted by the user in the query
    else if (args.length == 7) {
      LogMonitorSystem obj = new LogMonitorSystem();
      obj.fetchData(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
    } else {
      System.out.println("Please enter the correct arguments for the simulator to run correctly");
    }
  }


}
