package Service;

import Manager.AdminManager;

public class AdminService implements AdminManager {
    @Override
    public boolean loginAdmin(String username, String password) {
        return "admin".equals(username) && "1234".equals(password);
    }
}
