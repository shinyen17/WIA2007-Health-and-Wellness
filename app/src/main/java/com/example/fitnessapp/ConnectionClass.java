package com.example.fitnessapp;


import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Objects;

public class ConnectionClass {

    protected  static String db = "maddb";

    protected static String ip = "10.0.2.2";

    protected static String port = "3306";

    protected static String username = "root";

    protected static String password = "Tanyirou9898";

    public Connection CONN(){
        Connection conn = null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            String connectionString = "jdbc:mysql://"+ip+":"+port+"/"+db;
            conn = DriverManager.getConnection(connectionString, username, password);
            Log.d("DB", "Connection successful");  // Log success
        } catch (Exception e) {
            Log.e("DB", "Connection failed: " + e.getMessage());  // Log error if connection fails
        }
        return conn;
    }

}
