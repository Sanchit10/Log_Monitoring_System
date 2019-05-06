import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


/**
 * A java class that implements the read and write function for the log monitoring system.
 */
public class LogMonitorSystem {

  /**
   * A function that generates text files for 1000 servers wherein each text file contains the
   * server log information for 24 hours identified by a unique IP address. For example
   * 192.168.2.11.txt, 192.168.2.12.txt and so on. All these text files are created within a
   * directory named as Logs.
   *
   * @param path the path that the user needs to enter wherein the directory with the text files
   * will be created. For example writing "/Users/sanchitsaini/" will create a Logs directory with
   * all the text files for 1000 servers in the home directory of my machine.
   */
  public void generateLog(String path) {

    String serverIP = "192.168.1.254";

    //create a Directory for all the files
    File myDirectory = new File(path + "Logs/");
    boolean createDirectory = myDirectory.mkdir();
    // if directory of files already exists
    if (!createDirectory) {
      System.out.println(
          "The directory for all the log files could not be created or already exists. Please recheck your path again.");
      return;
    }
    // creating text files
    for (int i = 0; i < 1000; i++) {
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
      //specify the date object to be able to get unix timestamp
      Date date;
      try {
        date = format.parse("2019-01-09");
      } catch (ParseException e) {
        System.out.println(
            "The date string is not in the required format (yyyy-mm-dd). Error is : " + e
                .getMessage());
        return;
      }
      //get Unix Timestamp
      long unixTime = date.getTime() / 1000;

      // to write to text file
      PrintWriter myWriter;
      try {
        //create empty text file
        myWriter = new PrintWriter(path + "Logs/" + serverIP + ".txt");
      } catch (FileNotFoundException e) {
        System.out.println(
            "Attempt to open the file name " + path + serverIP + ".txt" + " has failed. Error: " + e
                .getMessage());
        return;
      }
      // begin writing to file
      myWriter.println("timestamp          IP            cpu_id  usage");
      for (int j = 0; j < Constants.NUMBER_OF_MINS_IN_A_DAY; j++) {
        Random rand = new Random();
        //random CPU load between 0-100%
        int cpuLoad = rand.nextInt(101);
        //begin writing data
        myWriter.println(
            unixTime + Constants.DELIMITER + serverIP + Constants.DELIMITER + "0"
                + Constants.DELIMITER + cpuLoad);
        cpuLoad = rand.nextInt(101);
        myWriter.println(
            unixTime + Constants.DELIMITER + serverIP + Constants.DELIMITER + "1"
                + Constants.DELIMITER + cpuLoad);
        //increment unix time by 1 minute i.e. 60000 seconds
        unixTime += Constants.NUMBER_OF_MILLISECONDS_IN_A_MIN;

      }
      //increment the serverIP string to get the name for the next text file for a different server
      // for example "192.168.3.199" will change to "192.168.3.200"
      serverIP = getNextIP(serverIP);

      //stop writing to the current text file
      myWriter.close();
    }


  }

  /**
   * A function that is used to scan a target text file to be able to display the results for the
   * query entered by the user.
   *
   * @param pathName the path to the text file to be read in a directory of log text files.
   * @param fileName the name of the file to be read, identified by the IP address entered by the
   * user in the query.
   * @param cpuID the cpu ID for which the information needs to be displayed.
   * @param date the date for which we are retrieving the information.
   * @param start the start time in our query.
   * @param endDate the end time for our query window.
   * @param end the end time for our query window.
   */
  public void fetchData(String pathName, String fileName, String cpuID, String date, String start,
      String endDate, String end) {

    //validate the parameters
    if (!validateParameters(cpuID, date, start, endDate, end)) {
      return;
    }

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    //specify the date
    Date myDate = null;
    try {
      //create date object to get unix time
      myDate = format.parse("2019-01-09");
    } catch (ParseException e) {
      System.out.println(
          "Error when the date is not in the format of yyyy-MM-dd. Error is:" + e.getMessage());
    }
    //get unix time from date
    long unixTime = myDate.getTime() / 1000;

    long unixStartTime = calculateStartUnixTime(unixTime, start);

    long unixEndTime = calculateEndUnixTime(unixTime, end);

    if (end.equals("00:00")) {
      unixEndTime = Constants.LAST_MIN_UNIX_TIMESTAMP;
    }

    File myFile = new File(pathName + "Logs/" + fileName + ".txt");
    if (!myFile.isFile()) {
      System.out.println(
          "Invalid filename/pathname. Please try again with the correct filename or the correct path.");
      return;
    }

    if (myFile.isFile()) {
      try {
        //read file at the specified location
        BufferedReader myReader = new BufferedReader(
            new FileReader(pathName + "Logs/" + fileName + ".txt"));
        String line = myReader.readLine();
        System.out.println("CPU" + cpuID + " usage on " + fileName + ":");
        //window for parsing the files with the start and end time
        while (line != null && unixStartTime <= unixEndTime) {
          String[] myArray1 = line.split(Constants.DELIMITER);
          //found unixTime match
          if (myArray1[0].equals(Long.toString(unixStartTime)) && myArray1[2].equals(cpuID)) {
            System.out.println("(" + date + " " + start + ", " + myArray1[3] + "%)");
            //update the start time string
            start = formatStartTime(start);
            //increment time
            unixStartTime += Constants.NUMBER_OF_MILLISECONDS_IN_A_MIN;
          } else {
            //read next line
            line = myReader.readLine();

          }

        }
        //close the reader
        myReader.close();


      } catch (FileNotFoundException e) {
        System.out
            .println("The file at the specified path does not exist. Erros is :" + e.getMessage());
      } catch (IOException e) {
        System.out.println(
            "The source of the text stream is no longer available. Error is: " + e.getMessage());
      }
    }


  }


