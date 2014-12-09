//public class TrackerTrendBasedOnCategoryComputationMe 

package TotalNumberOfTrackersPerYear;

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
import java.util.Date;
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
public class TrackerTrendBasedOnCategoryComputationMe {

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

		System.out
				.println("Enter the date range for generating the overall tracker trend. \n"
						+ "Format of input: 'dd-mm-yy'\n"
						+ "Example: 26-Jan-10");

		System.out
				.println("Please enter the 'from_date' in the format mentioned above: \t");

		String from_date = br.readLine();

		System.out
				.println("Please enter the 'from_date' in the format mentioned above: \t");

		String to_date = br.readLine();

		System.out.println("You entered the date range:" + from_date + "\tTo\t"
				+ to_date + "\t and category id:" + category_id);

		Statement stmt = conn.createStatement();

		try {

			my_bar_chart_dataset = queryResult(stmt, my_bar_chart_dataset,
					category_id, from_date, to_date, conn);
			/*
			 * Generate the chart.
			 */
			JFreeChart BarChartObject = ChartFactory.createBarChart("Trackers", // chart
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
			Color c1 = Color.decode("0x00478F");
			Color c2 = Color.decode("0x6B008F");
			Color c3 = Color.decode("0x000047");

			renderer.setSeriesPaint(0, c1);
			renderer.setSeriesPaint(1, c2);
			renderer.setSeriesPaint(3, c3);
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

			stmt.close();
			conn.close();

			int width = 2000; /* Width of the image */
			int height = 480; /* Height of the image */

			/*
			 * Save the chart in a file.
			 */
			File BarChart = new File(
					"TrackerTrendBasedOnCategoryFromTrendTable.png");
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
	 * @throws ClassNotFoundException
	 */
	private static DefaultCategoryDataset queryResult(Statement stmt,
			DefaultCategoryDataset my_bar_chart_dataset, String category_id,
			String from_date, String to_date, Connection conn)
			throws SQLException, ClassNotFoundException {
		ResultSet query_set = stmt
				.executeQuery("SELECT insert_date, extract(year from insert_date) year, new_count- LAG(new_count) "
						+ "OVER (ORDER BY insert_date) trendCount "
						+ "From Trend  where category_id="
						+ category_id
						+ "and insert_date between '"
						+ from_date
						+ "' and '"
						+ to_date + "'");

		while (query_set.next()) {

			int count = query_set.getInt("trendCount");

			String insert_date = query_set.getString("insert_date");

			String year = query_set.getString("year");

			if (count == 0) {
				computeInitialCount(year, category_id);
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

	/**
	 * Compute the initial count.
	 * 
	 * @param year
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	private static int computeInitialCount(String year, String category_id)
			throws SQLException, ClassNotFoundException {
		Class.forName("oracle.jdbc.OracleDriver");
		Connection conn = DriverManager.getConnection(
				"jdbc:oracle:thin:@//localhost:1521/xe", "ODSREPORT",
				"odsreport");

		Statement stmt = conn.createStatement();

		int initCount = 0;

		int yearVal = Integer.parseInt(year);

		ResultSet query_set = stmt
				.executeQuery("Select b.insert_date insertDate, (b.New_Count-a.new_count) trendCount"
						+ "From "
						+ "(Select New_Count, insert_date "
						+ "From Trend "
						+ "Where Insert_Date "
						+ "In "
						+ "(Select Max(Insert_Date) "
						+ "From Trend "
						+ "Where Extract(Year From Insert_Date) = '"
						+ yearVal
						+ "')  "
						+ "And Category_Id="
						+ category_id
						+ ") A, "
						+ "(Select New_Count , insert_date "
						+ "From Trend "
						+ "Where Insert_Date "
						+ "In "
						+ "(Select Min(Insert_Date) "
						+ "From Trend "
						+ "Where Extract(Year From Insert_Date) = '"
						+ (yearVal - 1)
						+ "') "
						+ "And Category_Id="
						+ category_id + ") B");

		while (query_set.next()) {

			initCount = query_set.getInt("trendCount");

			 year= query_set.getString("insertDate");
		}

		return initCount;
	}
}
