package breakout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import breakout.Brick.Type;

public class Canvas extends JPanel implements ActionListener, MouseMotionListener, MouseListener, KeyListener {
    /**
     *
     */
    private static final long serialVersionUID = -5699255769305413877L;
    public static final int HEIGHT = 800;
    public static final int WIDTH = 1500;

    private int horizontalCount;
    private BufferedImage image;
    private Graphics2D bufferedGraphics;
    private Timer time;
    private static final Font endFont = new Font(Font.SANS_SERIF, Font.BOLD, 20);
    private static final Font scoreFont = new Font(Font.SANS_SERIF, Font.BOLD, 15);

    private Paddle player1;
    private Paddle player2;
    private Ball ball1;
    private Ball ball2;
    ArrayList<ArrayList<Brick>> bricks;

    /**
     * Prepares the screen, centers the paddle and the ball. The ball
     * will be located in the center of the paddle, and the paddle will
     * be located on the center of the screen
     * Sunde
     * The bricks are displayed in columns across the screen with the
     * screen being split based on the width of an individual brick.
     * Each brick is stored in a temporary ArrayList, which is added
     * to the classes ArrayList which contains all of the bricks.
     */
    public Canvas() {
        super();
        setFocusable(true);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        bufferedGraphics = image.createGraphics();

        //time set to 15 to get ~67fps
        time = new Timer(15, this);

        //instantiates the two paddles
        player1 = new Paddle((WIDTH / 2) - (Paddle.PADDLE_WIDTH / 2), 30);
        player2 = new Paddle((WIDTH / 2) - (Paddle.PADDLE_WIDTH / 2), 770);

        ball1 = new Ball(((player1.getX() + (Paddle.PADDLE_WIDTH / 2)) - (Ball.DIAMETER / 2)),
                (player1.getyPos() + (Ball.DIAMETER + 10)), -5, 5);
        ball2 = new Ball(((player2.getX() + (Paddle.PADDLE_WIDTH / 2)) - (Ball.DIAMETER / 2)),
                (player2.getyPos() - (Ball.DIAMETER + 10)), -5, -5);

        bricks = new ArrayList<ArrayList<Brick>>();
        horizontalCount = WIDTH / Brick.BRICK_WIDTH;
        for (int i = 0; i < 8; ++i) {
            ArrayList<Brick> temp = new ArrayList<Brick>();
            Type rowColor = null;
            switch (i) {
                case 0:
                case 2:
                    rowColor = Type.LOW;
                    break;
                case 1:
                case 3:
                case 5:
                    rowColor = Type.MEDIUM;
                    break;
                case 4:
                case 6:
                    rowColor = Type.HIGH;
                    break;
                case 7:
                default:
                    rowColor = Type.ULTRA;
                    break;
            }
            for (int j = 0; j < horizontalCount; ++j) {
                Brick tempBrick = new Brick((j * Brick.BRICK_WIDTH), ((i + 15) * Brick.BRICK_HEIGHT), rowColor);
                temp.add(tempBrick);
            }
            bricks.add(temp);
            addMouseMotionListener(this);
            addMouseListener(this);
            addKeyListener(this);
            requestFocus();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        checkCollisions();
        ball1.move();
        for (int i = 0; i < bricks.size(); ++i) {
            ArrayList<Brick> al = bricks.get(i);
            for (int j = 0; j < al.size(); ++j) {
                Brick b = al.get(j);
                if (b.dead()) {
                    al.remove(b);
                }
            }
        }
        ball2.move();
        for (int i = 0; i < bricks.size(); ++i) {
            ArrayList<Brick> al = bricks.get(i);
            for (int j = 0; j < al.size(); ++j) {
                Brick b = al.get(j);
                if (b.dead()) {
                    al.remove(b);
                }
            }
        }
        repaint();
    }

    /**
     * Checks for any collisions, if the ball hits the upper wall, or the side
     * walls it changes direction. If the ball goes below the paddle, the position
     * of the ball gets reset and the player loses a life
     */
    private void checkCollisions() {
        if (player1.hitPaddle(ball1)) {
            ball1.setDY(ball1.getDY() * -1);
            return;
        }

        if (player1.hitPaddle(ball2)) {
            ball2.setDY(ball2.getDY() * -1);
            return;
        }
        //first check if ball hit any walls
        if (ball1.getX() >= (WIDTH - Ball.DIAMETER) || ball1.getX() <= 0) { // >=800
            ball1.setDX(ball1.getDX() * -1);
        }

        if (ball1.getY() > (Paddle.Y_POS + Paddle.PADDLE_HEIGHT + 10)) {
            resetBall1();
        }

        if (ball1.getY() <= 0) {
            resetBall1();
        }


        //next handle collisions between bricks


        for (int i = 0; i < bricks.size(); ++i) {
            for (Brick b : bricks.get(i)) {
                if (b.hitBy(ball1)) {
                    player1.setScore(player1.getScore() + b.getBrickType().getPoints());
                    b.decrementType();
                }
            }
        }

        if (player2.hitPaddle2(ball2)) {
            ball2.setDY(ball2.getDY() * -1);
            return;
        }

        if (player2.hitPaddle2(ball1)) {
            ball2.setDY(ball1.getDY() * -1);
            return;
        }
        //first check if ball hit any walls
        if (ball2.getX() >= (WIDTH - Ball.DIAMETER) || ball2.getX() <= 0) {
            ball2.setDX(ball2.getDX() * -1);
        }
        if (ball2.getY() > (Paddle.Y_POS + Paddle.PADDLE_HEIGHT + 10)) {
            resetBall2();
        }

        if (ball2.getY() <= 0) {
            resetBall2();
        }



        for (int i = 0; i < bricks.size(); ++i) {
            for (Brick b : bricks.get(i)) {
                if (b.hitBy(ball2)) {
                    player1.setScore(player1.getScore() + b.getBrickType().getPoints());
                    b.decrementType();
                }
            }
        }
    }

    /**
     * Sets the balls position to approximately the center of the screen, and
     * deducts a point from the user. If necessary, ends the game
     */
    private void resetBall1() {
        if (gameOver()) {
            time.stop();
            return;
        }
        ball1.setX(WIDTH / 2);
        ball1.setY((HEIGHT / 2) - 120);
        player1.setLives(player1.getLives() - 1);
        player1.setScore(player1.getScore() - 1000);

    }

    private void resetBall2() {
        if (gameOver()) {
            time.stop();
            return;
        }

        ball2.setX(WIDTH / 2);
        ball2.setY((HEIGHT / 2) + 80);
        player1.setLives(player1.getLives() - 1);
        player1.setScore(player1.getScore() - 1000);
    }

    private boolean gameOver() {
        if (player1.getLives() <= -100)
            return true;
        return false;
    }


    /**
     * Draws the screen for the game, first sets the screen up (clears it)
     * and then it begins by setting the entire screen to be white. Finally
     * it draws all of the bricks, the players paddle, and the ball on the
     * screen
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        //draws the two paddles
        bufferedGraphics.clearRect(0, 0, WIDTH, HEIGHT);
        bufferedGraphics.setColor(Color.WHITE);
        bufferedGraphics.fillRect(0, 0, WIDTH, HEIGHT);
        player1.drawPaddle(bufferedGraphics);
        player2.drawPaddle(bufferedGraphics);

        ball1.drawBall(bufferedGraphics);
        ball2.drawBall(bufferedGraphics);
        for (ArrayList<Brick> row : bricks) {
            for (Brick b : row) {
                b.drawBrick(bufferedGraphics);
            }
        }
        bufferedGraphics.setFont(scoreFont);
        bufferedGraphics.drawString("Score: " + player1.getScore(), 10, 25);
        if (gameOver() &&
                (ball1.getY() >= HEIGHT || ball2.getY() >= HEIGHT || ball2.getY() <= 0 || ball1.getY() <= 0)) {
            bufferedGraphics.setColor(Color.black);
            bufferedGraphics.setFont(endFont);
            bufferedGraphics.drawString("Game Over!  Score: " + player1.getScore(), (WIDTH / 2) - 85, (HEIGHT / 2));
        }
        if (empty()) {
            bufferedGraphics.setColor(Color.black);
            bufferedGraphics.setFont(endFont);
            bufferedGraphics.drawString("You won!  Score: " + player1.getScore(), (WIDTH / 2) - 85, (HEIGHT / 2));
            time.stop();
        }
        g.drawImage(image, 0, 0, this);
        Toolkit.getDefaultToolkit().sync();
    }


    private boolean empty() {
        for (ArrayList<Brick> al : bricks) {
            if (al.size() != 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        player2.setX(e.getX() - (Paddle.PADDLE_WIDTH / 2));
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (time.isRunning()) {
            return;
        }
        time.start();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        Canvas c = new Canvas();
        frame.add(c);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                player1.setX(player1.getX() - 15);
                break;
            case KeyEvent.VK_RIGHT:
                player1.setX(player1.getX() + 15);
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
