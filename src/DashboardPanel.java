import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.*;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

public class DashboardPanel extends JFrame {

    // --- UI Components --- or classes
    private final JLabel lblTodaySales = createValueLabel();
    private final JLabel lblTotalRevenue = createValueLabel(); // Renamed for clarity based on period
    private final JLabel lblTotalOrders = createValueLabel();
    private final JLabel lblBestSeller = createValueLabel();
    private final JLabel lblAvgOrder = createValueLabel();
    private final JLabel lblTopPayment = createValueLabel();

    private JComboBox<String> cmbPeriod;
    private javax.swing.Timer swingTimer;
    
    // Chart Containers
    private final JPanel chartContainerSales = new JPanel(new BorderLayout());
    private final JPanel chartContainerTopProducts = new JPanel(new BorderLayout());
    
    // Tables
    private final JTable tblRecentOrders = new JTable();
    private final JTable tblPaymentMethods = new JTable();

    // Database
    private final Db db = new Db();
    private final NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.US);

    //Color Delcare
    private final Color PRIMARY = new Color(3, 102, 87);
    private final Color ACCENT = new Color(52, 168, 83);
    private final Color CARD_BG = Color.WHITE;
    private final Color BG = new Color(245, 247, 250);
    private final Color TEXT_PRIMARY = new Color(33, 37, 41);
    private final Color TEXT_SECONDARY = new Color(108, 117, 125);

    public DashboardPanel() {
        super("Amazon Forest Cafe - Live Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1280, 850);
        setLocationRelativeTo(null);

        initUI();
        
        // Initial Load (Default: This Month)
        refreshData("This Month");
        startAutoRefresh();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBorder(new EmptyBorder(15, 15, 15, 15));
        root.setBackground(BG);
        setContentPane(root);

        root.add(createHeader(), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(15, 15));
        center.setOpaque(false);

        center.add(createSummaryPanel(), BorderLayout.NORTH);
        center.add(createChartsPanel(), BorderLayout.CENTER);
        center.add(createTablesPanel(), BorderLayout.SOUTH);

        JScrollPane mainScroll = new JScrollPane(center);
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        root.add(mainScroll, BorderLayout.CENTER);
    }

    private JComponent createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel left = new JPanel(new GridLayout(2, 1));
        left.setOpaque(false);
        JLabel title = new JLabel("Sales Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT_PRIMARY);
        left.add(title);
        JLabel date = new JLabel(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")));
        date.setForeground(TEXT_SECONDARY);
        left.add(date);
        header.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        right.setOpaque(false);
        right.add(new JLabel("Period:"));
        cmbPeriod = new JComboBox<>(new String[]{"Today", "Yesterday", "This Week", "This Month", "Last Month", "All Time"});
        cmbPeriod.setSelectedIndex(3); 
        cmbPeriod.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbPeriod.setBackground(Color.WHITE);
        cmbPeriod.addActionListener(e -> refreshData((String) cmbPeriod.getSelectedItem()));
        right.add(cmbPeriod);

        JButton btnRefresh = new JButton("Refresh Data");
        styleButton(btnRefresh, PRIMARY);
        btnRefresh.addActionListener(e -> refreshData((String) cmbPeriod.getSelectedItem()));
        right.add(btnRefresh);

        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JComponent createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 6, 12, 12));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(0, 110));
        panel.add(makeCard("Period Revenue", lblTotalRevenue, "Total sales for selection", new Color(76, 175, 80)));
        panel.add(makeCard("Total Orders", lblTotalOrders, "Count for selection", new Color(33, 150, 243)));
        panel.add(makeCard("Avg Ticket", lblAvgOrder, "Revenue / Orders", new Color(156, 39, 176)));
        panel.add(makeCard("Today's Sales", lblTodaySales, "Specific to today", new Color(255, 152, 0)));
        panel.add(makeCard("Best Seller", lblBestSeller, "Top qty item", new Color(233, 30, 99)));
        panel.add(makeCard("Top Method", lblTopPayment, "Most used payment", new Color(0, 188, 212)));

        return panel;
    }

    private JPanel makeCard(String title, JLabel valueLabel, String subtitle, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JPanel leftBar = new JPanel();
        leftBar.setBackground(accent);
        leftBar.setPreferredSize(new Dimension(4, 0));
        card.add(leftBar, BorderLayout.WEST);

        JPanel content = new JPanel(new GridLayout(3,1));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(0, 10, 0, 0));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitle.setForeground(TEXT_SECONDARY);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(TEXT_PRIMARY);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        lblSub.setForeground(TEXT_SECONDARY);

        content.add(lblTitle);
        content.add(valueLabel);
        content.add(lblSub);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JComponent createChartsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 15, 15));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(0, 350));

        chartContainerSales.setBackground(CARD_BG);
        chartContainerSales.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        chartContainerTopProducts.setBackground(CARD_BG);
        chartContainerTopProducts.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        panel.add(chartContainerSales);
        panel.add(chartContainerTopProducts);
        return panel;
    }

    private JComponent createTablesPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 15, 15));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(0, 300));

        // Recent Orders Table
        JPanel recentCard = new JPanel(new BorderLayout());
        recentCard.setBackground(CARD_BG);
        recentCard.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        recentCard.add(makeSectionTitle("Recent Orders"), BorderLayout.NORTH);
        
        styleTable(tblRecentOrders);
        recentCard.add(new JScrollPane(tblRecentOrders), BorderLayout.CENTER);

        // Payment Methods Table
        JPanel payCard = new JPanel(new BorderLayout());
        payCard.setBackground(CARD_BG);
        payCard.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        payCard.add(makeSectionTitle("Payment Methods Stats"), BorderLayout.NORTH);
        
        styleTable(tblPaymentMethods);
        payCard.add(new JScrollPane(tblPaymentMethods), BorderLayout.CENTER);

        panel.add(recentCard);
        panel.add(payCard);
        return panel;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(240,240,240));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(248,248,248));
        table.getTableHeader().setOpaque(true);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(232, 245, 233));
        table.setSelectionForeground(Color.BLACK);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);
    }

    private JComponent makeSectionTitle(String s) {
        JLabel lbl = new JLabel("  " + s);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(TEXT_PRIMARY);
        lbl.setBorder(new EmptyBorder(10, 0, 10, 0));
        return lbl;
    }
    
    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
    }

    // --- LOGIC & DATA FETCHING ---

    private void refreshData(String period) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            // Declare
            String revenue = "$0.00";
            String todaySales = "$0.00";
            String totalOrders = "0";
            String avgOrder = "$0.00";
            String bestSeller = "-";
            String topPayment = "-";
            
            DefaultCategoryDataset salesDataset = new DefaultCategoryDataset();
            DefaultCategoryDataset productDataset = new DefaultCategoryDataset();
            DefaultTableModel recentOrdersModel = new DefaultTableModel();
            DefaultTableModel paymentModel = new DefaultTableModel();

            @Override
            protected Void doInBackground() {
                try (Connection conn = db.getConnection()) {
                    String dateCondition = getDateConditionSQL(period);
                    
                    // 1. KPI Cards
                    fetchKPIData(conn, dateCondition);
                    fetchTodaySales(conn); //Auto fetch : (i alr fix thsi code)

                    // 2. Charts
                    fetchSalesChartData(conn, period, dateCondition, salesDataset);
                    fetchTopProductsData(conn, dateCondition, productDataset);

                    // 3. Tables
                    fetchRecentOrders(conn, recentOrdersModel);
                    fetchPaymentStats(conn, dateCondition, paymentModel);

                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Database Error: " + e.getMessage());
                }
                return null;
            }

            private void fetchKPIData(Connection conn, String cond) throws SQLException {
                String sql = "SELECT " +
                        "COALESCE(SUM(total_amount), 0) as rev, " +
                        "COUNT(*) as cnt " +
                        "FROM orders WHERE 1=1 " + cond;
                
                try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                    if (rs.next()) {
                        double rev = rs.getDouble("rev");
                        int cnt = rs.getInt("cnt");
                        revenue = currency.format(rev);
                        totalOrders = String.valueOf(cnt);
                        avgOrder = cnt > 0 ? currency.format(rev / cnt) : "$0.00";
                    }
                }
                //Best Seller Query
                String sqlBest = "SELECT i.name FROM order_items oi " +
                        "JOIN orders o ON oi.order_id = o.order_id " +
                        "JOIN items i ON oi.item_id = i.item_id " +
                        "WHERE 1=1 " + cond + " " +
                        "GROUP BY i.name ORDER BY SUM(oi.quantity) DESC LIMIT 1";
                try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sqlBest)) {
                    if (rs.next()) bestSeller = rs.getString("name");
                }
                // Top Payment (Currently Not working
                String sqlPay = "SELECT payment_method FROM orders WHERE 1=1 " + cond + 
                                " GROUP BY payment_method ORDER BY COUNT(*) DESC LIMIT 1"; // Note: Orders table usually has payment_method. If not, adjust.
                try (Statement stmt = conn.createStatement()) {                 
                    try(ResultSet rs = stmt.executeQuery(sqlPay)){
                        if (rs.next()) topPayment = rs.getString(1);
                    } catch (SQLException ignored) { topPayment = "N/A"; }
                }
            }
            
            private void fetchTodaySales(Connection conn) throws SQLException {
                String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE DATE(order_date) = CURDATE()";
                try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                    if (rs.next()) todaySales = currency.format(rs.getDouble(1));
                }
            }

            private void fetchSalesChartData(Connection conn, String period, String cond, DefaultCategoryDataset ds) throws SQLException {
               String sql;
                boolean isDayView = period.equals("Today") || period.equals("Yesterday");
                if (isDayView) {
                    // Group by Hour 
                    sql = "SELECT DATE_FORMAT(order_date, '%H:00') as lbl, SUM(total_amount) as val " +
                          "FROM orders WHERE 1=1 " + cond + 
                          " GROUP BY lbl ORDER BY lbl";
                } else {
                    // Group by Date
                    sql = "SELECT DATE_FORMAT(order_date, '%d/%m') as lbl, DATE(order_date) as sort_date, SUM(total_amount) as val " +
                          "FROM orders WHERE 1=1 " + cond + 
                          " GROUP BY lbl, sort_date ORDER BY sort_date";
                }

                try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                    boolean hasData = false;
                    while (rs.next()) {
                        double val = rs.getDouble("val");
                        String lbl = rs.getString("lbl");
                        ds.addValue(val, "Revenue", lbl);
                        hasData = true;
                    }
                    
                    // If no data found for the period, add a zero entry so the chart isn't blank
                    if (!hasData) {
                        ds.addValue(0, "Revenue", "No Data");
                    }
                }
            }

            private void fetchTopProductsData(Connection conn, String cond, DefaultCategoryDataset ds) throws SQLException {
                String sql = "SELECT i.name, SUM(oi.quantity) as qty " +
                        "FROM order_items oi " +
                        "JOIN orders o ON oi.order_id = o.order_id " +
                        "JOIN items i ON oi.item_id = i.item_id " +
                        "WHERE 1=1 " + cond + 
                        " GROUP BY i.name ORDER BY qty DESC LIMIT 5";

                try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        ds.addValue(rs.getInt("qty"), "Quantity", rs.getString("name"));
                    }
                }
            }

            private void fetchRecentOrders(Connection conn, DefaultTableModel model) throws SQLException {
                String[] cols = {"ID", "Date", "Total", "Items"}; // Simplified columns based on known schema
                model.setColumnIdentifiers(cols);
                
                // Getting items count per order 
                String sql = "SELECT o.order_id, o.order_date, o.total_amount, " +
                             "(SELECT COUNT(*) FROM order_items WHERE order_id = o.order_id) as item_count " +
                             "FROM orders o ORDER BY o.order_date DESC LIMIT 15";

                try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM-dd HH:mm");
                    while (rs.next()) {
                        Timestamp ts = rs.getTimestamp("order_date");
                        String dateStr = (ts != null) ? ts.toLocalDateTime().format(dtf) : "-";
                        model.addRow(new Object[]{
                                rs.getInt("order_id"),
                                dateStr,
                                currency.format(rs.getDouble("total_amount")),
                                rs.getInt("item_count")
                        });
                    }
                }
            }
            
            private void fetchPaymentStats(Connection conn, String cond, DefaultTableModel model) throws SQLException {
                String[] cols = {"Method", "Txns", "Amount"};
                model.setColumnIdentifiers(cols);
                
           
                String sql = "SELECT payment_method, COUNT(*) as txns, SUM(total_amount) as total " +
                             "FROM orders WHERE 1=1 " + cond + 
                             " GROUP BY payment_method";
                             
                try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                     while(rs.next()) {
                         String method = rs.getString("payment_method");
                         if(method == null) method = "Unknown";
                         model.addRow(new Object[]{
                             method,
                             rs.getInt("txns"),
                             currency.format(rs.getDouble("total"))
                         });
                     }
                } catch (SQLException e) {
                    model.setColumnCount(1);
                    model.addRow(new Object[]{"Payment info unavailable"});
                }
            }

            @Override
            protected void done() {
                lblTotalRevenue.setText(revenue);
                lblTotalOrders.setText(totalOrders);
                lblAvgOrder.setText(avgOrder);
                lblTodaySales.setText(todaySales);
                lblBestSeller.setText(bestSeller);
                lblTopPayment.setText(topPayment);

                updateSalesChart(salesDataset, period);
                updateTopProductsChart(productDataset);
                
                tblRecentOrders.setModel(recentOrdersModel);
                tblPaymentMethods.setModel(paymentModel);
                
                // Re-style tables after model update
                styleTable(tblRecentOrders);
                styleTable(tblPaymentMethods);
            }
        };
        worker.execute();
    }

    private String getDateConditionSQL(String period) {
        switch (period) {
            case "Today":
                return " AND DATE(order_date) = CURDATE()";
            case "Yesterday":
                return " AND DATE(order_date) = CURDATE() - INTERVAL 1 DAY";
            case "This Week":
                return " AND YEARWEEK(order_date, 1) = YEARWEEK(CURDATE(), 1)";
            case "This Month":
                return " AND MONTH(order_date) = MONTH(CURDATE()) AND YEAR(order_date) = YEAR(CURDATE())";
            case "Last Month":
                return " AND MONTH(order_date) = MONTH(CURDATE() - INTERVAL 1 MONTH) " +
                       "AND YEAR(order_date) = YEAR(CURDATE() - INTERVAL 1 MONTH)";
            case "All Time":
            default:
                return "";
        }
    }

    // --- JFREECHART CONFIG ---

    private void updateSalesChart(DefaultCategoryDataset dataset, String period) {
        JFreeChart chart = ChartFactory.createLineChart(
                "Sales Trend (" + period + ")", 
                period.contains("Today") ? "Hour" : "Date", // 
                "Revenue", 
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false
        );

        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        plot.setOutlineVisible(false);

        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, PRIMARY);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        
       
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        ChartPanel cp = new ChartPanel(chart);
        chartContainerSales.removeAll();
        chartContainerSales.add(cp, BorderLayout.CENTER);
        chartContainerSales.revalidate();
        chartContainerSales.repaint();
    }

    private void updateTopProductsChart(DefaultCategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createBarChart(
                "Top 5 Products",
                null,
                "Qty Sold",
                dataset,
                PlotOrientation.HORIZONTAL, 
                false, true, false
        );

        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        plot.setOutlineVisible(false);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, ACCENT);
        renderer.setBarPainter(new StandardBarPainter()); 
        renderer.setDrawBarOutline(false);

        ChartPanel cp = new ChartPanel(chart);
        chartContainerTopProducts.removeAll();
        chartContainerTopProducts.add(cp, BorderLayout.CENTER);
        chartContainerTopProducts.revalidate();
        chartContainerTopProducts.repaint();
    }

    private void startAutoRefresh() {
        if (swingTimer != null && swingTimer.isRunning()) swingTimer.stop();
        swingTimer = new javax.swing.Timer(30_000, e -> refreshData((String) cmbPeriod.getSelectedItem()));
        swingTimer.start();
    }
    
    private static JLabel createValueLabel() {
        JLabel l = new JLabel("Loading...");
        l.setFont(new Font("Segoe UI", Font.BOLD, 18));
        l.setForeground(new Color(33, 37, 41));
        return l;
    }

    // --- DB CONNECTION ---
    private static class Db {
        private final String URL = "jdbc:mysql://localhost:3306/cafepos?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        private final String USER = "root";
        private final String PASS = "nith2020"; 

        Db() {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                System.err.println("MySQL Driver not found! Add mysql-connector-j to library.");
            }
        }

        Connection getConnection() throws SQLException {
            return DriverManager.getConnection(URL, USER, PASS);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set System Look and Feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                // Make Tooltips look nicer
                UIManager.put("ToolTip.background", Color.WHITE);
                UIManager.put("ToolTip.border", BorderFactory.createLineBorder(new Color(200,200,200)));
            } catch (Exception ignored) {}
            
            new DashboardPanel().setVisible(true);
        });
    }
}