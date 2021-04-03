import MenuInteraction.MenuDrivenClI;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class App {

    //main menu options
    private static final String MAIN_MENU_OPTION_CREATE_NEW_EMPLOYEE = "Create new employee";
    private static final String MAIN_MENU_OPTION_LOG_HOURS = "Log hours";
    private static final String MAIN_MENU_OPTIONS_GOAL_MENU = "View or set goal/progress menu";
    private static final String MAIN_MENU_OPTION_QUIT = "Exit application";
    private static final String[] MAIN_MENU_OPTIONS = {MAIN_MENU_OPTION_CREATE_NEW_EMPLOYEE, MAIN_MENU_OPTION_LOG_HOURS, MAIN_MENU_OPTIONS_GOAL_MENU,
     MAIN_MENU_OPTION_QUIT};

    //goal menu options
    private static final String GOAL_MENU_OPTION_SHOW_ALL_EMPLOYEES_WITH_GOAL_PROGRESS = "View all tracked employees and their annual goal progress";
    private static final String GOAL_MENU_OPTION_SHOW_GOAL_PROGRESS_FOR_ONE_EMPLOYEE = "View tracked hours/goals for a single employee";
    private static final String GOAL_MENU_OPTION_RESET_EMPLOYEE = "Reset an existing employee's charge hour goals";
    private static final String GOAL_MENU_OPTION_GET_MONTHLY_PROJECTION_FOR_EMPLOYEE = "See how many hours per week an employee needs to log to stay on track for this month's goal";
    private static final String GOAL_MENU_OPTION_CHANGE_EMPLOYEE_GOALS = "Change the annual/monthly goals for an employee";
    private static final String GOAL_MENU_OPTION_BACK_TO_MAIN = "Back to main menu";
    private static final String[] GOAL_MENU_OPTIONS = {GOAL_MENU_OPTION_SHOW_ALL_EMPLOYEES_WITH_GOAL_PROGRESS, GOAL_MENU_OPTION_SHOW_GOAL_PROGRESS_FOR_ONE_EMPLOYEE,
            GOAL_MENU_OPTION_RESET_EMPLOYEE, GOAL_MENU_OPTION_GET_MONTHLY_PROJECTION_FOR_EMPLOYEE, GOAL_MENU_OPTION_CHANGE_EMPLOYEE_GOALS, GOAL_MENU_OPTION_BACK_TO_MAIN};

    //menu for yes or no
    private static final String YES_OR_NO_MENU_YES = "Yes";
    private static final String YES_OR_NO_MENU_NO = "No";
    private static final String[] YES_OR_NO_MENU = {YES_OR_NO_MENU_YES, YES_OR_NO_MENU_NO};

    private final MenuDrivenClI ui = new MenuDrivenClI();
    private final Scanner userStringInput = new Scanner(System.in);
    private final JDBCEmployeeDAO employeeDao;


    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    public App() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/billablehourstrackerdb");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres1");
        employeeDao = new JDBCEmployeeDAO(dataSource);
    }


    public void run() {
        //check log and carry balance over from previous month for all employees if
        //a month has passed since the last log
        Logger.logCheck("Logs/Log.txt");
        if (Logger.monthElapsedSinceLastLog){
            //find out how many months it's been since the goal balances have last been carried
            int monthsSinceLastBalanceCarry = Logger.getMonthsSinceLastBalanceCarry(Logger.getMonthOfLastLog());
            if (monthsSinceLastBalanceCarry > 0 && Logger.getMonthOfLastLog() != Month.DECEMBER) {
                //grab all the employees from the db and carry their balances over starting from the oldest uncarried month to the most recent one before the current month
               List<Employee> employees = employeeDao.getAllEmployees();
                for (Employee e : employees) {
                    int i = monthsSinceLastBalanceCarry;
                    while (i > 0){
                        e.carryGoalBalance(e, Month.of(LocalDate.now().getMonth().getValue() - i));
                        i--;
                    }
                    //save updated values for each employee back to the db
                    employeeDao.updateEmployeeHours(e);
                    employeeDao.updateEmployeeGoals(e);
                }
            }
        }
        //write current date to txt file
        Logger.log();
        handleMainMenu();
    }

    public void handleMainMenu() {
        boolean running = true;
        while (running) {
            String selection = ui.promptForSelection(MAIN_MENU_OPTIONS);
            if (selection.equals(MAIN_MENU_OPTION_CREATE_NEW_EMPLOYEE)) {
                System.out.println("Please enter the full name for the employee (note, this will be the employee's unique identifier, so you may wish to " +
                        "enter their full name, or perhaps use their email instead. Whatever you enter is how you will refer to this employee in this application): ");
                String employeeName = userStringInput.nextLine();
                validateNewEmployee(employeeName);
            }
            if (selection.equals(MAIN_MENU_OPTION_LOG_HOURS)) {
                System.out.println("Enter the name of the employee you would like to log hours for: ");
                String employeeName = userStringInput.nextLine();
                System.out.println("Enter the month in which you would like to log hours for " + employeeName + "(or just press enter to log them in the current month):");
                String monthName = userStringInput.nextLine();
                Month month = null;
                Double hoursToLog = null;
               try{ if (monthName.isBlank()) {
                    month = LocalDate.now().getMonth();
                } else {
                    month = Month.valueOf(monthName.toUpperCase(Locale.ROOT));
                } } catch (IllegalArgumentException e){
                   System.out.println("The month name you entered is invalid. Please check your spelling, and provide the full name of the month.");
                   handleMainMenu();
               }
                assert month != null;
                System.out.println("How many hours are you logging for " + employeeName + " in " + month.toString() + "?: ");
               try {
                  hoursToLog = Double.parseDouble(userStringInput.nextLine());
               } catch (NumberFormatException e){
                   System.out.println("Could not log your input as a number of hours. Please input the number as digits (ex '1' not 'one')");
                   handleMainMenu();
               }
               try{ handleLogHours(employeeName, month, hoursToLog);
               }catch(NullPointerException e){
                   System.out.println("There wasn't an employee with that name in the database. Please check your spelling and try again.");
                   handleMainMenu();
               }
            }
            if (selection.equals(MAIN_MENU_OPTIONS_GOAL_MENU)){
                handleGoalMenu();;
            }
            if (selection.equals(MAIN_MENU_OPTION_QUIT)) {
                running = false;
            }
        }
    }

    public List<Employee> grabAllEmployeesFromStorage(){
        return employeeDao.getAllEmployees();
    }

    public Map<Long, Employee> prepareEmployeesForLookup(List<Employee> employees){
        Map<Long, Employee>  employeeMap = new HashMap<>();
        for (Employee e : employees) {
            employeeMap.put(e.getEmployee_id(), e);
        }
        return employeeMap;
    }

    public void printEmployeesWithID(List<Employee> employees){
        for (Employee e : employees){
            System.out.println("ID: " + e.getEmployee_id() + " Name: " + e.getFullName());
        }
    }

    public void handleGoalMenu() {
        boolean inGoalMenu = true;
        while (inGoalMenu) {
            String selection = ui.promptForSelection(GOAL_MENU_OPTIONS);

            if (selection.equals(GOAL_MENU_OPTION_RESET_EMPLOYEE)) {
                handleResetEmployee();
            }
            if (selection.equals(GOAL_MENU_OPTION_SHOW_ALL_EMPLOYEES_WITH_GOAL_PROGRESS)) {
                List<Employee> employees = grabAllEmployeesFromStorage();
                for (Employee e : employees){
                    printGoalsAndHoursForSingleEmployee(e);
                }
            }
            if (selection.equals(GOAL_MENU_OPTION_SHOW_GOAL_PROGRESS_FOR_ONE_EMPLOYEE)) {
                handleLookupEmployeeGoalsAndProgress();
            }
            if (selection.equals(GOAL_MENU_OPTION_GET_MONTHLY_PROJECTION_FOR_EMPLOYEE)){
                handleSingleMonthProjection();
            }
            if (selection.equals(GOAL_MENU_OPTION_CHANGE_EMPLOYEE_GOALS)){
                handleChangeEmployeeGoals();
            }
            if (selection.equals(GOAL_MENU_OPTION_BACK_TO_MAIN)) {
                inGoalMenu = false;
                handleMainMenu();
            }

        }

    }

    public void handleLookupEmployeeGoalsAndProgress(){
        //get each employee and store them in a map to be referenced by their id
        List<Employee> employees = grabAllEmployeesFromStorage();
        Map<Long, Employee> employeeMap = prepareEmployeesForLookup(employees);
        //get user input for desired employee's goals/hours and pass the Map of employees + lookup id to the relevant method
        printEmployeesWithID(employees);
        System.out.println("Enter the id of the employee you wish to view goals/logged hours for: ");
        Long employeeId = Long.parseLong(userStringInput.nextLine());
        Employee employee = lookupEmployee(employeeMap, employeeId);
        if(employee!= null){
            printGoalsAndHoursForSingleEmployee(employee);
        } else {
            System.out.println("There isn't an employee with that ID in the database. Please make another selection.");
            handleLookupEmployeeGoalsAndProgress();
        }
    }


    public void handleChangeEmployeeGoals(){
        //get each employee and store them in a map to be referenced by their id
        List<Employee> employees = grabAllEmployeesFromStorage();
        Map<Long, Employee> employeeMap = prepareEmployeesForLookup(employees);
        //get user input for desired employee's goals/hours and pass the Map of employees + lookup id to the relevant method
        printEmployeesWithID(employees);
        System.out.println("Enter the id of the employee you wish to view goals/logged hours for: ");
        Long employeeId = Long.parseLong(userStringInput.nextLine());
        Employee employee = lookupEmployee(employeeMap, employeeId);
        if(employee!= null){
            System.out.println("You can either enter a single annual goal, or you can enter their 12 monthly goals. If you enter a single goal, it will be distributed throughout the" +
                    "year based on the configured defaults. Enter number(s) below, separated by commas: ");
            try {    String[] newGoalArr = userStringInput.nextLine().split(",");
                int createType = newGoalArr.length;
                switch (createType) {
                    case 1:
                        employee.setEmployeeGoalsToDefaults(Double.parseDouble(newGoalArr[0]));
                        employeeDao.updateEmployeeGoals(employee);
                        System.out.println(employee.getFullName() + "'s annual goal is now " + Double.parseDouble(newGoalArr[0]) + ". If they have already logged any hours " +
                                "this year, please log them from the 'Log Hours' menu.");
                        handleMainMenu();
                        break;
                    case 12:
                        double sum = 0;
                        for (String num : newGoalArr) {
                            double sumToAdd = Double.parseDouble(num);
                            sum += sumToAdd;
                        }
                        //check to make sure the user entered the amounts they want
                        System.out.println(employee.getFullName() + "'s " + "annual charge hour goal will be " + sum + ". Is that what you want?");
                        String selection = ui.promptForSelection(YES_OR_NO_MENU);
                        if (selection.equals(YES_OR_NO_MENU_YES)) {
                            int i = 1;
                            while (i <= 12){
                                employee.setMonthGoal(Month.of(i), Double.parseDouble(newGoalArr[i+1]));
                                i++;
                            }
                            employeeDao.updateEmployeeGoals(employee);
                        }
                        System.out.println(employee.getFullName() + "'s annual goal is now " + sum + ". If they have already billed any hours " +
                                "this year, please log them from the 'Log Hours' menu.");

                        handleMainMenu();
                        if (selection.equals(YES_OR_NO_MENU_NO)) {
                            System.out.println("Please try again.");
                            handleCreateEmployee(employee.getFullName());
                        }
                        break;
                    default:
                        System.out.println("You entered too many or too few numbers. Would you like to try again?");
                        String tryAgain = ui.promptForSelection(YES_OR_NO_MENU);
                        if (tryAgain.equals(YES_OR_NO_MENU_YES)) {
                            handleCreateEmployee(employee.getFullName());
                        }
                        if (tryAgain.equals(YES_OR_NO_MENU_NO)) {
                            handleMainMenu();
                        }
                        break;

                } }catch (NumberFormatException e){
                System.out.println("Something was wrong with your inputs. Please make sure you enter all numerical values as digits (ex '1' not 'one').");
            }
        } else {
            System.out.println("There isn't an employee with that ID in the database. Please make another selection.");
            handleChangeEmployeeGoals();
        }
    }



    public void validateNewEmployee(String employeeName) {
        //ensure an employee with that name doesn't already exist. If it does, ask the user if they want to reset that person's goals/hours. If not, return them to main menu.
        if (employeeDao.employeeExists(employeeName)) {
            System.out.println("There is already an employee with this name in the database. Would you like to reset this employee's goals/logged hours?");
            String selection = ui.promptForSelection(YES_OR_NO_MENU);
            if (selection.equals(YES_OR_NO_MENU_YES)) {
               handleResetEmployee();
            }
            if (selection.equals(YES_OR_NO_MENU_NO)) {
                handleMainMenu();
            }
        } else {
            handleCreateEmployee(employeeName);
        }
    }

    public void handleCreateEmployee(String employeeName) {
        System.out.println("You can either enter a single annual goal, or you can enter their 12 monthly goals. If you enter a single goal, it will be distributed throughout the" +
                "year based on the configured defaults. Enter number(s) below, separated by commas: ");
    try {    String[] newEmployeeArr = userStringInput.nextLine().split(",");
        int createType = newEmployeeArr.length;
        Employee employee;
        switch (createType) {
            case 1:
                employee = new Employee(employeeName, Double.parseDouble(newEmployeeArr[0]));
                employeeDao.saveNewEmployee(employee);
                System.out.println(employeeName + " has been added with an annual goal of " + Double.parseDouble(newEmployeeArr[0]) + ". If they have already logged any hours " +
                        "this year, please log them from the 'Log Hours' menu.");
                handleMainMenu();
                break;
            case 12:
                double sum = 0;
                for (String num : newEmployeeArr) {
                    double sumToAdd = Double.parseDouble(num);
                    sum += sumToAdd;
                }
                //check to make sure the user entered the amounts they want
                System.out.println(employeeName + "'s " + "annual charge hour goal will be " + sum + ". Is that what you want?");
                String selection = ui.promptForSelection(YES_OR_NO_MENU);
                if (selection.equals(YES_OR_NO_MENU_YES)) {
                    employee = new Employee(employeeName, Double.parseDouble(newEmployeeArr[0]), Double.parseDouble(newEmployeeArr[1]), Double.parseDouble(newEmployeeArr[2]),
                            Double.parseDouble(newEmployeeArr[3]), Double.parseDouble(newEmployeeArr[4]), Double.parseDouble(newEmployeeArr[5]), Double.parseDouble(newEmployeeArr[6]),
                            Double.parseDouble(newEmployeeArr[7]), Double.parseDouble(newEmployeeArr[8]), Double.parseDouble(newEmployeeArr[9]), Double.parseDouble(newEmployeeArr[10]),
                            Double.parseDouble(newEmployeeArr[11]));
                    employeeDao.saveNewEmployee(employee);
                }
                System.out.println(employeeName + " has been added with an annual goal of " + sum + ". If they have already billed any hours " +
                        "this year, please log them from the 'Log Hours' menu.");

                handleMainMenu();
                if (selection.equals(YES_OR_NO_MENU_NO)) {
                    System.out.println("Please try again.");
                    handleCreateEmployee(employeeName);
                }
                break;
            default:
                System.out.println("You entered too many or too few numbers. Would you like to try again?");
                String tryAgain = ui.promptForSelection(YES_OR_NO_MENU);
                if (tryAgain.equals(YES_OR_NO_MENU_YES)) {
                    handleCreateEmployee(employeeName);
                }
                if (tryAgain.equals(YES_OR_NO_MENU_NO)) {
                    handleMainMenu();
                }
                break;

        } }catch (NumberFormatException e){
        System.out.println("Something was wrong with your inputs. Please make sure you enter all numerical values as digits (ex '1' not 'one').");
    }

    }

    public void printGoalsAndHoursForSingleEmployee(Employee employee){
        System.out.println("-------------------------------------------------------");
        System.out.println(employee.getFullName());
        System.out.println("MONTH     GOAL      HOURS LOGGED     GOAL EXCEEDED BY");
        System.out.println("-------------------------------------------------------");
        int i = 1;
        while (i <= 12) {
            Month m = Month.of(i);
            Double g = employee.getMonthGoal(Month.of(i));
            Double h = employee.getMonthHours(Month.of(i));
            Double d = employee.getMonthGoalDiff(Month.of(i));
            if(g != null && h != null && d != null) {
                System.out.format("%.3s       %.2f      %.2f          %.2f\n", m, g, h, d);
            }
            i++;
        }
        String annual = "YTD";
        Double annualGoal = employee.getAnnualBillableHoursGoal();
        Double annualHoursLogged = employee.getAnnualHoursLogged();
        Double annualDiff = employee.getAnnualGoalDiff();
        System.out.format("%.3s       %.2f      %.2f          %.2f\n", annual, annualGoal, annualHoursLogged, annualDiff);
    }
    public Employee lookupEmployee(Map<Long, Employee> employeeLookup, Long idToLookup) {
        return employeeLookup.get(idToLookup);
    }


    public void handleLogHours(String employeeName, Month month, Double hoursToLog) {
        Employee employee = employeeDao.grabEmployeeFromStorage(employeeName);
        employeeDao.logHours(employee, month, hoursToLog);
        Double newHours = employeeDao.getCurrentLogProgressForMonth(employee, month);
        System.out.println("The hours you entered have been logged for " + employeeName + ". Their new logged hours total for " + month.toString() + " is " + newHours + ".");
    }

    public void handleResetEmployee() {
        System.out.println("Enter the name of the employee you would like to reset: ");
        String employeeName = userStringInput.nextLine();
        Employee employee = employeeDao.grabEmployeeFromStorage(employeeName);
        if (employee != null) {
            String selection = ui.promptForSelection(YES_OR_NO_MENU);
            if (selection.equals(YES_OR_NO_MENU_YES)) {
                employee.resetGoalsAndHoursLogged();
                System.out.println(employeeName + "'s hours and goals have been reset. You can set new goals for them from the goal menu.");
                employeeDao.updateEmployeeGoals(employee);
                employeeDao.updateEmployeeHours(employee);
            }
            if (selection.equals(YES_OR_NO_MENU_NO)) {
                employee.resetGoals();
                System.out.println(employeeName + "'s goals have been reset. You can set new goals for them from the goal menu.");
                employeeDao.updateEmployeeGoals(employee);
            }
        } else {
            System.out.println("There isn't an employee with that name in the database. You will be returned to the goal menu.");
            handleGoalMenu();
        }
        handleGoalMenu();
    }

    public double getWeeksTillNextMonth(){
        LocalDate today = LocalDate.now();
        LocalDate startOfNextMonth = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth().plus(1), 1);
        return (double) ChronoUnit.WEEKS.between(today, startOfNextMonth);
    }

    public double getHoursLeftTillGoal(String employeeName){
        Employee employee = employeeDao.grabEmployeeFromStorage(employeeName);
       return  employeeDao.getHoursLeftTillGoal(employee);
    }

    public double hoursPerWeekToMeetGoal(String employeeName){
        double hoursLeft = getHoursLeftTillGoal(employeeName);
        double weeksLeft = getWeeksTillNextMonth();
        return hoursLeft/weeksLeft;
    }


    public void getSingleMonthProjection(String employeeName){
        double hoursLeft = getHoursLeftTillGoal(employeeName);
        double hoursPerWeek = hoursPerWeekToMeetGoal(employeeName);
        System.out.println(employeeName + " needs to log " + hoursLeft + " to meet their goal for the month. \n" +
                "To meet the goal, they must log an average of " + hoursPerWeek + " per week until the end of the month.");

    }

    public void handleSingleMonthProjection(){
        System.out.println("Enter the name of the employee you want to get a projection for: ");
        String employeeName = userStringInput.nextLine();
        getSingleMonthProjection (employeeName);
        handleGoalMenu();
    }

}


