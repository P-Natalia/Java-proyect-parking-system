this proyect is about a system that is required for a 24/7 central parking garage. There are monthly customers who leave their vehicles for as long as they wish for a monthly fee, and there are also vehicles that enter the garage by the hour.
In the garage, cars are left at the entrance and employees place them in an available space.
At the start of the system, it should be possible to access the main menu with:
Management:
1. Customer Management
2. Vehicle Management
3. Employee Management
4. Contract Management
Movements:
5. Inputs
6. Outputs
7. Additional Services
Miscellaneous:
8. Reports
9. Data Recovery
10. Data Recording
11. Mini-Game
12. Author Information
Terminate:
13. Exit
    Each one is described below:
1) Customer Management: You must be able to register and delete monthly customers.
  Enter their name, ID number (unique, no further validation is performed), address, cell phone number, and the year they have been customers.
 You must be able to view all customers in a list, and selecting one will load their information on the screen.
2) Vehicle Management: You must be able to register vehicles. Each vehicle has a license plate (must be unique, no further validation is performed), make, model, and condition (string).
 You must be able to view all vehicles in a list, and selecting one will load their information on the screen.
3) Employee Management: Register employees: name, ID number (unique, no further validation is performed), address, and employee number.
  You must be able to view the list of employees, and selecting one will display their information on the screen.
4) Contract Management: A vehicle can be associated with a registered customer. In this case, the employee who issued the contract and the monthly amount are indicated.
     Contracts are automatically numbered starting from 1. If the customer is deleted, their contract(s) are also automatically deleted.
  5) Entrances: Select a vehicle from a list (that is not already in the parking lot), enter the date, time (in HH:MM, 24-hour format), a note (for example, to note a flat tire),
 and select the employee who will receive and park the vehicle.
    The system must indicate whether they have a contract or not.
6) Exit: Select an entrance from a list (only entrances that have not been checked out are shown),
    select the employee who will bring the vehicle to the door from a list, record the date, time (in HH:MM, 24-hour format, and must be after the entrance), and a comment on the vehicle's condition.
    Enter the time (in hours and minutes) that the vehicle was there in relation to the entrance and whether they have a contract or not.
7) Additional Services: Additional services can be added to vehicles. Select the service type from a combo: "wash," "tire change," "upholstery cleaning," "light replacement," "other."
  Enter the date and time. Select the vehicle and employee performing the service from lists and the cost. You should be able to view all the services performed, including all their details.
Selecting one should display all the details on the screen.
  All contracts should be viewable, and when one is selected, their details should be displayed on the screen.
8) Reports. The following are displayed in a single window in different tabs:
a) Vehicle History: Select a vehicle and all its movements (inputs, outputs, additional services) are displayed in a table.
 You should be able to sort by date/time (increasing or decreasing) and also filter the type of movement you want to view.
You can also export a .txt file named after the license plate (e.g., "ABC123.txt") in the same project directory, containing each line presented in the table.
b) Movement Grid: You should be able to specify a date, and a grid of buttons with 4 rows (hours 0-5:59, 6:00 to 11:59, 12:00 to 17:59, and 18:00 to 23:59)
 and 3 columns (one for the requested day and two for the following two days) is displayed.
 Pressing each button displays a pop-up window, sorted by date/time, showing all transactions (entry, exit, additional service) during that period, indicating the license plate and type of transaction.
Each button in the grid should be green if it has fewer than 5 transactions, yellow if it has 5 to 8 transactions, and red if it has more than 8 transactions.
c) General statistics: most frequently used additional services, longest stays, employees with the fewest transactions, customers with the largest number of vehicles.
Note: For tab b) Movements, include a panel ("panelMovimientos") with a grid layout of 4 rows and 3 columns. Buttons can be created dynamically within repeating structures:
new JButton = new JButton(" ");
new.setMargin(new Insets(-5, -5, -5, -5));
new.setBackground(Color.BLACK);
new.setForeground(Color.WHITE);
new.setText("text"); // sample text, to be completed
new.addActionListener(new MovListener());
panelMovimientos.add(new);
Add the required imports (e.g., import java.awt.event.*, import java.awt.*, and import javax.swing.*).
Include this class in the window:
private class MovListener implements ActionListener {
public void actionPerformed(ActionEvent e) {
// This code will be executed when the button is pressed. I get which button.
JButton which = ((JButton) e.getSource());
// Code to complete based on the button pressed.
}
}
9) Data retrieval. Selecting Retrieval retrieves the system data serialized in DATOS.ser and replaces it with the current data upon confirmation. If there is no previous data, the system restarts.
10) Data recording. Selecting Recording immediately serializes all data to the DATOS.ser file upon confirmation. The file will be located in the same folder as the project.
11) Minigame. Having a Minigame provides a fun space to relax during times when there is no activity in the parking lot.
 Create an interactive minigame (such as avoiding obstacles or clicking on random places). It must include the use of a mouse and/or keyboard.
12) Exit. Exit the system after confirmation.
    Required:
Implement a system in Java to provide all the features presented. The interface must be Windows-style, in separate windows, and with the options presented.
All necessary validations must be performed. All information must be constantly updated on screen.
