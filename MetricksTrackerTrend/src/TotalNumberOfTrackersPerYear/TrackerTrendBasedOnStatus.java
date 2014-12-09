package TotalNumberOfTrackersPerYear;

import java.awt.Color;
import java.io.Console;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class TrackerTrendBasedOnStatus {

	public static void main(String[] args) throws Exception {

		System.out.println("Enter the value of status \n" + "0-other \n"
				+ "1-New \n" + "2- In progress \n" + "3- Testing \n"
				+ "4- Reviewed \n" + "5- Closed \n" + "6- Deleted \n");
		System.out.println("Your choice?");

		// delete the following things and read input from console or a
		// .properties file.
		int other = 0;
		int newlyOpened = 1;
		int inProgress = 2;
		int testing = 3;
		int reviwed = 4;
		int closed = 5;
		int deleted = 6;

		/*
		 * JDBC connection string to connect to the local database.
		 */
		Class.forName("oracle.jdbc.OracleDriver");
		Connection conn = DriverManager.getConnection(
				"jdbc:oracle:thin:@//localhost:1521/xe", "ODSREPORT",
				"odsreport");
		DefaultCategoryDataset my_bar_chart_dataset = new DefaultCategoryDataset();

		Statement stmt = conn.createStatement();

		try {

			/*
			 * Query to obtain the year-wise count of trackers.
			 */
			ResultSet query_set = stmt
					.executeQuery("select extract(YEAR from CREATE_DATE) year, count(CREATE_DATE) totalCount from TRACKER where status="
							+ newlyOpened
							+ " group by extract(year from CREATE_DATE) order by year asc");

			while (query_set.next()) {

				int count = query_set.getInt("totalCount");

				String year = query_set.getString("year");

				/*
				 * Add the resulting data to the dataset.
				 */
				my_bar_chart_dataset.addValue(count, "Trackers", year);

			}
			/*
			 * Generate the chart.
			 */
			JFreeChart BarChartObject = ChartFactory.createBarChart(
					"Tracker total count v/s year - Bar Chart", "year",
					"totalCount", my_bar_chart_dataset,
					PlotOrientation.VERTICAL, true, true, false);

			CategoryPlot p = BarChartObject.getCategoryPlot();
			p.setRangeGridlinePaint(Color.black);

			/*
			 * Setting the range for y-axis.
			 */
			// CategoryPlot plot = (CategoryPlot) BarChartObject.getPlot();
			// ValueAxis yAxis = plot.getRangeAxis();
			// yAxis.setRange(0, 100);

			query_set.close();
			stmt.close();
			conn.close();

			int width = 640; /* Width of the image */
			int height = 480; /* Height of the image */

			/*
			 * Save the chart in a file.
			 */
			File BarChart = new File("TotalVsYearBasedOnStatus.png");
			ChartUtilities.saveChartAsPNG(BarChart, BarChartObject, width,
					height);

		} catch (SQLException e) {
			System.out.println(e);
		}
	}
}
