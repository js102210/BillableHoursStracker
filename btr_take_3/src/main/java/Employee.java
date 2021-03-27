import com.sun.source.tree.DoWhileLoopTree;

import java.time.Month;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Employee {

    private String fullName;


    private Long employee_id;

    private Double janGoal = 0.0;
    private Double febGoal = 0.0;
    private Double marGoal = 0.0;
    private Double aprGoal = 0.0;
    private Double mayGoal = 0.0;
    private Double junGoal = 0.0;
    private Double julGoal = 0.0;
    private Double augGoal = 0.0;
    private Double sepGoal = 0.0;
    private Double octGoal = 0.0;
    private Double novGoal = 0.0;
    private Double decGoal = 0.0;



    private Double janHoursLogged = 0.0;
    private Double febHoursLogged = 0.0;
    private Double marHoursLogged = 0.0;
    private Double aprHoursLogged = 0.0;
    private Double mayHoursLogged = 0.0;
    private Double junHoursLogged = 0.0;
    private Double julHoursLogged = 0.0;
    private Double augHoursLogged = 0.0;
    private Double sepHoursLogged = 0.0;
    private Double octHoursLogged = 0.0;
    private Double novHoursLogged = 0.0;
    private Double decHoursLogged = 0.0;



    private Map<Month, Double> goalsByMonth = new HashMap<Month, Double>();
    private Map<Month, Double> hoursByMonth = new HashMap<Month, Double>();

    //generic constructor to be called ONLY by DAO
    Employee() {
    this.hoursByMonth = new HashMap<>();
    this.goalsByMonth = new HashMap<>();
    }
    public Long getEmployee_id() {
        return employee_id;
    }


    public void saveGoalsToMap(){
        //store monthly goals and associate them with months by storing them in a map
        this.goalsByMonth.put(Month.JANUARY, this.janGoal);
        this.goalsByMonth.put(Month.FEBRUARY, this.febGoal);
        this.goalsByMonth.put(Month.MARCH, this.marGoal);
        this.goalsByMonth.put(Month.APRIL, this.aprGoal);
        this.goalsByMonth.put(Month.MAY, this.mayGoal);
        this.goalsByMonth.put(Month.JUNE, this.junGoal);
        this.goalsByMonth.put(Month.JULY, this.julGoal);
        this.goalsByMonth.put(Month.AUGUST, this.augGoal);
        this.goalsByMonth.put(Month.SEPTEMBER, this.sepGoal);
        this.goalsByMonth.put(Month.OCTOBER, this.octGoal);
        this.goalsByMonth.put(Month.NOVEMBER, this.novGoal);
        this.goalsByMonth.put(Month.DECEMBER, this.decGoal);
    }
    public void saveHoursLoggedToMap(){
        //store hours logged and associate them with months by storing them in a map
        this.hoursByMonth.put(Month.JANUARY, this.janHoursLogged);
        this.hoursByMonth.put(Month.FEBRUARY, this.febHoursLogged);
        this.hoursByMonth.put(Month.MARCH, this.marHoursLogged);
        this.hoursByMonth.put(Month.APRIL, this.aprHoursLogged);
        this.hoursByMonth.put(Month.MAY, this.mayHoursLogged);
        this.hoursByMonth.put(Month.JUNE, this.junHoursLogged);
        this.hoursByMonth.put(Month.JULY, this.julHoursLogged);
        this.hoursByMonth.put(Month.AUGUST, this.augHoursLogged);
        this.hoursByMonth.put(Month.SEPTEMBER, this.sepHoursLogged);
        this.hoursByMonth.put(Month.OCTOBER, this.octHoursLogged);
        this.hoursByMonth.put(Month.NOVEMBER, this.novHoursLogged);
        this.hoursByMonth.put(Month.DECEMBER, this.decHoursLogged);
    }

    //constructor for "generic" employee whose goals are set methodically (need to fill in the month goals with the correct percentages)
    public Employee(String fullName, double annualBillableHoursGoal){
        this.fullName = fullName;
        //derive monthly goals from annual
        this.janGoal = annualBillableHoursGoal * 0.072;
        this.febGoal = annualBillableHoursGoal* 0.13;
        this.marGoal = annualBillableHoursGoal* 0.147;
        this.aprGoal = annualBillableHoursGoal * 0.1;
        this.mayGoal = annualBillableHoursGoal * 0.072;
        this.junGoal = annualBillableHoursGoal * 0.067;
        this.julGoal = annualBillableHoursGoal* 0.067;
        this.augGoal = annualBillableHoursGoal* 0.078;
        this.sepGoal = annualBillableHoursGoal* 0.092;
        this.octGoal = annualBillableHoursGoal * 0.075;
        this.novGoal = annualBillableHoursGoal * 0.056;
        this.decGoal = annualBillableHoursGoal * 0.044;
        this.saveGoalsToMap();
    }

    //constructor for an existing employee with goals set that deviate from the "standard" template

    public Employee (String fullName,  Double janGoal, Double febGoal, Double marGoal, Double aprGoal,
                     Double mayGoal, Double junGoal, Double julGoal, Double augGoal, Double sepGoal, Double octGoal, Double novGoal, Double decGoal){
        this.fullName = fullName;
        this.janGoal = janGoal;
        this.febGoal = febGoal;
        this.marGoal = marGoal;
        this.aprGoal = aprGoal;
        this.mayGoal = mayGoal;
        this.junGoal = junGoal;
        this.julGoal = julGoal;
        this.augGoal = augGoal;
        this.sepGoal = sepGoal;
        this.octGoal = octGoal;
        this.novGoal = novGoal;
        this.decGoal = decGoal;
        //store monthly goals and associate them with months by storing them in a map
        this.saveGoalsToMap();
    }

    //getters and setters
    public String getFullName() {
        return this.fullName;
    }
    public void setFullName(String fullName){
        this.fullName = fullName;
    }
    public Double getAnnualBillableHoursGoal() {
        return this.getMonthGoal(Month.JANUARY) +  this.getMonthGoal(Month.FEBRUARY) +  this.getMonthGoal(Month.MARCH)
                +  this.getMonthGoal(Month.APRIL) +  this.getMonthGoal(Month.MAY)
                +  this.getMonthGoal(Month.JUNE) +  this.getMonthGoal(Month.JULY)
                +  this.getMonthGoal(Month.AUGUST) +  this.getMonthGoal(Month.SEPTEMBER)
                +  this.getMonthGoal(Month.OCTOBER) +  this.getMonthGoal(Month.NOVEMBER) +  this.getMonthGoal(Month.DECEMBER);
    }
    //a getter to get the month goal using either a string or the string name parsed as a month (for convenience)
    public Double getMonthGoal(String month){
        return this.goalsByMonth.get(Month.valueOf(month));
    }
    public Double getMonthGoal(Month month){
        return this.goalsByMonth.get(month);
    }
    public Double getMonthHours(String month){
        return this.hoursByMonth.get(Month.valueOf(month));
    }
    public Double getMonthHours(Month month){
        return this.hoursByMonth.get(month);
    }
    public void setEmployee_id(Long employee_id){
        this.employee_id = employee_id;
    }

    //reset employee's goals but leave hours as they were
    public void resetGoals() {
        this.janGoal = 0.0;
        this.febGoal = 0.0;
        this.marGoal = 0.0;
        this.aprGoal = 0.0;
        this.mayGoal = 0.0;
        this.junGoal = 0.0;
        this.julGoal = 0.0;
        this.augGoal = 0.0;
        this.sepGoal = 0.0;
        this.octGoal = 0.0;
        this.novGoal = 0.0;
        this.decGoal = 0.0;
        this.saveGoalsToMap();

    }
    //set an employee's goals and hours logged back to zero.
    public void resetGoalsAndHoursLogged(){
        this.resetGoals();
        this.janHoursLogged = 0.0;
        this.febHoursLogged = 0.0;
        this.marHoursLogged = 0.0;
        this.aprHoursLogged = 0.0;
        this.mayHoursLogged = 0.0;
        this.junHoursLogged = 0.0;
        this.julHoursLogged = 0.0;
        this.augHoursLogged = 0.0;
        this.sepHoursLogged = 0.0;
        this.octHoursLogged = 0.0;
        this.novHoursLogged = 0.0;
        this.decHoursLogged = 0.0;
        this.saveHoursLoggedToMap();
    }

    public void setEmployeeGoalsToDefaults(Double annualBillableHoursGoal){

        //derive monthly goals from annual
        this.janGoal = annualBillableHoursGoal * 0.072;
        this.febGoal = annualBillableHoursGoal* 0.13;
        this.marGoal = annualBillableHoursGoal* 0.147;
        this.aprGoal = annualBillableHoursGoal * 0.1;
        this.mayGoal = annualBillableHoursGoal * 0.072;
        this.junGoal = annualBillableHoursGoal * 0.067;
        this.julGoal = annualBillableHoursGoal* 0.067;
        this.augGoal = annualBillableHoursGoal* 0.078;
        this.sepGoal = annualBillableHoursGoal* 0.092;
        this.octGoal = annualBillableHoursGoal * 0.075;
        this.novGoal = annualBillableHoursGoal * 0.056;
        this.decGoal = annualBillableHoursGoal * 0.044;
        this.saveGoalsToMap();
    }
    public Double getAnnualHoursLogged() {
        return this.getMonthHours(Month.JANUARY) + this.getMonthHours(Month.FEBRUARY) + this.getMonthHours(Month.MARCH)
                + this.getMonthHours(Month.APRIL)
                + this.getMonthHours(Month.MAY) + this.getMonthHours(Month.JUNE)
                + this.getMonthHours(Month.JULY) + this.getMonthHours(Month.AUGUST)
                + this.getMonthHours(Month.SEPTEMBER) + this.getMonthHours(Month.OCTOBER)
                + this.getMonthHours(Month.NOVEMBER) + this.getMonthHours(Month.DECEMBER);
    }

    public Double getMonthGoalDiff(Month month) {
        return this.getMonthGoal(month) - this.getMonthHours(month);
    }

    public Double getAnnualGoalDiff() {
        Double result = 0.0;
        for (int i = 1; i <=12; i++){
            result += this.getMonthGoalDiff(Month.of(i));
        }
        return result;
    }
    //logs hours to current month if no month specified
    //overloaded to take either month as string or parsed as month for convenience
    public void logHours (double hours) {
        Month currentMonth = Month.of(Calendar.getInstance().get(Calendar.MONTH));
        Double updatedHours = this.hoursByMonth.get(currentMonth) + hours;
        this.hoursByMonth.put(currentMonth, updatedHours);
    }
    public void logHours (double hours, String month) {
        Month targetMonth = Month.valueOf(month.toUpperCase());
        Double updatedHours = this.hoursByMonth.get(targetMonth) + hours;
        this.hoursByMonth.put(targetMonth, updatedHours);
    }
    public void logHours (double hours, Month month) {
        Double updatedHours = this.hoursByMonth.get(month) + hours;
        this.hoursByMonth.put(month, updatedHours);
    }

    public void setMonthGoal(String month, Double monthGoal){

        this.goalsByMonth.put(Month.valueOf(month), monthGoal);
    }
    public void setMonthGoal(Month month, Double monthGoal){
        this.goalsByMonth.put(month, monthGoal);
    }

    public void setMonthHours(Month month, Double monthHours){
        this.hoursByMonth.put(month, monthHours);
    }

    //carries balance (positive or negative) of the previous month's goal vs actual to the next month, unless the month provided is Dec (goals/progress reset each year)
    public void carryGoalBalance(Employee employee, Month month ) {
        if (month != Month.DECEMBER) {
            Month nextMonth = month.plus(1);
            Double balanceToCarry = employee.getMonthGoal(month.toString()) - employee.getMonthHours(month.toString());
            //balance should only carry over for positive balances (to encourage maximum sales activity), if an org wants to carry negative balances so that an
            //overshot goal reduces next month's goal, simply remove this 'if' statement from the below lines
            if (balanceToCarry > 0){
                Double newGoal = employee.getMonthGoal(nextMonth) + balanceToCarry;
                employee.setMonthGoal(nextMonth, newGoal);
            }

        }
    }


}


