package utc.it.thucnd.skybattle;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Smokepuff extends GameObject {
    private long startTime;
    private int timeLife;
    public int r;

    public Smokepuff(int x, int y) {
        r = 7;
        super.x = x + 40;
        super.y = y ;
        timeLife = 0;
        startTime = System.nanoTime();
    }

    public void update() {
        long elapsed = (System.nanoTime() - startTime) / 1000000;
        if (elapsed > 10) {
            timeLife ++;
            startTime = System.nanoTime();
        }
        y -= 3;
    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(x - r, y - r, r, paint);
        canvas.drawCircle(x - r + 2, y - r - 2, r, paint);
        canvas.drawCircle(x - r + 4, y - r + 1, r, paint);
    }
    public int getTimeLife() {
        return timeLife;
    }
}
