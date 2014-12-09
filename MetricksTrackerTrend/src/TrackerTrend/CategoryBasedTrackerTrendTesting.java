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
public class CategoryBasedTrackerTrendTesting {

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

		System.out.println("Enter the category id you want the trend for.\n"
				+ "1-default \n" + "2-editorial \n" + "3-java metadata \n"
				+ "4-java mainframe \n" + "5-repositories \n" + "6-rat \n");

		String c_id = br.readLine();

		int category_id = Integer.parseInt(c_id);

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
		System.out.println(year);

		try {

			my_bar_chart_dataset = queryResult(from_date, to_date, "New_Count",
					my_bar_chart_dataset, 2, "New open", year);

			my_bar_chart_dataset = queryResultClosed(from_date, to_date,
					"Closed_Count", my_bar_chart_dataset, category_id,
					"Closed", year);

			pendingTrackersdataset = queryResultPendingTrend(from_date,
					to_date, "Total_Pending", pendingTrackersdataset,
					category_id, "Total pending");

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
			
			  CategoryItemLabelGenerator lblGenerator = new
			  StandardCategoryItemLabelGenerator();
			  renderer.setBaseItemLabelGenerator(lblGenerator);
			  renderer.setBaseItemLabelsVisible(true);
			  renderer.setBaseItemLabelPaint(Color.black);
			  renderer.setItemMargin(0.01);
			 

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
			File BarChart = new File("CategorybasedChartTest.png");
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
			DefaultCategoryDataset my_bar_chart_dataset, int category_id,
			String series, int year) throws SQLException {

		String newSQL = "select * from (Select b.insert_date,(b.New_Count-a.new_count) newcount  From (Select New_Count , insert_date From Trend Where Insert_Date In (Select Max(Insert_Date) From Trend Where TO_CHAR(insert_date, 'YY') =?) And Category_Id=?) A, (Select New_Count ,insert_date From Trend Where Insert_Date In (Select Min(Insert_Date) From Trend Where TO_CHAR(insert_date, 'YY') =?) And Category_Id=?) B union (SELECT distinct insert_date,new_count- LAG(new_count) OVER (ORDER BY insert_date)  as newdiff FROM   trend where category_id=?  and insert_date between ? and ?)) where  newcount is not null ";

		pstmt = conn.prepareStatement(newSQL);

		pstmt.setInt(1, (year - 1));
		pstmt.setInt(2, category_id);
		pstmt.setInt(3, year);
		pstmt.setInt(4, category_id);
		pstmt.setInt(5, category_id);
		pstmt.setString(6, from_date);
		pstmt.setString(7, to_date);

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
			DefaultCategoryDataset my_bar_chart_dataset, int category_id,
			String series, int year) throws SQLException {

		String closedSQL = "select * from (Select b.insert_date,(b.closed_Count-a.closed_Count) closedcount  From (Select closed_count, insert_date From Trend Where Insert_Date In (Select Max(Insert_Date) From Trend Where TO_CHAR(insert_date, 'YY') =?) And Category_Id=?) A,(Select closed_count, insert_date From Trend Where Insert_Date In (Select Min(Insert_Date) From Trend Where TO_CHAR(insert_date, 'YY') =?) And Category_Id=?) B union (SELECT distinct insert_date,closed_count-LAG(closed_count) OVER (ORDER BY insert_date)  AS closediff FROM   trend where category_id=?  and insert_date between ? and ?)) where closedcount is not null";

		pstmt = conn.prepareStatement(closedSQL);

		pstmt.setInt(1, (year - 1));
		pstmt.setInt(2, category_id);
		pstmt.setInt(3, year);
		pstmt.setInt(4, category_id);
		pstmt.setInt(5, category_id);
		pstmt.setString(6, from_date);
		pstmt.setString(7, to_date);

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
			DefaultCategoryDataset pendingTrackersdataset, int category_id,
			String series) throws SQLException {

		String SqlPending = "SELECT insert_date, total_pending pendingtrend FROM trend WHERE category_id=? AND insert_date BETWEEN ? and ? group by insert_date, total_pending order by insert_date";

		pstmt = conn.prepareStatement(SqlPending);

		pstmt.setInt(1, category_id);
		pstmt.setString(2, from_date);
		pstmt.setString(3, to_date);

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
		yAxis.setRange(0, 200.0);

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
