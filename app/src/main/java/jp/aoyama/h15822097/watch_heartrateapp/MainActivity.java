package jp.aoyama.h15822097.watch_heartrateapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SensorEventListener, OnSuccessListener, OnFailureListener {

    private Button start_btn;
    private Button stop_btn;
    private TextView t_heart;
    private TextView t_time;
    int id=0;
    int id2=0;

    FirebaseFirestore firebase;
    private String info;
    private String sensorname="heartbeat";
    private String sensorname2="acc";

    //chart用


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.start_btn=findViewById(R.id.start_btn);
        this.stop_btn=findViewById(R.id.stop_btn);
        this.t_heart=findViewById(R.id.beat);
        this.t_time=findViewById(R.id.time);

        Log.d("test", "Activity created");

        Context context = getApplicationContext();
        FirebaseApp.initializeApp(context);

    }
    public void start_onClick(View view){

        Context context = getApplicationContext();
        FirebaseApp.initializeApp(context);
        // Dateオブジェクトを用いて現在時刻を取得してくる値を 変数 date に格納
        Date date = new Date();
        // SimpleDateFormat をオブジェクト化し、任意のフォーマットを設定
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy.MM.dd");
        //日付
        info=sdf.format(date) ;
        Log.d("test",info+"\n");


        // Access a Cloud Firestore instance from your Activity
        firebase=FirebaseFirestore.getInstance();

        //コレクション名決定
        CollectionReference names= firebase.collection(sensorname);
        Map<String,Object> pdata=new HashMap<>();
        pdata.put("beat","test");
        pdata.put("time","test");

        firebase.collection(sensorname).document(info).collection(sensorname).add(pdata).addOnSuccessListener(this);


        //コレクション名決定2
        CollectionReference names2= firebase.collection(sensorname2);
        Map<String,Object> p2data=new HashMap<>();
        p2data.put("acc","test");
        p2data.put("time","test");

        firebase.collection(sensorname2).document(info).collection(sensorname2).add(p2data).addOnSuccessListener(this);


        //センサー起動
        SensorManager sma=(SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //センサー1起動
        sma.registerListener(this, sma.getDefaultSensor(Sensor.TYPE_HEART_RATE), SensorManager.SENSOR_DELAY_NORMAL);
        //センサー2起動
        sma.registerListener(this, sma.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        Log.d("test", "Sensor started");
    }
    public void stop_onClick(View view){
        //センサー止める
        SensorManager sma=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sma.unregisterListener((SensorEventListener) this);
        Log.d("test", "Sensor stopped"); // ログメッセージを追加

        // 心拍センサーの値が Firebase に格納されたら、加速度センサーの値も格納できるようにフラグをリセットする
        isHeartRateChanged = false;

    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        // Dateオブジェクトを用いて現在時刻を取得してくる値を 変数 date に格納
        Date date = new Date();
        // SimpleDateFormat をオブジェクト化し、任意のフォーマットを設定
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        //日付
        String time = sdf.format(date);
        t_time.setText(time);
        Log.d(TAG, time);

        String documentid = String.valueOf(System.currentTimeMillis());


        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            double heart;

            heart = event.values[0];
            t_heart.setText(Double.toString(heart));
            Log.d("test", "Heart rate: " + heart); // ログメッセージを追加


            Map<String, Object> pdata = new HashMap<>();
            pdata.put("beat", heart);
            pdata.put("time", time);

            //firebase.collection(sensorname).document(info).collection("heartbeat").document(documentid).set(pdata).addOnSuccessListener(this);

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
                //firebase.collection(sensorname2).document(info).collection(sensorname2).document(documentid).set(p2data).addOnSuccessListener(this);
            }
        }


    }

    private boolean isHeartRateChanged = false;

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onSuccess(Object o) {
        Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_LONG).show();
        // 心拍センサーの値が Firebase に格納されたら、加速度センサーの値も格納できるようにフラグをリセットする
        isHeartRateChanged = false;
    }


    @Override
    public void onFailure(@NonNull Exception e) {
        Toast.makeText(getApplicationContext(),"Failure",Toast.LENGTH_LONG).show();

    }

    // 心拍センサーの値が変化したことをフラグで示すメソッド
    public void heartRateChanged() {
        isHeartRateChanged = true;
        Log.d("test","sensor get");
    }
}