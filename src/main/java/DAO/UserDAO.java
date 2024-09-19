package DAO;

import beans.User;
public interface UserDAO {
    //void saveUser(User user);
    void updateUserEmail(int id, String newEmail);
    void deleteUser(int id);
    User findUserById(int id);
    //User findUserByEmail(String email);
    User findUserByUsername(String username);
    boolean authenticate(String username, String password);
    int register(String username, String password, String email);

}
