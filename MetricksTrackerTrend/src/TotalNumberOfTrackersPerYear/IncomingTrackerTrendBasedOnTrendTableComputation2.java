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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
public class IncomingTrackerTrendBasedOnTrendTableComputation2 {

	static Map<Integer, List<Integer>> trendMap2 = new HashMap<Integer, List<Integer>>();

	static String key = null;

	static Object value = null;

	static List<Object> count = new ArrayList<>();

	private static int diff;

	static List<Object> diffCount = new ArrayList<>();

	private static int sumCount;

	static List<Integer> valueList = new ArrayList<>();

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

		// String category_id = br.readLine();

		String category_id = "2";

		System.out
				.println("Enter the date range for generating the overall tracker trend. \n"
						+ "Format of input: 'dd-mm-yy'\n"
						+ "Example: 26-Jan-10");

		System.out
				.println("Please enter the 'from_date' in the format mentioned above: \t");

		// String from_date = br.readLine();

		String from_date = "01-jan-11";

		System.out
				.println("Please enter the 'from_date' in the format mentioned above: \t");

		// String to_date = br.readLine();

		String to_date = "31-dec-12";

		System.out.println("You entered the date range:" + from_date + "\tTo\t"
				+ to_date + "\t and category id:" + category_id);

		Statement stmt = conn.createStatement();

		try {

			my_bar_chart_dataset = queryResult(stmt, from_date, to_date,
					"New_Count", my_bar_chart_dataset, category_id);

			/*
			 * my_bar_chart_dataset = queryResult(stmt, from_date, to_date,
			 * "Closed_Count", my_bar_chart_dataset);
			 * 
			 * my_bar_chart_dataset = queryResult(stmt, from_date, to_date,
			 * "Total_Pending", my_bar_chart_dataset);
			 */

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

			stmt.close();
			conn.close();

			int width = 640; /* Width of the image */
			int height = 480; /* Height of the image */

			/*
			 * Save the chart in a file.
			 */
			File BarChart = new File(
					"IncomingTrackerTrendBasedOnCategoryFromTrendTable.png");
			ChartUtilities.saveChartAsPNG(BarChart, BarChartObject, width,
					height);

		} catch (SQLDataException e) {
			System.out.println("\nInput data is incorrect" + e.getMessage());
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	private static DefaultCategoryDataset queryResult(Statement stmt,
			String from_date, String to_date, String status,
			DefaultCategoryDataset my_bar_chart_dataset, String category_id)
			throws SQLException {
		ResultSet query_set = stmt
				.executeQuery("Select Extract(Year From Insert_Date) Year, New_Count "
						+ "From Trend "
						+ "Where Insert_Date Between '"
						+ from_date
						+ "' And '"
						+ to_date
						+ "' And Category_Id="
						+ category_id
						+ "Order by extract(year from insert_date), new_count");

		while (query_set.next()) {

			int count = query_set.getInt("New_Count");

			int year = query_set.getInt("Year");

			// int yearInteger=year;

			//System.out.println(year + "and" + count);
	
			if (trendMap2.isEmpty()) {
				trendMap2.put(year, valueList);
			}
			if (!trendMap2.containsKey(year)) {
				valueList.clear();
				//valueList.add(count);
				trendMap2.put(year , valueList);
			} 
			
			valueList.add(count);
			
			System.out.println("valueList:"+valueList);

			// trendMap2.put(year, count);

			// if (!trendMap2.containsKey(year)|| year==null) {
			//
			// sumCount = trendComputation(trendMap2);
			// } else
			// continue;

			/*
			 * Add the resulting data to the dataset.
			 */
			// my_bar_chart_dataset.addValue(sumCount, "Total", year);

		}
		query_set.close();
		

		System.out.println(trendMap2.size() + "\n");

		// Set<String> keys = trendMap2.keySet();
		//
		// for (Iterator i = keys.iterator(); i.hasNext();) {
		// Object key = i.next();
		// List value = trendMap2.get(key);
		// System.out.println(key + " = " + value);
		// }

		trendComputation(trendMap2);
		
		trendMap2.clear();
		valueList.clear();

		return my_bar_chart_dataset;
	}

	private static int trendComputation(Map<Integer, List<Integer>> trendMap) {

		int sum = 0;

		Set<Integer> keys = trendMap.keySet();

		for (Iterator i = keys.iterator(); i.hasNext();) {
			Object key = i.next();
			List value = trendMap.get(key);
			System.out.println(key + " = " + value);
		}

		/*
		 * for (Map.Entry<Integer, List<Integer>> entry : trendMap2.entrySet())
		 * {
		 * 
		 * int key = entry.getKey(); List<Integer> value = entry.getValue(); for
		 * (int i = 0; i < value.size(); i++) { System.out.println("key : " +
		 * key + " value : " + value.get(i));
		 * 
		 * int diff = value.get(i + 1) - value.get(i); //
		 * System.out.println(diff); sum += diff;
		 * 
		 * // System.out.println(" " + sum);
		 * 
		 * // count.add(trendMap22.get(key)); } }
		 */
		// System.out.println("count list:" + count);
		//
		// for (int i = 0; i < count.size() - 1; i++) {
		//
		// // System.out.println(i + " " + count.get(i + 1) + "\tprevious\t"
		// // + count.get(i));
		//
		// int diff = (int) count.get(i + 1) - (int) count.get(i);
		//
		// diffCount.add(diff);
		//
		// sum += diff;
		//
		// // System.out.println(" " + sum);
		// }
		//
		// System.out.println("plot the sum: " + sum);
		// // for (int i = 0; i < diffCount.size(); i++) {
		// // System.out.println("difference:" + diffCount.get(i) + "\t"
		// // + diffCount.size());
		// // }

		return sum;
	}
}
