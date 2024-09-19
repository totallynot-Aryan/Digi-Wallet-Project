package DAO;

import beans.Wallet;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

public class WalletDAOImpl implements WalletDAO {

    @Override
    public void saveWallet(Wallet wallet) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = s.beginTransaction();
            s.save(wallet);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            s.close();
        }
    }

    @Override
    public Wallet findWalletByUserId(int userId) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Wallet wallet = null;
        try {
            wallet = (Wallet) s.createQuery("FROM Wallet WHERE user.id = :userId", Wallet.class)
                    .setParameter("userId", userId)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            s.close();
        }
        return wallet;
    }

    @Override
    public void updateWalletBalance(int walletID, double amount) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = s.beginTransaction();
            Wallet wallet = s.get(Wallet.class, walletID);
            wallet.updateBalance(amount); // Method to update balance
            s.update(wallet);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            s.close();
        }
    }

    public void displayWalletInfo(int userId) {
        Wallet wallet = findWalletByUserId(userId);
        if (wallet != null) {
            System.out.println("Wallet Balance: " + wallet.getBalance());
            System.out.println("Wallet ID: " + wallet.getId());
        } else {
            System.out.println("No wallet information found.");
        }
    }
}