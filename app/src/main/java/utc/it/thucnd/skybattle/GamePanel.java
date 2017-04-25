package utc.it.thucnd.skybattle;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {
    //Khai báo các biến cần sử dụng
    public static final int WIDTH = 512;
    public static final int HEIGHT = 768;
    public static float scaleX;
    public static float scaleY;
    public static final int MOVESPEED = 5;
    private Context gameContext;
    public int highscore;
    public int wait;
    private MainThread thread;
    private boolean playing;
    private boolean getout;
    private Background background;
    private MyPlane myplane;
    private long bulletStartTime;
    private Combustor combustor;
    private long missileStartTime;
    private long whiteplaneStartTime;
    private long yellowplaneStartTime;
    private long redplaneStartTime;
    private long smokeStartTime;
    private SoundPlayer sound;
    public MediaPlayer bgmusic;
    private Random rand = new Random();
    private ArrayList<Missile> missiles;
    private ArrayList<BossPlane> bossplane;
    private ArrayList<Explosion> explosion;
    private ArrayList<Bullet> bullet;
    private ArrayList<Boom> boom;
    private ArrayList<Smokepuff> smokepuff;


    public static final String GAME_PREFERENCES = "GamePrefs";
    private SharedPreferences gamePrefs;
    private SharedPreferences.Editor editor;

    public GamePanel(Context context) {
        super(context);
        this.gameContext = context;
        //Sét đặt các sự kiện liên quan tới Game
        getHolder().addCallback(this);

        //Lấy điểm sô cao nhất
        gamePrefs = context.getSharedPreferences(GAME_PREFERENCES, Context.MODE_PRIVATE);
        editor = gamePrefs.edit();
        highscore = gamePrefs.getInt("HighScore", 0);

        //cho phép gamePanel xử lý các sự kiện
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //khởi chạy vòng lặp
        thread = new MainThread(getHolder(), this);
        thread.setRunning(true);
        thread.start();
        getout = false;
        wait = 50;
        int randmap = rand.nextInt(5);
        sound = new SoundPlayer(gameContext);
        // phat am thanh nen
        bgmusic = MediaPlayer.create(gameContext, R.raw.newbattle);
        bgmusic.setLooping(true);
        bgmusic.start();
        //tính tỷ lệ màn hình với ảnh nguồn
        scaleX = getWidth() / (WIDTH * 1.f);
        scaleY = getHeight() / (HEIGHT * 1.f);

        //khởi tạo các đối tượng trong game
        if (randmap < 3)
            background = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.game_map));
        else
            background = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.game_map2));
        myplane = new MyPlane(BitmapFactory.decodeResource(getResources(), R.drawable.myplane), 76, 70, 4);
        combustor = new Combustor(BitmapFactory.decodeResource(getResources(), R.drawable.combustor), 23, 40, 4);
        missileStartTime = smokeStartTime = bulletStartTime =
                whiteplaneStartTime = yellowplaneStartTime = redplaneStartTime = System.nanoTime();
        missiles = new ArrayList<Missile>();
        explosion = new ArrayList<Explosion>();
        bossplane = new ArrayList<BossPlane>();
        bullet = new ArrayList<Bullet>();
        boom = new ArrayList<Boom>();
        smokepuff = new ArrayList<Smokepuff>();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        int counter = 0;
        while (retry && counter < 1000) {
            counter++;
            try {
                thread.setRunning(false);
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!getout) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE: {
                    playing = true;
                    myplane.setMove((int) event.getX(), (int) event.getY());
                    return true;
                }
            }
        }
        return super.onTouchEvent(event);
    }


    public void update() {
        if (playing) {
            background.setSpeed(MOVESPEED);
            background.update();
            myplane.update();
            combustor.setMove(myplane.getX(), myplane.getY());
            combustor.update();
            long elapsed;
            //bắt đầu thêm đạn bắn
            elapsed = (System.nanoTime() - bulletStartTime) / 1000000;
            if (elapsed > 200) {
                bullet.add(new Bullet(BitmapFactory.decodeResource(getResources(), R.drawable.bullet),
                        myplane.getX(), myplane.getY(), 25, 36, 15, 1));
                //sound.playGunSound();
                //reset timer
                bulletStartTime = System.nanoTime();
            }
            //cập nhật mỗi bullet và kiểm tra va trạm
            for (int i = 0; i < bullet.size(); i++) {
                bullet.get(i).update();
                for (int j = 0; j < bossplane.size(); j++) {
                    if (collision(bullet.get(i), bossplane.get(j))) {
                        myplane.addScore(3);
                        andBoom(j);
                        //xóa boss plane nếu bắn trúng boss plane
                        bossplane.remove(j);
                        sound.playHitSound();
                        break;
                    }
                }
                //loại bỏ Bullet nếu nó đi ra ngoài màn hình
                if (bullet.get(i).getY() < 20) {
                    bullet.remove(i);
                    break;
                }
            }

            //bắt đầu thêm tên lửa
            elapsed = (System.nanoTime() - missileStartTime) / 1000000;
            if (elapsed > (2000 - myplane.getScore() / 2)) {
                missiles.add(new Missile(BitmapFactory.decodeResource(getResources(), R.drawable.missile),
                        (int) (rand.nextDouble() * WIDTH - 20), -100, 20, 60, myplane.getScore(), 13));
                //reset timer
                missileStartTime = System.nanoTime();
            }
            //cập nhật mỗi tên lửa và kiểm tra va trạm
            for (int i = 0; i < missiles.size(); i++) {
                missiles.get(i).update();
                if (collision(missiles.get(i), myplane)) {
                    //xóa tên lửa nếu nó đã phát nổ
                    missiles.remove(i);
                    gameover();
                    break;
                }
                //loại bỏ tên lửa nếu nó đi ra ngoài màn hình
                if (missiles.get(i).getY() > HEIGHT + 50) {
                    missiles.remove(i);
                    break;
                }
            }
            //bắt đầu thêm boss plane
            elapsed = (System.nanoTime() - whiteplaneStartTime) / 1000000;
            if (elapsed > (2000 - myplane.getScore() / 2)) {
                bossplane.add(new BossPlane(BitmapFactory.decodeResource(getResources(), R.drawable.whiteboss),
                        (int) (rand.nextDouble() * WIDTH), -200, 70, 50, myplane.getScore(), 3));
                //reset timer
                whiteplaneStartTime = System.nanoTime();
            }
            elapsed = (System.nanoTime() - yellowplaneStartTime) / 1000000;
            if (elapsed > (3000 - myplane.getScore() / 2)) {
                bossplane.add(new BossPlane(BitmapFactory.decodeResource(getResources(), R.drawable.yellowboss),
                        (int) (rand.nextDouble() * WIDTH), -200, 70, 50, myplane.getScore(), 3));
                //reset timer
                yellowplaneStartTime = System.nanoTime();
            }

            elapsed = (System.nanoTime() - redplaneStartTime) / 1000000;
            if (elapsed > (5000 - myplane.getScore() / 2)) {
                bossplane.add(new BossPlane(BitmapFactory.decodeResource(getResources(), R.drawable.redboss),
                        (int) (rand.nextDouble() * WIDTH), -200, 70, 50, myplane.getScore(), 3));
                //reset timer
                redplaneStartTime = System.nanoTime();
            }
            elapsed = (System.nanoTime() - smokeStartTime) / 1000000;
            //cập nhật mỗi boss plane và kiểm tra va trạm
            for (int i = 0; i < bossplane.size(); i++) {
                bossplane.get(i).update();
                //thêm khói sau  boss plane
                if (elapsed > 120) {
                    smokepuff.add(new Smokepuff(bossplane.get(i).getX(), bossplane.get(i).getY()));
                    smokeStartTime = System.nanoTime();
                }
                if (collision(bossplane.get(i), myplane)) {
                    //xóa boss plane nếu nó đã phát nổ
                    bossplane.remove(i);
                    gameover();
                    break;
                }
                //loại bỏ plane nếu nó đi ra ngoài màn hình
                if (bossplane.get(i).getY() > HEIGHT + 10) {
                    bossplane.remove(i);
                    break;
                }
            }
            //cập nhật mỗi viên khói và loại bỏ
            for (int i = 0; i < smokepuff.size(); i++) {
                smokepuff.get(i).update();
                if (smokepuff.get(i).getTimeLife() > 8) {
                    smokepuff.remove(i);
                }
            }
            // cập nhật bắn nổ boss plane
            for (int i = 0; i < boom.size(); i++) {
                boom.get(i).update();
            }

        } else {
            //thêm vụ nổ khi có va trạm
            if (explosion.size() == 0) {
                explosion.add(new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.explosion), myplane.getX(),
                        myplane.getY() - 30, 100, 100, 25));
            }
            //cập nhật vụ nổ
            explosion.get(0).update();
            //đợi vụ nổ diễn ra
            if (wait != 0 && getout) {
                wait--;
            }
            if (wait == 0) {
                moveResult();
            }
        }
    }

    //hàm kiểm tra va trạm giữa 2 đối tượng trong game
    public boolean collision(GameObject a, GameObject b) {
        if (Rect.intersects(a.getRectangle(), b.getRectangle())) {
            return true;
        }
        return false;
    }

    @Override
    public void draw(Canvas canvas) {
        if (canvas != null) {
            final int savedState = canvas.save();
            canvas.scale(scaleX, scaleY);
            //bắt đầu vẽ các đối tượng lên canvas
            background.draw(canvas);
            drawText(canvas);
            //vẽ máy bay và lửa
            if (!getout) {
                myplane.draw(canvas);
                combustor.draw(canvas);
            }
            //vẽ tên lửa
            for (Missile m : missiles) {
                m.draw(canvas);
            }
            //vẽ đạn bắn
            for (Bullet bu : bullet) {
                bu.draw(canvas);
            }
            //vẽ bắn trúng
            for (Boom b : boom) {
                b.draw(canvas);
            }
            //vẽ boss plane
            for (BossPlane bp : bossplane) {
                bp.draw(canvas);
            }
            //vẽ khói
            for (Smokepuff sp : smokepuff) {
                sp.draw(canvas);
            }
            //vẽ vụ nổ
            if (!playing && wait < 50) {
                for (Explosion ex : explosion) {
                    ex.draw(canvas);
                }
            }
            canvas.restoreToCount(savedState);
        }
    }

    public void drawText(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#c60606"));
        paint.setTextSize(30);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Score: " + myplane.getScore(), 10, 25, paint);
        //canvas.drawText("HighScore: " + highscore,GamePanel.WIDTH - 210, 25, paint);
    }

    public void andBoom(int j) {
        boom.add(new Boom(BitmapFactory.decodeResource(getResources(), R.drawable.boom),
                bossplane.get(j).getX(), bossplane.get(j).getY(), 64, 64, 3));
    }

    public void gameover() {
        playing = false;
        getout = true;
        bgmusic.stop();
        sound.playOverSound();
        //lấy điểm số và lưu lại điểm cao nhất
        if (highscore < myplane.getScore()) {
            highscore = myplane.getScore();
            editor.clear();
            editor.putInt("HighScore", highscore);
            editor.commit();
        }
        //xóa các đối tượng trong game
        bullet.clear();
        boom.clear();
        explosion.clear();
        smokepuff.clear();
        bossplane.clear();
        missiles.clear();
    }

    public void moveResult() {
        gameContext = getContext();
        Intent intent = new Intent(gameContext, GameOver.class);
        intent.putExtra("Score", myplane.getScore());
        intent.putExtra("HighScore", highscore);
        gameContext.startActivity(intent);
        thread.setRunning(false);
    }

}
