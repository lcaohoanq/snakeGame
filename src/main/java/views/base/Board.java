package views.base;

import views.UIPrompts;
import constants.ResourcePaths;
import styles.UISizes;
import styles.UILabels;
import controllers.LoginController;
import modules.user.UserDAO;
import styles.UIBorders;
import styles.UIColors;
import styles.UIFonts;
import modules.sound.AudioHandler;
import views.MenuView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.InputStream;
import java.util.Objects;

public abstract class Board extends JPanel implements ActionListener {

    // Board dimensions and settings
    protected final int DOT_SIZE = 10;        // Size of the snake's body
    protected final int RAND_POS = 29;        // Random positioning parameter
    private final int ALL_DOTS = 900;       // Maximum number of dots on the board
    // Snake position and movement
    protected final int[] x = new int[ALL_DOTS];  // X-coordinate of each snake dot
    protected final int[] y = new int[ALL_DOTS];  // Y-coordinate of each snake dot
    private final int DELAY = 50;           // Timer delay for the game loop
    private final int DECREASE_DELAY = 2;       // Decrease delay for faster snake movement
    protected boolean inGame = true;    // Flag indicating whether the game is currently active
    protected int dots;                          // Current number of snake dots
    protected int apple_count = 0;               // Counter for regular apples
    protected int apple_x;                       // X-coordinate of a regular apple
    protected int bigApple_x;                    // X-coordinate of a big apple
    protected int bigApple_y;                    // Y-coordinate of a big apple
    protected int apple_y;                       // Y-coordinate of a regular apple
    // Game timers and images
    protected Timer timer;                       // Timer for regular game events
    protected AudioHandler audioHandler = new AudioHandler();
    // Snake movement directions
    protected boolean leftDirection = false;     // Flag for moving left
    protected boolean rightDirection = true;     // Flag for moving right
    protected boolean upDirection = false;       // Flag for moving up
    protected boolean downDirection = false;     // Flag for moving down
    // Game state variables
    private int score = 0;            // Player's score
    private Timer bigAppleTimer;               // Timer for big apple appearance
    private Image ball;                        // Snake body image
    private Image apple;                       // Regular apple image
    private Image head;                        // Snake head image
    private Image bigApple;                    // Big apple image
    // UI components
    private JLabel gameOverLabel;              // Label to display the "Game Over" message
    private JPanel gameOverPanel;              // Panel for UI components at the game over
    private JButton playAgainButton;           // Button to play the game again
    private JButton exitButton;                // Button to exit the game
    private JPanel playAgainExitButtonPanel;   // Panel for UI components at the game over
    private JButton backToMainMenuButton;      // Button to go back to the main menu
    private JPanel backToMainMenuButtonPanel;  // Panel for UI components at the game over
    private JLabel scoreLabel;                 // Label to display the player's score
    private int lineBottom;                    // Bottom line
    private JProgressBar bigAppleProgressBar;  // Progress bar for big apple timer
    private final JPanel bottomPanel = new JPanel(); // Panel for UI components at the bottom
    private final JPanel gameOverButtonPanel = new JPanel(); // Panel for UI components at the game over

    public Board() {
        initBoard();
    }

    private void initBoard() {
        addKeyListener(new TAdapter());
        setBackground(UIColors.OTHER_OPTIONS_L);
        setFocusable(true);
        bottomPanel.setVisible(true);
        setPreferredSize(UISizes.SIZE_BOARD);

        setLayout(new BorderLayout());
        loadImages();
        initGame();

        initBottomPanel();
        initLine();
        initGameOverPanel();
    }

    private void initLine() {
        lineBottom =
            UISizes.HEIGHT_BOARD - UISizes.LINE_SPACE_FROM_BOTTOM; // Adjust this value as needed
    }

    private void initScoreLabel() {
        // Initialize the JLabel for live score display
        scoreLabel = new JLabel(UILabels.SCORE_LIVE);
        scoreLabel.setForeground(Color.white);
        scoreLabel.setFont(UIFonts.SCORE_LIVE);
        scoreLabel.setBounds(10, UISizes.HEIGHT_BOARD - 30, 100, 20);
        scoreLabel.setVisible(true);
    }

