import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class QRPaymentDialog extends JDialog {
    private JLabel jlblQRImage;
    private JLabel jlblAmount;
    private JLabel jlblStatus;
    private JLabel jlblBankName;
    private JLabel jlblRecipient;
    private JButton jbtnConfirm;
    private JButton jbtnCancel;
    private boolean paymentConfirmed = false;
    private String selectedBank;
    
    public QRPaymentDialog(JFrame parent, double totalAmount, String bankName) {
        super(parent, bankName + " Payment", true);
        this.selectedBank = bankName;
        initComponents(totalAmount);
    }
    
    private void initComponents(double totalAmount) {
        setLayout(new BorderLayout(0, 0));
        setSize(540, 920);
        setLocationRelativeTo(getParent());
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);
        
        // Main Container Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        // Header Panel - Dark Blue
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(new Color(20, 55, 92)); // ACLEDA dark blue
        headerPanel.setPreferredSize(new Dimension(540, 60));
        headerPanel.setMaximumSize(new Dimension(540, 60));
        
        JLabel headerLabel = new JLabel("QR Payment", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel, BorderLayout.CENTER);
        mainPanel.add(headerPanel);
        
        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(245, 245, 245)); // Light gray background
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 40));
        
        // Bank Logo Panel (if you have logo image)
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setBackground(new Color(245, 245, 245));
        jlblBankName = new JLabel(selectedBank);
        jlblBankName.setFont(new Font("Segoe UI", Font.BOLD, 28));
        jlblBankName.setForeground(new Color(20, 55, 92));
        logoPanel.add(jlblBankName);
        contentPanel.add(logoPanel);
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // "Scan. Pay Done." Label
        JLabel scanLabel = new JLabel("Scan. Pay Done.", SwingConstants.CENTER);
        scanLabel.setFont(new Font("Segoe UI", Font.ITALIC, 20));
        scanLabel.setForeground(Color.BLACK);
        scanLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(scanLabel);
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // QR Code Container with white background and shadow effect
        JPanel qrContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw shadow
                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 20, 20);
                
                // Draw white background
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 20, 20);
                
                g2d.dispose();
            }
        };
        qrContainer.setLayout(new BorderLayout());
        qrContainer.setBackground(new Color(245, 245, 245));
        qrContainer.setPreferredSize(new Dimension(340, 340));
        qrContainer.setMaximumSize(new Dimension(340, 340));
        qrContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        qrContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // QR Code Image
        jlblQRImage = new JLabel();
        jlblQRImage.setHorizontalAlignment(SwingConstants.CENTER);
        jlblQRImage.setVerticalAlignment(SwingConstants.CENTER);
        
        try {
            ImageIcon qrIcon = new ImageIcon(getClass().getResource("/Image/qr.jpg"));
            Image originalImage = qrIcon.getImage();
            
            // Calculate scaling to maintain aspect ratio
            int originalWidth = originalImage.getWidth(null);
            int originalHeight = originalImage.getHeight(null);
            int targetSize = 340; // Maximum size for the container
            
            int newWidth, newHeight;
            
            // Maintain aspect ratio - fit to container
            if (originalWidth > originalHeight) {
                newWidth = targetSize;
                newHeight = (int) ((double) originalHeight / originalWidth * targetSize);
            } else {
                newHeight = targetSize;
                newWidth = (int) ((double) originalWidth / originalHeight * targetSize);
            }
            
            // Scale with high quality
            Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            jlblQRImage.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            jlblQRImage.setText("<html><div style='text-align: center; padding: 50px;'>" +
                    "<p style='font-size: 18px; color: #333;'>QR Code</p>" +
                    "<p style='font-size: 12px; color: #999;'>Image: /Image/qrcode.png</p>" +
                    "</div></html>");
            jlblQRImage.setBackground(Color.WHITE);
            jlblQRImage.setOpaque(true);
        }
        
        qrContainer.add(jlblQRImage, BorderLayout.CENTER);
        contentPanel.add(qrContainer);
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Recipient Name Panel
        JPanel recipientPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        recipientPanel.setBackground(new Color(245, 245, 245));
        jlblRecipient = new JLabel("CHOUN THEACHUMNITH");
        jlblRecipient.setFont(new Font("Segoe UI", Font.BOLD, 24));
        jlblRecipient.setForeground(new Color(20, 55, 92));
        recipientPanel.add(jlblRecipient);
        contentPanel.add(recipientPanel);
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Amount Display Panel with styled box
        JPanel amountContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(220, 38, 38),
                    getWidth(), getHeight(), new Color(185, 28, 28)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                g2d.dispose();
            }
        };
        amountContainer.setLayout(new FlowLayout(FlowLayout.CENTER));
        amountContainer.setPreferredSize(new Dimension(350, 70));
        amountContainer.setMaximumSize(new Dimension(350, 70));
        amountContainer.setOpaque(false);
        
        JPanel amountInner = new JPanel();
        amountInner.setLayout(new BoxLayout(amountInner, BoxLayout.Y_AXIS));
        amountInner.setOpaque(false);
        
        JLabel amountTitle = new JLabel("Total Amount");
        amountTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        amountTitle.setForeground(new Color(255, 255, 255, 200));
        amountTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        jlblAmount = new JLabel(String.format("$ %.2f", totalAmount));
        jlblAmount.setFont(new Font("Segoe UI", Font.BOLD, 32));
        jlblAmount.setForeground(Color.WHITE);
        jlblAmount.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        amountInner.add(amountTitle);
        amountInner.add(jlblAmount);
        amountContainer.add(amountInner);
        
        contentPanel.add(amountContainer);
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        // Status/Instructions Label
        jlblStatus = new JLabel("<html><center>Open your banking app and scan<br>the QR code to complete payment</center></html>");
        jlblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        jlblStatus.setForeground(new Color(100, 100, 100));
        jlblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        jlblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(jlblStatus);
        
        mainPanel.add(contentPanel);
        
        // Button Panel with modern styling
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 25, 20));
        
        jbtnConfirm = new JButton("Payment Completed") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(new Color(3, 78, 53));
                } else if (getModel().isRollover()) {
                    g2d.setColor(new Color(5, 108, 73));
                } else {
                    g2d.setColor(new Color(4, 98, 66));
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2d.dispose();
                
                super.paintComponent(g);
            }
        };
        jbtnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 16));
        jbtnConfirm.setForeground(Color.WHITE);
        jbtnConfirm.setPreferredSize(new Dimension(220, 50));
        jbtnConfirm.setFocusPainted(false);
        jbtnConfirm.setBorderPainted(false);
        jbtnConfirm.setContentAreaFilled(false);
        jbtnConfirm.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jbtnConfirm.addActionListener(e -> confirmPayment());
        
        jbtnCancel = new JButton("Cancel") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(new Color(200, 40, 40));
                } else if (getModel().isRollover()) {
                    g2d.setColor(new Color(230, 50, 50));
                } else {
                    g2d.setColor(new Color(255, 51, 51));
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2d.dispose();
                
                super.paintComponent(g);
            }
        };
        jbtnCancel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        jbtnCancel.setForeground(Color.WHITE);
        jbtnCancel.setPreferredSize(new Dimension(140, 50));
        jbtnCancel.setFocusPainted(false);
        jbtnCancel.setBorderPainted(false);
        jbtnCancel.setContentAreaFilled(false);
        jbtnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jbtnCancel.addActionListener(e -> cancelPayment());
        
        buttonPanel.add(jbtnConfirm);
        buttonPanel.add(jbtnCancel);
        
        mainPanel.add(buttonPanel);
        
        // Add main panel to dialog WITHOUT scroll pane
        add(mainPanel, BorderLayout.CENTER);
        
        // Close on ESC key
        getRootPane().registerKeyboardAction(
            e -> cancelPayment(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }
    
    private void confirmPayment() {
        paymentConfirmed = true;
        dispose();
    }
    
    private void cancelPayment() {
        paymentConfirmed = false;
        dispose();
    }
    
    public boolean isPaymentConfirmed() {
        return paymentConfirmed;
    }
    
    // Method to update amount if needed
    public void updateAmount(double newAmount) {
        jlblAmount.setText(String.format("Amount: $ %.2f", newAmount));
    }
}