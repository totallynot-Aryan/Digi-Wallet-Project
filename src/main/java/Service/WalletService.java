package Service;

import beans.*;
import DAO.*;
import org.hibernate.Hibernate;
import util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.transaction.Transactional;

public class WalletService {
    private UserDAO userDAO;
    private WalletDAO walletDAO;
    private TransactionDAO transactionDAO;

    public WalletService(UserDAO userDAO, WalletDAO walletDAO, TransactionDAO transactionDAO) {
        this.userDAO = userDAO;
        this.walletDAO = walletDAO;
        this.transactionDAO = transactionDAO;
    }

    @Transactional
    public Wallet getWalletWithTransactions(int walletId) {
        Wallet wallet = walletDAO.findWalletByUserId(walletId);
        Hibernate.initialize(wallet.getTransactions());
        return wallet;
    }

    public void addTransaction(int userId, Transactions transactions) {

        // Begin transaction
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();

            // Fetch user and wallet details
            User user = userDAO.findUserById(userId);
            Wallet wallet = user.getWallet();

            // Update wallet balance
            if (transactions.getType().equalsIgnoreCase("income")) {
                wallet.setBalance(wallet.getBalance() + transactions.getAmount());
            } else {
                wallet.setBalance(wallet.getBalance() - transactions.getAmount());
            }

            // Save the transaction
            transactions.setWallet(wallet);
            transactionDAO.saveTransaction(transactions);

            // Commit transaction
            tx.commit();
        }
    }
}
