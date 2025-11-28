import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.border.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class LoginScreen extends JFrame {
    private JPanel mainPanel;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private CardLayout cardLayout;
    private Timer carouselTimer;
    private Timer fadeTimer;
    private int currentSlide = 0;
    private int nextSlide = 1;
    private float alpha = 1.0f;
    private boolean isFading = false;
    
    // Promotional images data
    private String[] promoTitles = {
        "Welcome to Amazon Forest Cafe",
        "Fresh Coffee Daily",
        "Delicious Desserts",
        "Special Combo Deals"
    };
    
    private String[] promoDescriptions = {
        "Your favorite neighborhood coffee shop",
        "Premium beans roasted to perfection",
        "Handcrafted cakes and pastries",
        "Save 20% on combo meals"
    };
    
    // Add your image paths here (place images in a "promos" folder)
    private String[] imagePaths = {
         "Image" + File.separator + "p1.jpg",
    "Image" + File.separator + "p2.jpg",
    "Image" + File.separator + "p3.jpg",
    "Image" + File.separator + "p4.jpg"
    };
    
    private BufferedImage[] promoImages;

    public LoginScreen() {
        loadImages();
        initComponents();
        startCarousel();
    }

    private void loadImages() {
       promoImages = new BufferedImage[imagePaths.length];
    
    for (int i = 0; i < imagePaths.length; i++) {
        try {
            // Try multiple path variations
            File imgFile = new File(imagePaths[i]);
            
            // Debug: Print the absolute path being checked
            System.out.println("Attempting to load: " + imgFile.getAbsolutePath());
            System.out.println("File exists: " + imgFile.exists());
            
            if (imgFile.exists()) {
                promoImages[i] = ImageIO.read(imgFile);
                System.out.println("✓ Successfully loaded: " + imagePaths[i]);
            } else {
                // Try alternative paths
                String[] alternativePaths = {
                    imagePaths[i],
                    "src/" + imagePaths[i],
                    "../" + imagePaths[i],
                    System.getProperty("user.dir") + File.separator + imagePaths[i]
                };
                
                boolean loaded = false;
                for (String altPath : alternativePaths) {
                    File altFile = new File(altPath);
                    if (altFile.exists()) {
                        promoImages[i] = ImageIO.read(altFile);
                        System.out.println("✓ Successfully loaded from alternative path: " + altPath);
                        loaded = true;
                        break;
                    }
                }
                
                if (!loaded) {
                    System.err.println("✗ Image not found: " + imagePaths[i]);
                    System.err.println("  Current directory: " + System.getProperty("user.dir"));
                    promoImages[i] = createDefaultImage(i);
                }
            }
        } catch (Exception e) {
            System.err.println("✗ Error loading image: " + imagePaths[i]);
            System.err.println("  Error: " + e.getMessage());
            e.printStackTrace();
            promoImages[i] = createDefaultImage(i);
        }
    }
    }

    private BufferedImage createDefaultImage(int index) {
        BufferedImage img = new BufferedImage(600, 700, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        
        // Create gradient background
        Color[] colors = {
            new Color(76, 175, 80),
            new Color(255, 152, 0),
            new Color(233, 30, 99),
            new Color(33, 150, 243)
        };
        
        GradientPaint gradient = new GradientPaint(
            0, 0, colors[index % colors.length],
            0, 700, colors[index % colors.length].darker()
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, 600, 700);
        
        // Add decorative elements
        g2d.setColor(new Color(255, 255, 255, 30));
        for (int i = 0; i < 5; i++) {
            int size = 100 + (i * 50);
            g2d.fillOval(300 - size/2, 350 - size/2, size, size);
        }
        
        g2d.dispose();
        return img;
    }

    private void initComponents() {
        setTitle("Amazon Forest Cafe - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        mainPanel = new JPanel(new GridLayout(1, 2));
        
        // Left Panel - Login/Register Forms
        leftPanel = new JPanel();
        leftPanel.setBackground(new Color(250, 250, 250));
        cardLayout = new CardLayout();
        leftPanel.setLayout(cardLayout);
        
        leftPanel.add(createLoginPanel(), "login");
        leftPanel.add(createRegisterPanel(), "register");
        
        // Right Panel - Carousel
        rightPanel = createCarouselPanel();
        
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        
        add(mainPanel);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(250, 250, 250));
        panel.setLayout(null);

        // Logo/Title
        JLabel titleLabel = new JLabel("Welcome Back");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(46, 125, 50));
        titleLabel.setBounds(150, 80, 300, 40);
        panel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Login to your account");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setBounds(150, 125, 300, 25);
        panel.add(subtitleLabel);

        // Username Field
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setBounds(100, 200, 100, 25);
        panel.add(userLabel);

        JTextField userField = new JTextField();
        userField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userField.setBounds(100, 230, 400, 45);
        userField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        panel.add(userField);

        // Password Field
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passLabel.setBounds(100, 300, 100, 25);
        panel.add(passLabel);

        JPasswordField passField = new JPasswordField();
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passField.setBounds(100, 330, 400, 45);
        passField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        panel.add(passField);

        // Remember Me Checkbox
        JCheckBox rememberCheck = new JCheckBox("Remember me");
        rememberCheck.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rememberCheck.setBackground(new Color(250, 250, 250));
        rememberCheck.setBounds(100, 390, 150, 25);
        panel.add(rememberCheck);

        // Login Button
        JButton loginBtn = new JButton("LOGIN");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setBackground(new Color(76, 175, 80));
        loginBtn.setBounds(100, 440, 400, 50);
        loginBtn.setBorder(new LineBorder(new Color(76, 175, 80), 1, true));
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.setFocusPainted(false);
        
        loginBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                loginBtn.setBackground(new Color(67, 160, 71));
            }
            public void mouseExited(MouseEvent e) {
                loginBtn.setBackground(new Color(76, 175, 80));
            }
        });

        loginBtn.addActionListener(e -> handleLogin(userField.getText(), 
            new String(passField.getPassword())));
        panel.add(loginBtn);

        // Register Link
        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        registerPanel.setBackground(new Color(250, 250, 250));
        registerPanel.setBounds(100, 510, 400, 30);
        
        JLabel noAccountLabel = new JLabel("Don't have an account?");
        noAccountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        noAccountLabel.setForeground(new Color(100, 100, 100));
        
        JLabel registerLink = new JLabel("Register here");
        registerLink.setFont(new Font("Segoe UI", Font.BOLD, 13));
        registerLink.setForeground(new Color(33, 150, 243));
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(leftPanel, "register");
            }
            public void mouseEntered(MouseEvent e) {
                registerLink.setText("<html><u>Register here</u></html>");
            }
            public void mouseExited(MouseEvent e) {
                registerLink.setText("Register here");
            }
        });
        
        registerPanel.add(noAccountLabel);
        registerPanel.add(registerLink);
        panel.add(registerPanel);

        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(250, 250, 250));
        panel.setLayout(null);

        // Title
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(46, 125, 50));
        titleLabel.setBounds(150, 30, 300, 35);
        panel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Join Amazon Forest Cafe today");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setBounds(150, 70, 300, 20);
        panel.add(subtitleLabel);

        // Full Name Field
        JLabel nameLabel = new JLabel("Full Name");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameLabel.setBounds(100, 110, 100, 20);
        panel.add(nameLabel);

        JTextField nameField = new JTextField();
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        nameField.setBounds(100, 135, 400, 40);
        nameField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        panel.add(nameField);

        // Username Field
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userLabel.setBounds(100, 185, 100, 20);
        panel.add(userLabel);

        JTextField userField = new JTextField();
        userField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userField.setBounds(100, 210, 400, 40);
        userField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        panel.add(userField);

        // Email Field
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        emailLabel.setBounds(100, 260, 100, 20);
        panel.add(emailLabel);

        JTextField emailField = new JTextField();
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        emailField.setBounds(100, 285, 400, 40);
        emailField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        panel.add(emailField);

        // Phone Field
        JLabel phoneLabel = new JLabel("Phone (Optional)");
        phoneLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        phoneLabel.setBounds(100, 335, 150, 20);
        panel.add(phoneLabel);

        JTextField phoneField = new JTextField();
        phoneField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        phoneField.setBounds(100, 360, 400, 40);
        phoneField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        panel.add(phoneField);

        // Password Field
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passLabel.setBounds(100, 410, 100, 20);
        panel.add(passLabel);

        JPasswordField passField = new JPasswordField();
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passField.setBounds(100, 435, 400, 40);
        passField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        panel.add(passField);

        // Confirm Password Field
        JLabel confirmLabel = new JLabel("Confirm Password");
        confirmLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        confirmLabel.setBounds(100, 485, 150, 20);
        panel.add(confirmLabel);

        JPasswordField confirmField = new JPasswordField();
        confirmField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        confirmField.setBounds(100, 510, 400, 40);
        confirmField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        panel.add(confirmField);

        // Register Button
        JButton registerBtn = new JButton("CREATE ACCOUNT");
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setBackground(new Color(33, 150, 243));
        registerBtn.setBounds(100, 565, 400, 45);
        registerBtn.setBorder(new LineBorder(new Color(33, 150, 243), 1, true));
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerBtn.setFocusPainted(false);
        
        registerBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                registerBtn.setBackground(new Color(30, 136, 229));
            }
            public void mouseExited(MouseEvent e) {
                registerBtn.setBackground(new Color(33, 150, 243));
            }
        });

        registerBtn.addActionListener(e -> handleRegister(
            nameField.getText(),
            userField.getText(),
            emailField.getText(),
            phoneField.getText(),
            new String(passField.getPassword()),
            new String(confirmField.getPassword())
        ));
        panel.add(registerBtn);

        // Login Link
        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        loginPanel.setBackground(new Color(250, 250, 250));
        loginPanel.setBounds(100, 625, 400, 25);
        
        JLabel haveAccountLabel = new JLabel("Already have an account?");
        haveAccountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        haveAccountLabel.setForeground(new Color(100, 100, 100));
        
        JLabel loginLink = new JLabel("Login here");
        loginLink.setFont(new Font("Segoe UI", Font.BOLD, 12));
        loginLink.setForeground(new Color(76, 175, 80));
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(leftPanel, "login");
            }
            public void mouseEntered(MouseEvent e) {
                loginLink.setText("<html><u>Login here</u></html>");
            }
            public void mouseExited(MouseEvent e) {
                loginLink.setText("Login here");
            }
        });
        
        loginPanel.add(haveAccountLabel);
        loginPanel.add(loginLink);
        panel.add(loginPanel);

        return panel;
    }

    private JPanel createCarouselPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                
                // Draw current slide
                if (promoImages[currentSlide] != null) {
                    Image scaledImg = promoImages[currentSlide].getScaledInstance(
                        getWidth(), getHeight(), Image.SCALE_SMOOTH);
                    g2d.drawImage(scaledImg, 0, 0, null);
                }
                
                // Draw fading next slide
                if (isFading && promoImages[nextSlide] != null) {
                    g2d.setComposite(AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER, 1.0f - alpha));
                    Image scaledImg = promoImages[nextSlide].getScaledInstance(
                        getWidth(), getHeight(), Image.SCALE_SMOOTH);
                    g2d.drawImage(scaledImg, 0, 0, null);
                    g2d.setComposite(AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER, 1.0f));
                }
                
                // Dark overlay for text readability
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Title with shadow
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 38));
                String title = promoTitles[currentSlide];
                FontMetrics fm = g2d.getFontMetrics();
                int titleWidth = fm.stringWidth(title);
                int titleX = (getWidth() - titleWidth) / 2;
                
                // Shadow
                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.drawString(title, titleX + 2, 252);
                // Text
                g2d.setColor(Color.WHITE);
                g2d.drawString(title, titleX, 250);
                
                // Description with shadow
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 18));
                String desc = promoDescriptions[currentSlide];
                fm = g2d.getFontMetrics();
                int descWidth = fm.stringWidth(desc);
                int descX = (getWidth() - descWidth) / 2;
                
                // Shadow
                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.drawString(desc, descX + 2, 302);
                // Text
                g2d.setColor(Color.WHITE);
                g2d.drawString(desc, descX, 300);
                
                // Slide indicators
                int indicatorY = getHeight() - 50;
                int indicatorSpacing = 25;
                int totalWidth = (promoTitles.length * indicatorSpacing);
                int startX = (getWidth() - totalWidth) / 2;
                
                for (int i = 0; i < promoTitles.length; i++) {
                    if (i == currentSlide) {
                        g2d.setColor(Color.WHITE);
                        g2d.fillRoundRect(startX + (i * indicatorSpacing), 
                            indicatorY, 20, 8, 4, 4);
                    } else {
                        g2d.setColor(new Color(255, 255, 255, 120));
                        g2d.fillOval(startX + (i * indicatorSpacing) + 4, 
                            indicatorY, 12, 12);
                    }
                }
            }
        };
        
        return panel;
    }

    private void startCarousel() {
        // Main carousel timer - switches slides every 4 seconds
        carouselTimer = new Timer(4000, e -> {
            nextSlide = (currentSlide + 1) % promoTitles.length;
            isFading = true;
            alpha = 1.0f;
            
            // Start fade animation
            if (fadeTimer != null) {
                fadeTimer.stop();
            }
            
            fadeTimer = new Timer(30, new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    alpha -= 0.05f;
                    if (alpha <= 0) {
                        alpha = 0;
                        currentSlide = nextSlide;
                        isFading = false;
                        fadeTimer.stop();
                    }
                    rightPanel.repaint();
                }
            });
            fadeTimer.start();
        });
        carouselTimer.start();
    }

    private void handleLogin(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }

        try (Connection conn = CafePos.DBConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE username = ? AND password_hash = ? AND is_active = 1";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, hashPassword(password));
            
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                showSuccess("Welcome back, " + rs.getString("full_name") + "!");
                carouselTimer.stop();
                if (fadeTimer != null) fadeTimer.stop();
                dispose();
                
                SwingUtilities.invokeLater(() -> {
                    new CafePos().setVisible(true);
                });
            } else {
                showError("Invalid username or password");
            }
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleRegister(String fullName, String username, String email,
                                String phone, String password, String confirmPassword) {
        // Validation
        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() ||
            password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Please fill in all required fields");
            return;
        }

        if (username.length() < 4) {
            showError("Username must be at least 4 characters");
            return;
        }

        // Email validation
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Please enter a valid email address");
            return;
        }

        // Phone validation (optional but if provided should be valid)
        if (!phone.isEmpty() && !phone.matches("^[0-9+\\-\\s()]{8,}$")) {
            showError("Please enter a valid phone number");
            return;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        // Show Manager Authorization Dialog
        ManagerAuthDialog authDialog = new ManagerAuthDialog(this);
        authDialog.setVisible(true);
        
        if (!authDialog.isAuthorized()) {
            showError("Registration cancelled or authorization failed");
            return;
        }

        try (Connection conn = CafePos.DBConnection.getConnection()) {
            // Check if username exists
            String checkQuery = "SELECT username FROM users WHERE username = ?";
            PreparedStatement checkPst = conn.prepareStatement(checkQuery);
            checkPst.setString(1, username);
            ResultSet rs = checkPst.executeQuery();
            
            if (rs.next()) {
                showError("Username already exists");
                return;
            }

            // Check if email exists
            checkQuery = "SELECT email FROM users WHERE email = ?";
            checkPst = conn.prepareStatement(checkQuery);
            checkPst.setString(1, email);
            rs = checkPst.executeQuery();
            
            if (rs.next()) {
                showError("Email already registered");
                return;
            }

            // Insert new user
            String insertQuery = "INSERT INTO users (full_name, username, email, phone, password_hash, is_active) VALUES (?, ?, ?, ?, ?, 1)";
            PreparedStatement insertPst = conn.prepareStatement(insertQuery);
            insertPst.setString(1, fullName);
            insertPst.setString(2, username);
            insertPst.setString(3, email);
            insertPst.setString(4, phone.isEmpty() ? null : phone);
            insertPst.setString(5, hashPassword(password));
            
            int result = insertPst.executeUpdate();
            
            if (result > 0) {
                showSuccess("Registration successful! Please login.");
                cardLayout.show(leftPanel, "login");
            }
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password; // Fallback (not recommended for production)
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", 
            JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new LoginScreen().setVisible(true);
        });
    }
}

