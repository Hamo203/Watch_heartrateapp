package jp.aoyama.h15822097.watch_heartrateapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class selectActivity extends AppCompatActivity {
    RadioGroup radioGroup;
    FirebaseAuth firebaseAuth;
    int checkedId=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        radioGroup=(RadioGroup) findViewById(R.id.radioGroup);
        Log.d("test","select oncreate");
        firebaseAuth=FirebaseAuth.getInstance();
    }

    public void nextonClick(View v){
        //次へボタンが押されたとき
        //radio button のid取得
        checkedId = radioGroup.getCheckedRadioButtonId();

        Intent intent=new Intent(getApplicationContext(), MainWatch.class);


        if(checkedId == R.id.restbeat){
            Toast.makeText(getApplicationContext(), "安静時心拍数", Toast.LENGTH_SHORT).show();
            intent.putExtra("mode","rest");
            startActivity(intent);
            finish();

        }else if(checkedId==R.id.heartbeat){
            Toast.makeText(getApplicationContext(), "心拍数", Toast.LENGTH_SHORT).show();
            intent.putExtra("mode","heart");
            startActivity(intent);
            finish();

        }else{
            Toast.makeText(getApplicationContext(), "何も選択されていません", Toast.LENGTH_SHORT).show();
        }

    }

    public void backonClick(View view){
        Intent intent=new Intent(getApplicationContext(),RegistActivity.class);
        startActivity(intent);
        finish();
    }
}