/**
 * CS4432-Project1:
 * These are the test cases for clock replacement policy. 
 * To run this test case, change "policyConfig" variable in "AdvancedBufferMgr" class to "CLOCK".
 */
package simpledb.buffer;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Statement;

import simpledb.remote.SimpleDriver;
import simpledb.remote.SimpleResultSet;

public class TestAdvancedBufferMgrClock {
	public static void main(String[] args)
	{
		Connection conn = null;
		try{
			Driver d = new SimpleDriver();
			conn = d.connect("jdbc:simpledb://localhost", null);
			Statement stmt = conn.createStatement();
			System.out.println("Connection established");
			/**
			 * CS4432-Project1:
			 * Both test cases are discussed in the testMethod.pdf file.
			 * Run this file to run the first test case.
			 * Restart the program, comment out the first test case, uncomment the second test case and run this file again to run the second test case.
			*/
			/**
			 * CS4432-Project1:
			 * The first test case:
			 * Test for normal CLOCK case. The first test case covers the following:
			 * - The clock pointer move to the next and set the current reference bit to 0 when the current buffer is not pinned.
			 * - The clock pointer move to the next and leave the current reference bit unchanged when the current buffer is pinned.
			 * - The clock pointer remains the same when the block accessed is already in the buffer pool.
			 */
			//account
			String s = "create table ACCOUNT(ANum int, CName varchar(10), Saving int, Checking int)";
			stmt.executeUpdate(s);
			System.out.println("Table ACCOUNT created");
			
			//address
			s = "create table ADDRESS(City varchar(10), State varchar(10), Country varchar(10))";
			stmt.executeUpdate(s);
			System.out.println("Table ADDRESS created");
			
			//card
			s = "create table CARD(CType varchar(10), HName varchar(10), CNum int)";
			stmt.executeUpdate(s);
			System.out.println("Table CARD created");
			
			//student
			s = "create table STUDENT(SId int, SName varchar(10), MajorId int, GradYear int)";
			stmt.executeUpdate(s);			
			System.out.println("Table STUDENT created.");
			
			//client
			s = "create table CLIENT(SSN int, CName varchar(10), CAge int, CGender varchar(1))";
			stmt.executeUpdate(s);			
			System.out.println("Table CLIENT created.");
			
			//insert account
			s = "insert into ACCOUNT(ANum, CName, Saving, Checking) values (100000000, 'Tom', 200, 200)";
			stmt.executeUpdate(s);
			System.out.println("ACCOUNT records inserted");
			
			//insert client
			s = "insert into CLIENT(SSN, CName, CAge, CGender) values (000000000, 'maomao', 1, 'f')";
			stmt.executeUpdate(s);
			System.out.println("Client records inserted");		
			
			//insert student
			s = "insert into STUDENT(SId, SName, MajorId, GradYear) values (1, 'joe', 10, 2004)";
			stmt.executeUpdate(s);
			System.out.println("STUDENT records inserted.");
			
			//insert address
			s = "insert into ADDRESS(City, State, Country) values ('Worcester', 'Ma', 'US')";
			stmt.executeUpdate(s);
			System.out.println("ADDRESS records inserted.");
			
			//select
			s = "select ANum from ACCOUNT";
			stmt.executeQuery(s);
			
			//insert client
			s = "insert into CLIENT(SSN, CName, CAge, CGender) values (000000001, 'maomao', 1, 'f')";
			stmt.executeUpdate(s);
			System.out.println("Client records inserted");	
			
			//insert student
			s = "insert into STUDENT(SId, SName, MajorId, GradYear) values (10, 'j', 10, 2004)";
			stmt.executeUpdate(s);
			System.out.println("STUDENT records inserted.");
			
			/**
			 * CS4432-Project1:
			 * The second test case:
			 * All buffers are pinned, if database want to pin another buffer, an exception is threw.
			 * To run this test case, uncomment the code below and comment out the code between here and the first test case comment.
			 */
			
//			//account
//			String s = "create table ACCOUNT(ANum int, CName varchar(10), Saving int, Checking int)";
//			stmt.executeUpdate(s);
//			System.out.println("Table ACCOUNT created");
//			
//			//address
//			s = "create table ADDRESS(City varchar(10), State varchar(10), Country varchar(10))";
//			stmt.executeUpdate(s);
//			System.out.println("Table ADDRESS created");
//			
//			//card
//			s = "create table CARD(CType varchar(10), HName varchar(10), CNum int)";
//			stmt.executeUpdate(s);
//			System.out.println("Table CARD created");
//			
//			//student
//			s = "create table STUDENT(SId int, SName varchar(10), MajorId int, GradYear int)";
//			stmt.executeUpdate(s);			
//			System.out.println("Table STUDENT created.");
//			
//			//client
//			s = "create table CLIENT(SSN int, CName varchar(10), CAge int, CGender varchar(1))";
//			stmt.executeUpdate(s);			
//			System.out.println("Table CLIENT created.");
//			
//			//T5 table.
//			s = "create table T5(ANum int, CName varchar(10), Saving int, Checking int)";
//			stmt.executeUpdate(s);
//			System.out.println("Table ACCOUNT created");
//			
//			//T6 table.
//			s = "create table T6(City varchar(10), State varchar(10), Country varchar(10))";
//			stmt.executeUpdate(s);
//			System.out.println("Table ADDRESS created");
//			
//			//T7 table.
//			s = "create table T7(CType varchar(10), HName varchar(10), CNum int)";
//			stmt.executeUpdate(s);
//			System.out.println("Table CARD created");
//			
//			//T8 table.
//			s = "create table T8(SId int, SName varchar(10), MajorId int, GradYear int)";
//			stmt.executeUpdate(s);			
//			System.out.println("Table STUDENT created.");
//			
//			//insert account
//			s = "insert into ACCOUNT(ANum, CName, Saving, Checking) values (100000000, 'Tom', 200, 200)";
//			stmt.executeUpdate(s);
//			System.out.println("ACCOUNT records inserted");
//			
//			//insert address
//			s = "insert into ADDRESS(City, State, Country) values ";
//			String[] addrvals = {"('Worcester', 'Ma', 'US')"};
//			for (int i=0; i<addrvals.length; i++)
//				stmt.executeUpdate(s + addrvals[i]);
//			System.out.println("ADDRESS records inserted.");
//			
//			//insert card
//			s = "insert into CARD(CType, HName, CNum) values ";
//			String[] cardvals = {"('Worcester', 'maomao', 100000)"};
//			for (int i=0; i<cardvals.length; i++)
//				stmt.executeUpdate(s + cardvals[i]);
//			System.out.println("CARD records inserted.");
//			
//			//insert student
//			s = "insert into STUDENT(SId, SName, MajorId, GradYear) values ";
//			String[] studvals = {"(1, 'joe', 10, 2004)"};
//			for (int i=0; i<studvals.length; i++)
//				stmt.executeUpdate(s + studvals[i]);
//			System.out.println("STUDENT records inserted.");
//			
//			//insert client
//			s = "insert into CLIENT(SSN, CName, CAge, CGender) values ";
//			String[] clientVals = {"(000000000, 'maomao', 1, 'f')"};
//			for(int i=0; i<clientVals.length; i++)
//			{
//				stmt.executeUpdate(s + clientVals[i]);
//			}
//			System.out.println("Client records inserted");
//			
//			//insert T5
//			s = "insert into T5(ANum, CName, Saving, Checking) values (100000000, 'Tom', 200, 200)";
//			stmt.executeUpdate(s);
//			System.out.println("ACCOUNT records inserted");
//			
//			//insert T6
//			s = "insert into T6(City, State, Country) values ";
//			String[] T6val = {"('Worcester', 'Ma', 'US')"};
//			for (int i=0; i<addrvals.length; i++)
//				stmt.executeUpdate(s + T6val[i]);
//			System.out.println("ADDRESS records inserted.");
//			
//			//insert T7
//			s = "insert into T7(CType, HName, CNum) values ";
//			String[] T7val = {"('Worcester', 'maomao', 100000)"};
//			for (int i=0; i<cardvals.length; i++)
//				stmt.executeUpdate(s + T7val[i]);
//			System.out.println("CARD records inserted.");
//			
//			//insert T8
//			s = "insert into T8(SId, SName, MajorId, GradYear) values (1, 'joe', 10, 2004)";
//			stmt.executeUpdate(s);
//			System.out.println("STUDENT records inserted.");
//			
//			//After insert, use select statement to pin all those buffers. At some point, the buffer will run out of space,
//			//Since all the buffers are pinned, an exception will be threw.
//			s = "select CName from ACCOUNT";
//			SimpleResultSet srs = (SimpleResultSet) stmt.executeQuery(s);
//			while(srs.next())
//			{
//				System.out.println(srs.getString("CName"));
//			}
//			
//			s = "select State from ADDRESS";
//			srs = (SimpleResultSet) stmt.executeQuery(s);
//			while(srs.next())
//			{
//				System.out.println(srs.getString("State"));
//			}
//			
//			s = "select CType from CARD";
//			srs = (SimpleResultSet) stmt.executeQuery(s);
//			while(srs.next())
//			{
//				System.out.println(srs.getString("CType"));
//			}
//			
//			s = "select SId from STUDENT";
//			srs = (SimpleResultSet) stmt.executeQuery(s);
//			while(srs.next())
//			{
//				System.out.println(srs.getString("SId"));
//			}
//			
//			s = "select CName from CLIENT";
//			srs = (SimpleResultSet) stmt.executeQuery(s);
//			while(srs.next())
//			{
//				System.out.println(srs.getString("CName"));
//			}
//			
//			s = "select CName from T5";
//			srs = (SimpleResultSet) stmt.executeQuery(s);
//			while(srs.next())
//			{
//				System.out.println(srs.getString("CName"));
//			}
//			
//			s = "select State from T6";
//			srs = (SimpleResultSet) stmt.executeQuery(s);
//			while(srs.next())
//			{
//				System.out.println(srs.getString("State"));
//			}
//			
//			s = "select CType from T7";
//			srs = (SimpleResultSet) stmt.executeQuery(s);
//			while(srs.next())
//			{
//				System.out.println(srs.getString("CType"));
//			}
//			
//			s = "select SId from T8";
//			srs = (SimpleResultSet) stmt.executeQuery(s);
//			while(srs.next())
//			{
//				System.out.println(srs.getString("SId"));
//			}
		}
		catch(SQLException e){
			e.printStackTrace();
		}
	}
}
