import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class BankSelectionDialog extends JDialog {
    private String selectedBank = null;
    private JPanel btnABA;
    private JPanel btnACLEDA;
    private JPanel btnBAKONG;
    private JButton btnCancel;
    
    public BankSelectionDialog(JFrame parent) {
        super(parent, "Select Payment Method", true);
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setSize(500, 620);
        setLocationRelativeTo(getParent());
        setResizable(false);
        getContentPane().setBackground(new Color(250, 250, 250));
        
        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(250, 250, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        // Header Panel with gradient
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(4, 98, 66),
                    getWidth(), 0, new Color(5, 130, 88)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setPreferredSize(new Dimension(500, 100));
        headerPanel.setMaximumSize(new Dimension(500, 100));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        JLabel headerLabel = new JLabel("Choose Payment Method");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subHeaderLabel = new JLabel("Select your bank to scan QR code");
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
        contentPanel.setBackground(new Color(250, 250, 250));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(35, 35, 25, 35));
        
        // ABA BANK Button
        btnABA = createBankCard("ABA BANK", "/Image/aba.png");
        btnABA.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { selectBank("ABA BANK"); }
        });
        contentPanel.add(btnABA);
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, 18)));
        
        // ACLEDA BANK Button
        btnACLEDA = createBankCard("ACLEDA BANK", "/Image/acleda.jpg");
        btnACLEDA.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { selectBank("ACLEDA BANK"); }
        });
        contentPanel.add(btnACLEDA);
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, 18)));
        
        // BAKONG Button
        btnBAKONG = createBankCard("BAKONG", "/Image/bakong.png");
        btnBAKONG.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { selectBank("BAKONG"); }
        });
        contentPanel.add(btnBAKONG);
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        mainPanel.add(contentPanel);
        
        // Cancel Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(250, 250, 250));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 25, 20));
        
        btnCancel = new JButton("Cancel") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(new Color(200, 200, 200));
                } else if (getModel().isRollover()) {
                    g2d.setColor(new Color(230, 230, 230));
                } else {
                    g2d.setColor(Color.WHITE);
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                
                // Border
                g2d.setColor(new Color(220, 220, 220));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnCancel.setForeground(new Color(100, 100, 100));
        btnCancel.setPreferredSize(new Dimension(150, 48));
        btnCancel.setFocusPainted(false);
        btnCancel.setBorderPainted(false);
        btnCancel.setContentAreaFilled(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(e -> cancelSelection());
        
        buttonPanel.add(btnCancel);
        mainPanel.add(buttonPanel);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // ESC key to cancel
        getRootPane().registerKeyboardAction(
            e -> cancelSelection(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }
    
    private JPanel createBankCard(String bankName, String logoPath) {
        JPanel card = new JPanel() {
            private boolean isHovered = false;
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Shadow
                g2d.setColor(new Color(0, 0, 0, isHovered ? 40 : 25));
                g2d.fillRoundRect(4, 4, getWidth() - 8, getHeight() - 8, 20, 20);
                
                // Card background
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 8, 20, 20);
                
                // Border
                if (isHovered) {
                    g2d.setColor(new Color(4, 98, 66));
                    g2d.setStroke(new BasicStroke(2.5f));
                } else {
                    g2d.setColor(new Color(230, 230, 230));
                    g2d.setStroke(new BasicStroke(1.5f));
                }
                g2d.drawRoundRect(0, 0, getWidth() - 8, getHeight() - 8, 20, 20);
                
                g2d.dispose();
            }
        };
        
        card.setLayout(new BorderLayout(20, 0));
        card.setPreferredSize(new Dimension(430, 110));
        card.setMaximumSize(new Dimension(430, 110));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.setOpaque(false);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        
        // Hover effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.getComponent(0).setEnabled(true); // Trigger repaint
                card.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.getComponent(0).setEnabled(false);
                card.repaint();
            }
        });
        
        // Bank Logo
        JLabel logoLabel = new JLabel();
        logoLabel.setPreferredSize(new Dimension(150, 80));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setVerticalAlignment(SwingConstants.CENTER);
        
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(logoPath));
            Image originalImage = icon.getImage();
            
            // Scale to fit while maintaining aspect ratio
            int maxWidth = 150;
            int maxHeight = 70;
            
            int originalWidth = originalImage.getWidth(null);
            int originalHeight = originalImage.getHeight(null);
            
            double scale = Math.min(
                (double) maxWidth / originalWidth,
                (double) maxHeight / originalHeight
            );
            
            int scaledWidth = (int) (originalWidth * scale);
            int scaledHeight = (int) (originalHeight * scale);
            
            Image scaledImage = originalImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            // Fallback if image not found
            logoLabel.setText("<html><div style='text-align:center; color:#999; font-size:12px;'>" + 
                            bankName + "<br>Logo</div></html>");
            logoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        }
        
        card.add(logoLabel, BorderLayout.CENTER);
        
        // Arrow Icon
        JLabel arrowLabel = new JLabel("›");
        arrowLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        arrowLabel.setForeground(new Color(4, 98, 66));
        arrowLabel.setPreferredSize(new Dimension(30, 80));
        arrowLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(arrowLabel, BorderLayout.EAST);
        
        return card;
    }
    
    private void selectBank(String bank) {
        selectedBank = bank;
        dispose();
    }
    
    private void cancelSelection() {
        selectedBank = null;
        dispose();
    }
    
    public String getSelectedBank() {
        return selectedBank;
    }
}