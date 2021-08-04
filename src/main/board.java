package main;

import java.util.Random;
import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class board extends JPanel implements ActionListener {

    private String Score = "Score:";
    private String finalScore = "Final Score:";
    private int scoreCounter = 0;
    private String scoreString = String.valueOf(scoreCounter);

    private final int B_WIDTH = 300;
    private final int B_HEIGHT = 300;
    private final int DOT_SIZE = 10;
    private final int ALL_DOTS = 900;
    private final int RAND_POS = 29;
    private final int SRAND_POS = 29;
    private final int DELAY = 140;
    private int initial_speed = 150;

    //this is the x y coords for the snake head
    private final int x[] = new int[ALL_DOTS];
    private final int y[] = new int[ALL_DOTS];

    //number of dots following the snake head
    private int dots;

    //these 2 used for locateApple x,y are the coords for the apple
    //google how to use the math.Rand function
    private int apple_x;
    private int apple_y;

    private int super_apple_x;
    private int super_apple_y;

    private int Rocket_x;
    private int Rocket_y;

    private int bad_apple_x;
    private int bad_apple_y;

    //this will be used in the keyPressed function
    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;


    private boolean inGame = true;

    //dont worry about these 4
    private Timer timer;
    private Image ball;
    private Image apple;
    private Image head;
    private Image superApple;
    private Image Rocket;
    private Image badApple;

    public String filePath;
    public String filePath2;
    public String filePath3;
    public String filePath4;
    public String filePath5;
    public String message = "Game Over";
    public SimpleAudioPlayer bg;
    public SimpleAudioPlayer eatingnoise;
    public SimpleAudioPlayer eatingSApple;
    public SimpleAudioPlayer eatingBApple;
    public SimpleAudioPlayer goingFast;

    public board() {

        initBoard();
    }

    private void initBoard() {

        addKeyListener(new TAdapter());
        setBackground(Color.black);
        setFocusable(true);

        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        loadImages();
        initGame();
    }



    private void loadImages() {

        ImageIcon iid = new ImageIcon("src/resources/dot.png");
        ball = iid.getImage();

        ImageIcon iia = new ImageIcon("src/resources/apple.png");
        apple = iia.getImage();

        ImageIcon iih = new ImageIcon("src/resources/head.png");
        head = iih.getImage();

        ImageIcon iic = new ImageIcon("src/resources/superApple.png");
        superApple = iic.getImage();

        ImageIcon iix = new ImageIcon("src/resources/Rocket.png");
        Rocket = iix.getImage();

        ImageIcon iim = new ImageIcon("src/resources/badApple.png");
        badApple = iim.getImage();

    }

    private void initGame() {
        dots = 3;

        for (int z = 0; z < dots; z++) {
            x[z] = 50 - z * 10;
            y[z] = 50;
        }

        locateApple();

        locateSuperApple();

        locateRocket();

        locateBadApple();

        try{
            filePath =  "src/resources/BackgroundSong.wav";
            bg = new SimpleAudioPlayer(filePath);


            bg.play();
        }catch(Exception ex){
            System.out.println("Error with playing sound.");
            ex.printStackTrace();
        }

        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }

    private void doDrawing(Graphics g) {

        if (inGame) {

            g.drawImage(apple, apple_x, apple_y, this);

            g.drawImage(superApple, super_apple_x, super_apple_y, this);

            g.drawImage(Rocket, Rocket_x, Rocket_y, this);

            g.drawImage(badApple, bad_apple_x, bad_apple_y, this);

            for (int z = 0; z < dots; z++) {
                if (z == 0) {
                    g.drawImage(head, x[z], y[z], this);
                } else {
                    g.drawImage(ball, x[z], y[z], this);
                }
            }

            Toolkit.getDefaultToolkit().sync();
            Font small = new Font("Comic Sans", Font.BOLD, 14);
            FontMetrics metr = getFontMetrics(small);
            g.setColor(Color.white);
            g.setFont(small);
            g.drawString(scoreString, (B_WIDTH - metr.stringWidth(scoreString)) / 2 - 95, B_HEIGHT / 2 - 133);
            g.drawString(Score, (B_WIDTH - metr.stringWidth(Score)) / 2 - 126, B_HEIGHT / 2 - 134);

        } else {

            gameOver(g);

        }
    }

    private void gameOver(Graphics g) {
        try{
            bg.stop();
        }catch(Exception ex){
            System.out.println("error");
            ex.printStackTrace();
        }

    }

    //check if your head is on the apple, if it is then add an extra "dot"
    // then spawn a new apple using the function locateapple()
    private void checkApple() {
        if((x[0] == apple_x) && (y[0] == apple_y)){
            locateApple();

            try{
                filePath2 =  "src/resources/AppleEating.wav";
                eatingnoise = new SimpleAudioPlayer(filePath2);
                eatingnoise.play();
            }catch(Exception ex){
                System.out.println("Error with playing sound.");
                ex.printStackTrace();
            }

            dots++;
            scoreCounter ++;
            scoreString = String.valueOf(scoreCounter);
        }
    }

    private void checkSuperApple() {
        if((x[0] == super_apple_x) && (y[0] == super_apple_y)){
            locateSuperApple();

            try{
                filePath3 = "src/resources/eatingSApple.wav";
                eatingSApple = new SimpleAudioPlayer(filePath3);
                eatingSApple.play();
            }catch(Exception ex){
                System.out.println("Error with playing sound.");
                ex.printStackTrace();
            }

            dots = dots + 3;
            scoreCounter = scoreCounter + 5;
            scoreString = String.valueOf(scoreCounter);
        }
    }

    private void checkRocket(){
        if((x[0] == Rocket_x) && (y[0] == Rocket_y)) {
            locateRocket();

            try{
                filePath5 = "src/resources/goingFast.wav";
                goingFast = new SimpleAudioPlayer(filePath5);
                goingFast.play();
            }catch(Exception ex){
                System.out.println("Error with playing sound.");
                ex.printStackTrace();
            }

            initial_speed = initial_speed - 20;
            if(initial_speed <= 40){
                initial_speed = 150;
            }
            timer.setDelay(initial_speed);

        }
    }

    private void checkBadApple() {
        if((x[0] ==  bad_apple_x) && (y[0] == bad_apple_y)){
            locateBadApple();

            try{
                filePath4 = "src/resources/eatingBApple.wav";
                eatingBApple = new SimpleAudioPlayer(filePath4);
                eatingBApple.play();
            }catch(Exception ex){
                System.out.println("Error with playing sound.");
                ex.printStackTrace();
            }

            dots = dots - 4;
            if (dots <= 1){
                inGame = false;
            }
            scoreCounter--;
            scoreString = String.valueOf(scoreCounter);
        }
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

    //checks if the snake collides with the border, or if the snake collides with itself
    private void checkCollision() {
        if(x[0] < 0){
            inGame = false;
        }

        if(x[0] > B_WIDTH){
            inGame = false;
        }

        if(y[0] < 0){
            inGame = false;
        }

        if(y[0] > B_HEIGHT){
            inGame = false;
        }


        for (int i = 1; i < ALL_DOTS; i++){
            if ((x[0] == x[i]) && (y[0] == y[i])){
                inGame = false;

            }
        }

    }


    //sets a random position for the apple
    private void locateApple() {
        apple_x = (int)(Math.random() * RAND_POS);
        apple_x = apple_x * DOT_SIZE;
        apple_y = (int)(Math.random() * RAND_POS);
        apple_y = apple_y * DOT_SIZE;

    }

    private void locateSuperApple() {
        super_apple_x = (int)(Math.random() * SRAND_POS);
        super_apple_x = super_apple_x * DOT_SIZE;
        super_apple_y = (int)(Math.random() * SRAND_POS);
        super_apple_y = super_apple_y * DOT_SIZE;

    }

    private void locateRocket() {
        Rocket_x = (int)(Math.random() * SRAND_POS);
        Rocket_x = Rocket_x * DOT_SIZE;
        Rocket_y = (int)(Math.random() * SRAND_POS);
        Rocket_y = Rocket_y * DOT_SIZE;

    }

    private void locateBadApple() {
        bad_apple_x = (int)(Math.random() * SRAND_POS);
        bad_apple_x = bad_apple_x * DOT_SIZE;
        bad_apple_y = (int)(Math.random() * SRAND_POS);
        bad_apple_y = bad_apple_y * DOT_SIZE;

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (inGame) {

            checkApple();
            checkSuperApple();
            checkRocket();
            checkBadApple();
            checkCollision();
            move();

        }
        else{
            String[] options = new String[] {"Play again","Quit"};
            int choice = JOptionPane.showOptionDialog(null, message, "Game over", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,null,options,options[0]);

            if (choice == 0) {
                timer.stop();
                inGame=true;
                initBoard();
                leftDirection = false;
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            } else {
                System.exit(0);
            }
        }


        repaint();
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            if ((e.getKeyCode() == KeyEvent.VK_RIGHT) && (leftDirection == false)) {
                leftDirection = false;
                rightDirection = true;
                upDirection = false;
                downDirection = false;

            }

            if ((e.getKeyCode() == KeyEvent.VK_LEFT) && (rightDirection == false)) {
                leftDirection = true;
                rightDirection = false;
                upDirection = false;
                downDirection = false;
            }

            if ((e.getKeyCode() == KeyEvent.VK_DOWN) && (upDirection == false)) {
                leftDirection = false;
                rightDirection = false;
                upDirection = false;
                downDirection = true;

            }

            if ((e.getKeyCode() == KeyEvent.VK_UP) && (downDirection == false)) {
                leftDirection = false;
                rightDirection = false;
                upDirection = true;
                downDirection = false;
            }


            //up/left/down/right arrowkeys have keycodes

            //this function detects what key is pressed so the correct user input can be used to move the snake around the box

        }
    }
}
