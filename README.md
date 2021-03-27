# BillableHoursTracker
App to track billable hours for one or more employees with annual charge hour goals

An app for employees who have a goal for a certain number of billable hours to be logged per month, and per year.

Employees can be created to be tracked in the system, and assigned monthly goals. The assigning of monthly goals can either be done based on a goal 'template' in which a typical annual spread is calculated from their annual goal, or each month can be provided for a given employee.

Billable hours can be looged for any tracked employee, which accumulate for the month and are stored in a database, to be referenced against their goals.
Goal progress is considered persistent throughout they year, meaning that an employee who falls 10 hours of one month, will have 10 hours added to their goal for the next (this is handled with a routine that runs once per month).

Various business inteligence reports are provided to help employees stay on track to meet their goals, including a monthly report that accounts for how much time is left in the month, and how many hours are left of that month's goal to let the employee know how many hours per week they need to log in order to meet it before the month ends. 

This app currently functions in the command line, but is still in progress in that I plan to implement further functionality, write a graphical front end, and deploy it to Heroku. 




TODO: Add 'Tax season' projection report for hour goals /n
TODO: Add tracking for how much an employee exceeded or fell short of their goal each month. This is purely for reporting purposes. /n
TODO: Write API to be called from a front-end /n
TODO: Write gui front-end /n
TODO: Add SQL script to produce a 'clean' database /n
TODO: Deploy to Heroku /n

