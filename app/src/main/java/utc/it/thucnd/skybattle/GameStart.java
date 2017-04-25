package utc.it.thucnd.skybattle;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class GameStart extends Activity {
    private Button btnStart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //tắt hiển thị title
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //set full màn hình
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.game_start);
        // Get screen size.
        WindowManager wm = getWindowManager();
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);

        float scaleX = size.x / 512;
        float scaleY = size.y / 768;
        btnStart = (Button) findViewById(R.id.btn_start);
        btnStart.setWidth((int) (280*scaleX));
        btnStart.setHeight((int) (100*scaleY));
    }
    public void GameStart (View v){
        setContentView(new GamePanel(this));
    }
}
