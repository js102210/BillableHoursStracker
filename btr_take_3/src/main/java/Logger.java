import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.util.Scanner;

public class Logger {
    static boolean monthElapsedSinceLastLog;
    private static Month monthOfLastLog;
    private static int monthsSinceLastLog;

    public static Month getMonthOfLastLog(){
        return monthOfLastLog;
    }


    //write current time stamp to txt file
    public static void log(){
        try(PrintWriter logWriter = new PrintWriter("Logs/Log.txt")){
            LocalDate timeStamp = LocalDate.now();
            logWriter.println(timeStamp);
        } catch (IOException e) {
            System.out.println("Could not create the log.");
        }


    }


    // read time stamp from last log, parse it as a date, and compare it to the current date to set
    //monthOfLastLog and monthElapsedSinceLastLog
    public static void logCheck(String pathToLog){
        Path myPath = Path.of(pathToLog);
        LocalDate currentDate = LocalDate.now();

        try(Scanner logReader = new Scanner(myPath)){
            String lastLog = logReader.nextLine();
            LocalDate dateOfLastLog = LocalDate.parse(lastLog);
            monthOfLastLog = dateOfLastLog.getMonth();
            Period period = Period.between(dateOfLastLog, currentDate);
            monthElapsedSinceLastLog = period.getMonths() > 0;
        } catch (IOException e) {
            System.out.println("Could not check previous log");
        }
    }

    public static int getMonthsSinceLastBalanceCarry(Month monthOfLastLog){
        return monthOfLastLog.getValue() - LocalDate.now().getMonthValue();
    }

}
