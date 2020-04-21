package com.selenide.sample;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DBUtilSample {

	Connection conn = null;
	QueryRunner queryRunner = null;
	String select_sqlall = "select empno, ename from employee";
	String select_sql1 = "select empno, ename from employee WHERE empno = ?";

	@Before
	public void setUp() {
		ConnectionFactory factory = ConnectionFactory.getInstance();
		conn = factory.getConnection();
		queryRunner = new QueryRunner();
	}

	@After
	public void tearDown() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void SelectTest01() {
		try {//empno=1000をキーに検索
			Map<String, Object> map = queryRunner.query(conn, select_sql1, new MapHandler(), 1000);
			System.out.println(map.get("ename"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void SelectTest02 () {
		try {//employeeテーブルの全データを表示(Map使用)
			List<Map<String, Object>> list = queryRunner.query(conn, select_sqlall, new MapListHandler());
			for (Map<String, Object> element : list) {
				System.out.println(element.get("ename"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void SelectTest03 () {
		try {//employeeとdepartmentの全データを表示(各テーブルに対応するクラスを使用)
			List<Emp> list = queryRunner.query(conn, select_sqlall, new BeanListHandler<>(Emp.class));
			for (Emp emp : list) {
				System.out.println(emp);
			}
			List<Dep> depList = queryRunner.query(conn, "select * from department", new BeanListHandler<>(Dep.class));
			for (Dep dep : depList) {
				System.out.println(dep);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void InsertUpdateDeleteTest() {
		String insertSQL = "INSERT INTO department (deptno,dname,location) VALUES (?, ?, ?)";
		String updateSQL = "update department set dname = ? where deptno = ?";
		String deleteSQL = "DELETE from department where deptno = ?";

		//1.新規データを挿入
		//2.empno=50で検索し意図通りか確認
		//3.新規に追加したデータの名前を修正(総務部→開発部)
		//4.2.と同じように検索し確認
		//5.新規に追加したデータを削除
	    try {
			int newId = queryRunner.update(	conn, insertSQL, "50", "総務部", "東京");
			assertThat(newId, is(1));
			Dep newDep = queryRunner.query(conn, "select * from department where deptno = ?", new BeanHandler<>(Dep.class),50);
			assertThat(newDep.getDname(), is("総務部"));
			assertThat(newDep.getLocation(), is("東京"));
			int updNum = queryRunner.update(conn,updateSQL,"開発部",50);
			assertThat(updNum, is(1));
			Dep updDep = queryRunner.query(conn, "select * from department where deptno = ?", new BeanHandler<>(Dep.class),50);
			assertThat(updDep.getDname(), is("開発部"));
			int delNum = queryRunner.update(conn,deleteSQL,50);
			assertThat(delNum, is(1));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void InsertDatasAndDeleteThem() {
		String insertSQL = "INSERT INTO department (deptno,dname,location) VALUES (?, ?, ?)";
		String deleteSQL = "DELETE from department where deptno = ?";

		//1.新規データを複数挿入
		//2.empno=50,60で検索し意図通りか確認
		//3.新規に追加したデータを削除
	    try {
	    	List<Object[]> params = new ArrayList<Object[]>();
	        params.add(new Object[]{ "50", "総務部", "東京"});
	        params.add(new Object[]{ "60", "開発部",  "鹿児島"});
			queryRunner.batch(	conn, insertSQL, params.toArray(new Object[0][]));
			Dep newDep = queryRunner.query(conn, "select * from department where deptno = ?", new BeanHandler<>(Dep.class),50);
			assertThat(newDep.getDname(), is("総務部"));
			assertThat(newDep.getLocation(), is("東京"));
			newDep = queryRunner.query(conn, "select * from department where deptno = ?", new BeanHandler<>(Dep.class),60);
			assertThat(newDep.getDname(), is("開発部"));
			assertThat(newDep.getLocation(), is("鹿児島"));
			params.clear();
			params.add(new Object[]{ "50"});
			params.add(new Object[]{ "60"});
			queryRunner.batch(conn, deleteSQL, params.toArray(new Object[0][]));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
