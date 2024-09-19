import DAO.WalletDAO;
import DAO.WalletDAOImpl;
import DAO.TransactionDAO;
import DAO.TransactionDAOImpl;
import DAO.UserDAOImpl;
import Service.ReportService;
import beans.*;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        SessionFactory sf = new Configuration().configure().buildSessionFactory();
        Session s = sf.openSession();

        try {
            UserDAOImpl userDAO = new UserDAOImpl();
            boolean running = true;

            User user = null;
            while (running) {
                System.out.println("1. Log In");
                System.out.println("2. Register");
                System.out.println("3. Exit");

                System.out.print("Welcome to your Digital Wallet! Please choose an option (1,2 or 3): ");
                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1:
                        // Login
                        boolean loggedIn = false;

                        while (!loggedIn) {
                            System.out.print("Enter Username: ");
                            String username = sc.nextLine();
                            System.out.print("Enter Password: ");
                            String password = sc.nextLine();

                            user = userDAO.findUserByUsername(username);
                            if (user != null && user.getPassword().equals(password)) {
                                loggedIn = true;
                                System.out.println();
                                System.out.println("Login successful!");
                                System.out.println();

                                // After Login: Menu for choosing an action
                                System.out.println("1. Check Your Wallet Information");
                                System.out.println("2. Perform a Transaction");
                                System.out.println("3. Display Last Transactions");
                                System.out.println("4. Update Your Email");
                                System.out.println("5. Delete Your Account");
                                System.out.println("6. Exit/Logout");

                                System.out.print("What would you like to do? Choose from the above menu: ");
                                int action = sc.nextInt();
                                sc.nextLine();

                                switch (action) {
                                    case 1:
                                        // Implemented wallet info check
                                        WalletDAO walletInfo = new WalletDAOImpl();
                                        Wallet userWallet=walletInfo.findWalletByUserId(user.getId());
                                        if (userWallet != null) {
                                            System.out.println("Wallet Balance: " + userWallet.getBalance());
                                            System.out.println("Wallet ID: " + userWallet.getId());
                                        } else {
                                            System.out.println("No wallet information found for the user.");
                                        }
                                        break;
                                    case 2:
                                        Transaction tx = s.beginTransaction();
                                        // Implemented transaction process
                                        Category category = new Category();
                                        System.out.print("Enter Category of Expense: ");
                                        String c = sc.nextLine();
                                        category.setName(c);
                                        s.save(category);

                                        Transactions transaction = new Transactions();
                                        System.out.print("Enter Transaction Amount: ");
                                        double a = sc.nextDouble();
                                        sc.nextLine();
                                        transaction.setAmount(a);

                                        transaction.setDate(new java.util.Date()); // Set to current date/time

                                        transaction.setDescription(c); //Transaction Description

                                        System.out.print("Receiver's Name: ");
                                        String madeTo = sc.nextLine();
                                        transaction.setMadeTo(madeTo);

                                        System.out.print("Is this a Credit or Debit?: ");
                                        String t = sc.nextLine();
                                        transaction.setType(t);

                                        Wallet walletFromDB = s.get(Wallet.class, user.getWallet().getId());
                                        Hibernate.initialize(walletFromDB.getTransactions()); // Initialize transactions to avoid LazyInitializationException

                                        if (t.equalsIgnoreCase("credit")) {
                                            walletFromDB.setBalance(walletFromDB.getBalance() + a); // Add amount to wallet
                                        } else if (t.equalsIgnoreCase("debit")) {
                                            if (walletFromDB.getBalance() >= a) {
                                                walletFromDB.setBalance(walletFromDB.getBalance() - a); // Subtract amount from wallet
                                            } else {
                                                System.out.println("Insufficient balance for this transaction.");
                                                return; // Exit if not enough balance
                                            }
                                        }

                                        // Set transaction details and save
                                        transaction.setCategory(category);
                                        transaction.setWallet(walletFromDB);
                                        s.save(transaction);
                                        s.update(walletFromDB);
                                        tx.commit();

                                        System.out.println();
                                        System.out.println("Transaction saved successfully!");
                                        System.out.println();

                                        ReportService rs = new ReportService();
                                        rs.generateReport(user.getId());
                                        break;
                                    case 3:
                                        TransactionDAO transactionDAO = new TransactionDAOImpl();
                                        List<Transactions> lastTransactions = transactionDAO.getTransactionByWallet(user.getWallet().getId());

                                        if (lastTransactions == null || lastTransactions.isEmpty()) {
                                            System.out.println("No transactions found for this wallet.");
                                        } else {
                                            System.out.println("Last Transactions:");
                                            for (Transactions tx1 : lastTransactions) {
                                                System.out.println(tx1);
                                            }
                                        }
                                        break;
                                    case 4:
                                        // Update Email
                                        System.out.print("Enter new email: ");
                                        String newEmail = sc.nextLine();
                                        userDAO.updateUserEmail(user.getId(), newEmail);
                                        break;
                                    case 5:
                                        // Delete Account
                                        System.out.print("Are you sure you want to delete your account? (yes/no): ");
                                        String confirm = sc.nextLine();
                                        if (confirm.equalsIgnoreCase("yes")) {
                                            userDAO.deleteUser(user.getId());
                                            System.out.println("Account deleted successfully. Exiting...");
                                            running = false;
                                            loggedIn = false;
                                        }
                                        break;
                                    case 6:
                                        System.out.println("Exiting...");
                                        running = false;
                                        break;
                                    default:
                                        System.out.println("Invalid action. Please try again.");
                                }
                            } else {
                                System.out.println("Invalid credentials. Please try again.");
                                System.out.print("Would you like to try again or go back to the main menu? (try/back): ");
                                String choiceAgain = sc.nextLine();
                                if (choiceAgain.equalsIgnoreCase("back")) {
                                    break;
                                }
                            }
                        }
                        break;

                    case 2:
                        Transaction tx = s.beginTransaction();
                        // Register
                        System.out.print("Enter Username: ");
                        String newUsername = sc.nextLine();
                        System.out.print("Enter Password: ");
                        String newPassword = sc.nextLine();
                        System.out.print("Enter Email: ");
                        String newEmail = sc.nextLine();

                        // Register user
                        int userId = userDAO.register(newUsername, newPassword, newEmail);
                        System.out.println();
                        System.out.println("Registration successful! Your unique ID is: " + userId);

                        // Set up Wallet and Spending Limit for the new user
                        Wallet wallet = new Wallet();
                        System.out.println();
                        System.out.print("Enter Wallet Balance: ");
                        double initialBalance = sc.nextDouble();
                        sc.nextLine();
                        wallet.setBalance(initialBalance);
                        wallet.setUser(userDAO.findUserById(userId)); // Attach wallet to the user

                        // Save the wallet
                        s.save(wallet);

                        SpendingLimit spendingLimit = new SpendingLimit();
                        System.out.println();
                        System.out.print("Enter Spending Limit: ");
                        double limitAmount = sc.nextDouble();
                        sc.nextLine();

                        spendingLimit.setLimitAmount(limitAmount);
                        spendingLimit.setWallet(wallet); // Attach spending limit to the wallet
                        s.save(spendingLimit);

                        System.out.println();
                        System.out.println("Spending Limit saved successfully!");

                        // Save the user with the wallet and spending limit
                        User registeredUser = userDAO.findUserById(userId);
                        registeredUser.setWallet(wallet); // Update user with wallet

                        s.update(registeredUser);
                        tx.commit();

                        System.out.println();
                        System.out.println("Congratulations User, Wallet, and Spending Limit saved successfully!");
                        break;

                    case 3:
                        // Exit
                        System.out.println("Exiting...");
                        running = false;
                        break;

                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            s.close();
            sf.close();
        }
    }
}

