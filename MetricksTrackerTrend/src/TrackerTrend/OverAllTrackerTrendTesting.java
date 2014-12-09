package TrackerTrend;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;

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
import java.sql.PreparedStatement;

/**
 * Program to generate tracker trend based on category.
 * 
 * @author UC167162.
 * 
 */
public class OverAllTrackerTrendTesting {

	public static Connection conn;
	public static PreparedStatement pstmt;
	public static ResultSet query_set;

	public static void main(String[] args) throws Exception {

		/*
		 * obtain JDBC connection.
		 */
		Class.forName("oracle.jdbc.OracleDriver");
		conn = DriverManager.getConnection(
				"jdbc:oracle:thin:@//localhost:1521/xe", "ODSREPORT",
				"odsreport");
		DefaultCategoryDataset my_bar_chart_dataset = new DefaultCategoryDataset();

		DefaultCategoryDataset pendingTrackersdataset = new DefaultCategoryDataset();

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Please enter from_date in the below format. \n"
				+ "Format of input: 'dd-mmm-yy'\n");

		String from_date = br.readLine();

		System.out.println("from date:" + from_date);

		System.out.println("Please enter to_date in the below format. \n"
				+ "Format of input: 'dd-mmm-yy'\n");

		String to_date = br.readLine();

		System.out.println("Please enter the chart title\n");
		String displayName = br.readLine();

		String year1 = "" + from_date.charAt(from_date.length() - 2)
				+ from_date.charAt(from_date.length() - 1);

		int year = Integer.parseInt(year1);

