import java.time.Month;
import java.util.List;

public interface EmployeeDAO {

    //check db to see if there is already an employee with this name
     boolean employeeExists(String nameToSearch);
     // access Employee name in storage, create Employee object in Memory and populate said object with goals and progress from db
     Employee grabEmployeeFromStorage(String employeeName);

     //return a list of all employees in the system
     List<Employee> getAllEmployees();
     //add a new employee to the db based on object in memory, with monthly goals set to match the object's and their logged hours set to 0 for each month
     void saveNewEmployee(Employee employee);
     //update all 12 monthly goals for an employee to new values
     void updateEmployeeGoals(Employee employee);
     //update all 12 monthly hours logged for an employee to new values
     void updateEmployeeHours(Employee employee);
     //update single monthly goal to a new value
     void updateSingleGoal(Employee employee, Month month, double newGoal);
     //update single monthly log to a new value
     void updateSingleMonthLog(Employee employee, Month month, double newLogProgress);
     //increase an employee's logged hours for a month by a specified amount
    void logHours(Employee employee, Month month, double hoursToLog);
    //get current logged hours from the db
    Double getCurrentLogProgressForMonth(Employee employee, Month month);
    //get the number of hours to log until goal for current month is hit
    Double getHoursLeftTillGoal(Employee employee);
}
