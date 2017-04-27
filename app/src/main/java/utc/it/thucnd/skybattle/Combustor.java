package utc.it.thucnd.skybattle;


import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Combustor extends GameObject {
    private Bitmap spritesheet;
    private Animation animation = new Animation();
    private int mx;
    private int my;

    public Combustor(Bitmap res, int w, int h, int numFrames) {
        x = GamePanel.WIDTH / 2 - 10;
        y = GamePanel.HEIGHT - 138;
        height = h;
        width = w;
        Bitmap[] image = new Bitmap[numFrames];
        spritesheet = res;
        for (int i = 0; i < image.length; i++) {
            image[i] = Bitmap.createBitmap(spritesheet, i * width, 0, width, height);
        }
        animation.setFrames(image);
        animation.setDelay(100);
    }
    public void setMove(int mx, int my) {

        this.mx = mx + 28;
        this.my = my + 63;
    }
    public void update() {
        animation.update();
        x = mx;
        y = my;
    }
    public void draw(Canvas canvas) {

        canvas.drawBitmap(animation.getImage(), x, y, null);
    }
}