		try {

			my_bar_chart_dataset = queryResult(from_date, to_date, "New_Count",
					my_bar_chart_dataset, "New open", year);

			my_bar_chart_dataset = queryResultClosed(from_date, to_date,
					"Closed_Count", my_bar_chart_dataset, "Closed", year);

			pendingTrackersdataset = queryResultPendingTrend(from_date,
					to_date, "Total_Pending", pendingTrackersdataset,
					"Total pending");

			/*
			 * Generate the chart.
			 */
			JFreeChart BarChartObject = createChart(my_bar_chart_dataset,
					pendingTrackersdataset, "", displayName);
			/*
			 * ChartFactory.createBarChart("Trackers", // chart // title null,
			 * // domain axis label null, // range axis label
			 * my_bar_chart_dataset, // data PlotOrientation.VERTICAL, //
			 * orientation true, // include legend true, // tooltips? false //
			 * URLs? );
			 */

			CategoryPlot plot = (CategoryPlot) BarChartObject.getPlot();
			plot.getRangeAxis();

			plot.setBackgroundPaint(Color.WHITE);
			plot.setRangeGridlinePaint(Color.BLACK);

			/**
			 * Customizations for bar series.
			 */
			BarRenderer renderer = (BarRenderer) plot.getRenderer();
			renderer.setDrawBarOutline(false);
			renderer.setMaximumBarWidth(.05);
			// if label is not needed on bar chart//

			CategoryItemLabelGenerator generator = null;
			renderer.setBaseItemLabelGenerator(generator);
			renderer.setItemMargin(0.01);

			// if we need to print labels on bar chart//
			/*
			 * CategoryItemLabelGenerator lblGenerator = new
			 * StandardCategoryItemLabelGenerator();
			 * renderer.setBaseItemLabelGenerator(lblGenerator);
			 * renderer.setBaseItemLabelsVisible(true);
			 * renderer.setBaseItemLabelPaint(Color.black);
			 * renderer.setItemMargin(0.01);
			 */

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

			pstmt.close();
			conn.close();

			int width = 640; /* Width of the image */
			int height = 480; /* Height of the image */

			/*
			 * Save the chart as image.
			 */
			File BarChart = new File("OverAllTrendChartTest.png");
			ChartUtilities.saveChartAsPNG(BarChart, BarChartObject, width,
					height);

		} catch (SQLDataException e) {
			System.out.println("\nInput data is incorrect" + e.getMessage());
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	private static DefaultCategoryDataset queryResult(String from_date,
			String to_date, String status,
			DefaultCategoryDataset my_bar_chart_dataset, String series, int year)
			throws SQLException {

		String NewSQL = "select * from (Select b.insert_date, b.ncb-a.nca newcount From (Select insert_date, insert_date,sum( New_Count) nca From Trend Where Insert_Date In (Select Max(Insert_Date) From Trend Where TO_CHAR(insert_date, 'YY') =?) group by insert_date) A,(Select insert_date,sum(New_Count)ncb From Trend Where Insert_Date In (Select Min(Insert_Date) From Trend Where TO_CHAR(insert_date, 'YY') =?)group by insert_date ) B union Select insert_date,Sum(New_Count)-(Lag(Sum(New_Count)) Over (Order By insert_date )) newCount From Trend  Where Insert_Date Between ? And ? Group By insert_date)where newcount is  not null ";
		pstmt = conn.prepareStatement(NewSQL);

		pstmt.setInt(1, (year - 1));
		pstmt.setInt(2, year);
		pstmt.setString(3, from_date);
		pstmt.setString(4, to_date);

		query_set = pstmt.executeQuery();

		while (query_set.next()) {

			int newcount = query_set.getInt("newcount");

			Date insert_date = query_set.getDate("Insert_Date");
			/*
			 * System.out.println("insertdate is: " + insert_date +
			 * "newdiff is: " + newcount);
			 */
			/*
			 * Add the resulting data to the dataset.
			 */
			my_bar_chart_dataset.addValue(newcount, series, insert_date);

		}
		query_set.close();
		return my_bar_chart_dataset;
	}

	private static DefaultCategoryDataset queryResultClosed(String from_date,
			String to_date, String status,
			DefaultCategoryDataset my_bar_chart_dataset, String series, int year)
			throws SQLException {

		String closedSQL = "select * from (Select b.insert_date,b.ccb-a.cca closedcount  From (Select insert_date, insert_date,sum(closed_count)cca From Trend Where Insert_Date In (Select Max(Insert_Date) From Trend Where TO_CHAR(insert_date, 'YY') =?) group by insert_date) A, (Select insert_date,sum(closed_count)ccb From Trend Where Insert_Date In (Select Min(Insert_Date) From Trend Where TO_CHAR(insert_date, 'YY') =?)group by insert_date ) B union Select insert_date, Sum(closed_Count)-(Lag(Sum(closed_Count)) Over (Order By insert_date )) closedcount From Trend Where Insert_Date Between ? And ? Group By insert_date)where  closedcount is not null";
		pstmt = conn.prepareStatement(closedSQL);

		pstmt.setInt(1, (year - 1));
		pstmt.setInt(2, year);
		pstmt.setString(3, from_date);
		pstmt.setString(4, to_date);

		query_set = pstmt.executeQuery();

		while (query_set.next()) {

			int closedcount = query_set.getInt("closedcount");
			Date insert_date = query_set.getDate("Insert_Date");

			/*
			 * System.out.println("insertdate is: " + insert_date +
			 * " closeddiff is :" + closedcount);
			 */

			/*
			 * Add the resulting data to the dataset.
			 */

			my_bar_chart_dataset.addValue(closedcount, series, insert_date);

		}
		query_set.close();
		return my_bar_chart_dataset;
	}

	private static DefaultCategoryDataset queryResultPendingTrend(
			String from_date, String to_date, String status,
			DefaultCategoryDataset pendingTrackersdataset, String series)
			throws SQLException {

		String SqlPending = "select insert_date,SUM(total_pending) pendingtrend from trend where insert_date between ? and ? group by insert_date ORDER BY insert_date";

		pstmt = conn.prepareStatement(SqlPending);

		pstmt.setString(1, from_date);
		pstmt.setString(2, to_date);

		query_set = pstmt.executeQuery();

		while (query_set.next()) {

			int pendingcount = query_set.getInt("pendingtrend");

			Date insert_date = query_set.getDate("insert_date");

			/*
			 * Add the resulting data to the dataset.
			 */

			/*
			 * System.out.println("insert_date is" + insert_date +
			 * "pendingcount is " + pendingcount);
			 */

			pendingTrackersdataset.addValue(pendingcount, series, insert_date);

		}
		query_set.close();
		return pendingTrackersdataset;
	}

	private static JFreeChart createChart(
			final DefaultCategoryDataset openAndClosedTrackersdataset,
			final DefaultCategoryDataset pendingTrackersdataset,
			String category, String displayName) {

		JFreeChart jfreechart = ChartFactory.createBarChart(displayName
				+ category, null, null, openAndClosedTrackersdataset,
				PlotOrientation.VERTICAL, true, true, false);

		CategoryPlot categoryplot = (CategoryPlot) jfreechart.getPlot();
		DefaultCategoryDataset categorydataset = pendingTrackersdataset;
		categoryplot.setDataset(1, categorydataset);

		CategoryAxis categoryaxis = categoryplot.getDomainAxis();
		ValueAxis yAxis = categoryplot.getRangeAxis();
		//yAxis.setRange(0, 400.0);

		categoryaxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);

		LineAndShapeRenderer lineandshaperenderer = new LineAndShapeRenderer();
		CategoryItemLabelGenerator lblGenerator = new StandardCategoryItemLabelGenerator();
		lineandshaperenderer.setBaseItemLabelGenerator(lblGenerator);
		lineandshaperenderer.setBaseItemLabelsVisible(true);
		lineandshaperenderer.setBaseItemLabelPaint(Color.black);

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

		blockcontainer.add(new EmptyBlock(2000D, 0.0D));
		CompositeTitle compositetitle = new CompositeTitle(blockcontainer);
		compositetitle.setPosition(RectangleEdge.BOTTOM);
		jfreechart.addSubtitle(compositetitle);
		return jfreechart;
	}
}
