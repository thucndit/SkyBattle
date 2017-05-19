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
    private int randmap;
    private int randboss;
    private int highscore;
    private int wait;
    private int bossLevel;
    private int picPlus;

    private boolean playing;
    private boolean getout;
    private boolean addMaster;
    private boolean addExplosion;
    private boolean left;

    private MainThread thread;
    private Background background;
    private MyPlane myplane;
    private Explosion explosion;
    private Combustor combustor;

    private long StartTime;
    private long bulletStartTime;
    private long missileStartTime;
    private long bossplaneStartTime;
    private long smokeStartTime;
    private long laserStartTime;
    private long elapsed;

    private SoundPlayer sound;
    private MediaPlayer bgmusic;

    private ArrayList<Missile> missiles;
    private ArrayList<BossPlane> bossplane;
    private ArrayList<BossMaster> bossmaster;
    private ArrayList<Bullet> bullet;
    private ArrayList<Laser> laser;
    private ArrayList<Broken> broken;
    private ArrayList<Smokepuff> smokepuff;

    private Random rand = new Random();
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
        bossLevel = 1;
        addMaster = false;
        addExplosion = false;
        sound = new SoundPlayer(gameContext);

        //tính tỷ lệ màn hình với ảnh nguồn
        scaleX = getWidth() / (WIDTH * 1.f);
        scaleY = getHeight() / (HEIGHT * 1.f);
        resetTimer();
        //khởi tạo các đối tượng trong game
        randmap = rand.nextInt(2);
        switch (randmap) {
            case 0:
                background = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.game_map));
                bgmusic = MediaPlayer.create(gameContext, R.raw.newbattle);
                break;
            case 1:
                background = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.game_map2));
                bgmusic = MediaPlayer.create(gameContext, R.raw.finalbattle);
                break;
        }
        bgmusic.setLooping(true);

        myplane = new MyPlane(BitmapFactory.decodeResource(getResources(), R.drawable.myplane), 76, 70, 4);
        combustor = new Combustor(BitmapFactory.decodeResource(getResources(), R.drawable.combustor), 23, 40, 4);
        missiles = new ArrayList<Missile>();
        bossplane = new ArrayList<BossPlane>();
        bossmaster = new ArrayList<BossMaster>();
        bullet = new ArrayList<Bullet>();
        laser = new ArrayList<Laser>();
        broken = new ArrayList<Broken>();
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
                case MotionEvent.ACTION_MOVE:
                    playing = true;
                    bgmusic.start();
                    myplane.setMove((int) event.getX(), (int) event.getY());
                    return true;
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
            addBullet();
            addMissile();
            addBossPlane();
            addBossMaster();
            addLaser();

        } else {
            addExplosion();
            //đợi vụ nổ diễn ra
            if (wait != 0 && getout) {
                //cập nhật vụ nổ
                explosion.update();
                wait--;
            }
            if (wait == 0) {
                moveResult();
            }
        }
    }

    public void addBullet() {
        //bắt đầu thêm đạn bắn
        elapsed = (System.nanoTime() - bulletStartTime) / 1000000;
        if (elapsed > 180) {
            bullet.add(new Bullet(BitmapFactory.decodeResource(getResources(), R.drawable.bullet),
                    myplane.getX(), myplane.getY(), 25, 36, 15, 1));
            //reset timer
            bulletStartTime = System.nanoTime();
        }
        //cập nhật mỗi bullet
        for (int i = 0; i < bullet.size(); i++) {
            bullet.get(i).update();
            //loại bỏ Bullet nếu nó đi ra ngoài màn hình
            if (bullet.get(i).getY() < 20) {
                bullet.remove(i);
                break;
            }
        }
        //kiểm tra va chạm với boss plane
        for (int i = 0; i < bullet.size(); i++) {
            for (int j = 0; j < bossplane.size(); j++) {
                if (collision(bullet.get(i), bossplane.get(j))) {
                    myplane.addScore(3);
                    addBroken(j);
                    //xóa bullet và boss plane nếu bắn trúng boss plane
                    bossplane.remove(j);
                    bullet.remove(i);
                    sound.playHitSound();
                    break;
                }
            }
        }
        // cập nhật bắn nổ boss plane
        for (int i = 0; i < broken.size(); i++) {
            broken.get(i).update();
        }
        // kiểm tra va trạm với boss master
        for (int i = 0; i < bullet.size(); i++) {
            for (int j = 0; j < bossmaster.size(); j++) {
                if (collision(bullet.get(i), bossmaster.get(j))) {
                    //tăng điểm cộng dồn và xóa bullet
                    picPlus++;
                    bullet.remove(i);
                    sound.playPicSound();
                    if (picPlus > (bossmaster.get(j).getPicPlus())) {
                        myplane.addScore(bossmaster.get(j).getUpScore());
                        bossmaster.remove(j);
                        sound.playDestroySound();
                        picPlus = 0;
                        bossLevel++;
                        addMaster = false;
                        resetTimer();
                        break;
                    }
                    break;
                }
            }
        }
    }

    public void addBroken(int j) {
        broken.add(new Broken(BitmapFactory.decodeResource(getResources(), R.drawable.broken),
                bossplane.get(j).getX(), bossplane.get(j).getY(), 64, 64, 3));
    }

    public void addMissile() {
        if (!addMaster) {
            //bắt đầu thêm tên lửa
            elapsed = (System.nanoTime() - missileStartTime) / 1000000;
            if (elapsed > (2500 - myplane.getScore() / 2)) {
                missiles.add(new Missile(BitmapFactory.decodeResource(getResources(), R.drawable.missile),
                        (int) (rand.nextDouble() * WIDTH), -100, 20, 60, myplane.getScore(), 13));
                //reset timer
                missileStartTime = System.nanoTime();
            }
        }
        //cập nhật mỗi tên lửa
        for (int i = 0; i < missiles.size(); i++) {
            missiles.get(i).update();
            //loại bỏ tên lửa nếu nó đi ra ngoài màn hình
            if (missiles.get(i).getY() > HEIGHT + 10) {
                missiles.remove(i);
                break;
            }
            // kiểm tra va chạm
            if (collision(missiles.get(i), myplane)) {
                //xóa tên lửa nếu nó đã phát nổ
                missiles.remove(i);
                gameover();
                break;
            }
        }
    }

    public void addBossPlane() {
        if (!addMaster) {
            //bắt đầu thêm boss plane white, yellow, red
            elapsed = (System.nanoTime() - bossplaneStartTime) / 1000000;
            if (elapsed > (1000 - myplane.getScore() / 2)) {
                randboss = rand.nextInt(3);
                switch (randboss) {
                    case 0:
                        bossplane.add(new BossPlane(BitmapFactory.decodeResource(getResources(), R.drawable.whiteboss),
                                (int) (rand.nextDouble() * WIDTH), -200, 70, 50, myplane.getScore(), 3));
                        //reset timer
                        bossplaneStartTime = System.nanoTime();
                        break;
                    case 1:
                        bossplane.add(new BossPlane(BitmapFactory.decodeResource(getResources(), R.drawable.yellowboss),
                                (int) (rand.nextDouble() * WIDTH), -200, 70, 50, myplane.getScore(), 3));
                        //reset timer
                        bossplaneStartTime = System.nanoTime();
                        break;
                    case 2:
                        bossplane.add(new BossPlane(BitmapFactory.decodeResource(getResources(), R.drawable.redboss),
                                (int) (rand.nextDouble() * WIDTH), -200, 70, 50, myplane.getScore(), 3));
                        //reset timer
                        bossplaneStartTime = System.nanoTime();
                        break;
                }

            }
        }
        //cập nhật mỗi boss plane
        for (int i = 0; i < bossplane.size(); i++) {
            bossplane.get(i).update();
            //loại bỏ plane nếu nó đi ra ngoài màn hình
            if (bossplane.get(i).getY() > HEIGHT + 10) {
                bossplane.remove(i);
                break;
            }
            //thêm khói sau  boss plane
            elapsed = (System.nanoTime() - smokeStartTime) / 1000000;
            if (elapsed > 120) {
                smokepuff.add(new Smokepuff(bossplane.get(i).getX(), bossplane.get(i).getY()));
                smokeStartTime = System.nanoTime();
            }
        }
        // kiểm tra va chạm
        for (int i = 0; i < bossplane.size(); i++) {
            if (collision(bossplane.get(i), myplane)) {
                //xóa boss plane nếu nó đã phát nổ
                bossplane.remove(i);
                gameover();
                break;
            }
        }
        //cập nhật mỗi viên khói
        for (int i = 0; i < smokepuff.size(); i++) {
            smokepuff.get(i).update();
            // loại bỏ
            if (smokepuff.get(i).getTimeLife() > 8) {
                smokepuff.remove(i);
            }
        }
    }

    public void addBossMaster() {
        //bắt đầu thêm boss master
        elapsed = (System.nanoTime() - StartTime) / 1000000000;
        if (elapsed > 75) {
            bossmaster.add(new BossMaster(BitmapFactory.decodeResource(getResources(), R.drawable.bossmaster),
                    (int) (rand.nextDouble() * WIDTH), -150, 278, 150, bossLevel, 3));
            addMaster = true;
            StartTime = System.nanoTime();
        }
        //cập nhật boss master
        for (int i = 0; i < bossmaster.size(); i++) {
            bossmaster.get(i).update();
            // kiểm tra va chạm
            if (collision(bossmaster.get(i), myplane)) {
                gameover();
                break;
            }
            //loại bỏ boss master nếu nó đi ra ngoài màn hình
            if (bossmaster.get(i).getY() > HEIGHT + 10) {
                bossmaster.remove(i);
                picPlus = 0;
                bossLevel++;
                break;
            }
        }
    }

    public void addLaser() {
        if (addMaster) {
            //bắt đầu thêm laser
            elapsed = (System.nanoTime() - laserStartTime) / 1000000;
            for (int i = 0; i < bossmaster.size(); i++) {
                if (elapsed > 1200) {
                    if (left) left = false;
                    else left = true;
                    laser.add(new Laser(BitmapFactory.decodeResource(getResources(), R.drawable.laser),
                            bossmaster.get(i).getX(), bossmaster.get(i).getY(), 25, 69, 12, left, 3));
                    sound.playLaserSound();
                    //reset timer
                    laserStartTime = System.nanoTime();
                }
            }
        }
        // cập nhật laser
        for (int i = 0; i < laser.size(); i++) {
            laser.get(i).update();
            //loại bỏ laser nếu nó đi ra ngoài màn hình
            if (laser.get(i).getY() > HEIGHT + 10) {
                laser.remove(i);
                break;
            }
            //kiểm tra va chạm
            if (collision(laser.get(i), myplane)) {
                //xóa laser nếu nó bắn trúng
                laser.remove(i);
                gameover();
                break;
            }
        }
    }

    public void addExplosion() {
        //thêm vụ nổ khi có va chạm
        if (addExplosion) {
            explosion = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.explosion),
                    myplane.getX(), myplane.getY() - 30, 100, 100, 25);
            addExplosion = false;
        }
    }

    //hàm kiểm tra va chạm giữa 2 đối tượng trong game
    public boolean collision(GameObject a, GameObject b) {
        return Rect.intersects(a.getRectangle(), b.getRectangle());
    }

    public void resetTimer() {
        StartTime = bulletStartTime = missileStartTime = smokeStartTime = bossplaneStartTime = System.nanoTime();
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
            for (Broken b : broken) {
                b.draw(canvas);
            }
            //vẽ boss plane
            for (BossPlane bp : bossplane) {
                bp.draw(canvas);
            }
            //vẽ laser
            for (Laser l : laser) {
                l.draw(canvas);
            }
            //vẽ boss master
            for (BossMaster bm : bossmaster) {
                bm.draw(canvas);
            }

            //vẽ khói
            for (Smokepuff sp : smokepuff) {
                sp.draw(canvas);
            }
            //vẽ vụ nổ
            if (!playing && wait < 50) {
                explosion.draw(canvas);
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
    }

    public void gameover() {
        playing = false;
        getout = true;
        addExplosion = true;
        bgmusic.stop();
        sound.playDestroySound();
        //lấy điểm số và lưu lại điểm cao nhất
        if (highscore < myplane.getScore()) {
            highscore = myplane.getScore();
            editor.clear();
            editor.putInt("HighScore", highscore);
            editor.commit();
        }
        //Xóa hiệu ứng còn dư
        bullet.clear();
        smokepuff.clear();
        broken.clear();
        laser.clear();
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
