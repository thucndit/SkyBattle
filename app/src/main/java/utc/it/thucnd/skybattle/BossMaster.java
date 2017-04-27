package utc.it.thucnd.skybattle;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.Random;

public class BossMaster extends GameObject {

    private Random rand = new Random();
    private Animation animation = new Animation();
    private Bitmap spritesheet;
    private int dx;
    private int upScore;
    private int picPlus;
    private long startTime;

    public BossMaster(Bitmap res, int x, int y, int w, int h, int lv, int numFrames) {
        super.x = x;
        super.y = y;
        width = w;
        height = h;
        upScore = 10 + lv * 5;
        picPlus = 25 + lv * 10;
        if (picPlus > 65) picPlus = 65;
        dx = rand.nextInt(8) - 4;
        Bitmap[] image = new Bitmap[numFrames];
        spritesheet = res;
        for (int i = 0; i < image.length; i++) {
            image[i] = Bitmap.createBitmap(spritesheet, i * width, 0, width, height);
        }
        animation.setFrames(image);
        animation.setDelay(100);
        startTime = System.nanoTime();
    }

    public void update() {
        long elapsed = (System.nanoTime() - startTime) / 1000000;
        if (elapsed > 1700) {
            dx = rand.nextInt(8) - 4;
            startTime = System.nanoTime();
        }
        x += dx;
        y += 1;
        animation.update();
        //giới hạn trong màn hình
        if (x < 5) x = 5;
        if (x > GamePanel.WIDTH - 280) x = GamePanel.WIDTH - 280;
    }

    public void draw(Canvas canvas) {
        try {

            canvas.drawBitmap(animation.getImage(), x, y, null);
        } catch (Exception e) {
        }
    }

    public int getUpScore() {
        return upScore;
    }

    public int getPicPlus() {
        return picPlus;
    }

    @Override
    public Rect getRectangle() {
        //giảm khoảng cách để thấy được va chạm
        return new Rect(x + 5, y + 5, x + width - 5, y + height - 5);
    }
}
