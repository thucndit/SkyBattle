package utc.it.thucnd.skybattle;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Background {

    private Bitmap image;
    private int y, speed;

    public Background(Bitmap res) {
        image = res;
    }

    public void update() {
        y += speed;
        if (y > GamePanel.HEIGHT) {
            y = 0;
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, 0, y, null);
        if (y > 0) {
            canvas.drawBitmap(image, 0, -(GamePanel.HEIGHT-y), null);
        }
    }

    public void setSpeed(int s) {
        this.speed = s;
    }
}
