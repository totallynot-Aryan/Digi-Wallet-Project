package DAO;

import beans.Transactions;
import java.util.List;

public interface TransactionDAO {
    void saveTransaction(Transactions transactions);
    //Transactions findTransactionById(int id);
    List<Transactions> getTransactionByWallet(int walletId);
}
