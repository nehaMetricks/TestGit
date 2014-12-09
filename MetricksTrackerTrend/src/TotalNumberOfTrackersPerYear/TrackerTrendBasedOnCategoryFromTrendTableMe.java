package TotalNumberOfTrackersPerYear;

/*
 * Created by Neha(uc167165).
 */

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Date;
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
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.block.BorderArrangement;
import org.jfree.chart.block.EmptyBlock;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.CompositeTitle;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;

/**
 * Program to generate year-wise JSS Tracker trend.
 * 
 * @author UC167165.
 * 
 */
public class TrackerTrendBasedOnCategoryFromTrendTableMe {
	public static void main(String[] args) throws Exception {

		/*
		 * JDBC connection string to connect to the local database.
		 */
		Class.forName("oracle.jdbc.OracleDriver");
		Connection conn = DriverManager.getConnection(
				"jdbc:oracle:thin:@//localhost:1521/xe", "ODSREPORT",
				"odsreport");
		DefaultCategoryDataset my_bar_chart_dataset = new DefaultCategoryDataset();

		DefaultCategoryDataset pendingTrackersdataset = new DefaultCategoryDataset();

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Enter the category id you want the trend for.\n"
				+ "1-default \n" + "2-editorial \n" + "3-java metadata \n"
				+ "4-java mainframe \n" + "5-repositories \n" + "6-rat \n");

		String category_id = br.readLine();

		System.out
				.println("Enter the date range for generating the overall tracker trend. \n"
						+ "Format of input: 'dd-mm-yy'\n"
						+ "Example: 26-Jan-10");

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

			my_bar_chart_dataset = queryResult(stmt, from_date, to_date,
					"New_Count", my_bar_chart_dataset, category_id, "New open");

			my_bar_chart_dataset = queryResult(stmt, from_date, to_date,
					"Closed_Count", my_bar_chart_dataset, category_id, "Closed");

			pendingTrackersdataset = queryResult(stmt, from_date, to_date,
					"Total_Pending", pendingTrackersdataset, category_id,
					"Total pending");

			/*
			 * Generate the chart.
			 */
			JFreeChart BarChartObject = createChart(my_bar_chart_dataset,
					pendingTrackersdataset, "");
			/*
			 * ChartFactory.createBarChart("Trackers", // chart // title null,
			 * // domain axis label null, // range axis label
			 * my_bar_chart_dataset, // data PlotOrientation.VERTICAL, //
			 * orientation true, // include legend true, // tooltips? false //
			 * URLs? );
			 */

			CategoryPlot plot = (CategoryPlot) BarChartObject.getPlot();
			ValueAxis yAxis = plot.getRangeAxis();

			plot.setBackgroundPaint(Color.WHITE);
			plot.setRangeGridlinePaint(Color.BLACK);
			// yAxis.setRange(0, 50.0);

			/**
			 * Customizations for bar series.
			 */
			BarRenderer renderer = (BarRenderer) plot.getRenderer();
			renderer.setSeriesPaint(0, Color.cyan);
			renderer.setSeriesPaint(1, Color.magenta);
			renderer.setSeriesPaint(3, Color.red);
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
					"TrackerTrendBasedOnCategoryFromTrendTableMe.png");
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
			DefaultCategoryDataset my_bar_chart_dataset, String category_id,
			String series) throws SQLException {
		ResultSet query_set = stmt.executeQuery("Select Insert_Date, " + status
				+ " totalCount From Trend Where Insert_Date Between '"
				+ from_date + "' And '" + to_date + "' and category_id='"
				+ category_id + "' Group By insert_Date, " + status
				+ " order by insert_date");

		while (query_set.next()) {

			int count = query_set.getInt("totalCount");

			Date insert_date = query_set.getDate("Insert_Date");

			/*
			 * Add the resulting data to the dataset.
			 */
			my_bar_chart_dataset.addValue(count, series, insert_date);

		}
		query_set.close();
		return my_bar_chart_dataset;
	}

	private static JFreeChart createChart(
			final DefaultCategoryDataset openAndClosedTrackersdataset,
			final DefaultCategoryDataset pendingTrackersDataset, String category) {

		JFreeChart jfreechart = ChartFactory.createBarChart("Tracker Trend "
				+ category, null, null, openAndClosedTrackersdataset,
				PlotOrientation.VERTICAL, true, true, false);
		// jfreechart.setBackgroundPaint(Color.white);
		CategoryPlot categoryplot = (CategoryPlot) jfreechart.getPlot();
		// categoryplot.setBackgroundPaint(new Color(238, 238, 255));
		DefaultCategoryDataset categorydataset = pendingTrackersDataset;
		categoryplot.setDataset(1, categorydataset);
		// categoryplot.mapDatasetToRangeAxis(1, 1);
		CategoryAxis categoryaxis = categoryplot.getDomainAxis();
		categoryaxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
		// NumberAxis numberaxis = new NumberAxis("No Of trackers");
		// categoryplot.setRangeAxis(1, numberaxis);
		LineAndShapeRenderer lineandshaperenderer = new LineAndShapeRenderer();
		lineandshaperenderer
				.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
		categoryplot.setRenderer(1, lineandshaperenderer);
		categoryplot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
		LegendTitle legendtitle = new LegendTitle(categoryplot.getRenderer(0));
		legendtitle.setMargin(new RectangleInsets(2D, 2D, 2D, 2D));
		legendtitle.setFrame(new BlockBorder());
		LegendTitle legendtitle1 = new LegendTitle(categoryplot.getRenderer(1));
		legendtitle1.setMargin(new RectangleInsets(2D, 2D, 2D, 2D));
		legendtitle1.setFrame(new BlockBorder());
		BlockContainer blockcontainer = new BlockContainer(
				new BorderArrangement());
		// blockcontainer.add(legendtitle, RectangleEdge.LEFT);
		// blockcontainer.add(legendtitle1, RectangleEdge.RIGHT);
		blockcontainer.add(new EmptyBlock(2000D, 0.0D));
		CompositeTitle compositetitle = new CompositeTitle(blockcontainer);
		compositetitle.setPosition(RectangleEdge.BOTTOM);
		jfreechart.addSubtitle(compositetitle);
		return jfreechart;
	}
}
