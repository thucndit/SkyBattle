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
    private long startTime;

    public BossMaster(Bitmap res, int x, int y, int w, int h, int lv, int numFrames) {
        super.x = x;
        super.y = y;
        width = w;
        height = h;
        upScore = lv * 50;
        dx = rand.nextInt(10) - 5;
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
        if (elapsed > 1000) {
            dx = rand.nextInt(10) - 5;
            startTime = System.nanoTime();
        }
        x += dx;
        y += 2;
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

    @Override
    public Rect getRectangle() {
        //giảm khoảng cách để thấy được va chạm
        return new Rect(x + 5, y + 5, x + width - 5, y + height - 5);
    }
}
