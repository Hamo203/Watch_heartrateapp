package jp.aoyama.h15822097.watch_heartrateapp;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class MainWatch extends AppCompatActivity implements SensorEventListener {
    Button funcBtn;
    String btntext;
    private String info;
    FirebaseFirestore firebase;
    FirebaseUser user;
    FirebaseAuth mAuth;
    String date;
    SimpleDateFormat sdf;


    String getMode;
    String getDate;
    String getId;

    private long startTime;
    private long endTime;
    private long currenttime;

    private boolean isHeartRateChanged = false;
    String customId;

    private Handler handler;
    private Runnable updateTimer;

    long diffTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("test", "mainWatch onCreate \n");

        setContentView(R.layout.activity_main_watch);
        funcBtn=findViewById(R.id.funcBtn);

        Intent intent=getIntent();
        getId =DataHolder.getInstance().getPersonalid();
        getDate =DataHolder.getInstance().getDate();
        getMode=intent.getStringExtra("mode");

        TextView mode =findViewById(R.id.setmode);
        mode.setText(getMode);

        handler=new Handler();
        // handler
        updateTimer = new Runnable() {
            @Override
            public void run() {
                //経過時間
                currenttime = System.currentTimeMillis();;
                diffTime = (currenttime - startTime)/1000;
                TextView settime=findViewById(R.id.settime);
                settime.setText(String.valueOf((int) diffTime));
                handler.postDelayed(this, 1000);
                Log.d("test", "handler: "+String.valueOf(diffTime));
            }
        };
    }
    public void funcBtn(View view){
        btntext=funcBtn.getText().toString();
        if(btntext.equals("Start")){

            // 現在のタイムスタンプを取得する
            Timestamp timestamp = Timestamp.now();
            // タイムスタンプをベースにしてIDを生成する
            customId = String.valueOf(timestamp.getSeconds());

            Log.d("test","start");
            //開始時間を記録
            startTime=System.currentTimeMillis();

            //start押されたらstop表示
            funcBtn.setText("Stop");
            Context context = getApplicationContext();
            FirebaseApp.initializeApp(context);

            firebase= FirebaseFirestore.getInstance();

            //startボタン押されたらセンサ起動する
            //センサー起動
            SensorManager sma=(SensorManager) getSystemService(Context.SENSOR_SERVICE);

            //センサー1(心拍数)起動
            sma.registerListener(this, sma.getDefaultSensor(Sensor.TYPE_HEART_RATE), SensorManager.SENSOR_DELAY_NORMAL);
            //センサー2起動
            //sma.registerListener(this, sma.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

            handler.post(updateTimer);

        }else if(btntext.equals("Stop")){

            endTime=System.currentTimeMillis();
            long diffTime = (endTime - startTime);

            //センサー止める
            SensorManager sma=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
            sma.unregisterListener(this);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
            Log.d("test", "Sensor stopped:"+sdf.format(diffTime)); // ログメッセージを追加

            // 心拍センサーの値が Firebase に格納されたら、加速度センサーの値も格納できるようにフラグをリセットする
            isHeartRateChanged = false;
            funcBtn.setText("Start");

        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {

            //心拍数の値を得た場合
            double heart;
            heart = event.values[0];
            TextView setheart=findViewById(R.id.setheart);
            setheart.setText(String.valueOf((int) heart));

            Map<String, Object> pdata = new HashMap<>();
            pdata.put("beat", heart);
            pdata.put("time", diffTime);

            if(getMode.equals("rest")){
                firebase.collection(getId).document(getDate).collection(getMode).add(pdata);
            }else{
                firebase.collection(getId).document(getDate).collection(getMode).document("title").collection(customId).add(pdata);
            }
            heartRateChanged();

        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // 加速度センサーのデータ処理
            float[] values = event.values;
            float x = values[0];
            float y = values[1];
            float z = values[2];

            // 心拍センサーの値が変化した場合のみ Firebase に格納するようにする
            if (isHeartRateChanged) {
                Map<String, Object> p2data = new HashMap<>();
                p2data.put("x", x);
                p2data.put("y", y);
                p2data.put("z", z);
                Log.d("test", "Accelerometer Data - X: " + x + ", Y: " + y + ", Z: " + z);
                isHeartRateChanged = false;
                //firebase.collection(name).document(info).collection(sensorname2).document(customId).set(p2data).addOnSuccessListener(this);
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // 心拍センサーの値が変化したことをフラグで示すメソッド
    public void heartRateChanged() {
        isHeartRateChanged = true;
        Log.d("test","sensor get");
    }
}