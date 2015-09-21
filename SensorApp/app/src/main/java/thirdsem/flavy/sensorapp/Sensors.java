package thirdsem.flavy.sensorapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;

public class Sensors extends AppCompatActivity {

    private SensorManager sm;
    private List<Sensor> availableSensors;
    private TextView desc;
    private int selectedSensor;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);

        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        availableSensors = sm.getSensorList(Sensor.TYPE_ALL);
        desc = (TextView) findViewById(R.id.desc);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sensors, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        selectedSensor = item.getItemId();
        updateDescriptionInTextView();
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        menu.clear();
        int cnt = 0;
        for(Sensor s : availableSensors)
        {
            menu.add(0,cnt,0,s.getType() + " " + s.getName());
            cnt++;
        }
        return true;
    }

    private void updateDescriptionInTextView()
    {
        String txt = "";
        Sensor sensor = availableSensors.get(selectedSensor);
        txt += "Name: " + sensor.getName();
        txt += "\nType: " + sensor.getType();
        txt += "\nPower: " + sensor.getPower();
        txt += "\nRange: " + sensor.getMaximumRange();
        txt += "\nResolution: " + sensor.getResolution();
        if(sensor.getMinDelay() == 0 )
            txt += "\nNot a streaming sensor";
        else
            txt += "\nMin Delay: " + ((double)sensor.getMinDelay()/1000000.0) + " seconds \n";
        desc.setText(txt);
    }
}
