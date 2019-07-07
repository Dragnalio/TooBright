package com.example.toobright;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button onOffButton;
    private String status;
    private ConstraintLayout mLayout;
    private int mDefaultColor;


    boolean success = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLayout=findViewById(R.id.layout);
        mDefaultColor=Color.BLACK;
        mLayout.setBackgroundColor(mDefaultColor);

        getPermission();
        onOffButton=findViewById(R.id.onOffBtn);

        if(getBrightness()==255){
            status="TURN OFF";
            mDefaultColor=Color.WHITE;
            mLayout.setBackgroundColor(mDefaultColor);
        }else{
            status="TURN ON";
        }
        setOnOffButton();

        onOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status.equals("TURN ON")){
                    status="TURN OFF";
                    mDefaultColor=Color.WHITE;
                    mLayout.setBackgroundColor(mDefaultColor);
                    if(success){
                        setBrightness(255);
                    }
                }else{
                    status="TURN ON";
                    mDefaultColor=Color.BLACK;
                    mLayout.setBackgroundColor(mDefaultColor);
                    if(success){
                        setBrightness(0);
                    }
                }
                setOnOffButton();
            }
        });
    }

    private void setBrightness(int brightness){
        if(brightness < 0){
            brightness = 0;
        }else if(brightness > 255){
            brightness = 255;
        }
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
    }

    private int getBrightness(){
        int brightness = 100;
        try{
            ContentResolver contentResolver = getApplicationContext().getContentResolver();
            brightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS);
        }catch (Settings.SettingNotFoundException e){
            e.printStackTrace();
        }
        return brightness;
    }

    private void getPermission(){
        boolean value;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            value = Settings.System.canWrite(getApplicationContext());
            if(value){
                success = true;
            }else{
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                startActivityForResult(intent, 1000);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 1000){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                boolean value = Settings.System.canWrite(getApplicationContext());
                if(value){
                    success = true;
                }else{
                    Toast.makeText(this, "Permission not granted!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void setOnOffButton(){onOffButton.setText(status);};
}
