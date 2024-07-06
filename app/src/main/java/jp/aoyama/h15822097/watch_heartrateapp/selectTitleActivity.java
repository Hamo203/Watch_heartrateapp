package jp.aoyama.h15822097.watch_heartrateapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class selectTitleActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_title);
        ArrayAdapter<String> titleadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        titleadapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("123456")
                .document("2024-07-06")
                .collection("heart")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //firebaseの(mode)コレクション中のdocument全て
                                titleadapter.add((String) document.getId());
                            }
                        } else {
                            Log.d("test", "Error getting documents: ", task.getException());
                        }
                    }


                });
        Spinner spinner = (Spinner) findViewById(R.id.title_spinner);
        spinner.setAdapter(titleadapter);


    }
}