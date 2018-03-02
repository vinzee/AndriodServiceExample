package com.example.vinzee.andriodserviceexample;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ServiceConfigurationError;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText xaccelEditText, yaccelEditText, zaccelEditText;
    private Button myButton, myButton2;
    private MyService myService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myButton = findViewById(R.id.button);
        myButton.setOnClickListener(this);

        myButton2 = findViewById(R.id.button2);
        myButton2.setOnClickListener(this);

        xaccelEditText = findViewById(R.id.editText2);
        yaccelEditText = findViewById(R.id.editText3);
        zaccelEditText = findViewById(R.id.editText4);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.button:
                Intent myIntent = new Intent(this, MyService.class);
                bindService(myIntent, myServiceConnection, BIND_AUTO_CREATE);
                break;
            case R.id.button2:
                if(myService == null) {
                    Toast.makeText(getApplicationContext(), "Please start the service first.", Toast.LENGTH_SHORT).show();
                } else {
                    float[] accelerometerValues = myService.getMeanAccelerometerValues();
                    Log.d("MyActivity", accelerometerValues[0] + " , " + accelerometerValues[1] + " , " + accelerometerValues[2]);

                    if(accelerometerValues[0] == 0 && accelerometerValues[1] == 0 && accelerometerValues[2] == 0){
                        Toast.makeText(getApplicationContext(), "No Sufficient values to calculate Mean", Toast.LENGTH_SHORT).show();
                    } else {
                        xaccelEditText.setText(String.valueOf(accelerometerValues[0]));
                        yaccelEditText.setText(String.valueOf(accelerometerValues[1]));
                        zaccelEditText.setText(String.valueOf(accelerometerValues[2]));
                    }
                }
        }
    }

    private ServiceConnection myServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            myService = ((MyService.MyBinder) iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}
