package thirdsem.flavy.sensorapp;

import android.annotation.TargetApi;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class Geiger extends AppCompatActivity implements SensorEventListener {


    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float[] readingMagnometer = new float[3];
    private float[] readingAccelerometer = new float[3];

    private SoundPool soundPool;
    private int soundID;
    private AudioManager audioManager;
    private float actVolume, maxVolume, volume;
    private long curTime, lastUpdate;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geiger);

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        createSoundPool();
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete (SoundPool soundPool, int sampleId, int status) {
            }
        });

        actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = actVolume / maxVolume;

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        soundID = soundPool.load(this, R.raw.tick, 1);
        lastUpdate = System.currentTimeMillis();
    }

    public void createSoundPool () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            createNewSoundPool();
        } else {
            createOldSoundPool();
        }
    }

    @TargetApi(21)
    protected void createNewSoundPool () {
        AudioAttributes attributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
        soundPool = new SoundPool.Builder().setAudioAttributes(attributes).build();
    }

    @SuppressWarnings("deprecation")
    protected void createOldSoundPool () {
        soundPool = new SoundPool(50, AudioManager.STREAM_MUSIC, 0);
    }

    public void playSound (float vol, int loop, float rate) {
        soundPool.play(soundID, vol, vol, 1, loop, rate);

    }

    @Override
    protected void onResume () {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        soundPool.autoResume();
    }


    @Override
    protected void onPause () {
        super.onPause();
        sensorManager.unregisterListener(this, accelerometer);
        soundPool.autoPause();
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        soundPool.stop(soundID);
        soundPool.release();
    }

    @Override
    protected void onStart () {
        super.onStart();
    }

    @Override
    protected void onRestart () {
        super.onRestart();
    }

    @Override
    protected void onStop () {
        super.onStop();
    }

    @Override
    public void onSensorChanged (SensorEvent event) {
        curTime = System.currentTimeMillis();

        if (curTime - lastUpdate > 250) {
            lastUpdate = curTime;
            int tiltValue = (int) event.values[1];
            if (tiltValue < 3) {
                playSound(1, -1, 2);
            }
            else if (tiltValue >= 3 && tiltValue < 7) {
                playSound((float) 0.6, -1, (float) 1.3);
            }
            else if (tiltValue >= 7) {
                playSound((float) 0.3, 0, (float) 0.5);
            }
        }

    }


    @Override
    public void onAccuracyChanged (Sensor sensor, int accuracy) {

    }
}
