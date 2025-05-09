package com.venteSwing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
	private static final String URL = "jdbc:mysql://localhost:3306/vente";
    private static final String USER = "root";
    private static final String PASSWORD = "VotreMotDePasse";
    private static final String jdbc = "com.mysql.cj.jdbc.Driver";
    
    public static Connection getConnection() throws ClassNotFoundException, SQLException{
    	Class.forName(jdbc);
    	return DriverManager.getConnection(URL,USER, PASSWORD);
    	}
}