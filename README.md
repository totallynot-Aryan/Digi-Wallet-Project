Digital Wallet Project
Overview
This Digital Wallet application is built using Java, Hibernate, and MySQL, following a layered architecture with DAO, Service, and Controller layers. It allows users to securely manage wallet transactions, set spending limits, and track expenses. The project implements various features with a focus on security, modular design, and modern development practices.

Features
User Login: Secure login using username and password.
Wallet Management: Create, update, view, and delete wallets.
Transaction Handling: Add, update, view, and manage transactions (credit and debit).
Spending Limits: Set and enforce spending limits based on categories.
Transaction Categories: Automatically categorize transactions and generate reports.
Report Generation: Generate transaction reports for each user.
Spending Over Limit Alerts: Alert and deny transactions when spending exceeds set limits.

Project Structure
beans: Contains entity classes like User, Wallet, Transactions, Category, and SpendingLimit.
DAO: Data Access Object interfaces and implementations.
UserDAO, UserDAOImpl
WalletDAO, WalletDAOImpl
TransactionDAO, TransactionDAOImpl
Service: Business logic services such as WalletService and ReportService.
util: Utility classes like HibernateUtil for session management.
ExceptionHandling: Custom exceptions for database and application errors.
Main: Contains the main class for launching the application.

Setup and Installation
Prerequisites
Java 11+
Maven 3.x
MySQL Server
MySQL Workbench

Steps to Run
Clone the repository:
git clone https://github.com/your-username/digital-wallet.git
cd digital-wallet

Configure MySQL Database:
Create a MySQL database named digital_wallet.
Update your database credentials in hibernate.cfg.xml.

Build the project:
mvn clean install

Run the project:
mvn exec:java -Dexec.mainClass="Main"

Database Configuration
Ensure that hibernate.cfg.xml is correctly set up to connect to your MySQL database:
<property name="hibernate.connection.url">jdbc:mysql://localhost:3306/digital_wallet</property>
<property name="hibernate.connection.username">your-username</property>
<property name="hibernate.connection.password">your-password</property>

Usage

User Operations:
Register a new user with a username, email, and password.
Log in with valid credentials.

Wallet Operations:
View wallet balance.
Add or deduct funds.
Automatically update balance upon transactions.

Transaction Management:
Record credit and debit transactions.
View the last 5 transactions for a logged-in user.

Spending Limit:
Set spending limits based on categories.
Alerts are triggered if spending exceeds the limit.

Future Enhancements
UI Integration: Planning to add a modern UI using front-end frameworks.
Spring Boot Integration: Option to include Spring Boot for better modularization and web services.

Contributing
Feel free to contribute by submitting a pull request. For major changes, please open an issue first to discuss what you would like to change.
