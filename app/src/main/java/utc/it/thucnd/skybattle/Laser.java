package utc.it.thucnd.skybattle;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Laser extends GameObject {
    private Bitmap spritesheet;
    private Animation animation = new Animation();
    private int speed;

    public Laser(Bitmap res, int x, int y, int w, int h, int s, boolean left, int numFrames) {
        if(left) super.x = x + 75;
        else super.x = x + 175;
        super.y = y + 70;
        height = h;
        width = w;
        speed = s;
        Bitmap[] image = new Bitmap[numFrames];
        spritesheet = res;
        for (int i = 0; i < image.length; i++) {
            image[i] = Bitmap.createBitmap(spritesheet, i * width, 0, width, height);
        }
        animation.setFrames(image);
        animation.setDelay(100);
    }

    public void update() {
        animation.update();
        y += speed;
    }

    public void draw(Canvas canvas) {

        canvas.drawBitmap(animation.getImage(), x, y, null);
    }
}

