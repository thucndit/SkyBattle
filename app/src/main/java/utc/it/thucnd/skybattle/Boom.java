package utc.it.thucnd.skybattle;


import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Boom {
    private int x;
    private int y;
    private int width;
    private int height;
    private Animation animation = new Animation();
    private Bitmap spritesheet;

    public Boom(Bitmap res, int x, int y, int w, int h, int numFrames) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        Bitmap[] image = new Bitmap[numFrames];
        spritesheet = res;
        for (int i = 0; i < image.length; i++) {
            image[i] = Bitmap.createBitmap(spritesheet, i * width, 0, width, height);
        }
        animation.setFrames(image);
        animation.setDelay(100);
    }

    public void draw(Canvas canvas) {
        if (!animation.playedOnce()) {
            canvas.drawBitmap(animation.getImage(), x, y, null);
        }
    }

    public void update() {
        y += GamePanel.MOVESPEED;
        if (!animation.playedOnce()) {
            animation.update();
        }
    }

    public int getHeight() {
        return height;
    }
}
