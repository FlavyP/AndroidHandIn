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
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

public class Geiger extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnometer;
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
        setContentView(R.layout.activity_lightsaber);

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

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

        soundID = soundPool.load(this, R.raw.tick, 1);

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

    public void playSound(float vol, int loop)
    {
        soundPool.play(soundID, vol, vol, 1, loop, 1f);

    }

    @Override
    protected void onResume () {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnometer, SensorManager.SENSOR_DELAY_NORMAL);
        soundPool.autoResume();
    }


    @Override
    protected void onPause () {
        super.onPause();
        sensorManager.unregisterListener(this, accelerometer);
        sensorManager.unregisterListener(this, magnometer);
        soundPool.autoPause();
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        soundPool.stop(soundID);
        soundPool.release();
    }

    @Override
    public void onSensorChanged (SensorEvent event) {
        float[] rotation = new float[9];
        float[] orientation = new float[3];
        curTime = System.currentTimeMillis();

        synchronized (this) {
            if (event.sensor == accelerometer) {
                readingAccelerometer[0] = event.values[0];
                readingAccelerometer[1] = event.values[1];
                readingAccelerometer[2] = event.values[2];
            }

            if (event.sensor == magnometer) {
                readingMagnometer[0] = event.values[0];
                readingMagnometer[1] = event.values[1];
                readingMagnometer[2] = event.values[2];
            }

            sensorManager.getRotationMatrix(rotation, null, readingAccelerometer, readingMagnometer);
            sensorManager.getOrientation(rotation, orientation);
            float inclination = sensorManager.getInclination(rotation);
            float degreeInclination = (float) (Math.toDegrees(inclination) + 360 ) % 360;
            float azimuthRadians = orientation[0];
            float azimuthDegrees = -(float) (Math.toDegrees(azimuthRadians) + 360) % 360;

            //orientXValue.setText(Float.toString(degreeInclination));

            if((curTime - lastUpdate) > 250 )
            {
                if( degreeInclination < 100 )
                {
                    playSound((float) 0.2, -1);
                }
                if( degreeInclination > 100 && degreeInclination < 150)
                {
                    playSound((float) 0.5, 0);
                }
                else
                {
                    playSound((float) 1, -1);
                }
            }
        }

    }


    @Override
    public void onAccuracyChanged (Sensor sensor, int accuracy) {

    }
}
