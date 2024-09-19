package DAO;

import beans.Transactions;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.util.List;

public class TransactionDAOImpl implements TransactionDAO {

    @Override
    public void saveTransaction(Transactions transactions) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = s.beginTransaction();
        s.save(transactions);
        tx.commit();
        s.close();
    }

//    @Override
//    public Transactions findTransactionById(int id) {
//        Session s = HibernateUtil.getSessionFactory().openSession();
//        Transactions transaction = s.get(Transactions.class, id);
//        s.close();
//        return transaction;
//    }

    @Override
    public List<Transactions> getTransactionByWallet(int walletId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        List<Transactions> transactions = null;

        try {
            tx = session.beginTransaction();
            transactions = session.createQuery("FROM Transactions WHERE wallet.id = :walletId ORDER BY date DESC", Transactions.class)
                    .setParameter("walletId", walletId)
                    .list();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        return transactions;
    }
}
