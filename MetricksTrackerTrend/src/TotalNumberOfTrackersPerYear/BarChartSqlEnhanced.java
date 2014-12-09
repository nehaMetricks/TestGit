package TotalNumberOfTrackersPerYear;

/*
 * Created by Neha(uc167165).
 */

import java.awt.Color;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * Program to generate year-wise JSS Tracker trend.
 * 
 * @author UC167165.
 * 
 */
public class BarChartSqlEnhanced {
	public static void main(String[] args) throws Exception {

		/*
		 * JDBC connection string to connect to the local database.
		 */
		Class.forName("oracle.jdbc.OracleDriver");
		Connection conn = DriverManager.getConnection(
				"jdbc:oracle:thin:@//localhost:1521/xe", "ODSREPORT",
				"odsreport");
		DefaultCategoryDataset my_bar_chart_dataset = new DefaultCategoryDataset();

		Statement stmt = conn.createStatement();

		try {

			/*
			 * Query to obtain the year-wise count of trackers.
			 */
			ResultSet query_set = stmt
					.executeQuery("select extract(YEAR from CREATE_DATE) year, count(CREATE_DATE) totalCount from TRACKER group by extract(year from CREATE_DATE) order by year asc");

			while (query_set.next()) {

				int count = query_set.getInt("totalCount");

				String year = query_set.getString("year");

				/*
				 * Add the resulting data to the dataset.
				 */
				my_bar_chart_dataset.addValue(count, "Total", year);

			}
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

			query_set.close();
			stmt.close();
			conn.close();

			int width = 640; /* Width of the image */
			int height = 480; /* Height of the image */

			/*
			 * Save the chart in a file.
			 */
			File BarChart = new File("YearWiseTotalCount.png");
			ChartUtilities.saveChartAsPNG(BarChart, BarChartObject, width,
					height);

		} catch (SQLException e) {
			System.out.println(e);
		}
	}
}
