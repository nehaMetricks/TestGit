package TotalNumberOfTrackersPerYear;

import java.awt.Color;
import java.io.File;
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

public class TrackerTrendBasedOnCategory {

	public static void main(String[] args) throws Exception {

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

				my_bar_chart_dataset.addValue(count, "Trackers", year);

			}
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

}
