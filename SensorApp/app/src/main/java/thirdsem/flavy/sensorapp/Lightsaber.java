package thirdsem.flavy.sensorapp;

import android.annotation.TargetApi;
import android.content.Context;
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
import android.widget.Toast;

public class Lightsaber extends AppCompatActivity implements SensorEventListener {

    private static final int SHAKE_THRESHOLD = 450;

    private SoundPool soundPool;
    private int soundID;
    private boolean loaded = false;
    private AudioManager audioManager;
    private float actVolume, maxVolume, volume;

    private long curTime, lastUpdate;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lightsaber);

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        createSoundPool();
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete (SoundPool soundPool, int sampleId, int status) {
                //playSound(0);
            }
        });

        actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = actVolume / maxVolume;

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        soundID = soundPool.load(this, R.raw.saberswing , 1);
        lastUpdate = System.currentTimeMillis();
    }

    public void createSoundPool()
    {
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            createNewSoundPool();
        }
        else
        {
            createOldSoundPool();
        }
    }

    @TargetApi(21)
    protected void createNewSoundPool() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder().setAudioAttributes(attributes).build();
    }

    @SuppressWarnings("deprecation")
    protected void createOldSoundPool() {
        soundPool = new SoundPool(50, AudioManager.STREAM_MUSIC, 0);
    }
    
    public void playSound(float vol)
    {
        soundPool.play(soundID, vol, vol, 1, 0, 1f);

    }
    @Override
    protected void onResume () {
        super.onResume();
        soundPool.autoResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    protected void onPause () {
        super.onPause();
        soundPool.autoPause();
        sensorManager.unregisterListener(this, accelerometer);
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        soundPool.stop(soundID);
        soundPool.release();
    }


    @Override
    public void onSensorChanged (SensorEvent event) {
        float x, y, z;

        if (event.sensor == accelerometer) {

            curTime = System.currentTimeMillis();
            if((curTime - lastUpdate) > 250 )
            {
                lastUpdate = curTime;
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];

                float accelationSquareRoot = (x * x + y * y + z * z) / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);

                if(accelationSquareRoot >= 1.5)
                {
                    playSound(volume);
                }
            }
        }

    }

    @Override
    public void onAccuracyChanged (Sensor sensor, int accuracy) {

    }
}
