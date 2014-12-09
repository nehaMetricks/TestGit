//public class TrackerTrendBasedOnCategoryComputationMe 

package TrackerTrend;

/*
 * Created by Neha(uc167165).
 */

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
public class IncomingTrackerTrendBasedOnCategory {

	private static int sumCount = 0;

	static Map<String, Integer> trendMap = new TreeMap<String, Integer>();

	static List<Integer> countList = new ArrayList<Integer>();

	public static void main(String[] args) throws Exception {

		/*
		 * JDBC connection string to connect to the local database.
		 */
		Class.forName("oracle.jdbc.OracleDriver");
		Connection conn = DriverManager.getConnection(
				"jdbc:oracle:thin:@//localhost:1521/xe", "ODSREPORT",
				"odsreport");
		DefaultCategoryDataset my_bar_chart_dataset = new DefaultCategoryDataset();

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Enter the category id you want the trend for.\n"
				+ "1-default \n" + "2-editorial \n" + "3-java metadata \n"
				+ "4-java mainframe \n" + "5-repositories \n" + "6-rat \n");

		String category_id = br.readLine();

		// System.out
		// .println("Enter the date range for generating the overall tracker trend. \n"
		// + "Format of input: 'dd-mm-yy'\n"
		// + "Example: 26-Jan-10");
		//
		// System.out
		// .println("Please enter the 'from_date' in the format mentioned above: \t");
		//
		// String from_date = br.readLine();
		//
		// System.out
		// .println("Please enter the 'from_date' in the format mentioned above: \t");
		//
		// String to_date = br.readLine();
		//
		// System.out.println("You entered the date range:" + from_date +
		// "\tTo\t"
		// + to_date + "\t and category id:" + category_id);

		Statement stmt = conn.createStatement();

		try {

			my_bar_chart_dataset = queryResult(stmt, my_bar_chart_dataset,
					category_id);

			/*
			 * my_bar_chart_dataset = queryResult(stmt, from_date, to_date,
			 * "Closed_Count", my_bar_chart_dataset);
			 * 
			 * my_bar_chart_dataset = queryResult(stmt, from_date, to_date,
			 * "Total_Pending", my_bar_chart_dataset);
			 */

			String category_name = getCategoryName(conn, category_id)
					.toUpperCase();

			/*
			 * Generate the chart.
			 */
			JFreeChart BarChartObject = ChartFactory.createBarChart(
					category_name, // chart
					// title
					null, // domain axis label
					null, // range axis label
					my_bar_chart_dataset, // data
					PlotOrientation.VERTICAL, // orientation
					true, // include legend
					true, // tooltips?
					false // URLs?
					);

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
					ItemLabelAnchor.CENTER, TextAnchor.CENTER,
					TextAnchor.CENTER, 0.0));

			// Fallback for a negative value if the value does not fit inside
			// the bar
			renderer.setNegativeItemLabelPositionFallback(new ItemLabelPosition(
					ItemLabelAnchor.CENTER, TextAnchor.CENTER,
					TextAnchor.CENTER, 0.0));

			renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(
					ItemLabelAnchor.OUTSIDE12, TextAnchor.TOP_CENTER,
					TextAnchor.HALF_ASCENT_CENTER, 0D));

			renderer.setBaseNegativeItemLabelPosition(new ItemLabelPosition(
					ItemLabelAnchor.OUTSIDE12, TextAnchor.CENTER,
					TextAnchor.CENTER, 0.0));

			stmt.close();
			conn.close();

			int width = 640; /* Width of the image */
			int height = 480; /* Height of the image */

			/*
			 * Save the chart in a file.
			 */
			File BarChart = new File("IncomingTrackerTrendBasedOnCategory.png");
			ChartUtilities.saveChartAsPNG(BarChart, BarChartObject, width,
					height);

		} catch (SQLDataException e) {
			System.out.println("\nInput data is incorrect" + e.getMessage());
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	/**
	 * Get category name from the given id.
	 * 
	 * @param conn
	 * @param category_id
	 * @return
	 * @throws SQLException
	 */
	private static String getCategoryName(Connection conn, String category_id)
			throws SQLException {
		// TODO Auto-generated method stub
		Statement stmt = conn.createStatement();

		String category_name = null;

		ResultSet query_set = stmt
				.executeQuery("select name from category where id="
						+ category_id);

		while (query_set.next()) {
			category_name = query_set.getString("name");
		}

		return category_name;
	}

	/**
	 * Populate the dataset based on the category.
	 * 
	 * @param stmt
	 * @param my_bar_chart_dataset
	 * @param category_id
	 * @return
	 * @throws SQLException
	 */
	private static DefaultCategoryDataset queryResult(Statement stmt,
			DefaultCategoryDataset my_bar_chart_dataset, String category_id)
			throws SQLException {
		ResultSet query_set = stmt
				.executeQuery("Select New_Count-Lag(New_Count) "
						+ "Over (Order By Extract(Year From Insert_Date)) Trendcount, "
						+ "Extract(Year From Insert_Date) year "
						+ "From Trend  " + "Where Category_Id= " + category_id
						+ "Group By new_count, Extract(Year From Insert_Date) "
						+ "Order By Extract(Year From Insert_Date)");

		while (query_set.next()) {

			int count = query_set.getInt("Trendcount");

			String year = query_set.getString("year");

			if (trendMap.isEmpty()) {
				sumCount += count;
				trendMap.put(year, sumCount);
			} else if (trendMap.containsKey(year)) {
				sumCount += count;
				trendMap.put(year, sumCount);
			} else {
				sumCount = 0;
				sumCount += count;
				trendMap.put(year, sumCount);
			}
		}
		System.out.println(trendMap);

		for (Map.Entry<String, Integer> entry : trendMap.entrySet()) {

			String year = entry.getKey();
			int sumCount = entry.getValue();

			/*
			 * Add the resulting data to the dataset.
			 */
			my_bar_chart_dataset.addValue(sumCount, "Total", year);
		}
		query_set.close();

		return my_bar_chart_dataset;
	}

}