    private void initProgressBar() {
        // Initialize the JProgressBar for big apple countdown
        bigAppleProgressBar = new JProgressBar(UISizes.MIN_PROGRESS_BAR, UISizes.MAX_PROGRESS_BAR);
        bigAppleProgressBar.setPreferredSize(UISizes.SIZE_PROGRESS_BAR);
        bigAppleProgressBar.setValue(100);
        bigAppleProgressBar.setStringPainted(true);
        bigAppleProgressBar.setForeground(UIColors.PROGRESS_BAR_LOADING);
        bigAppleProgressBar.setBackground(UIColors.PRIMARY_COLOR_L);
        bigAppleProgressBar.setVisible(false);
    }

    private void initBottomPanel() {
        initScoreLabel();
        initProgressBar();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setBackground(UIColors.OTHER_OPTIONS_L);
        bottomPanel.setBorder(UIBorders.BOTTOM_SCORE_PROGRESS_BAR);
        bottomPanel.add(scoreLabel, BorderLayout.WEST);
        bottomPanel.add(bigAppleProgressBar, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    protected void renderProgressBar() {
        // Display the progress bar
        bigAppleProgressBar.setVisible(true);
        // Start the progress bar
        bigAppleProgressBar.setValue(100);
        // Start the timer
        Timer progressBarTimer = new Timer(45, e -> {
            int value = bigAppleProgressBar.getValue();
            if (value > 0) {
                bigAppleProgressBar.setValue(value - 1);
            } else {
                ((Timer) e.getSource()).stop();
            }
        });
        progressBarTimer.start();
    }

    private void initGameOverTitle() {
        gameOverPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        gameOverLabel = new JLabel(UILabels.GAME_OVER);
        gameOverPanel.setBackground(UIColors.OTHER_OPTIONS_L);
        gameOverLabel.setForeground(UIColors.PRIMARY_COLOR_L);
        gameOverLabel.setBackground(UIColors.OTHER_OPTIONS_L);
        gameOverLabel.setFont(UIFonts.GAME_OVER);
        gameOverLabel.setBounds((UISizes.WIDTH_BOARD - 260) / 2,
            (UISizes.HEIGHT_BOARD - 50) / 2 - 50, 260, 50);
        gameOverPanel.add(gameOverLabel);
    }

    private void initPlayAgainButton() {
        playAgainButton = new JButton(UILabels.PLAY_AGAIN);
        playAgainButton.setFont(UIFonts.PLAY_EXIT_BUTTON);
        playAgainButton.setBackground(UIColors.TEXT_COLOR_L);
        playAgainButton.setForeground(UIColors.PRIMARY_COLOR_L);
        playAgainButton.setPreferredSize(UISizes.SIZE_BUTTON_GAME_OVER);
        playAgainButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                // Reset game parameters and restart the game
                resetGame();
            });
        });
    }

    private void initExitButton() {
        exitButton = new JButton(UILabels.EXIT);
        exitButton.setFont(UIFonts.PLAY_EXIT_BUTTON);
        exitButton.setBackground(UIColors.PROGRESS_BAR_LOADING);
        exitButton.setForeground(UIColors.PRIMARY_COLOR_L);
        exitButton.addActionListener(e -> {
            if (UIPrompts.IS_CONFIRM_EXIT() == JOptionPane.YES_OPTION) {
                SwingUtilities.getWindowAncestor(this).dispose();
            }
        });
        exitButton.setPreferredSize(UISizes.SIZE_BUTTON_GAME_OVER);
    }

    private void initPlayAgainExitButtonPanel() {
        initPlayAgainButton();
        initExitButton();
        playAgainExitButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        playAgainExitButtonPanel.setBackground(UIColors.OTHER_OPTIONS_L);
        playAgainExitButtonPanel.add(playAgainButton);
        playAgainExitButtonPanel.add(exitButton);
    }

    private void initBackToMainMenuButton() {
        backToMainMenuButton = new JButton(UILabels.BACK_TO_MAIN_MENU);
        backToMainMenuButton.setFont(UIFonts.PLAY_EXIT_BUTTON);
        backToMainMenuButton.setBackground(UIColors.BACK_TO_MAIN_MENU);
        backToMainMenuButton.setForeground(UIColors.PRIMARY_COLOR_L);
        backToMainMenuButton.addActionListener(e -> {
            SwingUtilities.getWindowAncestor(this).dispose();
            new MenuView().setVisible(true);
        });
        backToMainMenuButton.setPreferredSize(UISizes.SIZE_BUTTON_GAME_OVER_BACK_TO_MAIN_MENU);
    }

    private void initBackToMainMenuButtonPanel() {
        initBackToMainMenuButton();
        backToMainMenuButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        backToMainMenuButtonPanel.setBackground(UIColors.OTHER_OPTIONS_L);
        backToMainMenuButtonPanel.add(backToMainMenuButton);
    }

    private void initGameOverPanel() {
        initGameOverTitle();
        initPlayAgainExitButtonPanel();
        initBackToMainMenuButtonPanel();
        gameOverButtonPanel.setLayout(new BorderLayout());
        gameOverButtonPanel.setBackground(UIColors.OTHER_OPTIONS_L);
        gameOverButtonPanel.setBorder(UIBorders.GAME_OVER_ELEMENT);
        gameOverButtonPanel.add(gameOverPanel, BorderLayout.NORTH);
        gameOverButtonPanel.add(playAgainExitButtonPanel, BorderLayout.CENTER);
        gameOverButtonPanel.add(backToMainMenuButtonPanel, BorderLayout.SOUTH);
        gameOverButtonPanel.setVisible(false);
        add(gameOverButtonPanel, BorderLayout.CENTER);
    }

    protected void loadImages() {

        ball = new ImageIcon(getClass().getResource(ResourcePaths.URL_DOT)).getImage();

        apple = new ImageIcon(getClass().getResource(ResourcePaths.URL_APPLE)).getImage();

        head = new ImageIcon(getClass().getResource(ResourcePaths.URL_HEAD)).getImage();

        bigApple = new ImageIcon(getClass().getResource(ResourcePaths.URL_BIG_APPLE)).getImage();
    }

    private void initGame() {

        dots = 3;
        for (int z = 0; z < dots; z++) {
            x[z] = 50 - z * 10;
            y[z] = 50;
        }

        locateApple();
        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
        g.setColor(UIColors.PRIMARY_COLOR_L);
        g.drawLine(0, lineBottom, UISizes.WIDTH_BOARD, lineBottom);
    }

    private void doDrawing(Graphics g) {
        if (inGame) {
            if (apple_count % 5 == 0 && apple_count != 0) {
                g.drawImage(bigApple, bigApple_x, bigApple_y, this);
            } else {
                g.drawImage(apple, apple_x, apple_y, this);
            }
            scoreLabel.setText("Score: " + score);
            for (int z = 0; z < dots; z++) {
                if (z == 0) {
                    g.drawImage(head, x[z], y[z], this);
                } else {
                    g.drawImage(ball, x[z], y[z], this);
                }
            }

            Toolkit.getDefaultToolkit().sync();
        } else {
            gameOver(g);
            updateScore();
        }
    }

    public int compareDatabaseAndCurrentScore(int dbScore, int currentScore) {
        return dbScore - currentScore;
    }

    public int handleScore(String username) {
        UserDAO executeQuery = UserDAO.getInstance();
        int currentScore = this.score;
        int dbScore = Objects.requireNonNull(executeQuery.selectEmailAndScoreByEmail(username))
            .getScore();
        return compareDatabaseAndCurrentScore(dbScore, currentScore);
    }

    public void updateScore() {
        UserDAO executeQuery = UserDAO.getInstance();
        String username = LoginController.email;
        if (username.isEmpty()) {
            return;
        }
        // if the current score > db score, update the score in the database
        if (handleScore(username) < 0) {
            if (executeQuery.setSafeUpdate() == 0) {
                executeQuery.updateEmailScore(username, String.valueOf(this.score));
            }
        }
    }

    private void gameOver(Graphics g) {
        // Show the "Play Again" and "Exit" button after displaying "Game Over" message
        gameOverButtonPanel.setVisible(true);
        playAgainButton.setVisible(true);
        exitButton.setVisible(true);
        backToMainMenuButton.setVisible(true);
//         Hide the progress bar
        bigAppleProgressBar.setVisible(false);
    }

    private void resetGame() {
        // Reset game variables here
        // For example:
        score = 0;
        dots = 3;
        apple_count = 0;
        inGame = true;
        bigApple_x = -100;
        bigApple_y = -100;
        // Reset the snake's position
        for (int z = 0; z < dots; z++) {
            x[z] = 50 - z * 10;
            y[z] = 50;
        }
        // Reset any other necessary game state variables
        rightDirection = true;
        leftDirection = false;
        upDirection = false;
        downDirection = false;
        // Hide the "Play Again" button again
        playAgainButton.setVisible(false);
        exitButton.setVisible(false);
        backToMainMenuButton.setVisible(false);
        // Ensure that the gameOverButtonPanel is not visible
        gameOverButtonPanel.setVisible(false);
        // Restart the timer and initialize the game
        timer.stop();
        initGame();
        timer.start();
    }

    private void checkApple() {

        if ((x[0] == apple_x) && (y[0] == apple_y)) {
            dots++;
            checkScore();
            apple_count++;
            locateApple();
            if (score % 5 != 0) {
                if (isOnSound()) {
                    InputStream inputStream = getClass().getResourceAsStream(
                        ResourcePaths.URL_EATING2);
                    audioHandler.playAudio(inputStream);
                }
            }
            return;
        }
        if ((x[0] >= bigApple_x) && (x[0] <= bigApple_x + 2 * DOT_SIZE)
            && (y[0] >= bigApple_y) && (y[0] <= bigApple_y + 2 * DOT_SIZE)) {
            dots += 5;
            checkBigScore();

            // change the game speed
            int newDelay = Math.max(timer.getDelay() - DECREASE_DELAY, 0);
            timer.setDelay(newDelay);

            // disable the big apple progress bar
            bigAppleProgressBar.setVisible(false);

            // check the big apple are eaten
            bigAppleTimer.stop();

            apple_count = 0;
            locateApple();
            if (isOnSound()) {
                InputStream inputStream = getClass().getResourceAsStream(ResourcePaths.URL_EATING);
                audioHandler.playAudio(inputStream);
            }
        }
    }

    private void checkScore() {
        score++;
    }

    private void checkBigScore() {
        score += 5;
    }

    private void move() {

        for (int z = dots; z > 0; z--) {
            x[z] = x[(z - 1)];
            y[z] = y[(z - 1)];
        }

        if (leftDirection) {
            x[0] -= DOT_SIZE;
        }

        if (rightDirection) {
            x[0] += DOT_SIZE;
        }

        if (upDirection) {
            y[0] -= DOT_SIZE;
        }

        if (downDirection) {
            y[0] += DOT_SIZE;
        }
    }

    protected abstract void checkCollision();

    protected abstract void locateApple();

    protected abstract void locateBigApple();

    public void setBigAppleTime() {
        // neu ma bigAppleTimer dang null thi tao mot timer moi
        if (bigAppleTimer != null) {
            bigAppleTimer.stop();
        }

        // Apple-related variables
        // Timer for big apple appearance
        int BIG_APPLE_TIMER = 5000;
        bigAppleTimer = new Timer(BIG_APPLE_TIMER, e -> {
            bigAppleTimer.stop();
            if (isOnSound()) {
                InputStream inputStream = getClass().getResourceAsStream(
                    ResourcePaths.URL_BIG_APPLE_DIS);
                audioHandler.playAudio(inputStream);
            }
            apple_count = 0;
            locateApple();
            bigAppleProgressBar.setVisible(false);
        });
        bigAppleProgressBar.setVisible(true);
        bigAppleTimer.start();
    }

    protected boolean isOnSound() {
        System.out.println("check is On Sound: " + !audioHandler.isEmptyPath());
        return !audioHandler.isEmptyPath();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (inGame) {

            checkApple();
            checkCollision();
            move();
        }

        repaint();
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
        }
    }
}