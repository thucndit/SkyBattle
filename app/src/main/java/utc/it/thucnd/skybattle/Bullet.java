package utc.it.thucnd.skybattle;


import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Bullet extends GameObject{
    private Bitmap spritesheet;
    private Animation animation = new Animation();
    private int speed;

    public Bullet(Bitmap res,int x, int y, int w, int h, int s, int numFrames) {
        super.x = x + 27;
        super.y = y - 25;
        height = h;
        width = w;
        speed = s;
        Bitmap[] image = new Bitmap[numFrames];
        spritesheet = res;
        for (int i = 0; i < image.length; i++) {
            image[i] = Bitmap.createBitmap(spritesheet, 0, i * height, width, height);
        }
        animation.setFrames(image);
        animation.setDelay(30);
    }
    public void update() {
        //animation.update();
        y -= speed;
    }
    public void draw(Canvas canvas) {

        canvas.drawBitmap(animation.getImage(), x, y, null);
    }
}