  /**
   * Helper method to format the log time while retrieving the results for a query.
   *
   * @param start the start string.
   * @return formatted start string.
   */
  private String formatStartTime(String start) {

    // split the hours and minutes in the start time string
    int hours = Integer.parseInt(start.substring(0, 2));
    int mins = Integer.parseInt(start.substring(3));
    //increment the minutes by one to fetch cpu server information for the next minute
    mins += 1;
    //formatting the start time string for various cases
    if (hours < 10 && mins < 10) {
      start = "0" + hours + ":0" + mins;
    } else if (hours < 10 || mins < 10) {
      if (hours < 10) {
        start = "0" + hours + ":" + mins;
      } else {
        start = hours + ":0" + mins;
      }

    }//day ends, no more cpu logs available to parse through
    else if (hours == 23 && mins == 60) {
      System.out.println("All logs for this day till 23:59 have been retrieved.");

    }
    // incrementing the hours when minutes become 60
    else if (mins == 60) {
      hours += 1;
      if (hours < 10) {
        start = "0" + hours + ":00";
      } else {
        start = hours + ":00";
      }
    } else {
      start = hours + ":" + mins;
    }
    return start;
  }

  /**
   * A helper function that calculates the start time in unix time.
   *
   * @param unixTime the starting unix time when the day begins i.e at 00:00.
   * @param start the string representing the start time.
   * @return the unix start time for the query calculated from the start string.
   */
  private long calculateStartUnixTime(long unixTime, String start) {
    String[] timeArray1 = start.split(":");
    int startHours = Integer.parseInt(timeArray1[0]);
    int startMins = Integer.parseInt(timeArray1[1]);
    return (unixTime + (startHours * Constants.NUMBER_OF_MILLISECONDS_IN_AN_HOUR
        + startMins * Constants.NUMBER_OF_MILLISECONDS_IN_A_MIN));
  }


  /**
   * A helper function that converts the end string to unix time.
   *
   * @param unixTime the starting unix time when the day begins i.e. at 00:00.
   * @param end the string representing the end time.
   * @return the unix end time for the query calculated from the end string.
   */
  private long calculateEndUnixTime(long unixTime, String end) {
    return (calculateStartUnixTime(unixTime, end)) - Constants.NUMBER_OF_MILLISECONDS_IN_A_MIN;
  }


  /**
   * A function that validates the query parameters required to search and fetch data from the text
   * files.
   *
   * @param cpuID the Cpu Id mentioned in the query.
   * @param date the date for which we are retrieving the information.
   * @param start the start time in our query.
   * @param endDate the same date as above since we are only generating files for 1 day.
   * @param end the end time for our query window.
   * @return returns false if any of the query parameters are invalid or not in the correct format.
   */
  private boolean validateParameters(String cpuID, String date, String start,
      String endDate, String end) {
    //case for different start and end dates for the query
    if (!date.equals(endDate)) {
      System.out.println("The dates should be the same!");
      return false;
    }
    //case for a date other than "2019-01-09" i.e. one day
    if (!date.equals("2019-01-09")) {
      System.out.println(
          "Log information is only available for the day - 2019-01-09. Please retry running the simulator with this date.");
      return false;
    }
    // case when starting time >ending time, example adding 14:32 as the start time & 07:40 as the end
    if (Integer.parseInt(start.substring(0, 2)) > Integer.parseInt(end.substring(0, 2)) && !end
        .equals("00:00")) {
      System.out.println("Incorrect time. Time should be increasing");
      return false;
    }
    // only 2 cpu's per server
    if (!cpuID.equals("0") && !cpuID.equals("1")) {
      System.out.println("Please enter a valid CPU ID.");
      return false;
    }

    //if time is 7:40 instead of 07:40
    if (start.length() != 5 || end.length() != 5) {
      System.out.println("Please enter the start/end time in hh:mm format and try again.");
      return false;
    }
    return true;
  }

  /**
   * A helper function that takes the server IP address string as input and increments it for the
   * next text file.
   *
   * @param serverIP the server IP string i.e. something like "192.168.4.12"
   * @return returns an incremented server IP address as a string. For example "192.168.4.20"
   * becomes "192.168.4.21"
   */
  private String getNextIP(String serverIP) {

    //split the serverIp string using '.' character, for example 192.168.2.10 ={192,168,2,10)
    String[] ipArray = serverIP.split("\\.");
    //access & increment the last element in the array i.e. 10
    int incrementIP = Integer.parseInt(ipArray[3]);
    incrementIP++;
    // cannot be greater than 254
    if (incrementIP > 254) {
      // increment the second last digit of the array in the case of {192,168,2,254} it will be changed to {192,168,3,10}
      int subIP = Integer.parseInt(ipArray[2]);
      subIP++;
      //add incremented subIP beack to the array
      ipArray[2] = Integer.toString(subIP);
      //update the last element to 10
      ipArray[3] = "10";
      //update the serverIP String
      serverIP = String.join(".", ipArray);

    } else {
      //converted the incremented integer to a string and add it back to the string array
      //for example {192,168,2,10} will be {192,168,2,11}
      ipArray[3] = Integer.toString(incrementIP);
      //create the string by joining the string array back to a single string with '.' as the delimiter
      serverIP = String.join(".", ipArray);


    }
    return serverIP;
  }


}
