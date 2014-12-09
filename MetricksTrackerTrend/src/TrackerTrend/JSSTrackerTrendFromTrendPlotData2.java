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
public class JSSTrackerTrendFromTrendPlotData2 {

	private static Connection conn;

	private static PreparedStatement stmt;

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

		System.out
				.println("Enter the date range for generating the JSS tracker trend. \n"
						+ "Format of input: 'YYYY'\n" + "Example: 2014");

		System.out
				.println("Please enter the 'from_date' in the format mentioned above: \t");

		String from_date = br.readLine();

		System.out
				.println("Please enter the 'from_date' in the format mentioned above: \t");

		String to_date = br.readLine();

		System.out.println("You entered the date range:" + from_date + "\t To "
				+ to_date);

		try {

			my_bar_chart_dataset = queryResult(my_bar_chart_dataset, from_date,
					to_date);

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
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("\nSQL exception" + e.getMessage());
			e.printStackTrace();

		}
	}

	/**
	 * Populate the dataset.
	 * 
	 * @param my_bar_chart_dataset
	 * @param from_date
	 * @param to_date
	 * @return my_bar_chart_dataset
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
						// + "Where Extract(Year From Insert_Date) Between ?"
						// + " And ? "
						+ "Order By insert_date");

		// stmt.setString(1, from_date);
		// stmt.setString(2, to_date);

		ResultSet query_set = stmt.executeQuery();

		while (query_set.next()) {

			int count = query_set.getInt("count");

			int month = query_set.getInt("insert_month");

			int year = query_set.getInt("insert_year");

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
		}// end of while loop

		System.out.println("Map contents= " + trendMap);

		/**
		 * Iterate over the tree map to get the year wise data.
		 */
		for (Map.Entry<Integer, Integer> entry : trendMap.entrySet()) {

			int year = entry.getKey();
			int sum = entry.getValue();

			/**
			 * Add the resulting data to the dataset.
			 */

			String year_val = Integer.toString(year);

			if (year >= Integer.parseInt(from_date)
					&& year <= Integer.parseInt(to_date))

				my_bar_chart_dataset.addValue(sum, "Total", year_val);

		}
		query_set.close();

		return my_bar_chart_dataset;
	}
}
