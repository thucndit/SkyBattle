package utc.it.thucnd.skybattle;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class MainThread extends Thread {
    private int FPS = 35;
    private double averageFPS;
    private SurfaceHolder surfaceHolder;
    private GamePanel gamePanel;
    private boolean running;
    public static Canvas canvas;

    public MainThread(SurfaceHolder surfaceHolder, GamePanel gamePanel) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;
    }

    @Override
    public void run() {
        long startTime;
        long timeMillis;
        long waitTime;
        long totalTime = 0;
        int frameCount = 0;
        //tính thời gian chạy 1 khung hình trên millis giây
        long targetTime = 1000 / FPS;

        while (running) {
            startTime = System.nanoTime();
            canvas = null;
            //Khóa canvas lại để chỉnh sửa
            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    this.gamePanel.update();
                    this.gamePanel.draw(canvas);
                }
            } catch (Exception e) {
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            timeMillis = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime - timeMillis;
            //tạm dừng game theo FPS
            try {
                this.sleep(waitTime);
            } catch (Exception e) {
            }

            totalTime += (System.nanoTime() - startTime) / 1000000;
            frameCount++;
            //đếm và hiển trị FSP trung bình
            if (frameCount == FPS) {
                averageFPS = 1000 * frameCount / totalTime;
                frameCount = 0;
                totalTime = 0;
                System.out.println("FPS : " + averageFPS);
            }
        }
    }

    public void setRunning(boolean b) {
        running = b;
    }
}