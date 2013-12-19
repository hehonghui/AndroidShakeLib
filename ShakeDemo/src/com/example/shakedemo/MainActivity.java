
package com.example.shakedemo;

import android.app.Activity;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import com.uit.shake.ShakeSensor;
import com.uit.shake.ShakeSensor.OnShakeListener;

public class MainActivity extends Activity {

    private ShakeSensor mShakeSensor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mShakeSensor = new ShakeSensor(MainActivity.this, 2200);
        mShakeSensor.setShakeListener(new OnShakeListener() {

            @Override
            public void onShakeComplete(SensorEvent event) {
                Toast.makeText(MainActivity.this, "摇啊摇", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * (非 Javadoc)
     * 
     * @Title: onResume
     * @Description:
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        mShakeSensor.register();
        super.onResume();
    }

    /**
     * (非 Javadoc)
     * 
     * @Title: onStop
     * @Description:
     * @see android.app.Activity#onStop()
     */
    @Override
    protected void onStop() {
        mShakeSensor.unregister();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
