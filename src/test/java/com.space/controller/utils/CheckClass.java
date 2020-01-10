package com.space.controller.utils;

import java.sql.*;

public class CheckClass {


    /**
     * @param args
     */
    public static void main(String[] args) throws Exception{

        try{
            System.out.println("hello, world!");
            Class.forName("org.h2.Driver");
//            Connection conn = DriverManager.getConnection("jdbc:h2:~/testdb", "sa", "");
//            // add application code here
//            conn.close();
        }catch(ClassNotFoundException ex){
            System.out.println( "ERROR: Class not found: " + ex.getMessage() );

        }
        System.exit(0);

    }

}
