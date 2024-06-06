package jp.aoyama.h15822097.watch_heartrateapp;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class mainWatch extends AppCompatActivity implements SensorEventListener {
    Button funcBtn;
    String btntext;
    private String info;
    FirebaseFirestore firebase;
    FirebaseUser user;
    FirebaseAuth mAuth;
    Date date;
    SimpleDateFormat sdf;
    private TextView showtime;
    private TextView showheart;
    String currenttime;

    String g_mode;
    String g_date;
    String g_id;

    private long startTime;
    private long endTime;

    private boolean isHeartRateChanged = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("test", "mainWatch onCreate \n");

        setContentView(R.layout.activity_main_watch);
        funcBtn=findViewById(R.id.funcBtn);

        Intent gintent=getIntent();
        g_id =gintent.getStringExtra("id");
        g_date =gintent.getStringExtra("date");
        g_mode=gintent.getStringExtra("mode");

    }
    public void funcBtn(View view){
        btntext=funcBtn.getText().toString();
        if(btntext.equals("Start")){
            //開始時間を記録
            startTime=System.currentTimeMillis();

            //start押されたらstop表示
            funcBtn.setText("Stop");
            Context context = getApplicationContext();
            FirebaseApp.initializeApp(context);

            Log.d("test",info+"\n");
            firebase= FirebaseFirestore.getInstance();

            //心拍数用テストデータの挿入
            Map<String,Object> pdata=new HashMap<>();
            pdata.put("beat","test");
            pdata.put("time","test");
            pdata.put("timestamp", FieldValue.serverTimestamp());
            firebase.collection(g_id).document(g_date).collection(g_mode).add(pdata);

            //startボタン押されたらセンサ起動する
            //センサー起動
            //SensorManager sma=(SensorManager) getSystemService(Context.SENSOR_SERVICE);

            //センサー1(心拍数)起動
            //sma.registerListener(this, sma.getDefaultSensor(Sensor.TYPE_HEART_RATE), SensorManager.SENSOR_DELAY_NORMAL);
            //センサー2起動
            //sma.registerListener(this, sma.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);


        }else if(btntext.equals("Stop")){
            endTime=System.currentTimeMillis();
            long diffTime = (endTime - startTime);

            //センサー止める
            //SensorManager sma=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
            //sma.unregisterListener((SensorEventListener) this);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
            Log.d("test", "Sensor stopped:"+sdf.format(diffTime)); // ログメッセージを追加

            // 心拍センサーの値が Firebase に格納されたら、加速度センサーの値も格納できるようにフラグをリセットする
            isHeartRateChanged = false;
            funcBtn.setText("Start");

        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // Dateオブジェクトを用いて現在時刻を取得してくる値を 変数 date に格納
        Date date = new Date();
        // SimpleDateFormat をオブジェクト化し、任意のフォーマットを設定
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss.SSS");
        //経過時間
        currenttime = sdf2.format(date);
        Log.d("test", currenttime);

        showtime.setText(info);
        Log.d("test", info);

        // 現在のタイムスタンプを取得する
        Timestamp timestamp = Timestamp.now();
        // タイムスタンプをベースにしてIDを生成する
        String customId = String.valueOf(timestamp.getSeconds());

        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            //心拍数の値を得た場合
            double heart;

            heart = event.values[0];
            showheart.setText(Double.toString(heart));
            Log.d("test", "Heart rate: " + heart); // ログメッセージを追加
            Map<String, Object> pdata = new HashMap<>();
            pdata.put("beat", heart);
            pdata.put("time", showtime);
            pdata.put("timestamp", timestamp);
            //firebase.collection(user.getUid()).document(info).collection("heartbeat").document(customId).set(pdata).addOnSuccessListener(this);
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
                p2data.put("timestamp", timestamp);
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