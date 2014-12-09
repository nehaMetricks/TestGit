package TrackerTrend;

/*
 * Created by Neha(uc167165).
 * Incoming tracker trend
 */

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.Statement;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleEdge;

/**
 * Program to generate year-wise JSS Tracker trend.
 * 
 * @author UC167165.
 * 
 */
public class IncomingTrackerTrendBasedOnCategoryComputeEndCount {

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
						+ "Enter the year" + "Example: 2010");

		System.out
				.println("Please enter the 'from_date' in the format mentioned above: \t");

		String from_date = br.readLine();

		System.out
				.println("Please enter the 'to_date' in the format mentioned above: \t");

		String to_date = br.readLine();

		System.out.println("You entered the date range:" + from_date + "\tTo\t"
				+ to_date + "\t and category id:" + category_id);

		Statement stmt = conn.createStatement();

		try {

			// my_bar_chart_dataset =
			queryResult(stmt, my_bar_chart_dataset, category_id, from_date,
					to_date);

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
			// BasicStroke stroke = new Stroke();
			// plot.setRangeGridlineStroke(stroke);
			plot.setOutlineVisible(false);

			// yAxis.setRange(0, 50.0);

			/**
			 * Customizations for bar series.
			 */
			BarChartObject.setBackgroundPaint(Color.WHITE);
			BarChartObject.setBorderVisible(true);
			BarRenderer renderer = (BarRenderer) plot.getRenderer();

			// set color of the bar chart
			Color c = Color.decode("0x0066CC");
			// Set image background color to grey
			// Color c2 = Color.decode("0xD5D4D4");
			// BarChartObject.setBackgroundPaint(c2);

			// set the type of painter
			renderer.setBarPainter(new StandardBarPainter());

			// Type and size of the title
			BarChartObject.getTitle().setFont(
					new Font("Calibri (Body)", Font.BOLD, 18));

			// Set position of the legend
			LegendTitle legend = BarChartObject.getLegend();
			legend.setPosition(RectangleEdge.RIGHT);
			legend.setBorder(0, 0, 0, 0);
			renderer.setSeriesPaint(0, c);
			renderer.setDrawBarOutline(false);
			renderer.setMaximumBarWidth(.08);
			CategoryItemLabelGenerator generator = null;
			renderer.setBaseItemLabelGenerator(generator);
			renderer.setBaseItemLabelsVisible(true);

			CategoryAxis categoryAxis = new CategoryAxis("Categories");
			// categoryAxis.setLowerMargin(.05);// distance between new and
			// closed bars
			// categoryAxis.setCategoryMargin(.05);
			// categoryAxis.setUpperMargin(.05);

			categoryAxis
					.setCategoryLabelPositions(CategoryLabelPositions.UP_90);

			// set label appearance and position
			CategoryItemLabelGenerator lblGenerator = new StandardCategoryItemLabelGenerator();
			renderer.setBaseItemLabelGenerator(lblGenerator);
			renderer.setBaseItemLabelsVisible(true);
			renderer.setBaseItemLabelPaint(Color.black);

			// to change thr gridline stroke
			renderer.setSeriesStroke(0, new BasicStroke(1.2f,
					BasicStroke.JOIN_ROUND, BasicStroke.JOIN_ROUND));
			plot.setDomainGridlineStroke(new BasicStroke(0.5f,
					BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

			// To set tick count for the domain axis
			// NumberAxis numberAxis = (NumberAxis) plot.getRangeAxis();
			// numberAxis.setTickUnit(new NumberTickUnit(20));

			// optional label specifications
			/*
			 * // Fallback for a positive value if the value does not fit inside
			 * // the bar renderer.setPositiveItemLabelPositionFallback(new
			 * ItemLabelPosition( ItemLabelAnchor.CENTER, TextAnchor.CENTER,
			 * TextAnchor.CENTER, 0.0));
			 * 
			 * // Fallback for a negative value if the value does not fit inside
			 * // the bar renderer.setNegativeItemLabelPositionFallback(new
			 * ItemLabelPosition( ItemLabelAnchor.CENTER, TextAnchor.CENTER,
			 * TextAnchor.CENTER, 0.0));
			 * 
			 * renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(
			 * ItemLabelAnchor.OUTSIDE12, TextAnchor.TOP_CENTER,
			 * TextAnchor.HALF_ASCENT_CENTER, 0D));
			 * 
			 * renderer.setBaseNegativeItemLabelPosition(new ItemLabelPosition(
			 * ItemLabelAnchor.OUTSIDE12, TextAnchor.CENTER, TextAnchor.CENTER,
			 * 0.0));
			 */

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
	 * @throws ClassNotFoundException
	 */
	private static void queryResult(Statement stmt,
			DefaultCategoryDataset my_bar_chart_dataset, String category_id,
			String fromdate, String todate) throws SQLException,
			ClassNotFoundException {

		int from_date = 0;
		int inputFromDate = Integer.parseInt(fromdate);
		int inputToDate = Integer.parseInt(todate);

		for (from_date = inputFromDate; from_date <= inputToDate; from_date++) {
			ResultSet query_set = stmt
					.executeQuery("select sum(newcount) count from ("
							+ "(Select b.insert_date,(b.New_Count-a.new_count) newcount From (Select  New_Count,insert_date "
							+ "From Trend "
							+ "Where CAST(insert_date AS DATE) In "
							+ "(Select Min(CAST(insert_date AS DATE)) From Trend Where Extract(Year From Insert_Date) ="
							+ (from_date + 1)
							+ ") "
							+ "And Category_Id = "
							+ category_id
							+ ") B, "
							+ "(Select New_Count ,insert_date "
							+ "From Trend "
							+ "Where CAST(insert_date AS DATE) In "
							+ "(Select Max(CAST(insert_date AS DATE)) From Trend Where Extract(Year From Insert_Date) = "
							+ from_date
							+ ") "
							+ "And Category_Id= "
							+ category_id
							+ ") A) "
							+ "union "
							+ "(SELECT distinct insert_date, "
							+ "New_Count- Lag(New_Count) Over (Order By Insert_Date)  As Newdiff "
							+ "From Trend "
							+ "where category_id= "
							+ category_id
							+ " and extract(year from insert_date) = "
							+ from_date
							+ ") "
							+ ") "
							+ "Where Newcount Is Not Null");

			while (query_set.next()) {

				int count = query_set.getInt("count");

				System.out.println(from_date + " " + count);

				String yearVal = Integer.toString(from_date);

				/*
				 * Add the resulting data to the dataset.
				 */
				my_bar_chart_dataset.addValue(count, "Total", yearVal);

			}

			/*
			 * Add the resulting data to the dataset.
			 */
			// my_bar_chart_dataset.addValue(sumCount, "Total", year);

			query_set.close();
		}
	}
}