import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class JavaQuizApp extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private String currentUser;
    private Map<String, String> users;
    private Map<String, List<QuizResult>> userScores;
    private static final String USERS_FILE = "users.dat";
    private static final String SCORES_FILE = "scores.dat";
    
    private javax.swing.Timer quizTimer;
    private int timeRemaining;
    private JLabel timerLabel;
    private int currentScore;
    private int currentQuestionIndex;
    private List<Question> currentQuiz;
    private String currentModule;

    public JavaQuizApp() {
        setTitle("Java Quiz Application");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        users = loadUsers();
        userScores = loadScores();
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        mainPanel.add(createLoginPanel(), "login");
        mainPanel.add(createDashboardPanel(), "dashboard");
        
        add(mainPanel);
        cardLayout.show(mainPanel, "login");
    }
    
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 240, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        JLabel titleLabel = new JLabel("Java Quiz App");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(50, 50, 150));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        panel.add(new JLabel("Username:"), gbc);
        
        JTextField usernameField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);
        
        JPasswordField passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);
        
        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");
        
        styleButton(loginBtn, new Color(50, 150, 50));
        styleButton(registerBtn, new Color(50, 100, 200));
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        loginBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both username and password");
                return;
            }
            
            if (users.containsKey(username) && users.get(username).equals(password)) {
                currentUser = username;
                usernameField.setText("");
                passwordField.setText("");
                updateDashboard();
                cardLayout.show(mainPanel, "dashboard");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!");
            }
        });
        
        registerBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both username and password");
                return;
            }
            
            if (users.containsKey(username)) {
                JOptionPane.showMessageDialog(this, "Username already exists!");
            } else {
                users.put(username, password);
                userScores.put(username, new ArrayList<>());
                saveUsers();
                saveScores();
                JOptionPane.showMessageDialog(this, "Registration successful! You can now login.");
            }
        });
        
        return panel;
    }
    
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255)); // Light blue background
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(50, 50, 150));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel welcomeLabel = new JLabel("Welcome, User!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        topPanel.add(welcomeLabel, BorderLayout.WEST);
        
        JButton logoutBtn = new JButton("Logout");
        styleButton(logoutBtn, new Color(200, 50, 50));
        logoutBtn.addActionListener(e -> {
            currentUser = null;
            cardLayout.show(mainPanel, "login");
        });
        topPanel.add(logoutBtn, BorderLayout.EAST);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel selectLabel = new JLabel("Select a Quiz Module:");
        selectLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        centerPanel.add(selectLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        
        JButton classesBtn = createModuleButton("Classes");
        JButton methodsBtn = createModuleButton("Methods");
        JButton loopsBtn = createModuleButton("Loops");
        JButton conditionalBtn = createModuleButton("Conditionals");
        JButton dataTypesBtn = createModuleButton("Data Types");
        JButton programmingBtn = createModuleButton("Common Program Questions");
        
        // First row
        centerPanel.add(classesBtn, gbc);
        gbc.gridx = 1;
        centerPanel.add(methodsBtn, gbc);
        
        // Second row
        gbc.gridx = 0; gbc.gridy = 2;
        centerPanel.add(loopsBtn, gbc);
        gbc.gridx = 1;
        centerPanel.add(conditionalBtn, gbc);
        
        // Third row
        gbc.gridx = 0; gbc.gridy = 3;
        centerPanel.add(dataTypesBtn, gbc);
        gbc.gridx = 1;
        centerPanel.add(programmingBtn, gbc);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        
        JTextArea scoreArea = new JTextArea(8, 50);
        scoreArea.setEditable(false);
        scoreArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(scoreArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Your Quiz History"));
        
        bottomPanel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JButton createModuleButton(String module) {
        JButton btn = new JButton(module + " Quiz");
        btn.setPreferredSize(new Dimension(250, 80));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        // Different colors for different modules
        Color buttonColor;
        switch(module) {
            case "Classes":
                buttonColor = new Color(75, 119, 190); // Royal blue
                break;
            case "Methods":
                buttonColor = new Color(56, 142, 60); // Green
                break;
            case "Loops":
                buttonColor = new Color(173, 20, 87); // Pink
                break;
            case "Conditionals":
                buttonColor = new Color(230, 74, 25); // Orange
                break;
            case "Data Types":
                buttonColor = new Color(123, 31, 162); // Purple
                break;
            case "Programming":
                buttonColor = new Color(255, 87, 34); // Deep Orange
                break;
            default:
                buttonColor = new Color(70, 130, 180); // Default blue
        }
        
        styleButton(btn, buttonColor);
        btn.addActionListener(e -> startQuiz(module));
        
        // Add hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(buttonColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(buttonColor);
            }
        });
        
        return btn;
    }
    
    private void startQuiz(String module) {
        currentModule = module;
        currentQuiz = getQuestionsForModule(module);
        currentQuestionIndex = 0;
        currentScore = 0;
        resetTimer(); // Reset timer for first question
        
        showQuizPanel();
    }
    
    private void showQuizPanel() {
        if (currentQuestionIndex >= currentQuiz.size()) {
            finishQuiz();
            return;
        }
        
        JPanel quizPanel = new JPanel(new BorderLayout());
        quizPanel.setBackground(new Color(240, 248, 255)); // Light blue background
        
        // Add a gradient panel at the top
        JPanel gradientPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                Color color1 = new Color(100, 149, 237);
                Color color2 = new Color(0, 191, 255);
                GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(50, 50, 150));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel moduleLabel = new JLabel(currentModule + " Quiz - Question " + 
                                        (currentQuestionIndex + 1) + "/" + currentQuiz.size());
        moduleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        moduleLabel.setForeground(Color.WHITE);
        topBar.add(moduleLabel, BorderLayout.WEST);
        
        timerLabel = new JLabel("Time: 5:00");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        timerLabel.setForeground(Color.WHITE);
        topBar.add(timerLabel, BorderLayout.EAST);
        
        quizPanel.add(topBar, BorderLayout.NORTH);
        
        Question q = currentQuiz.get(currentQuestionIndex);
        
        JPanel questionPanel = new JPanel();
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
        questionPanel.setBackground(Color.WHITE);
        questionPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JTextArea questionText = new JTextArea(q.question);
        questionText.setWrapStyleWord(true);
        questionText.setLineWrap(true);
        questionText.setEditable(false);
        questionText.setFont(new Font("Arial", Font.PLAIN, 16));
        questionText.setBackground(Color.WHITE);
        questionPanel.add(questionText);
        
        questionPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        ButtonGroup group = new ButtonGroup();
        JRadioButton[] options = new JRadioButton[4];
        
        for (int i = 0; i < 4; i++) {
            options[i] = new JRadioButton(q.options[i]);
            options[i].setFont(new Font("Arial", Font.PLAIN, 14));
            options[i].setBackground(Color.WHITE);
            group.add(options[i]);
            questionPanel.add(options[i]);
            questionPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        quizPanel.add(questionPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        
        JButton submitBtn = new JButton("Submit Answer");
        styleButton(submitBtn, new Color(50, 150, 50));
        submitBtn.addActionListener(e -> {
            int selected = -1;
            for (int i = 0; i < 4; i++) {
                if (options[i].isSelected()) {
                    selected = i;
                    break;
                }
            }
            
            if (selected == -1) {
                JOptionPane.showMessageDialog(this, "Please select an answer!");
                return;
            }
            
            if (selected == q.correctAnswer) {
                currentScore++;
                JOptionPane.showMessageDialog(this, "Correct!");
            } else {
                JOptionPane.showMessageDialog(this, "Wrong! Correct answer: " + 
                                             q.options[q.correctAnswer]);
            }
            
            currentQuestionIndex++;
            showQuizPanel();
        });
        
        buttonPanel.add(submitBtn);
        quizPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainPanel.add(quizPanel, "quiz");
        cardLayout.show(mainPanel, "quiz");
        
        resetTimer();
        quizTimer.start();
    }
    
    private void finishQuiz() {
        if (quizTimer != null) {
            quizTimer.stop();
        }
        QuizResult result = new QuizResult(currentModule, currentScore, currentQuiz.size(), new Date());
        // Ensure the user's score list exists before adding the result to avoid NPE
        userScores.computeIfAbsent(currentUser, k -> new ArrayList<>()).add(result);
        saveScores();
        
        JOptionPane.showMessageDialog(this, 
            String.format("Quiz Complete!\nScore: %d/%d (%.1f%%)", 
                         currentScore, currentQuiz.size(), 
                         (currentScore * 100.0 / currentQuiz.size())));
        
        updateDashboard();
        cardLayout.show(mainPanel, "dashboard");
    }
    
    private void updateDashboard() {
        // Find the dashboard panel
        for (Component comp : mainPanel.getComponents()) {
            if (comp instanceof JPanel && ((JPanel) comp).getLayout() instanceof BorderLayout) {
                JPanel dashboardPanel = (JPanel) comp;
                
                // Update welcome message
                Component northComp = ((BorderLayout)dashboardPanel.getLayout()).getLayoutComponent(BorderLayout.NORTH);
                if (northComp instanceof JPanel) {
                    for (Component c : ((JPanel)northComp).getComponents()) {
                        if (c instanceof JLabel && ((JLabel)c).getText().startsWith("Welcome")) {
                            ((JLabel)c).setText("Welcome, " + currentUser + "!");
                            break;
                        }
                    }
                }
                
                // Update score history
                Component southComp = ((BorderLayout)dashboardPanel.getLayout()).getLayoutComponent(BorderLayout.SOUTH);
                if (southComp instanceof JPanel) {
                    for (Component c : ((JPanel)southComp).getComponents()) {
                        if (c instanceof JScrollPane) {
                            JTextArea area = (JTextArea) ((JScrollPane)c).getViewport().getView();
                            updateScoreDisplay(area);
                            break;
                        }
                    }
                }
                break;
            }
        }
    }
    
    private void updateScoreDisplay(JTextArea area) {
        StringBuilder sb = new StringBuilder();
        // Be defensive: if there are no scores for the current user, treat as empty list
        List<QuizResult> scores = userScores.getOrDefault(currentUser, new ArrayList<>());

        if (scores.isEmpty()) {
            sb.append("No quiz attempts yet. Start a quiz to see your results!");
        } else {
            for (int i = scores.size() - 1; i >= 0 && i >= scores.size() - 10; i--) {
                QuizResult r = scores.get(i);
                sb.append(String.format("%s - %s: %d/%d (%.1f%%) - %s\n", 
                    r.date.toString(), r.module, r.score, r.totalQuestions,
                    (r.score * 100.0 / r.totalQuestions), 
                    r.score >= r.totalQuestions * 0.7 ? "PASS" : "FAIL"));
            }
        }
        
        area.setText(sb.toString());
    }
    
    private void resetTimer() {
        if (quizTimer != null) {
            quizTimer.stop();
        }
        // Set time based on module type
        timeRemaining = currentModule.equals("Common Program Questions") ? 45 : 10;
        
        quizTimer = new javax.swing.Timer(1000, e -> {
            timeRemaining--;
            int minutes = timeRemaining / 60;
            int seconds = timeRemaining % 60;
            timerLabel.setText(String.format("Time: %d:%02d", minutes, seconds));
            
            if (timeRemaining <= 0) {
                quizTimer.stop();
                JOptionPane.showMessageDialog(JavaQuizApp.this, "Time's up!");
                currentQuestionIndex++;
                if (currentQuestionIndex < currentQuiz.size()) {
                    showQuizPanel();
                } else {
                    finishQuiz();
                }
            }
        });
    }
    
    private void styleButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private List<Question> getQuestionsForModule(String module) {
        List<Question> questions = new ArrayList<>();
        
        switch (module) {
            case "Classes":
                questions.add(new Question(
                    "What type of class is shown in this code?\n\n" +
                    "public class Shape {\n" +
                    "    abstract void draw();\n" +
                    "    void resize() { /* code */ }\n" +
                    "}\n",
                    new String[]{"Abstract class", "Concrete class", "Final class", "Interface"}, 0));
                    
                questions.add(new Question(
                    "What will be the output of this code?\n\n" +
                    "public class Counter {\n" +
                    "    static int count = 0;\n" +
                    "    Counter() { count++; }\n" +
                    "    public static void main(String[] args) {\n" +
                    "        Counter c1 = new Counter();\n" +
                    "        Counter c2 = new Counter();\n" +
                    "        System.out.println(Counter.count);\n" +
                    "    }\n" +
                    "}\n",
                    new String[]{"2", "0", "1", "Compilation error"}, 0));
                    
                questions.add(new Question(
                    "What is wrong with this code?\n\n" +
                    "class Animal {\n" +
                    "    private String name;\n" +
                    "}\n" +
                    "class Dog extends Animal {\n" +
                    "    void printName() {\n" +
                    "        System.out.println(name);\n" +
                    "    }\n" +
                    "}\n",
                    new String[]{"Cannot access private field name", "Class should be public", 
                                "Missing constructor", "Missing main method"}, 0));
                    
                questions.add(new Question(
                    "How many objects are created in memory?\n\n" +
                    "String s1 = \"Hello\";\n" +
                    "String s2 = \"Hello\";\n" +
                    "String s3 = new String(\"Hello\");\n" +
                    "String s4 = new String(\"Hello\");\n",
                    new String[]{"3", "4", "2", "1"}, 0));
                    
                questions.add(new Question(
                    "What will be printed?\n\n" +
                    "class Parent {\n" +
                    "    String msg = \"Parent\";\n" +
                    "}\n" +
                    "class Child extends Parent {\n" +
                    "    String msg = \"Child\";\n" +
                    "    void printMsg() {\n" +
                    "        System.out.println(super.msg);\n" +
                    "    }\n" +
                    "}\n",
                    new String[]{"Parent", "Child", "null", "Compilation error"}, 0));
                    
                questions.add(new Question(
                    "What is the output?\n\n" +
                    "class Box<T> {\n" +
                    "    T content;\n" +
                    "    Box(T content) { this.content = content; }\n" +
                    "}\n" +
                    "Box<Integer> box = new Box<>(5);\n" +
                    "System.out.println(box.content + 3);\n",
                    new String[]{"8", "53", "Error", "5"}, 0));
                    
                questions.add(new Question(
                    "Why doesn't this code compile?\n\n" +
                    "interface Drawable {\n" +
                    "    public void draw() { }\n" +
                    "}\n",
                    new String[]{"Interface methods can't have bodies", "Missing class declaration", 
                                "Missing abstract keyword", "Missing return type"}, 0));
                    
                questions.add(new Question(
                    "What's the result?\n\n" +
                    "class Test {\n" +
                    "    private static Test instance;\n" +
                    "    private Test() {}\n" +
                    "    public static Test getInstance() {\n" +
                    "        if(instance == null) instance = new Test();\n" +
                    "        return instance;\n" +
                    "    }\n" +
                    "}\n",
                    new String[]{"Singleton pattern", "Factory pattern", "Builder pattern", "Prototype pattern"}, 0));
                    
                questions.add(new Question(
                    "How many classes are in this code?\n\n" +
                    "public class Outer {\n" +
                    "    class Inner {\n" +
                    "        interface InnerInterface {}\n" +
                    "    }\n" +
                    "    static class StaticNested {}\n" +
                    "}\n",
                    new String[]{"3", "4", "2", "1"}, 0));
                    
                questions.add(new Question(
                    "What's wrong with this class?\n\n" +
                    "abstract class Calculator {\n" +
                    "    abstract int add(int a, int b);\n" +
                    "    abstract int subtract(int a, int b);\n" +
                    "}\n" +
                    "class BasicCalc extends Calculator {\n" +
                    "    int add(int a, int b) { return a + b; }\n" +
                    "}\n",
                    new String[]{"Not implementing subtract method", "Wrong return type", 
                                "Missing constructor", "Missing main method"}, 0));
                break;
                
            case "Methods":
                questions.add(new Question(
                    "Which keyword is used to define a method that doesn't return any value?",
                    new String[]{"null", "void", "empty", "none"}, 1));
                questions.add(new Question(
                    "What is method overloading?",
                    new String[]{"Methods with same name but different parameters", 
                                "Methods in different classes", "Methods that load data",
                                "Methods that run in parallel"}, 0));
                questions.add(new Question(
                    "Can a method have the same name as the class?",
                    new String[]{"Yes, it's called a constructor", "No, it will cause error", 
                                "Only in abstract classes", "Only if it's static"}, 0));
                questions.add(new Question(
                    "What does the 'static' keyword mean for a method?",
                    new String[]{"Method cannot be changed", "Method belongs to class, not instance",
                                "Method is private", "Method returns nothing"}, 1));
                questions.add(new Question(
                    "Which is the correct way to call a method named 'calculate'?",
                    new String[]{"calculate()", "method.calculate()", "call calculate()", "run calculate()"}, 0));
                // Additional 5 questions
                questions.add(new Question(
                    "What is method overriding?",
                    new String[]{"Changing method implementation in subclass", "Creating multiple methods", 
                                "Making method private", "Adding new parameters"}, 0));
                questions.add(new Question(
                    "Which annotation is used to override a method?",
                    new String[]{"@Override", "@Inherit", "@Method", "@Super"}, 0));
                questions.add(new Question(
                    "What is a varargs parameter in Java?",
                    new String[]{"Variable length argument list", "Constant parameter", 
                                "Required parameter", "Optional parameter"}, 0));
                questions.add(new Question(
                    "What happens when a method throws an exception?",
                    new String[]{"Program terminates", "Exception must be handled or declared", 
                                "Method is ignored", "Program continues normally"}, 1));
                questions.add(new Question(
                    "What is method recursion?",
                    new String[]{"Method calling itself", "Method calling another method", 
                                "Method with multiple parameters", "Method with no parameters"}, 0));
                break;
                
            case "Loops":
                questions.add(new Question(
                    "Which loop is guaranteed to execute at least once?",
                    new String[]{"for loop", "while loop", "do-while loop", "foreach loop"}, 2));
                questions.add(new Question(
                    "What keyword is used to exit a loop prematurely?",
                    new String[]{"exit", "stop", "break", "end"}, 2));
                questions.add(new Question(
                    "What does the 'continue' statement do in a loop?",
                    new String[]{"Stops the loop", "Skips current iteration", "Restarts the loop", "Pauses the loop"}, 1));
                questions.add(new Question(
                    "Which loop is best for iterating through an array?",
                    new String[]{"All loops work equally well", "while loop only", 
                                "for loop or enhanced for loop", "do-while loop only"}, 2));
                questions.add(new Question(
                    "What is an infinite loop?",
                    new String[]{"A loop that runs very fast", "A loop that never terminates",
                                "A loop with no code inside", "A loop that loops backwards"}, 1));
                // Additional 5 questions
                questions.add(new Question(
                    "What is the enhanced for loop syntax in Java?",
                    new String[]{"for(Type var : array)", "for(var in array)", 
                                "foreach(var in array)", "for(array as var)"}, 0));
                questions.add(new Question(
                    "Which variable declaration is correct in a for loop?",
                    new String[]{"for(int i = 0; i < 10; i++)", "for(i = 0; i < 10; i++)", 
                                "for(int i < 10; i++)", "for(i < 10)"}, 0));
                questions.add(new Question(
                    "What happens when 'break' is used in nested loops?",
                    new String[]{"Breaks only inner loop", "Breaks all loops", 
                                "Breaks outer loop", "Causes error"}, 0));
                questions.add(new Question(
                    "What is the difference between break and return?",
                    new String[]{"break exits loop, return exits method", "They are the same", 
                                "break exits method, return exits loop", "No difference"}, 0));
                questions.add(new Question(
                    "What is a nested loop?",
                    new String[]{"A loop inside another loop", "A loop with multiple conditions", 
                                "A loop with break statement", "A loop with continue statement"}, 0));
                break;
                
            case "Conditionals":
                questions.add(new Question(
                    "What will be the output?\n\n" +
                    "int x = 5, y = 10;\n" +
                    "if(x > 0) {\n" +
                    "    if(y > 5) {\n" +
                    "        System.out.println(\"A\");\n" +
                    "    }\n" +
                    "} else {\n" +
                    "    System.out.println(\"B\");\n" +
                    "}\n",
                    new String[]{"A", "B", "No output", "Error"}, 0));
                    
                questions.add(new Question(
                    "What is the output of this switch statement?\n\n" +
                    "int day = 3;\n" +
                    "switch(day) {\n" +
                    "    case 1: System.out.print(\"M\");\n" +
                    "    case 2: System.out.print(\"T\");\n" +
                    "    case 3: System.out.print(\"W\");\n" +
                    "    case 4: System.out.print(\"T\");\n" +
                    "    break;\n" +
                    "    default: System.out.print(\"S\");\n" +
                    "}\n",
                    new String[]{"WTT", "W", "MTW", "MTWT"}, 0));
                    
                questions.add(new Question(
                    "What's wrong with this code?\n\n" +
                    "String str = \"Hello\";\n" +
                    "if(str.length > 5) {\n" +
                    "    System.out.println(\"Long\");\n" +
                    "} else {\n" +
                    "    System.out.println(\"Short\");\n" +
                    "}\n",
                    new String[]{"length is a method, not property", "Missing semicolon", 
                                "Wrong comparison operator", "Missing parentheses"}, 0));
                    
                questions.add(new Question(
                    "What will this print?\n\n" +
                    "boolean a = true, b = false;\n" +
                    "System.out.println(a || b ? \"Yes\" : \"No\");\n",
                    new String[]{"Yes", "No", "true", "false"}, 0));
                    
                questions.add(new Question(
                    "What's the result?\n\n" +
                    "String result = null;\n" +
                    "System.out.println(\n" +
                    "    result == null ? \"Empty\" :\n" +
                    "    result.length() == 0 ? \"Zero\" : \"Full\");\n",
                    new String[]{"Empty", "Zero", "Full", "NullPointerException"}, 0));
                    
                // Additional 5 questions
                questions.add(new Question(
                    "What will be printed?\n\n" +
                    "int score = 85;\n" +
                    "String result = switch(score/10) {\n" +
                    "    case 10, 9 -> \"A\";\n" +
                    "    case 8 -> \"B\";\n" +
                    "    case 7 -> \"C\";\n" +
                    "    default -> \"F\";\n" +
                    "};\n" +
                    "System.out.println(result);\n",
                    new String[]{"B", "A", "C", "F"}, 0));
                    
                questions.add(new Question(
                    "What's wrong with this code?\n\n" +
                    "int num = 5;\n" +
                    "if(num = 10) {\n" +
                    "    System.out.println(\"Equal\");\n" +
                    "}\n",
                    new String[]{"Assignment instead of comparison", "Missing else clause", 
                                "Wrong data type", "Missing semicolon"}, 0));
                    
                questions.add(new Question(
                    "What will this code print?\n\n" +
                    "String str = \"Java\";\n" +
                    "if(str instanceof String s && s.length() > 3) {\n" +
                    "    System.out.println(\"Long String\");\n" +
                    "}\n",
                    new String[]{"Long String", "Nothing", "Compilation error", "Runtime error"}, 0));
                    
                questions.add(new Question(
                    "What's the output?\n\n" +
                    "int x = 1, y = 1;\n" +
                    "if(x++ == 1 && ++y == 2) {\n" +
                    "    x++; y++;\n" +
                    "}\n" +
                    "System.out.println(x + \" \" + y);\n",
                    new String[]{"3 3", "2 2", "1 1", "3 2"}, 0));
                    
                questions.add(new Question(
                    "What will be printed?\n\n" +
                    "String type = \"int\";\n" +
                    "var result = switch(type) {\n" +
                    "    case \"int\" -> 42;\n" +
                    "    case \"String\" -> \"Hello\";\n" +
                    "    default -> null;\n" +
                    "};\n" +
                    "System.out.println(result.getClass());\n",
                    new String[]{"class java.lang.Integer", "class java.lang.String", 
                                "Compilation error", "Runtime error"}, 0));
                break;
                
            case "Programming":
                questions.add(new Question(
                    "What is the output of this code?\n\n" +
                    "public class Test {\n" +
                    "    public static void main(String[] args) {\n" +
                    "        int x = 5;\n" +
                    "        System.out.println(x++ + ++x);\n" +
                    "    }\n" +
                    "}", 
                    new String[]{"12", "11", "10", "13"}, 0));
                    
                questions.add(new Question(
                    "What is the output?\n\n" +
                    "String str = \"Hello\";\n" +
                    "str.concat(\" World\");\n" +
                    "System.out.println(str);",
                    new String[]{"Hello", "Hello World", "World", "null"}, 0));
                    
                questions.add(new Question(
                    "What is printed?\n\n" +
                    "int[] arr = {1, 2, 3, 4};\n" +
                    "System.out.println(arr[0] + arr[3] + arr.length);",
                    new String[]{"9", "10", "7", "8"}, 0));
                    
                questions.add(new Question(
                    "Output of:\n\n" +
                    "String s1 = \"Java\";\n" +
                    "String s2 = new String(\"Java\");\n" +
                    "System.out.println(s1 == s2);",
                    new String[]{"false", "true", "Java", "compilation error"}, 0));
                    
                questions.add(new Question(
                    "What's the result?\n\n" +
                    "int i = 0;\n" +
                    "while(i < 5) {\n" +
                    "    i++;\n" +
                    "    if(i == 3) continue;\n" +
                    "    System.out.print(i);\n" +
                    "}",
                    new String[]{"1245", "12345", "1234", "145"}, 3));
                    
                questions.add(new Question(
                    "Output?\n\n" +
                    "try {\n" +
                    "    System.out.print(\"A\");\n" +
                    "    throw new Exception();\n" +
                    "} catch(Exception e) {\n" +
                    "    System.out.print(\"B\");\n" +
                    "} finally {\n" +
                    "    System.out.print(\"C\");\n" +
                    "}",
                    new String[]{"ABC", "AB", "AC", "BC"}, 0));
                    
                questions.add(new Question(
                    "Result of:\n\n" +
                    "StringBuilder sb = new StringBuilder(\"Hello\");\n" +
                    "sb.reverse().append(\"!\").reverse();\n" +
                    "System.out.println(sb);",
                    new String[]{"Hello!", "!olleH", "olleH!", "Hello"}, 0));
                    
                questions.add(new Question(
                    "What prints?\n\n" +
                    "int x = 10;\n" +
                    "System.out.println(x > 5 ? x < 15 ? \"A\" : \"B\" : \"C\");",
                    new String[]{"A", "B", "C", "Error"}, 0));
                    
                questions.add(new Question(
                    "Output of:\n\n" +
                    "List<Integer> list = Arrays.asList(1, 2, 3);\n" +
                    "list.stream()\n" +
                    "    .map(x -> x * 2)\n" +
                    "    .forEach(System.out::print);",
                    new String[]{"246", "123", "666", "Error"}, 0));
                    
                questions.add(new Question(
                    "What's printed?\n\n" +
                    "int a = 5;\n" +
                    "System.out.println(a += a -= a *= a);",
                    new String[]{"0", "-20", "-25", "-30"}, 2));
                    
                questions.add(new Question(
                    "Result?\n\n" +
                    "String[] arr = {\"A\", \"B\", \"C\"};\n" +
                    "for(String s : arr) {\n" +
                    "    s = s.toLowerCase();\n" +
                    "}\n" +
                    "System.out.println(Arrays.toString(arr));",
                    new String[]{"[A, B, C]", "[a, b, c]", "null", "Error"}, 0));
                    
                questions.add(new Question(
                    "Output:\n\n" +
                    "int i = 1;\n" +
                    "do {\n" +
                    "    System.out.print(i++);\n" +
                    "} while(i <= 3);\n" +
                    "System.out.print(i);",
                    new String[]{"1234", "123", "1233", "124"}, 0));
                    
                questions.add(new Question(
                    "What prints?\n\n" +
                    "int[][] arr = {{1,2}, {3,4}};\n" +
                    "System.out.print(arr[1][0]);",
                    new String[]{"3", "1", "2", "4"}, 0));
                    
                questions.add(new Question(
                    "Result of:\n\n" +
                    "System.out.println(\"Java\".indexOf('a') + \n" +
                    "                   \"Java\".lastIndexOf('a'));",
                    new String[]{"3", "2", "4", "1"}, 1));
                    
                questions.add(new Question(
                    "Output?\n\n" +
                    "int mask = 0x000F;\n" +
                    "int value = 0x2222;\n" +
                    "System.out.println(value & mask);",
                    new String[]{"2", "4", "8", "16"}, 0));
                break;
                
            case "Data Types":
                questions.add(new Question(
                    "What is the size of int data type in Java?",
                    new String[]{"4 bytes", "2 bytes", "8 bytes", "1 byte"}, 0));
                questions.add(new Question(
                    "Which data type should be used to store decimal numbers with high precision?",
                    new String[]{"double", "int", "float", "BigDecimal"}, 3));
                questions.add(new Question(
                    "What is the default value of a boolean variable in Java?",
                    new String[]{"false", "true", "null", "0"}, 0));
                questions.add(new Question(
                    "Which of these is not a primitive data type in Java?",
                    new String[]{"String", "boolean", "char", "byte"}, 0));
                questions.add(new Question(
                    "What is type casting in Java?",
                    new String[]{"Converting one data type to another", "Creating new variables", 
                                "Declaring constants", "Comparing variables"}, 0));
                break;
        }
        
        return questions;
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, String> loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USERS_FILE))) {
            return (Map<String, String>) ois.readObject();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
    
    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, List<QuizResult>> loadScores() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SCORES_FILE))) {
            return (Map<String, List<QuizResult>>) ois.readObject();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
    
    private void saveScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SCORES_FILE))) {
            oos.writeObject(userScores);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JavaQuizApp app = new JavaQuizApp();
            app.setVisible(true);
        });
    }
}

class Question {
    String question;
    String[] options;
    int correctAnswer;
    
    Question(String q, String[] opts, int correct) {
        question = q;
        options = opts;
        correctAnswer = correct;
    }
}

class QuizResult implements Serializable {
    String module;
    int score;
    int totalQuestions;
    Date date;
    
    QuizResult(String m, int s, int t, Date d) {
        module = m;
        score = s;
        totalQuestions = t;
        date = d;
    }
}