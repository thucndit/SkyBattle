package utc.it.thucnd.skybattle;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class GameOver extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //tắt hiển thị title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //set full màn hình
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.game_over);
        TextView tvScore = (TextView) findViewById(R.id.tv_score);
        TextView tvHighScore = (TextView) findViewById(R.id.tv_highscore);
        Button btnAgain = (Button) findViewById(R.id.btn_again);

        int score = getIntent().getIntExtra("Score", 0);
        int highscore = getIntent().getIntExtra("HighScore", 0);
        tvScore.setText("" + score);
        tvHighScore.setText("HighScore : " + highscore);
        // Get screen size.
        WindowManager wm = getWindowManager();
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);

        float scaleX = size.x / 512;
        float scaleY = size.y / 768;

        btnAgain.setWidth((int) (300 * scaleX));
        btnAgain.setHeight((int) (120 * scaleY));

    }

    // Disable Return Button
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }

    public void tryAgain(View view) {
        setContentView(new GamePanel(this));
    }
}
