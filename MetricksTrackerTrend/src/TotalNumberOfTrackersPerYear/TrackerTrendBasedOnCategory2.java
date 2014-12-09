package TotalNumberOfTrackersPerYear;

import java.awt.Color;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

public class TrackerTrendBasedOnCategory2 {

	public static void main(String[] args) throws Exception {

		Map<String, Integer> trendMap = new HashMap<String, Integer>();

		Class.forName("oracle.jdbc.OracleDriver");
		Connection conn = DriverManager.getConnection(
				"jdbc:oracle:thin:@//localhost:1521/xe", "ODSREPORT",
				"odsreport");
		DefaultCategoryDataset my_bar_chart_dataset = new DefaultCategoryDataset();

		Statement stmt = conn.createStatement();

		try {

			ResultSet query_set = stmt
					.executeQuery("Select Extract(Year From T.Create_Date) Year, Count(T.Bug_Number) count from tracker t, application app,component com, category c where t.application_id=app.id and app.component_id=com.id and com.category_id=c.id and c.name='editorial' Group By Extract(Year From T.Create_Date) ORDER BY Year asc");

			while (query_set.next()) {

				int count = query_set.getInt("count");

				String year = query_set.getString("Year");

				trendMap.put(year, count);

				System.out.println(count + "\t" + year);

				// my_bar_chart_dataset.addValue(count, "Trackers", year);

			}

			trendComputation(trendMap);

			JFreeChart BarChartObject = ChartFactory.createBarChart(
					"Editorial", "", "", my_bar_chart_dataset,
					PlotOrientation.VERTICAL, true, true, false);

			CategoryPlot plot = (CategoryPlot) BarChartObject.getPlot();
			ValueAxis yAxis = plot.getRangeAxis();

			plot.setBackgroundPaint(Color.WHITE);
			plot.setRangeGridlinePaint(Color.BLACK);
			yAxis.setRange(0, 50.0);

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

	private static void trendComputation(Map<String, Integer> trendMap) {

		for (Map.Entry<String, Integer> entry : trendMap.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			System.out.println("key=" + key + "\t" + "Value=" + value);
		}
	}

}
