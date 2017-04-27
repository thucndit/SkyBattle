package utc.it.thucnd.skybattle;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import java.util.Random;

public class BossPlane extends GameObject {
    private int score;
    private int speed;
    private Random rand = new Random();
    private Animation animation = new Animation();
    private Bitmap spritesheet;
    private int dx;
    private long startTime;

    public BossPlane(Bitmap res, int x, int y, int w, int h, int s, int numFrames) {
        super.x = x;
        super.y = y;
        width = w;
        height = h;
        score = s;
        speed = 5 + (int) (rand.nextDouble() * score);
        if (speed > 15) speed = 15;
        dx = rand.nextInt(8) - 4;
        Bitmap[] image = new Bitmap[numFrames];
        spritesheet = res;
        for (int i = 0; i < image.length; i++) {
            image[i] = Bitmap.createBitmap(spritesheet, i * width, 0, width, height);
        }
        animation.setFrames(image);
        animation.setDelay(100 - speed);
        startTime = System.nanoTime();
    }
    public void update() {
        long elapsed = (System.nanoTime() - startTime) / 1000000;
        if (elapsed > 1000) {
            dx = rand.nextInt(8) - 4;
            startTime = System.nanoTime();
        }
        x += dx;
        y += speed;
        //animation.update();
        //giới hạn trong màn hình
        if (x < 5) x = 5;
        if (x > GamePanel.WIDTH - 75) x = GamePanel.WIDTH - 75;
    }

    public void draw(Canvas canvas) {
        try {

            canvas.drawBitmap(animation.getImage(), x, y, null);
        } catch (Exception e) {
        }
    }
    @Override
    public Rect getRectangle() {
        //giảm khoảng cách để thấy được va chạm
        return new Rect(x + 10, y + 10, x + width - 10, y + height - 10);
    }
}
