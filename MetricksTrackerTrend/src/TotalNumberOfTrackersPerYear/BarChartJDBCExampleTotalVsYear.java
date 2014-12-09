package TotalNumberOfTrackersPerYear;

import java.sql.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.Date;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class BarChartJDBCExampleTotalVsYear {

	public static void main(String[] args) throws Exception {

		List<String> yearList = new ArrayList<String>();
		int i = 0;

		Class.forName("oracle.jdbc.OracleDriver");
		Connection conn = DriverManager.getConnection(
				"jdbc:oracle:thin:@//localhost:1521/xe", "ODSREPORT",
				"odsreport");
		DefaultCategoryDataset my_bar_chart_dataset = new DefaultCategoryDataset();
		Statement stmt = conn.createStatement();
		try {

			ResultSet query_set = stmt
					.executeQuery("select bug_number, create_date, title from tracker order by CREATE_DATE");

			while (query_set.next()) {

				// int bug_number = query_set.getInt("BUG_NUMBER");
				//
				// String title = query_set.getString("TITLE");

				Timestamp create_date = query_set.getTimestamp("create_date");

				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date(create_date.getTime()));
				int dateWithoutTime = cal.get(Calendar.YEAR);
				String year = Integer.toString(dateWithoutTime).substring(2, 4);

				if (!yearList.contains(year)) {
					yearList.add(year);
					i++;
				}

				Statement stmt2 = conn.createStatement();
				ResultSet query_set2 = stmt2
						.executeQuery("select Count(BUG_NUMBER) from tracker where create_date like '__-___-"
								+ year + "%'");

				while (query_set2.next()) {
					int total = query_set2.getInt(1);

					my_bar_chart_dataset.addValue(total, "Trackers",
							yearList.get(i - 1));
				}
			}
			JFreeChart BarChartObject = ChartFactory.createBarChart(
					"Tracker total v/s Tracker year - Bar Chart", "year",
					"total", my_bar_chart_dataset, PlotOrientation.VERTICAL,
					true, true, false);

			query_set.close();
			stmt.close();
			conn.close();
			int width = 640; /* Width of the image */
			int height = 480; /* Height of the image */
			File BarChart = new File("output_bar_chart.png");
			ChartUtilities.saveChartAsPNG(BarChart, BarChartObject, width,
					height);
		} catch (Exception e) {
			System.out.println(e);
		}

	}
}
