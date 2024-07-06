package jp.aoyama.h15822097.watch_heartrateapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RegistActivity extends AppCompatActivity {
    EditText editid;
    Button nextBtn;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    Date date;
    SimpleDateFormat sdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        editid=findViewById(R.id.userid);
        nextBtn=findViewById(R.id.nextBtn);
        progressBar=findViewById(R.id.progressbar);
        mAuth= FirebaseAuth.getInstance();

        // Dateオブジェクトを用いて現在時刻を取得してくる値を 変数 date に格納
        date= new Date();
        // SimpleDateFormat をオブジェクト化し、任意のフォーマットを設定
        sdf= new SimpleDateFormat("yyyy-MM-dd");

        DataHolder.getInstance().setDate(date);

    }

    public void nextonClick(View v){
        //進度見れるバー
        progressBar.setVisibility(View.VISIBLE);

        String id=String.valueOf(editid.getText());
        DataHolder.getInstance().setPersonalid(id);

        if(TextUtils.isEmpty(id)){
            //email入力なかったら
            Toast.makeText(getApplicationContext(),"UserIdが空欄です",Toast.LENGTH_SHORT).show();
        }

        DocumentReference docRef = db.collection(id).document(sdf.format(date));
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Intent intent=new Intent(getApplicationContext(),selectActivity.class);
                        //intent.putExtra("id",id);//id
                        //intent.putExtra("date",sdf.format(date)); //日付を次ページへ
                        startActivity(intent);



                    } else {
                        Toast.makeText(getApplicationContext(), "タブレット側で新規データ登録を行ってください",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("test", "get failed with ", task.getException());
                }
            }
        });



    }
}