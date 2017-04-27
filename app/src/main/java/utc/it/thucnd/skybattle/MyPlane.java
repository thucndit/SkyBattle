package utc.it.thucnd.skybattle;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;


public class MyPlane extends GameObject {
    private Bitmap spritesheet;
    private int score;
    private int mx, my;
    private Animation animation = new Animation();
    private long startTime;

    public MyPlane(Bitmap res, int w, int h, int numFrames) {

        x = GamePanel.WIDTH / 2 - 38;
        y = GamePanel.HEIGHT - 200;
        score = 0;
        height = h;
        width = w;

        Bitmap[] image = new Bitmap[numFrames];
        spritesheet = res;

        for (int i = 0; i < image.length; i++) {
            image[i] = Bitmap.createBitmap(spritesheet, i * width, 0, width, height);
        }

        animation.setFrames(image);
        animation.setDelay(100);
        startTime = System.nanoTime();
    }

    public void setMove(int mx, int my) {

        this.mx = (int) (mx / GamePanel.scaleX) - 40;
        this.my = (int) (my / GamePanel.scaleY) - 75;
    }

    public void update() {
        long elapsed = (System.nanoTime() - startTime) / 1000000;
        if (elapsed > 3000) {
            score++;
            startTime = System.nanoTime();
        }
        animation.update();
        //thay đổi vị trí
        if (Math.abs(mx - x) > 8) {
            if (mx > x) x += 4.5 * GamePanel.scaleX;
            else x -= 4.5 * GamePanel.scaleX;
        }
        if (Math.abs(my - y) > 8) {
            if (my > y) y += 4 * GamePanel.scaleY;
            else y -= 4 * GamePanel.scaleY;
        }
        //giới hạn độ cao trong màn hình
        if (y < 0) y = 0;
        if (y > GamePanel.HEIGHT - 70) y = GamePanel.HEIGHT - 70;
        if (x < 0) x = 0;
        if (x > GamePanel.WIDTH - 76) x = GamePanel.WIDTH - 76;
    }

    public void draw(Canvas canvas) {

        canvas.drawBitmap(animation.getImage(), x, y, null);
    }

    public int getScore() {
        return score;
    }

    public void addScore(int s) {
        score += s;
    }

    public void reset() {
        x = GamePanel.WIDTH / 2 - 50;
        y = GamePanel.HEIGHT - 200;
        score = 0;
    }

    @Override
    public Rect getRectangle() {
        //giảm khoảng cách để thấy được va chạm
        return new Rect(x + 5, y + 5, x + width - 5, y + height - 5);
    }
}
