package TotalNumberOfTrackersPerYear;

/*
 * Created by Neha(uc167165).
 */

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.Statement;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.TextAnchor;

/**
 * Program to generate year-wise JSS Tracker trend.
 * 
 * @author UC167165.
 * 
 */
public class JssOverallTrackerTrendFromTrendTableMe {
	public static void main(String[] args) throws Exception {

		/*
		 * JDBC connection string to connect to the local database.
		 */
		Class.forName("oracle.jdbc.OracleDriver");
		Connection conn = DriverManager.getConnection(
				"jdbc:oracle:thin:@//localhost:1521/xe", "ODSREPORT",
				"odsreport");
		DefaultCategoryDataset my_bar_chart_dataset = new DefaultCategoryDataset();

		System.out
				.println("Enter the date range for generating the overall tracker trend. \n"
						+ "Format of input: 'dd-mm-yy'\n"
						+ "Example: 26-Jan-10");

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		String from_date = br.readLine();

		String to_date = br.readLine();

		System.out.println("You entered the date range:" + from_date + "\tTo\t"
				+ to_date);

		try {

			Statement stmt = conn.createStatement();

			/*
			 * Query to obtain the year-wise count of trackers.
			 */
			my_bar_chart_dataset = queryResult(stmt, from_date, to_date,
					"New_Count", my_bar_chart_dataset);

			/*my_bar_chart_dataset = queryResult(stmt, from_date, to_date,
					"Closed_Count", my_bar_chart_dataset);

			my_bar_chart_dataset = queryResult(stmt, from_date, to_date,
					"Total_Pending", my_bar_chart_dataset);
*/
			// ResultSet query_set = stmt
			// .executeQuery("Select Insert_Date, Sum(New_Count) totalCount From Trend Where Insert_Date Between '"
			// + from_date
			// + "' And '"
			// + to_date
			// + "' Group By insert_Date order by insert_date");
			//
			// while (query_set.next()) {
			//
			// int count = query_set.getInt("totalCount");
			//
			// Date insert_date = query_set.getDate("Insert_Date");
			//
			// /*
			// * Add the resulting data to the dataset.
			// */
			// my_bar_chart_dataset.addValue(count, "Total", insert_date);
			//
			// }
			//
			// query_set.close();
			// stmt.close();
			//
			// Statement stmt2 = conn.createStatement();
			//
			// /*
			// * Query to obtain the year-wise count of trackers.
			// */
			// ResultSet query_set2 = stmt2
			// .executeQuery("Select Insert_Date, Sum(CLOSED_COUNT) totalCount From Trend Where Insert_Date Between '"
			// + from_date
			// + "' And '"
			// + to_date
			// + "' Group By insert_Date order by insert_date");
			//
			// while (query_set2.next()) {
			//
			// int count2 = query_set.getInt("totalCount");
			//
			// Date insert_date2 = query_set.getDate("Insert_Date");
			//
			// /*
			// * Add the resulting data to the dataset.
			// */
			// // my_bar_chart_dataset.addValue(count2, "Total", insert_date2);
			//
			// }
			// query_set2.close();
			// stmt2.close();
			//
			// Statement stmt3 = conn.createStatement();
			//
			// /*
			// * Query to obtain the year-wise count of trackers.
			// */
			// ResultSet query_set3 = stmt3
			// .executeQuery("Select Insert_Date, Sum(CLOSED_COUNT) totalCount From Trend Where Insert_Date Between '"
			// + from_date
			// + "' And '"
			// + to_date
			// + "' Group By insert_Date order by insert_date");
			//
			// while (query_set3.next()) {
			//
			// int count3 = query_set.getInt("totalCount");
			//
			// Date insert_date3 = query_set.getDate("Insert_Date");
			//
			// /*
			// * Add the resulting data to the dataset.
			// */
			// // my_bar_chart_dataset.addValue(count3, "Total", insert_date3);
			//
			// }
			// query_set3.close();
			// stmt3.close();

			stmt.close();
			conn.close();

			/*
			 * Generate the chart.
			 */
			JFreeChart BarChartObject = ChartFactory.createBarChart("Trackers",
					null, null, my_bar_chart_dataset, PlotOrientation.VERTICAL,
					true, true, false);

			CategoryPlot plot = (CategoryPlot) BarChartObject.getPlot();
			ValueAxis yAxis = plot.getRangeAxis();

			plot.setBackgroundPaint(Color.WHITE);
			plot.setRangeGridlinePaint(Color.BLACK);
			// yAxis.setRange(0, 50.0);

			/**
			 * Customizations for bar series.
			 */
			BarRenderer renderer = (BarRenderer) plot.getRenderer();
			renderer.setSeriesPaint(0, Color.blue);
			renderer.setDrawBarOutline(false);
			renderer.setMaximumBarWidth(.05);
			CategoryItemLabelGenerator generator = null;
			renderer.setBaseItemLabelGenerator(generator);
			renderer.setBaseItemLabelsVisible(true);

			// set label appearance and position
			CategoryItemLabelGenerator lblGenerator = new StandardCategoryItemLabelGenerator();
			renderer.setBaseItemLabelGenerator(lblGenerator);
			renderer.setBaseItemLabelsVisible(true);
			renderer.setBaseItemLabelPaint(Color.black);

			// Fallback for a positive value if the value does not fit inside
			// the bar
			renderer.setPositiveItemLabelPositionFallback(new ItemLabelPosition(
					ItemLabelAnchor.OUTSIDE10, TextAnchor.CENTER_RIGHT));

			// Fallback for a negative value if the value does not fit inside
			// the bar
			renderer.setNegativeItemLabelPositionFallback(new ItemLabelPosition(
					ItemLabelAnchor.OUTSIDE10, TextAnchor.CENTER_RIGHT));

			renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(
					ItemLabelAnchor.INSIDE12, TextAnchor.TOP_CENTER));

			renderer.setBaseNegativeItemLabelPosition(new ItemLabelPosition(
					ItemLabelAnchor.INSIDE12, TextAnchor.TOP_CENTER));

			int width = 640; /* Width of the image */
			int height = 480; /* Height of the image */

			/*
			 * Save the chart in a file.
			 */
			File BarChart = new File(
					"JssOverallTrackerTrendFromTrendTable2.png");
			ChartUtilities.saveChartAsPNG(BarChart, BarChartObject, width,
					height);

		} catch (SQLDataException e) {
			System.out.println("\nInput data is incorrect" + e.getMessage());
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {

		}
	}

	private static DefaultCategoryDataset queryResult(Statement stmt,
			String from_date, String to_date, String status,
			DefaultCategoryDataset my_bar_chart_dataset) throws SQLException {
		ResultSet query_set = stmt
				.executeQuery("Select Insert_Date, Sum(New_Count) totalCount From Trend Where Insert_Date Between '"
						+ from_date
						+ "' And '"
						+ to_date
						+ "' Group By insert_Date order by insert_date");

		while (query_set.next()) {

			int count = query_set.getInt("totalCount");

			Date insert_date = query_set.getDate("Insert_Date");

			/*
			 * Add the resulting data to the dataset.
			 */
			my_bar_chart_dataset.addValue(count, "Total", insert_date);

		}
		query_set.close();
		return my_bar_chart_dataset;
	}
}
