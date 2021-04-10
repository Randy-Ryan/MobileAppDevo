package edu.wit.mobileapp.mobileappfinal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Post extends AppCompatActivity {
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        fAuth = FirebaseAuth.getInstance();

        //set username string
        final String username = fAuth.getCurrentUser().getDisplayName();

        //set home button
        Button homeButton;
        homeButton = findViewById(R.id.post_homebutton);
        homeButton.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Home.class)));

        //Input post text
        EditText post = findViewById(R.id.post_text);

        //set current date
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        //post button
        Button buttonTest = findViewById(R.id.post_button);
        buttonTest.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getData().get("Username").toString().equals(username)) {
                                    db.collection("users").document(document.getId()).update("postMessage", post.getText().toString());
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("Username", username);
                                    user.put("postMessage", post.getText().toString());
                                    //maybe pass the time too
                                    user.put("Date", currentDate);
                                    post.setText("");

                                    // Add a new document with a generated ID
                                    //creating new data instance in cloud firestore
                                    db.collection("feed")
                                            .add(user)
                                            .addOnSuccessListener(documentReference -> Log.v("randytest", "DocumentSnapshot added with ID: " + documentReference.getId()))
                                            .addOnFailureListener(e -> Log.v("randytest", "Error adding document", e));
                                }
                            }
                        } else {
                            Log.v("randytest", "Error getting documents.", task.getException());
                        }
                    });
        });
    }
}

