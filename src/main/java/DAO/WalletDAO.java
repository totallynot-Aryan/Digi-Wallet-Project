package DAO;

import beans.Wallet;
import org.hibernate.Session;

public interface WalletDAO {
    void saveWallet(Wallet wallet);
    Wallet findWalletByUserId(int userId);
    void updateWalletBalance(int walletID, double amount);
}
