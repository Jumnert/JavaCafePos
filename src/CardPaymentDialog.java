import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.border.*;

public class CardPaymentDialog extends JDialog {
    private JTextField jtxtCardNumber;
    private JTextField jtxtCardHolder;
    private JTextField jtxtExpiry;
    private JTextField jtxtCVV;
    private JLabel jlblAmount;
    private JButton jbtnConfirm;
    private JButton jbtnCancel;
    private boolean paymentConfirmed = false;
    private String cardType;
    
    public CardPaymentDialog(JFrame parent, double totalAmount, String cardType) {
        super(parent, cardType + " Payment", true);
        this.cardType = cardType;
        initComponents(totalAmount);
    }
    
    private void initComponents(double totalAmount) {
        setLayout(new BorderLayout(0, 0));
        setSize(550, 700);
        setLocationRelativeTo(getParent());
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);
        
        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        
        // Header Panel with gradient
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                Color color1, color2;
                if (cardType.equals("Visa Card")) {
                    color1 = new Color(26, 35, 126);  // Visa blue
                    color2 = new Color(21, 101, 192);
                } else {
                    color1 = new Color(235, 0, 27);   // MasterCard red
                    color2 = new Color(255, 95, 0);
                }
                
                GradientPaint gradient = new GradientPaint(0, 0, color1, getWidth(), 0, color2);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setPreferredSize(new Dimension(550, 100));
        headerPanel.setMaximumSize(new Dimension(550, 100));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        JLabel headerLabel = new JLabel(cardType + " Payment");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subHeaderLabel = new JLabel("Enter your card details securely");
        subHeaderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subHeaderLabel.setForeground(new Color(255, 255, 255, 200));
        subHeaderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(Box.createVerticalGlue());
        headerPanel.add(headerLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(subHeaderLabel);
        headerPanel.add(Box.createVerticalGlue());
        
        mainPanel.add(headerPanel);
        
        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Amount Display
        JPanel amountPanel = createAmountPanel(totalAmount);
        contentPanel.add(amountPanel);
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Card Number Field
        JPanel cardNumberPanel = createInputPanel("Card Number", "1234 5678 9012 3456", 16);
        jtxtCardNumber = (JTextField) cardNumberPanel.getComponent(1);
        contentPanel.add(cardNumberPanel);
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Card Holder Name Field
        JPanel cardHolderPanel = createInputPanel("Cardholder Name", "JOHN DOE", 50);
        jtxtCardHolder = (JTextField) cardHolderPanel.getComponent(1);
        contentPanel.add(cardHolderPanel);
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Expiry and CVV Row
        JPanel expiryAndCvvPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        expiryAndCvvPanel.setBackground(Color.WHITE);
        expiryAndCvvPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel expiryPanel = createInputPanel("Expiry Date", "MM/YY", 5);
        expiryPanel.setPreferredSize(new Dimension(200, 85));
        jtxtExpiry = (JTextField) expiryPanel.getComponent(1);
        expiryAndCvvPanel.add(expiryPanel);
        
        JPanel cvvPanel = createInputPanel("CVV", "123", 3);
        cvvPanel.setPreferredSize(new Dimension(150, 85));
        jtxtCVV = (JTextField) cvvPanel.getComponent(1);
        expiryAndCvvPanel.add(cvvPanel);
        
        contentPanel.add(expiryAndCvvPanel);
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Security Notice
        JLabel securityLabel = new JLabel("🔒 Your payment information is secure and encrypted");
        securityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        securityLabel.setForeground(new Color(100, 100, 100));
        securityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(securityLabel);
        
        mainPanel.add(contentPanel);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 25, 20));
        
        jbtnConfirm = createStyledButton("✓ Confirm Payment", new Color(4, 98, 66));
        jbtnConfirm.setPreferredSize(new Dimension(200, 50));
        jbtnConfirm.addActionListener(e -> confirmPayment());
        
        jbtnCancel = createStyledButton("✕ Cancel", new Color(180, 180, 180));
        jbtnCancel.setPreferredSize(new Dimension(120, 50));
        jbtnCancel.addActionListener(e -> cancelPayment());
        
        buttonPanel.add(jbtnConfirm);
        buttonPanel.add(jbtnCancel);
        
        mainPanel.add(buttonPanel);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // ESC key to cancel
        getRootPane().registerKeyboardAction(
            e -> cancelPayment(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }
    
    private JPanel createAmountPanel(double totalAmount) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(245, 245, 245),
                    getWidth(), getHeight(), new Color(230, 230, 230)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Border
                g2d.setColor(new Color(200, 200, 200));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                
                g2d.dispose();
            }
        };
        
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(470, 80));
        panel.setMaximumSize(new Dimension(470, 80));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Total Amount");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(100, 100, 100));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        jlblAmount = new JLabel(String.format("$ %.2f", totalAmount));
        jlblAmount.setFont(new Font("Segoe UI", Font.BOLD, 36));
        jlblAmount.setForeground(new Color(50, 50, 50));
        jlblAmount.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(titleLabel);
        panel.add(jlblAmount);
        
        return panel;
    }
    
    private JPanel createInputPanel(String label, String placeholder, int maxLength) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel jlabel = new JLabel(label);
        jlabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        jlabel.setForeground(new Color(70, 70, 70));
        jlabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField textField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Border
                if (isFocusOwner()) {
                    g2d.setColor(new Color(4, 98, 66));
                    g2d.setStroke(new BasicStroke(2));
                } else {
                    g2d.setColor(new Color(220, 220, 220));
                    g2d.setStroke(new BasicStroke(1.5f));
                }
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        textField.setPreferredSize(new Dimension(470, 45));
        textField.setMaximumSize(new Dimension(470, 45));
        textField.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        textField.setBackground(Color.WHITE);
        textField.setOpaque(false);
        
        // Add placeholder behavior
        textField.setForeground(new Color(180, 180, 180));
        textField.setText(placeholder);
        
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(new Color(50, 50, 50));
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(new Color(180, 180, 180));
                    textField.setText(placeholder);
                }
            }
        });
        
        panel.add(jlabel);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(textField);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color baseColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color color;
                if (getModel().isPressed()) {
                    color = baseColor.darker();
                } else if (getModel().isRollover()) {
                    color = baseColor.brighter();
                } else {
                    color = baseColor;
                }
                
                g2d.setColor(color);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2d.dispose();
                
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private void confirmPayment() {
        // Validate inputs
        if (isPlaceholder(jtxtCardNumber, "1234 5678 9012 3456") ||
            isPlaceholder(jtxtCardHolder, "JOHN DOE") ||
            isPlaceholder(jtxtExpiry, "MM/YY") ||
            isPlaceholder(jtxtCVV, "123")) {
            
            JOptionPane.showMessageDialog(this,
                "Please fill in all card details!",
                "Incomplete Information",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Basic validation
        String cardNumber = jtxtCardNumber.getText().replaceAll("\\s", "");
        if (cardNumber.length() < 13) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid card number!",
                "Invalid Card",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        paymentConfirmed = true;
        dispose();
    }
    
    private boolean isPlaceholder(JTextField field, String placeholder) {
        return field.getText().equals(placeholder) || field.getText().trim().isEmpty();
    }
    
    private void cancelPayment() {
        paymentConfirmed = false;
        dispose();
    }
    
    public boolean isPaymentConfirmed() {
        return paymentConfirmed;
    }
}