package edu.wit.mobileapp.mobileappfinal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class Feed extends AppCompatActivity {

    FirebaseAuth fAuth;
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        fAuth = FirebaseAuth.getInstance();

        //set home button
        Button homeButton = findViewById(R.id.feed_homebutton);
        homeButton.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Home.class)));

        arrayList = new ArrayList<>();

        //view feed
        ListView feedView = (ListView) findViewById(R.id.feed_view);
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
        feedView.setAdapter(adapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("feed")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            if(document.getData().get("postMessage").toString() != null) {
                                adapter.add(document.getData().get("Username").toString() + ": " + document.getData().get("postMessage").toString());
                            }
                        }
                    }
                });
    }
}

