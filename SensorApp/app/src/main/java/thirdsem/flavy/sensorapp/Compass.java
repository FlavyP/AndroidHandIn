package thirdsem.flavy.sensorapp;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class Compass extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnometer;
    private float currentCompassAngle = 0;
    private float[] readingMagnometer = new float[3];
    private float[] readingAccelerometer = new float[3];
    private ImageView compassImage;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        compassImage = (ImageView) findViewById(R.id.compassImg);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    }

    @Override
    public void onSensorChanged (SensorEvent event) {

        float[] rotation = new float[9];
        float[] orientation = new float[3];
        long curTime, lastUpdate = 0;

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
        float azimuthRadians = orientation[0];
        float azimuthDegrees = -(float) (Math.toDegrees(azimuthRadians) + 360) % 360;

        curTime = System.currentTimeMillis();
        if ((curTime - lastUpdate) > 250) {

            doAnimation(currentCompassAngle, azimuthDegrees, compassImage);
            currentCompassAngle = azimuthDegrees;
            lastUpdate = curTime;
        }
    }

    @Override
    public void onAccuracyChanged (Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume () {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause () {
        super.onPause();
        sensorManager.unregisterListener(this, accelerometer);
        sensorManager.unregisterListener(this, magnometer);
    }

    private void doAnimation (float from, float to, View rotateMe) {

        if (Math.abs(from - to) < 5 || Math.abs(to - from) < 5) {

            RotateAnimation ra = new RotateAnimation(from, to, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

            ra.setDuration(200);
            ra.setFillAfter(true);
            rotateMe.startAnimation(ra);
        }
    }
}
