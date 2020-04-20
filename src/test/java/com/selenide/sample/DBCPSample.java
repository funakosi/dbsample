package com.selenide.sample;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.Test;

public class DBCPSample {

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
	public void DBCPTest() {
		ConnectionFactory factory = ConnectionFactory.getInstance();
		try {
			conn = factory.getConnection();
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
