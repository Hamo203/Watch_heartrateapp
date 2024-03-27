package jp.aoyama.h15822097.watch_heartrateapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegistActivity extends AppCompatActivity {
    EditText registName;
    Button nextBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        registName=findViewById(R.id.registName);
        nextBtn=findViewById(R.id.nextbtn);
    }

    public void nextonClick(View v){
        String name=registName.getText().toString();
        //MainActivityで変数を利用できるようにする
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        //入力された名前
        intent.putExtra("name",name);
        startActivity(intent);
    }
}