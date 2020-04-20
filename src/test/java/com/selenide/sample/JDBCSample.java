package com.selenide.sample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.Test;

public class JDBCSample {

	String DRIVER = "com.mysql.cj.jdbc.Driver";
	String URL = "jdbc:mysql://192.168.33.10:3306/testdb";
	String USER = "testuser";
	String PASS = "password";
	Connection conn = null;

	@After
	public void tearDown() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void JDBCTest() {
		try {
			conn = DriverManager.getConnection(URL, USER, PASS);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select empno, ename from employee");
			while (rs.next()) {
				System.out.println(rs.getString(1)+":"+rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
