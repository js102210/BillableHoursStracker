
--create database billablehourstrackerdb;
/*
DROP TABLE employees CASCADE
DROP TABLE goalbalances CASCADE
DROP TABLE months CASCADE */
BEGIN TRANSACTION;

DROP TABLE IF EXISTS employees;
CREATE TABLE employees 
(
        employee_id SERIAL,
        employee_name varchar(64),
        
        CONSTRAINT pk_employees PRIMARY KEY (employee_id)
);
/*
DROP TABLE IF EXISTS months;
CREATE TABLE months
(
        month_id int,
        month_name varchar(64),
        
        CONSTRAINT pk_months PRIMARY KEY (month_id)
); */
DROP TABLE IF EXISTS goals;
CREATE TABLE goals
(
        employee_id int,
        month_number int,
        goal_amt float,
        goal_diff varchar DEFAULT 'NOT YET CALCULATED',

        CONSTRAINT fk_months_employee_id FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
       
);


DROP TABLE IF EXISTS logprogress;
CREATE TABLE logprogress
(
        
        employee_id int,
        hours_logged float DEFAULT 0.0,
        month_number int,
        date_of_log date,
        
        CONSTRAINT fk_logprogress_employee_id FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
);


--INSERT INTO months (month_id, month_name)
--VALUES (1, 'JANUARY'),(2, 'FEBRUARY'),(3, 'MARCH'),(4, 'APRIL'),(5, 'MAY'),(6, 'JUNE'),(7, 'JULY'),(8, 'AUGUST'),(9, 'SEPTEMBER'),(10, 'OCTOBER'),(11, 'NOVEMBER'),(12, 'DECEMBER');


 --for testing
INSERT INTO employees (employee_name)
VALUES ('PLYNCH'); 

INSERT INTO goals (employee_id, month_number, goal_amt)
VALUES ((SELECT employee_id FROM employees WHERE employee_name = 'PLYNCH'),1,90),
        ((SELECT employee_id FROM employees WHERE employee_name = 'PLYNCH'),2,160),
        ((SELECT employee_id FROM employees WHERE employee_name = 'PLYNCH'),3,200),
        ((SELECT employee_id FROM employees WHERE employee_name = 'PLYNCH'),4,165),
        ((SELECT employee_id FROM employees WHERE employee_name = 'PLYNCH'),5,130),
        ((SELECT employee_id FROM employees WHERE employee_name = 'PLYNCH'),6,120),
        ((SELECT employee_id FROM employees WHERE employee_name = 'PLYNCH'),7,120),
        ((SELECT employee_id FROM employees WHERE employee_name = 'PLYNCH'),8,140),
        ((SELECT employee_id FROM employees WHERE employee_name = 'PLYNCH'),9,150),
        ((SELECT employee_id FROM employees WHERE employee_name = 'PLYNCH'),10,140),
        ((SELECT employee_id FROM employees WHERE employee_name = 'PLYNCH'),11,105),
        ((SELECT employee_id FROM employees WHERE employee_name = 'PLYNCH'),12,80);

--query to select all the goals and logged hours for an employee
SELECT employees.employee_name, goals.month_number, (SELECT SUM(hours_logged) FROM logprogress WH) as total_hrs, goals.goal_amt
FROM employees
LEFT JOIN logprogress ON logprogress.employee_id = employees.employee_id
LEFT JOIN goals ON employees.employee_id = goals.employee_id
WHERE employees.employee_name = 'jo'
GROUP BY goals.month_number, employees.employee_name


SELECT SUM(hours_logged), month_number
FROM logprogress
WHERE employee_id = (SELECT employee_id FROM employees WHERE employee_name = 'jschwartz')
GROUP BY logprogress.month_number


SELECT DISTINCT goals.month_number, SUM(goals.goal_amt)
FROM goals
WHERE goals.employee_id = (SELECT employee_id FROM employees WHERE employee_name = 'jo')
GROUP BY goals.month_number



--better query for selecting employee stuff-- get the join right so it shows all goals regardless of logged hrs
SELECT DISTINCT logprogress.month_number, SUM(logprogress.hours_logged) as totalhrs, (SELECT 'jim') as name, sum(goals.goal_amt) as monthly_goal
FROM logprogress
LEFT OUTER JOIN goals on logprogress.employee_id = goals.employee_id AND goals.month_number = logprogress.month_number
WHERE logprogress.employee_id = (SELECT employee_id FROM employees WHERE employee_name ILIKE 'jim') 
GROUP BY logprogress.month_number


SELECT * FROM goals
WHERE employee_id = (SELECT employee_id from employees WHERE employee_name ILIKE 'jschwartz');
/*SELECT month_name, employee_name, goal_amt, hours_logged
FROM employees
JOIN goalbalances ON employees.employee_id = goalbalances.employee_id
JOIN months ON months.month_id = goalbalances.month_id
WHERE employees.employee_name ilike 'plynch'*/

SELECT * FROM employees
WHERE employee_name = 'PLYNCH'


SELECT *
FROM employees;
SELECT *
FROM months;
SELECT *
FROM goalbalances;


SELECT months.month_name,


/*
SELECT employees.employee_id, months.month_name, employee_name, goal_amt, hours_logged
FROM employees
JOIN goalbalances ON employees.employee_id = goalbalances.employee_id
JOIN months ON months.month_id = goalbalances.month_id
GROUP BY employees.employee_id; */

BEGIN TRANSACTION;
ROLLBACK;

INSERT INTO employee (first_name, last_name, birth_date, gender, hire_date)
VALUES ('jon','smith','1995-01-01','m','1999-01-01')

--check if employee with name exists
SELECT employee_name FROM employees WHERE employee_name ILIKE 'plynch';

INSERT INTO employees (employee_name)
VALUES ('jschwartz');

INSERT INTO goals (employee_id, month_number, goal_amt) VALUES ((SELECT employee_id FROM employees WHERE employee_name = 'jschwartz'),2,20);

--create log entry for employee for a month, defaults to 0.0
INSERT INTO logprogress (employee_id, month_number, date_of_log)
VALUES ((SELECT employee_id FROM employees WHERE employee_name ILIKE 'plynch'), 12, (SELECT CURRENT_DATE));

BEGIN TRANSACTION;
ROLLBACK;
--updates a single month goal for an employee
UPDATE goals 
SET goal_amt = 25
WHERE month_number = 1 AND employee_id = (SELECT employee_id FROM employees WHERE employee_name ILIKE 'jschwartz');

SELECT hours_logged 
FROM logprogress
WHERE month_number = 1 AND employee_id = (SELECT employee_id FROM employees WHERE employee_name ILIKE 'jschwartz');


UPDATE logprogress
SET hours_logged = 5
WHERE month_number = 1 AND employee_id = (SELECT employee_id FROM employees WHERE employee_name ILIKE 'jschwartz');


SELECT SUM(hours_logged)
FROM logprogress
WHERE month_number = 1 AND employee_id = (SELECT employee_id FROM employees WHERE employee_name ILIKE 'plynch');

SELECT goal_amt
FROM goals
WHERE month_number = 3 AND employee_id = (SELECT employee_id FROM employees WHERE employee_name ILIKE 'jim');

ROLLBACK;