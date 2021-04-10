package edu.wit.mobileapp.mobileappfinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Account extends AppCompatActivity {
    FirebaseAuth fAuth;
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        //get this firebase instance
        fAuth = FirebaseAuth.getInstance();

        //get username from firebase account
        final String username = fAuth.getCurrentUser().getDisplayName();

        //initialize database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //photoUrl =  fAuth.getCurrentUser().getPhotoUrl();
        //uid =  fAuth.getCurrentUser().getUid();
        //String email =  fAuth.getCurrentUser().getEmail();
        //boolean emailVerified =  fAuth.getCurrentUser().isEmailVerified();

        //set username text
        TextView t1;
        t1 = findViewById(R.id.account_username);
        t1.setText("Username: " + username);


        arrayList = new ArrayList<>();
        ListView listView = (ListView) findViewById(R.id.account_post_view);
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
        listView.setAdapter(adapter);

        //show this users posts
        db.collection("feed")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if(document.getData().get("postMessage").toString() != null) {
                                if (document.getData().get("Username").toString().equals(username)) {
                                    adapter.add(document.getData().get("Username").toString() + ": " + document.getData().get("postMessage").toString());
                                }
                            }
                        }
                    }
                });


        //initialize home button
        Button homeButton;
        homeButton = findViewById(R.id.account_homebutton);
        homeButton.setOnClickListener(v -> {
            //route to home
            startActivity(new Intent(getApplicationContext(), Home.class));
        });

    }
}
