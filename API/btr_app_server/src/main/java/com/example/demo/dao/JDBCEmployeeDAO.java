package com.example.demo.dao;
import com.example.demo.models.Employee;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class JDBCEmployeeDAO implements EmployeeDAO {
    private final JdbcTemplate jdbcTemplate;
    public JDBCEmployeeDAO(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    //instantiates Employee in memory
    private Employee mapRowToEmployee(SqlRowSet results){
        Employee theEmployee = new Employee();
        theEmployee.setFullName(results.getString("employee_name"));
        theEmployee.setEmployee_id(results.getLong("employee_id"));
        return theEmployee;
    }

    //populates an Employee in memory with their respective goals and progress
    private void mapRowToEmployeeGoalProgress(Employee employee ){

        String sql = "SELECT DISTINCT logprogress.month_number, SUM(logprogress.hours_logged) as totalhrs, (SELECT ?) as name, sum(goals.goal_amt) as monthly_goal\n" +
                "FROM logprogress\n" +
                "LEFT JOIN goals on logprogress.employee_id = goals.employee_id AND goals.month_number = logprogress.month_number\n" +
                "WHERE logprogress.employee_id = (SELECT employee_id FROM employees WHERE employee_name ILIKE ?)\n" +
                "GROUP BY logprogress.month_number";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, employee.getFullName(), employee.getFullName());

        while(results.next()){
            Month m = Month.of((int) results.getDouble("month_number"));
            employee.setMonthGoal(m, results.getDouble("monthly_goal") );
            employee.setMonthHours(m, results.getDouble("totalhrs"));

        }
    }

    @Override
    public Employee grabEmployeeFromStorage(String employeeName) {
        Employee employee = null;
        try{ String sql = "SELECT * FROM employees WHERE employee_name ILIKE ?;";
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, employeeName);

            if (results.next()){
                employee = mapRowToEmployee(results);
                mapRowToEmployeeGoalProgress(employee);
            }
        } catch (NullPointerException e){
            System.out.println("An employee with that name was not found in the database.");
        }
        return employee;
    }

    @Override
    public boolean employeeExists(String nameToSearch){
        String sql = "SELECT employee_name FROM employees WHERE employee_name ILIKE ?;";
        return jdbcTemplate.queryForRowSet(sql, nameToSearch).next();
    }

    @Override
    public List<Employee> getAllEmployees() {
        List<Employee> allEmployees = new ArrayList<>();
        String sql = "SELECT * FROM employees;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()){
            Employee employee = mapRowToEmployee(results);
            mapRowToEmployeeGoalProgress(employee);
            allEmployees.add(employee);
        }
        return allEmployees;
    }

    @Override
    public void saveNewEmployee(Employee employee) {
        String sql = "INSERT INTO employees (employee_name) VALUES (?); ";
        jdbcTemplate.update(sql, employee.getFullName());
        int i = 1;
        String sql2 = "INSERT INTO goals (employee_id, month_number, goal_amt) " +
                "VALUES ((SELECT employee_id FROM employees WHERE employee_name = ?),?,?);";
        //create new blank log entry for each month so that it will exist for other reports (defaults to 0.0)
        String sql3 = "INSERT INTO logprogress (employee_id, month_number, date_of_log)\n" +
                "VALUES ((SELECT employee_id FROM employees WHERE employee_name ILIKE ?), ?, (SELECT CURRENT_DATE));";
        while (i <= 12){
            jdbcTemplate.update(sql2, employee.getFullName(), i, employee.getMonthGoal(Month.of(i)));
            jdbcTemplate.update(sql3, employee.getFullName(), i);
            i++;
        }
    }
    @Override
    public void updateSingleGoal(Employee employee, Month month, double newGoal) {
        String sql = "UPDATE goals \n" +
                "SET goal_amt = ?\n" +
                "WHERE month_number = ? AND employee_id = (SELECT employee_id FROM employees WHERE employee_name ILIKE ?);";
        jdbcTemplate.update(sql, newGoal, month.getValue(), employee.getFullName());
    }

    @Override
    public void updateEmployeeGoals(Employee employee) {
        int i = 1;
        while (i <= 12){
            updateSingleGoal(employee, Month.of(i), employee.getMonthGoal(Month.of(i)));
            i++;
        }
    }

    @Override
    public void updateEmployeeHours(Employee employee) {
        int i = 1;
        while (i <= 12){
            updateSingleGoal(employee, Month.of(i), employee.getMonthHours(Month.of(i)));
            i++;
        }
    }

    @Override
    public void updateSingleMonthLog(Employee employee, Month month, double newHours) {
        String sql = "UPDATE logprogress\n" +
                "SET hours_logged = ?\n" +
                "WHERE month_number = ? AND employee_id = (SELECT employee_id FROM employees WHERE employee_name ILIKE ?);";
        jdbcTemplate.update(sql, newHours, month.getValue(), employee.getFullName());
    }

    @Override
    public void logHours(Employee employee, Month month, double hoursToLog) {
        //determine current balance for month from db
        Double currentMonthHours = getCurrentLogProgressForMonth(employee, month);
        //determine new total after logging and update the db with it
        Double newMonthTotal = hoursToLog + currentMonthHours;
        String sql = "UPDATE logprogress\n" +
                "SET hours_logged = ?\n" +
                "WHERE month_number = ? AND employee_id = (SELECT employee_id FROM employees WHERE employee_name ILIKE ?);";
        jdbcTemplate.update(sql, newMonthTotal, month.getValue(), employee.getFullName());
    }

    @Override
    public Double getCurrentLogProgressForMonth(Employee employee, Month month) {
        String sql = "SELECT hours_logged \n" +
                "FROM logprogress\n" +
                "WHERE month_number = ? AND employee_id = (SELECT employee_id FROM employees WHERE employee_name ILIKE ?);";
        Double currentMonthHours = null;
        SqlRowSet queryForHours = jdbcTemplate.queryForRowSet(sql, month.getValue(), employee.getFullName());
        if (queryForHours.next()){
            currentMonthHours = queryForHours.getDouble("hours_logged");
        }
        return currentMonthHours;
    }

    public Double getGoalForMonth(Employee employee, Month month) {
        String sql = "SELECT goal_amt \n" +
                "FROM goals\n" +
                "WHERE month_number = ? AND employee_id = (SELECT employee_id FROM employees WHERE employee_name ILIKE ?);";
        Double monthGoal = null;
        SqlRowSet queryForGoal = jdbcTemplate.queryForRowSet(sql, month.getValue(), employee.getFullName());
        if (queryForGoal.next()){
            monthGoal = queryForGoal.getDouble("goal_amt");
        }
        return monthGoal;
    }


    @Override
    public Double getHoursLeftTillGoal(Employee employee) {
        Double hoursLogged = getCurrentLogProgressForMonth(employee, LocalDate.now().getMonth());
        Double goalForMonth = getGoalForMonth(employee, LocalDate.now().getMonth());
        return goalForMonth - hoursLogged;
    }
}