// Manager Authorization Dialog
class ManagerAuthDialog extends JDialog {
    private boolean authorized = false;
    private JTextField managerUsernameField;
    private JPasswordField managerPasswordField;

    public ManagerAuthDialog(JFrame parent) {
        super(parent, "Manager Authorization Required", true);
        setSize(450, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
        
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(new Color(250, 250, 250));

        // Warning Icon and Title
        JLabel iconLabel = new JLabel("🔒");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        iconLabel.setBounds(190, 20, 70, 60);
        mainPanel.add(iconLabel);

        JLabel titleLabel = new JLabel("Authorization Required");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(46, 125, 50));
        titleLabel.setBounds(100, 85, 260, 30);
        mainPanel.add(titleLabel);

        JLabel infoLabel = new JLabel("Enter manager credentials to register new user");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoLabel.setForeground(new Color(100, 100, 100));
        infoLabel.setBounds(70, 115, 320, 20);
        mainPanel.add(infoLabel);

        // Manager Username
        JLabel userLabel = new JLabel("Manager Username");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userLabel.setBounds(50, 150, 150, 20);
        mainPanel.add(userLabel);

        managerUsernameField = new JTextField();
        managerUsernameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        managerUsernameField.setBounds(50, 175, 350, 35);
        managerUsernameField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        mainPanel.add(managerUsernameField);

        // Manager Password
        JLabel passLabel = new JLabel("Manager Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passLabel.setBounds(50, 220, 150, 20);
        mainPanel.add(passLabel);

        managerPasswordField = new JPasswordField();
        managerPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        managerPasswordField.setBounds(50, 245, 350, 35);
        managerPasswordField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        mainPanel.add(managerPasswordField);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(new Color(250, 250, 250));
        buttonPanel.setBounds(50, 300, 350, 50);

        // Authorize Button
        JButton authorizeBtn = new JButton("AUTHORIZE");
        authorizeBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        authorizeBtn.setForeground(Color.WHITE);
        authorizeBtn.setBackground(new Color(76, 175, 80));
        authorizeBtn.setPreferredSize(new Dimension(160, 40));
        authorizeBtn.setBorder(new LineBorder(new Color(76, 175, 80), 1, true));
        authorizeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        authorizeBtn.setFocusPainted(false);
        
        authorizeBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                authorizeBtn.setBackground(new Color(67, 160, 71));
            }
            public void mouseExited(MouseEvent e) {
                authorizeBtn.setBackground(new Color(76, 175, 80));
            }
        });

        authorizeBtn.addActionListener(e -> verifyManager());
        buttonPanel.add(authorizeBtn);

        // Cancel Button
        JButton cancelBtn = new JButton("CANCEL");
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cancelBtn.setForeground(new Color(100, 100, 100));
        cancelBtn.setBackground(Color.WHITE);
        cancelBtn.setPreferredSize(new Dimension(160, 40));
        cancelBtn.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
        cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelBtn.setFocusPainted(false);
        
        cancelBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                cancelBtn.setBackground(new Color(245, 245, 245));
            }
            public void mouseExited(MouseEvent e) {
                cancelBtn.setBackground(Color.WHITE);
            }
        });

        cancelBtn.addActionListener(e -> {
            authorized = false;
            dispose();
        });
        buttonPanel.add(cancelBtn);

        mainPanel.add(buttonPanel);

        add(mainPanel);

        // Press Enter to authorize
        managerPasswordField.addActionListener(e -> verifyManager());
    }

    private void verifyManager() {
        String username = managerUsernameField.getText().trim();
        String password = new String(managerPasswordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter manager credentials", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = CafePos.DBConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE username = ? AND password_hash = ? AND is_active = 1 AND (role = 'admin' OR role = 'manager')";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, hashPassword(password));
            
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                authorized = true;
                JOptionPane.showMessageDialog(this, 
                    "Authorization successful!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Invalid manager credentials or insufficient privileges", 
                    "Authorization Failed", JOptionPane.ERROR_MESSAGE);
                managerPasswordField.setText("");
                managerUsernameField.requestFocus();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Database error: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password;
        }
    }

    public boolean isAuthorized() {
        return authorized;
    }
}