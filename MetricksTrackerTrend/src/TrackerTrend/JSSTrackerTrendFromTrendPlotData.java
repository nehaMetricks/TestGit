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

import java.sql.PreparedStatement;

/**
 * Program to generate year-wise JSS Tracker trend.
 * 
 * @author UC167165.
 * 
 */
public class JSSTrackerTrendFromTrendPlotData {

	private static Connection conn;

	private static PreparedStatement stmt;

	// private static int sumCount = 0;

	// static private Map<Integer, Integer> trendMap = new TreeMap<Integer,
	// Integer>();

	// static List<Integer> countList = new ArrayList<Integer>();

	public static void main(String[] args) throws Exception {

		/*
		 * JDBC connection string to connect to the local database.
		 */
		Class.forName("oracle.jdbc.OracleDriver");
		conn = DriverManager.getConnection(
				"jdbc:oracle:thin:@//localhost:1521/xe", "ODSREPORT",
				"odsreport");
		DefaultCategoryDataset my_bar_chart_dataset = new DefaultCategoryDataset();

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Enter the category id you want the trend for.\n"
				+ "1-default \n" + "2-editorial \n" + "3-java metadata \n"
				+ "4-java mainframe \n" + "5-repositories \n" + "6-rat \n");

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
				+ to_date);

		try {

			my_bar_chart_dataset = queryResult(my_bar_chart_dataset, from_date,
					to_date);

			/*
			 * my_bar_chart_dataset = queryResult(stmt, from_date, to_date,
			 * "Closed_Count", my_bar_chart_dataset);
			 * 
			 * my_bar_chart_dataset = queryResult(stmt, from_date, to_date,
			 * "Total_Pending", my_bar_chart_dataset);
			 */

			// String category_name = getCategoryName(conn, category_id)
			// .toUpperCase();

			/*
			 * Generate the chart.
			 */
			JFreeChart BarChartObject = ChartFactory.createBarChart(
					"JSS TRACKERS", // chart
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
			File BarChart = new File("JSSTrackerTrend.png");
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
	private static DefaultCategoryDataset queryResult(
			DefaultCategoryDataset my_bar_chart_dataset, String from_date,
			String to_date) throws SQLException {

		Map<Integer, Integer> trendMap = new TreeMap<Integer, Integer>();

		int sumCount = 0;

		stmt = conn
				.prepareStatement("select new_count count, extract(month from insert_date) insert_month, extract(year from insert_date) insert_year "
						+ "from trend_plot_data "
						+ "Where Extract(Year From Insert_Date) Between ?"
						+ " And ? " + "Order By insert_date");

		stmt.setString(1, from_date);
		stmt.setString(2, to_date);

		ResultSet query_set = stmt.executeQuery();

		while (query_set.next()) {

			int count = query_set.getInt("count");

			int month = query_set.getInt("insert_month");

			String year_val = query_set.getString("insert_year");

			int year = Integer.parseInt(year_val);

			if (trendMap.isEmpty() && month == 1) {
				sumCount += count;
				trendMap.put(year - 1, sumCount);
			} else if (month == 1 && (!trendMap.containsKey(year))) {
				sumCount += count;
				trendMap.put(year - 1, sumCount);
			} else if (trendMap.containsKey(year) && month != 1) {
				sumCount += count;
				trendMap.put(year, sumCount);
			} else {
				sumCount = 0;
				sumCount += count;
				trendMap.put(year, sumCount);
			}
		}
		System.out.println("Map contents= " + trendMap);

		for (Map.Entry<Integer, Integer> entry : trendMap.entrySet()) {

			int year = entry.getKey();
			int sum = entry.getValue();

			/*
			 * Add the resulting data to the dataset.
			 */

			String year_int = Integer.toString(year);
			if (year >= Integer.parseInt(from_date))
				my_bar_chart_dataset.addValue(sum, "Total", year_int);
		}
		query_set.close();

		return my_bar_chart_dataset;
	}
}
