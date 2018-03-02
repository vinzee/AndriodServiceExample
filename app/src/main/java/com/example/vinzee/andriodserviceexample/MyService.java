package com.example.vinzee.andriodserviceexample;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MyService extends Service implements SensorEventListener {

    private MyBinder myBinder = new MyBinder();
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float x_mean, y_mean, z_mean, x_total, y_total, z_total;
    private static final int MEAN_THRESHOLD = 10;

    public MyService() {
    }

    private class AccelWork implements Runnable {
        private LinkedList<Float> x = new LinkedList<Float>(),
                y = new LinkedList<Float>(),
                z = new LinkedList<Float>();

        public AccelWork(){
        }

        public void addValues(float _x, float _y, float _z) {
            x.add(_x);
            y.add(_y);
            z.add(_z);
            x_total += _x;
            y_total += _x;
            z_total += _x;

            if(x.size() > MEAN_THRESHOLD) {
                x_total -= x.poll();
                y_total -= y.poll();
                z_total -= z.poll();
            }
        }

        @Override
        public void run() {
            if(x.size() == MEAN_THRESHOLD) {
                x_mean = calculateAverage(x_total, MEAN_THRESHOLD);
                y_mean = calculateAverage(y_total, MEAN_THRESHOLD);
                z_mean = calculateAverage(z_total, MEAN_THRESHOLD);
            }
        }

        private float calculateAverage(Float sum, int size) {
            return sum / size;
        }

    }
    private AccelWork accelWork = new AccelWork();


    @Override
    public void onCreate() {
        super.onCreate();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

//            Log.d("MyService", x + " : " + y + " : " + z);
            accelWork.addValues(x,y,z);
            accelWork.run();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public float[] getMeanAccelerometerValues(){
        return new float[]{x_mean, y_mean, z_mean};
    }

    public class MyBinder extends Binder {
        MyService getService(){
            return MyService.this;
        }

    }

}
