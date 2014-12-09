package TotalNumberOfTrackersPerYear;

/*
 * Author: UC167162 (Chaithra Rao J)
 * Copyrights protected.
 */

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class JSSoverallTrackerTrendExcelSheet {
	public static void main(String args[]) {
		// creating PreparedStatement object to execute query
		PreparedStatement preStatement = null;

		ResultSet result = null;
		Connection conn = null;
		BufferedWriter buffWrt = null;

		try {

			Class.forName("oracle.jdbc.OracleDriver");

			// URL of Oracle database server
			String url = "jdbc:oracle:thin:@//localhost:1521/xe";

			// properties for creating connection to Oracle database
			Properties props = new Properties();
			props.setProperty("user", "ODSREPORT");
			props.setProperty("password", "odsreport");

			// creating connection to Oracle database using JDBC

			conn = DriverManager.getConnection(url, props);

			String sql = ("select extract(YEAR from CREATE_DATE) YEAR, count(CREATE_DATE) totalCount from TRACKER group by extract(YEAR from CREATE_DATE) order by YEAR asc");

			buffWrt = new BufferedWriter(new FileWriter(new File(
					"outputserials.xls")));

			preStatement = conn.prepareStatement(sql);
			result = preStatement.executeQuery();
			buffWrt.write("YEAR" + "\t" + "count" + "\t" + "\n");
			while (result.next()) {

				int count = result.getInt("totalCount");

				String YEAR = result.getString("YEAR");

				System.out.println(YEAR + "\t" + count);

				/* writing into excel sheet */

				buffWrt.write(YEAR + "\t" + count + "\t" + "\n");
				buffWrt.flush();

			}

			result.close();
			preStatement.close();
		}

		catch (Exception e) {
			// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		} finally {
			try {

				buffWrt.close();

				conn.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
