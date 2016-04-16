/**
 * Setup the environment to run the program

a. Install Java on Ubuntu,

sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install oracle-java8-installer

b. Assuming java is installed under the path “/usr/bin/java” (could be something else in your case), the path should be imported on the $PATH environment variable using the terminal,

export PATH=/usr/bin:$PATH

c. Export the postgres driver path to the CLASSPATH variable (The path for you will be where you download the driver) using the terminal,

export CLASSPATH=$CLASSPATH:/Users/sneha/Library/Drivers/postgresql-9.4-1203.jdbc4.jar


Steps to execute the programs

a. Report 1 – On the terminal run the following commands:

javac Report1.java
java Report1 [username] [password] [database_name]

where username and password are access credentials to the database_name.

b. Report 2 – On the terminal run the following commands:

javac Report2.java
java Report2 [username] [password] [database_name]

where username and password are access credentials to the database_name.

c. Report 3 – On the terminal run the following commands:

javac Report3.java
java Report3 [username] [password] [database_name]

where username and password are access credentials to the database_name.
 */
/******
Justification of your choice of data structures for the program

The arraylist supports dynamic arrays that grow as and when needed.
Unlike arrays that are fixed size and cannot grow or shrink, arraylists are created with an initial size and can be enlarged or shrunk when the data grows or reduces respectively.

***/
/*****
 * This program calculates the average sales before and after each quarter for each customer and product
 * It does so by comparing one row at a time and incrementing the count. To calculate the new average, for example to add a number say X to an average T of A numbers it uses:
 * 
 * New Average = ((T*A) + X)/A+1
 */
