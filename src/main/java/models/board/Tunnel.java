package models.board;

import constants.Paths;
import constants.Sizes;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;

public class Tunnel extends Board {
    protected int wallThickness = 20;
    private Image wall;

    public Tunnel() {
        super();
    }

    @Override
    public void loadImages() {
        super.loadImages();
        wall = new ImageIcon(getClass().getResource(Paths.URL_WALL)).getImage();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i < 200; i += 20) {
            g.drawImage(wall, i, 0, this);
            g.drawImage(wall, i, Sizes.HEIGHT_BOARD - 70, this);
        }
        for (int i = 600; i < Sizes.WIDTH_BOARD; i += 20) {
            g.drawImage(wall, i, 0, this);
            g.drawImage(wall, i, Sizes.HEIGHT_BOARD - 70, this);
        }

        for (int i = 20; i < 200; i += 20) {
            g.drawImage(wall, 0, i, this);
            g.drawImage(wall, Sizes.WIDTH_BOARD - 20, i, this);
        }

        for (int i = 600; i < Sizes.HEIGHT_BOARD - 70; i += 20) {
            g.drawImage(wall, 0, i, this);
            g.drawImage(wall, Sizes.WIDTH_BOARD - 20, i, this);
        }

        for (int i = 220; i < 600; i += 20) {
            g.drawImage(wall, i, 200, this);
            g.drawImage(wall, i, Sizes.HEIGHT_BOARD - 270, this);
        }
    }

    @Override
    protected void checkCollision() {
        for (int z = dots; z > 0; z--) {

            if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {
                inGame = false;
                break;
            }
        }

        if (y[0] > Sizes.HEIGHT_BOARD - 50 && ((x[0] >= 0 && x[0] <= 200) || (x[0] >= 600 && x[0] < Sizes.WIDTH_BOARD))) {
            inGame = false;
        }

        if (y[0] >= Sizes.HEIGHT_BOARD - 50 && (x[0] > 200 && x[0] < 600)) {
            y[0] = 0;
        }

        if (y[0] < 20 && ((x[0] >= 0 && x[0] <= 200) || (x[0] >= 600 && x[0] < Sizes.WIDTH_BOARD))) {
            inGame = false;
        }

        if (y[0] < 0 && (x[0] > 200 && x[0] < 600)) {
            y[0] = Sizes.HEIGHT_BOARD - 50;
        }

        if (x[0] > Sizes.WIDTH_BOARD && ((y[0] >= 0 && y[0] <= 200) || (y[0] >= Sizes.HEIGHT_BOARD - 270 && y[0] < Sizes.HEIGHT_BOARD - 50))) {
            inGame = false;
        }

        if (x[0] >= Sizes.WIDTH_BOARD && (y[0] > 200 && y[0] < Sizes.HEIGHT_BOARD - 250)) {
            x[0] = 0;
        }

        if (x[0] < 20 && ((y[0] >= 0 && y[0] <= 200) || (y[0] >= Sizes.HEIGHT_BOARD - 270 && y[0] < Sizes.HEIGHT_BOARD - 50))) {
            inGame = false;
        }

        if (x[0] < 0 && (y[0] > 200 && y[0] < Sizes.HEIGHT_BOARD - 250)) {
            x[0] = Sizes.WIDTH_BOARD;
        }

        if (!inGame) {
            if (isOnSound()) {
                InputStream inputStream = getClass().getResourceAsStream(Paths.URL_GAME_OVER);
                audioHandler.playAudio(inputStream);
            }
            timer.stop();
        }
    }

    @Override
    protected void locateApple() {
        if (apple_count % 5 == 0 && apple_count != 0) {
            locateBigApple();
        } else {
            bigApple_x = -100;
            int r = (int) (Math.random() * (RAND_POS - 2 * wallThickness));
            apple_x = ((r + wallThickness) * DOT_SIZE);

            bigApple_y = -100;
            r = (int) (Math.random() * (RAND_POS - 2 * wallThickness));
            apple_y = ((r + wallThickness) * DOT_SIZE);
        }
    }

    @Override
    protected void locateBigApple() {
        if (isOnSound()) {
//            audioHandler.setAudio(audioHandler.formatAudioPath(getClass().getResource(Paths.URL_BIG_APPLE_APP).getPath()));
            InputStream inputStream = getClass().getResourceAsStream(Paths.URL_BIG_APPLE_APP);
            audioHandler.playAudio(inputStream);
        }
        int r = (int) (Math.random() * (RAND_POS - 2 * wallThickness));
        bigApple_x = ((r + wallThickness) * DOT_SIZE);
        apple_x = -100;

        r = (int) (Math.random() * (RAND_POS - 2 * wallThickness));
        bigApple_y = ((r + wallThickness) * DOT_SIZE);
        apple_y = -100;
        setBigAppleTime();
        renderProgressBar();
    }
}