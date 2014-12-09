package TotalNumberOfTrackersPerYear;

/*
 * Created by Neha(uc167165).
 */

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Stroke;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.border.StrokeBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.TextAnchor;

public class BarChartSqlEnhancedLookAndFeel {

	/**
	 * Program to generate year-wise JSS Tracker trend.
	 * 
	 * @author UC167165.
	 * 
	 */
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
					true, true, true);

			// // create the chart...
			// final JFreeChart chart =
			// ChartFactory.createBarChart("Tracker Trend "
			// + category, // chart
			// // title
			// "Months", // domain axis label
			// "No of Trackers", // range axis label
			// dataset, // data
			// PlotOrientation.VERTICAL, // orientation
			// true, // include legend
			// true, // tooltips?
			// false // URLs?
			// );

			// NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

			// set the background color for the chart...
			BarChartObject.setBackgroundPaint(Color.white);

			// get a reference to the plot for further customisation...
			final CategoryPlot plot = BarChartObject.getCategoryPlot();
			plot.setBackgroundPaint(Color.white);
			plot.setDomainGridlinePaint(Color.white);
			plot.setRangeGridlinePaint(Color.white);

			BarRenderer barRenderer = (BarRenderer) plot.getRenderer();
			barRenderer.setSeriesPaint(0, Color.blue);

			// set the range axis to display integers only...
			final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
			rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

			// disable bar outlines...
			barRenderer.setDrawBarOutline(false);

//			barRenderer.setMaxBarWidth(50);

			// set up gradient paints for series...
			// final GradientPaint gp0 = new GradientPaint(0.0f, 0.0f,
			// Color.getHSBColor(3, 5, 0), 0.0f, 0.0f, Color.getHSBColor(
			// 3, 5, 0));
			// final GradientPaint gp1 = new GradientPaint(0.0f, 0.0f,
			// Color.green, 0.0f, 0.0f, Color.lightGray);
			// final GradientPaint gp2 = new GradientPaint(0.0f, 0.0f,
			// Color.red,
			// 0.0f, 0.0f, Color.lightGray);
			// renderer.setSeriesPaint(0, gp0);
			// renderer.setSeriesPaint(1, gp1);
			// renderer.setSeriesPaint(2, gp2);

			final CategoryAxis domainAxis = plot.getDomainAxis();
			domainAxis.setCategoryLabelPositions(CategoryLabelPositions
					.createUpRotationLabelPositions(Math.PI / 6.0));
			// OPTIONAL CUSTOMISATION COMPLETED.

			CategoryPlot p = BarChartObject.getCategoryPlot();
			p.setRangeGridlinePaint(Color.black);

			/*
			 * Setting the range for y-axis.
			 */
			// CategoryPlot plot = (CategoryPlot) BarChartObject.getPlot();
			// ValueAxis yAxis = plot.getRangeAxis();
			// yAxis.setRange(0, 100);

			query_set.close();
			stmt.close();
			conn.close();

			int width = 640; /* Width of the image */
			int height = 480; /* Height of the image */

			/*
			 * Save the chart in a file.
			 */
			File BarChart = new File("YearTotalCount2.png");
			ChartUtilities.saveChartAsPNG(BarChart, BarChartObject, width,
					height);

		} catch (SQLException e) {
			System.out.println(e);
		}
	}
}