package com.dbms.assign2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Report2 
{
	public static void main(String[] args) 
	{
		
		String usr = args[0]; //first command line argument will be used as the username to connect to the database
		
		String pwd = args[1]; //second command line argument will be used as the password to connect to the database
		
		String url = "jdbc:postgresql://localhost:5432/"+args[2]; //url to connect to the database
		
		//load driver
		try
		{
			Class.forName("org.postgresql.Driver");
		}
		
		//throws an exception if it cannot load the driver
		catch (Exception e)
		{
			System.out.println("Failed to load the driver!");
			e.printStackTrace();
		}
		
		//connect server
		try
		{
			//establish connection with the database
			Connection conn = DriverManager.getConnection(url, usr, pwd);
			
			//get query result in ResultSet rs
			Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt.executeQuery("select * from sales");
			
			//declare variables to be used in the program
			ArrayList<String[]> display = new ArrayList<String[]>();
			int i=0, j=0, match=0, nomatch=0, temp_count=0;
			float new_quant=0;
	
			//fetch each element from the resultset rs
			while(rs.next())
			{
				//initialize the arraylist to start comparing the rows from resultset rs
				//each row has the attributes listed in the following order: cust, prod, quarter, before_avg, after_avg, before_avg_count, after_avg_count
				if(display.size() == 0)
				{
					if(Integer.parseInt(rs.getString("month")) <= 3)
					{
						display.add(new String[] {rs.getString("cust"), rs.getString("prod"), "Q1", "<NULL>", "0", "0", "0"});
						display.add(new String[] {rs.getString("cust"), rs.getString("prod"), "Q2", rs.getString("quant"), "0", "1", "0"});
						display.add(new String[] {rs.getString("cust"), rs.getString("prod"), "Q3", "0", "0", "0", "0"});
						display.add(new String[] {rs.getString("cust"), rs.getString("prod"), "Q4", "0", "<NULL>", "0", "0"});
					}
					else if(Integer.parseInt(rs.getString("month")) > 3 && Integer.parseInt(rs.getString("month")) <= 6)
					{
						display.add(new String[] {rs.getString("cust"), rs.getString("prod"), "Q1", "<NULL>", rs.getString("quant"), "0", "1"});
						display.add(new String[] {rs.getString("cust"), rs.getString("prod"), "Q2", "0", "0", "0", "0"});
						display.add(new String[] {rs.getString("cust"), rs.getString("prod"), "Q3", rs.getString("quant"), "0", "1", "0"});
						display.add(new String[] {rs.getString("cust"), rs.getString("prod"), "Q4", "0", "<NULL>", "0", "0"});
					}
					else if(Integer.parseInt(rs.getString("month")) > 6 && Integer.parseInt(rs.getString("month")) <= 9)
					{
						display.add(new String[] {rs.getString("cust"), rs.getString("prod"), "Q1", "<NULL>", "0", "0", "0"});
						display.add(new String[] {rs.getString("cust"), rs.getString("prod"), "Q2", "0", rs.getString("quant"), "0", "1"});
						display.add(new String[] {rs.getString("cust"), rs.getString("prod"), "Q3", "0", "0", "0", "0"});
						display.add(new String[] {rs.getString("cust"), rs.getString("prod"), "Q4", rs.getString("quant"), "<NULL>", "1", "0"});
					}
					else
					{
						display.add(new String[] {rs.getString("cust"), rs.getString("prod"), "Q1", "<NULL>", "0", "0", "0"});
						display.add(new String[] {rs.getString("cust"), rs.getString("prod"), "Q2", "0", "0", "0", "0"});
						display.add(new String[] {rs.getString("cust"), rs.getString("prod"), "Q3", "0", rs.getString("quant"), "0", "1"});
						display.add(new String[] {rs.getString("cust"), rs.getString("prod"), "Q4", "0", "<NULL>", "0", "0"});
					}
					continue;
				}
				match = 0;
				//compare each element of the resultset with the rows in the arraylist
				for(i=0; i < display.size(); i++)
				{
					//the cust and prod of the current row of the resultset matches with any row's cust and prod in the arraylist
					if(display.get(i)[0].equals(rs.getString("cust")) && display.get(i)[1].equals(rs.getString("prod")))
					{
						//if month in first quarter
						if(Integer.parseInt(rs.getString("month")) <= 3)
						{
							if(display.get(i)[2] == "Q2")
							{
								match++; //increment the flag for a match found
								temp_count = Integer.parseInt(display.get(i)[5]) +1; //increment count
								new_quant = ((Float.parseFloat(display.get(i)[3]) * Float.parseFloat(display.get(i)[5])) + Float.parseFloat(rs.getString("quant"))) / temp_count; //calculate new average
								display.get(i)[3] = String.valueOf(new_quant); //set new average
								display.get(i)[5] = String.valueOf(temp_count); //set new count
							}
						}
						//if month in second quarter
						else if(Integer.parseInt(rs.getString("month")) > 3 && Integer.parseInt(rs.getString("month")) <= 6)
						{
							if(display.get(i)[2] == "Q1")
							{
								match++; //increment the flag for a match found
								temp_count = Integer.parseInt(display.get(i)[6]) +1; //increment count
								new_quant = ((Float.parseFloat(display.get(i)[4]) * Float.parseFloat(display.get(i)[6])) + Float.parseFloat(rs.getString("quant"))) / temp_count; //calculate new average
								display.get(i)[4] = String.valueOf(new_quant); //set new average
								display.get(i)[6] = String.valueOf(temp_count); //set new count
							}
							if(display.get(i)[2] == "Q3")
							{
								match++; //increment the flag for a match found
								temp_count = Integer.parseInt(display.get(i)[5]) +1; //increment count
								new_quant = ((Float.parseFloat(display.get(i)[3]) * Float.parseFloat(display.get(i)[5])) + Float.parseFloat(rs.getString("quant"))) / temp_count; //calculate new average
								display.get(i)[3] = String.valueOf(new_quant); //set new average
								display.get(i)[5] = String.valueOf(temp_count); //set new count
							}
						}
						//if month in third quarter
						else if(Integer.parseInt(rs.getString("month")) > 6 && Integer.parseInt(rs.getString("month")) <= 9)
						{
							if(display.get(i)[2] == "Q2")
							{
								match++; //increment the flag for a match found
								temp_count = Integer.parseInt(display.get(i)[6]) +1; //increment count
								new_quant = ((Float.parseFloat(display.get(i)[4]) * Float.parseFloat(display.get(i)[6])) + Float.parseFloat(rs.getString("quant"))) / temp_count; //calculate new average
								display.get(i)[4] = String.valueOf(new_quant); //set new average
								display.get(i)[6] = String.valueOf(temp_count); //set new count
							}
							if(display.get(i)[2] == "Q4")
							{
								match++; //increment the flag for a match found
								temp_count = Integer.parseInt(display.get(i)[5]) +1; //increment count
								new_quant = ((Float.parseFloat(display.get(i)[3]) * Float.parseFloat(display.get(i)[5])) + Float.parseFloat(rs.getString("quant"))) / temp_count; //calculate new average
								display.get(i)[3] = String.valueOf(new_quant); //set new average
								display.get(i)[5] = String.valueOf(temp_count); //set new count
							}
						}
						//if month in fourth quarter
						else
						{
							if(display.get(i)[2] == "Q3")
							{
								match++; //increment the flag for a match found
								temp_count = Integer.parseInt(display.get(i)[6]) +1; //increment count
								new_quant = ((Float.parseFloat(display.get(i)[4]) * Float.parseFloat(display.get(i)[6])) + Float.parseFloat(rs.getString("quant"))) / temp_count; //calculate new average
								display.get(i)[4] = String.valueOf(new_quant); //set new average
								display.get(i)[6] = String.valueOf(temp_count); //set new count
							}
						}
					}
					//the cust and prod of the current row of the resultset does not match with any row's cust and prod in the arraylist
					else
					{
						nomatch++; //increment the flag for no match found
					}
				}
				//for the current resultset row, no match is found in the entire arraylist, add that row to the arraylist
				if(match == 0 && nomatch > 0)
				{
					if(Integer.parseInt(rs.getString("month")) <= 3)
					{
						display.add(new String[] {rs.getString("cust"), rs.getString("prod"), "Q1", "<NULL>", "0", "0", "0"});
						display.add(new String[] {rs.getString("cust"), rs.getString("prod"), "Q2", rs.getString("quant"), "0", "1", "0"});
						display.add(new String[] {rs.getString("cust"), rs.getString("prod"), "Q3", "0", "0", "0", "0"});
						display.add(new String[] {rs.getString("cust"), rs.getString("prod"), "Q4", "0", "<NULL>", "0", "0"});
					}
					
					else if(Integer.parseInt(rs.getString("month")) > 3 && Integer.parseInt(rs.getString("month")) <= 6)
					{
						display.add(new String[] {rs.getString("cust"), rs.getString("prod"), "Q1", "<NULL>", rs.getString("quant"), "0", "1"});
						display.add(new String[] {rs.getString("cust"), rs.getString("prod"), "Q2", "0", "0", "0", "0"});
						display.add(new String[] {rs.getString("cust"), rs.getString("prod"), "Q3", rs.getString("quant"), "0", "1", "0"});
						display.add(new String[] {rs.getString("cust"), rs.getString("prod"), "Q4", "0", "<NULL>", "0", "0"});
					}
					else if(Integer.parseInt(rs.getString("month")) > 6 && Integer.parseInt(rs.getString("month")) <= 9)
					{
						display.add(new String[] {rs.getString("cust"), rs.getString("prod"), "Q1", "<NULL>", "0", "0", "0"});
						display.add(new String[] {rs.getString("cust"), rs.getString("prod"), "Q2", "0", rs.getString("quant"), "0", "1"});
						display.add(new String[] {rs.getString("cust"), rs.getString("prod"), "Q3", "0", "0", "0", "0"});
						display.add(new String[] {rs.getString("cust"), rs.getString("prod"), "Q4", rs.getString("quant"), "<NULL>", "1", "0"});
					}
					else
					{
						display.add(new String[] {rs.getString("cust"), rs.getString("prod"), "Q1", "<NULL>", "0", "0", "0"});
						display.add(new String[] {rs.getString("cust"), rs.getString("prod"), "Q2", "0", "0", "0", "0"});
						display.add(new String[] {rs.getString("cust"), rs.getString("prod"), "Q3", "0", rs.getString("quant"), "0", "1"});
						display.add(new String[] {rs.getString("cust"), rs.getString("prod"), "Q4", "0", "<NULL>", "0", "0"});
					}
				
				}
			}
			//print the arraylist
			System.out.println("CUSTOMER" + "  " + "PRODUCT" + "   " + "QUARTER" + "  " + "BEFORE_AVG" + "  " + "AFTER_AVG");
			System.out.println("========" + "  " + "========" + "  " + "=======" + "  " + "==========" + "  " + "=========");
			for(j=0; j < display.size(); j++)
			{
				System.out.printf("%-9s %-7s %4s %16s %11s", display.get(j)[0], display.get(j)[1], display.get(j)[2], display.get(j)[3], display.get(j)[4] + "\n");
			}
		}
			//throws an exception if there are errors connecting to the database
			catch (SQLException e)
			{
				System.out.println("Connection URL or username or password errors!"); 
				e.printStackTrace();
			}
	}
}