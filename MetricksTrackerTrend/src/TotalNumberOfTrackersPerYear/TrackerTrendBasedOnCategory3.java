package TotalNumberOfTrackersPerYear;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

public class TrackerTrendBasedOnCategory3 {

	public static void main(String[] args) throws Exception {

		System.out.println("Enter the category name"
				+ " Default category type with ID \t 1.DEFAULT- 1,"
				+ " \n Editorial category time with ID \t 2.EDITORIAL-2),"
				+ "\n Metadata category type with ID \t 3.METADATA-3,"
				+ "\n WLNV category type with ID \t 4.WLNV- 4,"
				+ "\n REPOSITORIES category type with ID \t 5.REPOSITORIES-5,"
				+ "\n RAT category type with ID \t 6.RAT-6");

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("\nYour choice? \n");

		String categoryName = br.readLine();

		Class.forName("oracle.jdbc.OracleDriver");
		Connection conn = DriverManager.getConnection(
				"jdbc:oracle:thin:@//localhost:1521/xe", "ODSREPORT",
				"odsreport");
		DefaultCategoryDataset my_bar_chart_dataset = new DefaultCategoryDataset();

		Statement stmt = conn.createStatement();

		try {

			ResultSet query_set = stmt
					.executeQuery("Select Extract(Year From T.Create_Date) Year, Count(T.Bug_Number) count from tracker t, application app,component com, category c where t.application_id=app.id and app.component_id=com.id and com.category_id=c.id and c.name='"
							+ categoryName
							+ "' Group By Extract(Year From T.Create_Date) ORDER BY Year asc");

			while (query_set.next()) {

				int count = query_set.getInt("count");

				String year = query_set.getString("Year");

				my_bar_chart_dataset.addValue(count, "Trackers", year);

			}
			JFreeChart BarChartObject = ChartFactory.createBarChart(
					categoryName.toUpperCase(), "", "", my_bar_chart_dataset,
					PlotOrientation.VERTICAL, true, true, false);

			CategoryPlot plot = (CategoryPlot) BarChartObject.getPlot();
			// ValueAxis yAxis = plot.getRangeAxis();

			plot.setBackgroundPaint(Color.WHITE);
			plot.setRangeGridlinePaint(Color.BLACK);
			// yAxis.setRange(0, 50.0);

			BarRenderer renderer = (BarRenderer) plot.getRenderer();
			renderer.setSeriesPaint(0, Color.blue);
			renderer.setDrawBarOutline(false);
			renderer.setMaximumBarWidth(.05);

			query_set.close();
			stmt.close();
			conn.close();
			int width = 480; /* Width of the image */
			int height = 410; /* Height of the image */
			File BarChart = new File("IncomingTrackerTrend.png");
			ChartUtilities.saveChartAsPNG(BarChart, BarChartObject, width,
					height);
		} catch (Exception e) {
			System.out.println(e);
		}

	}
}
