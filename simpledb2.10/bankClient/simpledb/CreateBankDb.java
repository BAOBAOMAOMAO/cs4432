import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Statement;

import simpledb.remote.SimpleDriver;
import simpledb.remote.SimpleResultSet;

public class CreateBankDb {
	public static void main(String[] args)
	{
		Connection conn = null;
		try{
			Driver d = new SimpleDriver();
			conn = d.connect("jdbc:simpledb://localhost", null);
			Statement stmt = conn.createStatement();
			System.out.println("Connection established");
			
			
			//account
			String s = "create table ACCOUNT(ANum int, CName varchar(10), Saving int, Checking int)";
			stmt.executeUpdate(s);
			System.out.println("Table ACCOUNT created");
			
			s = "insert into ACCOUNT(ANum, CName, Saving, Checking) values ";
			String[] accVals = {"(100000000, 'Tom', 200, 200)",
					"(200000000, 'John', 2000, 200)",
					"(300000000, 'Mike', 100, 200)",
					"(400000000, 'Sam', 900, 200)",
					"(500000000, 'Dean', 200, 900)",
					"(600000000, 'Baobao', 8000, 8000)",
					"(700000000, 'a', 100, 100)",
					"(800000000, 'b', 200, 200)",
					"(900000000, 'c', 300, 300)",
					};
			for(int i=0; i<accVals.length; i++)
			{
				stmt.executeUpdate(s + accVals[i]);
			}
			System.out.println("ACCOUNT records inserted");
			
			
			//client
			s = "create table CLIENT(SSN int, CName varchar(10), CAge int, CGender varchar(1))";
			stmt.executeUpdate(s);
			
			System.out.println("Table CLIENT created.");
			
			s = "insert into CLIENT(SSN, CName, CAge, CGender) values";
			String[] CLIENTVals = {
					"(000000000, 'maomao', 1, 'f')",
					"(000000001, 'joe', 2, 'm')",
					"(000000002, 'amy', 2, 'm')",
					"(000000003, 'sue', 4, 'f')",
					"(000000004, 'art', 5, 'f')"};
			for(int i = 0; i < CLIENTVals.length; i++){
				stmt.executeUpdate(s + CLIENTVals[i]);
			}
			System.out.println("CLIENT records inserted");
			
			//First query.
			s = "select ANum from ACCOUNT";
			SimpleResultSet srs = (SimpleResultSet) stmt.executeQuery(s);
			while(srs.next())
			{
				System.out.println(srs.getInt("ANum"));
			}
			//Second query.
			s = "select CName from ACCOUNT where CName = 'Tom'";
			srs = (SimpleResultSet) stmt.executeQuery(s);
			while(srs.next())
			{
				System.out.println(srs.getString("CName"));
			}
			System.out.println("Finish second query!");
			//Third query
			s = "select CName from CLIENT where CAge = 2";
			srs = (SimpleResultSet) stmt.executeQuery(s);
			while(srs.next())
			{
				System.out.println(srs.getString("CName"));
			}
			
		}
		catch(SQLException e){
			e.printStackTrace();
		}
	}

}
