package TrackerTrend;

/*
 * Created by Neha(uc167165).
 */

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;

/**
 * Program to generate year-wise JSS Tracker trend.
 * 
 * @author UC167165.
 * 
 */
public class JssTrackersOld {
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

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		System.out
				.println("Enter the date range for generating the overall tracker trend. \n"
						+ "Enter the year" + "Example: 2010");

		System.out
				.println("Please enter the 'from_date' in the format mentioned above: \t");

		String from_date = br.readLine();

		System.out
				.println("Please enter the 'to_date' in the format mentioned above: \t");

		String to_date = br.readLine();

		try {

			/*
			 * Query to obtain the year-wise count of trackers.
			 */
			ResultSet query_set = stmt
					.executeQuery("select extract(YEAR from CREATE_DATE) year, count(CREATE_DATE) totalCount from TRACKER where extract(YEAR from CREATE_DATE) between '"
							+ from_date
							+ "' and '"
							+ to_date
							+ "' group by extract(year from CREATE_DATE) order by year asc");

			while (query_set.next()) {

				int count = query_set.getInt("totalCount");

				String year = query_set.getString("year");

				System.out.println(count + " " + year);

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
			// BasicStroke stroke = new Stroke();
			// plot.setRangeGridlineStroke(stroke);
			plot.setOutlineVisible(false);

			// yAxis.setRange(0, 50.0);

			/**
			 * Customizations for bar series.
			 */
			BarChartObject.setBackgroundPaint(Color.WHITE);

			BarRenderer renderer = (BarRenderer) plot.getRenderer();

			// set color of the bar chart
			Color c = Color.decode("0x0066CC");
			// Set image background color to grey
			// Color c2 = Color.decode("0xE6E6D8");
			// BarChartObject.setBackgroundPaint(c2);

			// set the type of painter
			renderer.setBarPainter(new StandardBarPainter());

			// Type and size of the title
			BarChartObject.getTitle().setFont(
					new Font("Calibri (Body)", Font.BOLD, 18));

			// Set position of the legend
			LegendTitle legend = BarChartObject.getLegend();
			legend.setPosition(RectangleEdge.RIGHT);

			renderer.setSeriesPaint(0, c);
			renderer.setDrawBarOutline(false);
			renderer.setMaximumBarWidth(.05);
			CategoryItemLabelGenerator generator = null;
			renderer.setBaseItemLabelGenerator(generator);
			renderer.setBaseItemLabelsVisible(true);

			// To set tick count for the domain axis
			/*
			 * NumberAxis numberAxis = (NumberAxis) plot.getRangeAxis();
			 * numberAxis.setTickUnit(new NumberTickUnit(20));
			 */
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
			File BarChart = new File("YearWiseTotalCount.png");
			ChartUtilities.saveChartAsPNG(BarChart, BarChartObject, width,
					height);

		} catch (SQLException e) {
			System.out.println(e);
		}
	}
}
