package com.example.ql_khachsan;

import com.example.ql_khachsan.untils.DatabaseConnection;

public class Test {
    public static void main(String[] args) {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        databaseConnection.testConnection();
        databaseConnection.listTables();
    }
}
