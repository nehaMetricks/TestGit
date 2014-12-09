package TotalNumberOfTrackersPerYear;

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
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
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
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.CompositeTitle;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;

/**
 * Program to generate tracker trend based on category.
 * 
 * @author UC167162.
 * 
 */
public class CategoryBasedTrackerTrendTuesday9thoct {
	public static void main(String[] args) throws Exception {

		/*
		 * obtain JDBC connection.
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

		System.out.println("Please enter from_date in the below format. \n"
				+ "Format of input: 'dd-mmm-yyyy'\n");

		String from_date = br.readLine();

		System.out.println("from date:" + from_date);

		System.out.println("Please enter to_date in the below format. \n"
				+ "Format of input: 'dd-mmm-yyyy'\n");

		String to_date = br.readLine();

		System.out.println("Please enter the chart title\n");
		String displayName = br.readLine();

		Statement stmt = conn.createStatement();

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

		Date date = sdf.parse(from_date);

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);

		try {

			my_bar_chart_dataset = queryResult(stmt, from_date, to_date,
					"New_Count", my_bar_chart_dataset, category_id, "New open",
					year);

			my_bar_chart_dataset = queryResult(stmt, from_date, to_date,
					"Closed_Count", my_bar_chart_dataset, category_id,
					"Closed", year);

			pendingTrackersdataset = queryResultPendingTrend(stmt, from_date,
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
			plot.setOutlinePaint(Color.gray);
			plot.setBackgroundPaint(Color.white);
			plot.setRangeGridlinePaint(Color.BLACK);

			/**
			 * Customizations for bar series.
			 */

			CategoryAxis categoryAxis = new CategoryAxis("Categories");
			categoryAxis.setLowerMargin(.05);
			categoryAxis.setCategoryMargin(.05);
			categoryAxis.setUpperMargin(.05);

			BarRenderer renderer = (BarRenderer) plot.getRenderer();
			/*
			 * renderer.setSeriesPaint(0, Color.BLUE);
			 * renderer.setSeriesPaint(1, Color.magenta);
			 * renderer.setSeriesPaint(3, Color.BLUE);
			 */

			// Code to change the color of new_open and closed count bars
			Color c1 = Color.decode("0x0066CC");
			Color c2 = Color.decode("0x800000");

			// set the type of painter
			renderer.setBarPainter(new StandardBarPainter());

			renderer.setSeriesPaint(0, c1);
			renderer.setSeriesPaint(1, c2);

			renderer.setDrawBarOutline(false);
			renderer.setMaximumBarWidth(.05);

			CategoryItemLabelGenerator generator = null;
			renderer.setBaseItemLabelGenerator(generator);
			renderer.setItemMargin(0.01);

			// To set tick count for the domain axis
			NumberAxis numberAxis = (NumberAxis) plot.getRangeAxis();
			numberAxis.setTickUnit(new NumberTickUnit(20));

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
			 * Save the chart as image.
			 */
			File BarChart = new File(
					"ChartForTrackerTrendBasedOnCategoryTrendTablesunday.png");
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
			String series, int year) throws SQLException {

		ResultSet query_set = stmt
				.executeQuery("SELECT DISTINCT * "
						+ "FROM "
						+ "  (SELECT DISTINCT insert_date, "
						+ "    LEAD(new_count) OVER (ORDER BY insert_date)   - new_count   AS newdiff, "
						+ "    LEAD(closed_count) OVER (ORDER BY insert_date)-closed_count AS closediff "
						+ "  FROM trend " + "  WHERE category_id= "
						+ category_id
						+ "  AND insert_date BETWEEN '"
						+ from_date
						+ "' AND '"
						+ to_date
						+ "' "
						+ "  UNION "
						+ "    (SELECT a.insert_date, "
						+ "      (b.New_Count   -a.new_count) newcount , "
						+ "      (b.closed_Count-a.closed_Count) closedcount "
						+ "    FROM "
						+ "      (SELECT New_Count, "
						+ "        closed_count, "
						+ "        insert_date "
						+ "      FROM Trend "
						+ "      WHERE Insert_Date IN "
						+ "        (SELECT MAX(Insert_Date) "
						+ "        FROM Trend "
						+ "        WHERE Extract(YEAR FROM Insert_Date) = "
						+ year
						+ "        ) "
						+ "      AND Category_Id="
						+ category_id
						+ ""
						+ "      ) A, "
						+ "      (SELECT New_Count , "
						+ "        closed_count, "
						+ "        insert_date "
						+ "      FROM Trend "
						+ "      WHERE Insert_Date IN "
						+ "        (SELECT MIN(Insert_Date) "
						+ "        FROM Trend "
						+ "        WHERE Extract(YEAR FROM Insert_Date) = "
						+ (year + 1)
						+ "        ) "
						+ "      AND Category_Id= "
						+ category_id
						+ "      ) B "
						+ "    ) "
						+ "  ) "
						+ "WHERE newdiff IS NOT NULL "
						+ "AND closediff IS NOT NULL");

		while (query_set.next()) {

			int newcount = query_set.getInt("newdiff");
			int closedcount = query_set.getInt("closediff");
			Date insert_date = query_set.getDate("Insert_Date");

			/*
			 * Add the resulting data to the dataset.
			 */
			my_bar_chart_dataset.addValue(newcount, series, insert_date);
			my_bar_chart_dataset.addValue(closedcount, series, insert_date);

		}
		query_set.close();
		return my_bar_chart_dataset;
	}

	private static DefaultCategoryDataset queryResultPendingTrend(
			Statement stmt, String from_date, String to_date, String status,
			DefaultCategoryDataset pendingTrackersdataset, String category_id,
			String series) throws SQLException {

		ResultSet query_set = stmt
				.executeQuery("SELECT EXTRACT(YEAR FROM a.insert_date) \"year\", "
						+ "a.insert_date, a.total_pending \"pendingTrend\" "
						+ "FROM trend a "
						+ "INNER JOIN trend b "
						+ "ON a.id+1          = b.id "
						+ "WHERE a.category_id= "
						+ category_id
						+ "AND a.insert_date BETWEEN '"
						+ from_date
						+ " ' AND '"
						+ to_date
						+ " ' GROUP BY EXTRACT(YEAR FROM a.insert_date), "
						+ "  a.insert_date, "
						+ "  a.total_pending "
						+ "ORDER BY EXTRACT(YEAR FROM a.insert_date) ASC");

		while (query_set.next()) {

			int pendingcount = query_set.getInt("pendingTrend");

			Date insert_date = query_set.getDate("insert_date");

			/*
			 * Add the resulting data to the dataset.
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