//import DAO.WalletDAO;
//import DAO.WalletDAOImpl;
//import DAO.TransactionDAO;
//import DAO.TransactionDAOImpl;
//import DAO.UserDAOImpl;
//import Service.ReportService;
//import beans.*;
//import org.hibernate.Hibernate;
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.hibernate.Transaction;
//import org.hibernate.cfg.Configuration;
//
//import java.util.List;
//import java.util.Scanner;
//
//public class Main {
//    public static void main(String[] args) {
//
//        Scanner sc = new Scanner(System.in);
//
//        SessionFactory sf = new Configuration().configure().buildSessionFactory();
//        Session s = sf.openSession();
//        Transaction tx = s.beginTransaction();
//
//        try {
//            UserDAOImpl userDAO = new UserDAOImpl();
//            boolean running = true;
//
//            User user = null;
//            while (running) {
//                System.out.println("1. Log In");
//                System.out.println("2. Register");
//                System.out.println("3. Exit");
//
//                System.out.print("Welcome to your Digital Wallet! Please choose an option (1,2 or 3): ");
//                int choice = sc.nextInt();
//                sc.nextLine();
//
//                switch (choice) {
//                    case 1:
//                        // Login
//                        boolean loggedIn = false;
//
//                        while (!loggedIn) {
//                            System.out.print("Enter Username: ");
//                            String username = sc.nextLine();
//                            System.out.print("Enter Password: ");
//                            String password = sc.nextLine();
//
//                            user = userDAO.findUserByUsername(username);
//                            if (user != null && user.getPassword().equals(password)) {
//                                loggedIn = true;
//                                System.out.println();
//                                System.out.println("Login successful!");
//                                System.out.println();
//
//                                // After Login: Menu for choosing an action
//                                System.out.println("1. Check Your Wallet Information");
//                                System.out.println("2. Perform a Transaction");
//                                System.out.println("3. Display Last Transactions");
//                                System.out.println("4. Update Your Email");
//                                System.out.println("5. Delete Your Account");
//                                System.out.println("6. Exit/Logout");
//
//                                System.out.print("What would you like to do? Choose from the above menu: ");
//                                int action = sc.nextInt();
//                                sc.nextLine();
//
//                                switch (action) {
//                                    case 1:
//                                        // Implemented wallet info check
//                                        WalletDAO walletInfo = new WalletDAOImpl();
//                                        Wallet userWallet=walletInfo.findWalletByUserId(user.getId());
//                                        if (userWallet != null) {
//                                            System.out.println("Wallet Balance: " + userWallet.getBalance());
//                                            System.out.println("Wallet ID: " + userWallet.getId());
//                                        } else {
//                                            System.out.println("No wallet information found for the user.");
//                                        }
//                                        break;
//                                    case 2:
//                                        // Implemented transaction process
//                                        Category category = new Category();
//                                        System.out.print("Enter Category of Expense: ");
//                                        String c = sc.nextLine();
//                                        category.setName(c);
//                                        s.save(category);
//
//                                        Transactions transaction = new Transactions();
//                                        System.out.print("Enter Transaction Amount: ");
//                                        double a = sc.nextDouble();
//                                        sc.nextLine();
//                                        transaction.setAmount(a);
//
//                                        transaction.setDate(new java.util.Date()); // Set to current date/time
//
//                                        transaction.setDescription(c); //Transaction Description
//
//                                        System.out.print("Receiver's Name: ");
//                                        String madeTo = sc.nextLine();
//                                        transaction.setMadeTo(madeTo);
//
//                                        System.out.print("Is this a Credit or Debit?: ");
//                                        String t = sc.nextLine();
//                                        transaction.setType(t);
//
//                                        Wallet walletFromDB = s.get(Wallet.class, user.getWallet().getId());
//                                        Hibernate.initialize(walletFromDB.getTransactions()); // Initialize transactions to avoid LazyInitializationException
//
//                                        if (t.equalsIgnoreCase("credit")) {
//                                            walletFromDB.setBalance(walletFromDB.getBalance() + a); // Add amount to wallet
//                                        } else if (t.equalsIgnoreCase("debit")) {
//                                            if (walletFromDB.getBalance() >= a) {
//                                                walletFromDB.setBalance(walletFromDB.getBalance() - a); // Subtract amount from wallet
//                                            } else {
//                                                System.out.println("Insufficient balance for this transaction.");
//                                                return; // Exit if not enough balance
//                                            }
//                                        }
//
//                                        // Set transaction details and save
//                                        transaction.setCategory(category);
//                                        transaction.setWallet(walletFromDB);
//                                        s.save(transaction);
//                                        s.update(walletFromDB);
//                                        tx.commit();
//
//                                        System.out.println();
//                                        System.out.println("Transaction saved successfully!");
//                                        System.out.println();
//                                        ReportService rs = new ReportService();
//                                        rs.generateReport(user.getId());
//                                        break;
//                                    case 3:
//                                        TransactionDAO transactionDAO = new TransactionDAOImpl();
//                                        List<Transactions> lastTransactions = transactionDAO.getTransactionByWallet(user.getWallet().getId());
//
//                                        if (lastTransactions == null || lastTransactions.isEmpty()) {
//                                            System.out.println("No transactions found for this wallet.");
//                                        } else {
//                                            System.out.println("Last Transactions:");
//                                            for (Transactions tx1 : lastTransactions) {
//                                                System.out.println(tx1);  // Ensure Transactions has a proper toString() method
//                                            }
//                                        }
//                                        break;
//                                    case 4:
//                                        // Update Email
//                                        System.out.print("Enter new email: ");
//                                        String newEmail = sc.nextLine();
//                                        userDAO.updateUserEmail(user.getId(), newEmail);
//                                        break;
//                                    case 5:
//                                        // Delete Account
//                                        System.out.print("Are you sure you want to delete your account? (yes/no): ");
//                                        String confirm = sc.nextLine();
//                                        if (confirm.equalsIgnoreCase("yes")) {
//                                            userDAO.deleteUser(user.getId());
//                                            System.out.println("Account deleted successfully. Exiting...");
//                                            running = false;
//                                            //loggedInMenu = false;
//                                        }
//                                        break;
//                                    case 6:
//                                        System.out.println("Exiting...");
//                                        running = false;
//                                        break;
//                                    default:
//                                        System.out.println("Invalid action. Please try again.");
//                                }
//                            } else {
//                                System.out.println("Invalid credentials. Please try again.");
//                                System.out.print("Would you like to try again or go back to the main menu? (try/back): ");
//                                String choiceAgain = sc.nextLine();
//                                if (choiceAgain.equalsIgnoreCase("back")) {
//                                    break;
//                                }
//                            }
//                        }
//                        break;
//
//                    case 2:
//                        // Register
//                        System.out.print("Enter Username: ");
//                        String newUsername = sc.nextLine();
//                        System.out.print("Enter Password: ");
//                        String newPassword = sc.nextLine();
//                        System.out.print("Enter Email: ");
//                        String newEmail = sc.nextLine();
//
//                        // Register user
//                        int userId = userDAO.register(newUsername, newPassword, newEmail);
//                        System.out.println();
//                        System.out.println("Registration successful! Your unique ID is: " + userId);
//
//                        // Set up Wallet and Spending Limit for the new user
//                        Wallet wallet = new Wallet();
//                        System.out.println();
//                        System.out.print("Enter Wallet Balance: ");
//                        double initialBalance = sc.nextDouble();
//                        sc.nextLine();
//                        wallet.setBalance(initialBalance);
//                        wallet.setUser(userDAO.findUserById(userId)); // Attach wallet to the user
//
//                        // Save the wallet
//                        s.save(wallet);
//
//                        SpendingLimit spendingLimit = new SpendingLimit();
//                        System.out.println();
//                        System.out.print("Enter Spending Limit: ");
//                        double limitAmount = sc.nextDouble();
//                        sc.nextLine();
//
//                        spendingLimit.setLimitAmount(limitAmount);
//                        spendingLimit.setWallet(wallet); // Attach spending limit to the wallet
//                        s.save(spendingLimit);
//
//                        System.out.println();
//                        System.out.println("Spending Limit saved successfully!");
//
//                        // Save the user with the wallet and spending limit
//                        User registeredUser = userDAO.findUserById(userId);
//                        registeredUser.setWallet(wallet); // Update user with wallet
//
//                        s.update(registeredUser);
//                        tx.commit();
//
//                        System.out.println();
//                        System.out.println("Congratulations User, Wallet, and Spending Limit saved successfully!");
//                        break;
//
//                    case 3:
//                        // Exit
//                        System.out.println("Exiting...");
//                        running = false;
//                        break;
//
//                    default:
//                        System.out.println("Invalid choice. Please try again.");
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            s.close();
//            sf.close();
//        }
//    }
//}