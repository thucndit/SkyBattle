package utc.it.thucnd.skybattle;


import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

public class SoundPlayer {

    private AudioAttributes audioAttributes;
    final int SOUND_POOL_MAX = 5;

    private static SoundPool soundPool;
    private static int hitSound;
    private static int overSound;
    private static int gunSound;

    public SoundPlayer(Context context) {

        // SoundPool voi phien ban API > 21. (Lollipop)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setMaxStreams(SOUND_POOL_MAX)
                    .build();

        } else {
            //SoundPool (int maxStreams, int streamType, int srcQuality)
            soundPool = new SoundPool(SOUND_POOL_MAX, AudioManager.STREAM_MUSIC, 0);
        }

        hitSound = soundPool.load(context, R.raw.hit, 1);
        overSound = soundPool.load(context, R.raw.destroy, 1);
        gunSound = soundPool.load(context, R.raw.gun, 1);

    }

    public void playHitSound() {

        // play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate)
        soundPool.play(hitSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }
    public void playGunSound() {
        // play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate)
        soundPool.play(gunSound, 0.3f, 0.3f, 1, 0, 1.0f);
    }

    public void playOverSound() {
        soundPool.play(overSound, 0.8f, 0.8f, 1, 0, 1.0f);
    }
}