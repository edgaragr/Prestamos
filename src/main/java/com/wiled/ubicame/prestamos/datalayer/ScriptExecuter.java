/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wiled.ubicame.prestamos.datalayer;

/**
 *
 * @author Edgar Garcia
 */
import com.wiled.ubicame.prestamos.utils.PrestamoConstants;
import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;

import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScriptExecuter {

    private static final String DRIVER_NAME = "com.mysql.jdbc.Driver";

    static {
        try {
            Class.forName(DRIVER_NAME).newInstance();
            System.out.println("*** Driver loaded");
        } catch (Exception e) {
            System.out.println("*** Error : " + e.toString());
            System.out.println("*** ");
            System.out.println("*** Error : ");
        }
    }
    private static final String URL = "jdbc:mysql://localhost:3306/"+PrestamoConstants.SYSTEM_DATABASE_NAME+"";
    //private static final String USER = "root";
    //private static final String PASSWORD = "wiled";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, PrestamoConstants.SYSTEM_USER, PrestamoConstants.SYSTEM_PASSWORD);
    }
    
    public static void main(String[] args) {
        try {
            ScriptExecuter.executeScript();
        } catch (SQLException ex) {
            Logger.getLogger(ScriptExecuter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void executeScript() throws SQLException {
        String s = new String();
        StringBuilder sb = new StringBuilder();

        try {
            InputStream url = Thread.currentThread().getContextClassLoader().getResourceAsStream("quartz_tables.sql"); //ScriptExecuter.class.getClassLoader().getResourceAsStream("./quartz_tables.sql");
            InputStreamReader isr = new InputStreamReader(url);

            BufferedReader br = new BufferedReader(isr);

            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
            br.close();

            // here is our splitter ! We use ";" as a delimiter for each request
            // then we are sure to have well formed statements
            String[] inst = sb.toString().split(";");

            Connection c = ScriptExecuter.getConnection();
            Statement st = c.createStatement();

            for (int i = 0; i < inst.length; i++) {
                // we ensure that there is no spaces before or after the request string
                // in order to not execute empty statements
                if (!inst[i].trim().equals("")) {
                    st.executeUpdate(inst[i]);
                    System.out.println(">>" + inst[i]);
                }
            }

        } catch (Exception e) {
            System.out.println("*** Error : " + e.toString());
            System.out.println("*** ");
            System.out.println("*** Error : ");
            System.out.println("################################################");
            System.out.println(sb.toString());
        }
    }
}
