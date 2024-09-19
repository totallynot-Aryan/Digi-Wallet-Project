package DAO;

import beans.User;
import beans.Wallet;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;
import org.hibernate.query.Query;
public class UserDAOImpl implements UserDAO{
//    @Override
//    public void saveUser(User user) {
//        Session s = HibernateUtil.getSessionFactory().openSession();
//        Transaction tx = s.beginTransaction();
//        s.save(user);
//        tx.commit();
//        s.close();
//    }

    @Override
    public User findUserById(int id) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        User user = s.get(User.class, id);
        s.close();
        return user;
    }
//    @Override
//    public User findUserByEmail(String email) {
//        Session s = HibernateUtil.getSessionFactory().openSession();
//        User user = (User) s.createQuery("FROM User WHERE email = :email")
//                .setParameter("email", email)
//                .uniqueResult();
//        s.close();
//        return user;
//    }

    @Override
    public User findUserByUsername(String username) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        User user = (User) s.createQuery("FROM User WHERE username = :username")
                .setParameter("username", username)
                .uniqueResult();
        s.close();
        return user;
    }

    @Override
    public boolean authenticate(String username, String password) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        User user = findUserByUsername(username);
        boolean isAuthenticated = user != null && user.getPassword().equals(password);
        s.close();
        return isAuthenticated;
    }

    @Override
    public int register(String username, String password, String email) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = s.beginTransaction();
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password); // Encrypt the password
        newUser.setEmail(email);
        Wallet wallet = new Wallet();
        newUser.setWallet(wallet); // Bi-Directional relationship
        s.save(newUser);
        tx.commit();
        s.close();
        return newUser.getId();
    }

    @Override
    public void deleteUser(int userId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        Query<Wallet> walletQuery = session.createQuery("FROM Wallet WHERE user.id = :userId", Wallet.class);
        walletQuery.setParameter("userId", userId);
        Wallet wallet = walletQuery.uniqueResult();

        if (wallet != null) {
            // Delete transactions associated with the wallet
            Query query1 = session.createQuery("DELETE FROM Transactions WHERE wallet.id = :id");
            query1.setParameter("walletId", wallet.getId());
            query1.executeUpdate();

            // Delete spending limits associated with the wallet
            Query query2 = session.createQuery("DELETE FROM SpendingLimit WHERE wallet.id = :walletId");
            query2.setParameter("walletId", wallet.getId());
            query2.executeUpdate();

            // Delete the wallet itself
            Query query3 = session.createQuery("DELETE FROM Wallet WHERE id = :walletId");
            query3.setParameter("walletId", wallet.getId());
            query3.executeUpdate();
        }

        // Delete the user
        User user = session.get(User.class, userId);
        if (user != null) {
            session.delete(user);
        }
        tx.commit();
        session.close();
    }
    @Override
    public void updateUserEmail(int userId, String newEmail) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = s.beginTransaction();
        User user = s.get(User.class, userId);
        if (user != null) {
            user.setEmail(newEmail);
            s.update(user);
            System.out.println("Email updated successfully.");
        } else {
            System.out.println("User not found.");
        }
        tx.commit();
        s.close();
    }
}