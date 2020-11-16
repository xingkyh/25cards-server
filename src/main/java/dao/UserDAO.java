package dao;

import model.User;

public abstract interface UserDAO {
	public String check(String username, String password);
	public String register(User user);
}
