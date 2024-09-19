package Service;

import DAO.TransactionDAO;
import DAO.TransactionDAOImpl;
import DAO.WalletDAO;
import DAO.WalletDAOImpl;
import beans.Transactions;
import beans.Wallet;
import java.util.List;

public class ReportService {

    private WalletDAO walletDAO;
    private TransactionDAO transactionsDAO;

    // Constructor to initialize DAOs
    public ReportService() {
        walletDAO = new WalletDAOImpl();
        transactionsDAO = new TransactionDAOImpl();
    }

    public void generateReport(int userId) {
        try {
            // Fetch the wallet associated with the user
            Wallet wallet = walletDAO.findWalletByUserId(userId);

            if (wallet != null) {
                // Fetch the transactions associated with the wallet
                List<Transactions> transactions = transactionsDAO.getTransactionByWallet(wallet.getId());

                // If transactions are found, print the report
                if (!transactions.isEmpty()) {
                    System.out.println("Transaction Report for User ID: " + userId);
                    System.out.println("Wallet Balance: " + wallet.getBalance());

                    // Iterate and print each transaction
                    for (Transactions transaction : transactions) {
                        System.out.println(transaction);
                    }
                } else {
                    System.out.println("No transactions found for the given user.");
                }
            } else {
                System.out.println("Wallet not found for the given user.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error generating the report.");
        }
    }
}
