package utc.it.thucnd.skybattle;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.Random;


public class Missile extends GameObject {
    private int score;
    private int speed;
    private Random rand = new Random();
    private Animation animation = new Animation();
    private Bitmap spritesheet;

    public Missile(Bitmap res, int x, int y, int w, int h, int s, int numFrames) {
        if (x < 5) {
            x += 5;
        }
        if (x > GamePanel.WIDTH - 25) {
            x -= GamePanel.WIDTH - 25;
        }
        super.x = x;
        super.y = y;
        width = w;
        height = h;
        score = s;
        speed = 5 + (int) (rand.nextDouble() * score);
        // giới hạn tốc độ bay của tên lửa
        if (speed > 20) speed = 20;
        Bitmap[] image = new Bitmap[numFrames];
        spritesheet = res;
        for (int i = 0; i < image.length; i++) {
            image[i] = Bitmap.createBitmap(spritesheet, i * width, 0, width, height);
        }
        animation.setFrames(image);
        animation.setDelay(100 - speed);
    }

    public void update() {
        y += speed;
        animation.update();
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
        return new Rect(x + 5, y + 5, x + width - 5, y + height - 5);
    }
}
