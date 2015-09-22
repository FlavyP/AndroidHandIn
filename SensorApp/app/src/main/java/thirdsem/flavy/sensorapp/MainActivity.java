package thirdsem.flavy.sensorapp;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "flavy";

    private Button sensorButton;
    private Button compassButton;
    private Button lightsaberButton;
    private Button geigerButton;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorButton = (Button) findViewById(R.id.sensorButton);
        sensorButton.setOnClickListener(this);

        compassButton = (Button) findViewById(R.id.compassButton);
        compassButton.setOnClickListener(this);

        lightsaberButton = (Button) findViewById(R.id.lightsaberButton);
        lightsaberButton.setOnClickListener(this);

        geigerButton = (Button) findViewById(R.id.geigerButton);
        geigerButton.setOnClickListener(this);
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onClick (View v) {
        switch (v.getId())
        {
            case R.id.sensorButton:
                Intent sensorIntent = new Intent(MainActivity.this, Sensors.class);
                startActivity(sensorIntent);
                break;
            case R.id.compassButton:
                Intent compassIntent = new Intent(MainActivity.this, Compass.class);
                startActivity(compassIntent);
                break;
            case R.id.lightsaberButton:
                Intent lightsaberIntent = new Intent(MainActivity.this, Lightsaber.class);
                startActivity(lightsaberIntent);
                break;
            case R.id.geigerButton:
                Intent geigerIntent = new Intent(MainActivity.this, Geiger.class);
                startActivity(geigerIntent);
                break;
        }
    }

    @Override
    protected void onStart () {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume () {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause () {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onRestart () {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onStop () {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
