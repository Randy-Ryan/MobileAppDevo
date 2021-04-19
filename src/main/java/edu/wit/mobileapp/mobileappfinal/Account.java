package edu.wit.mobileapp.mobileappfinal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Account extends AppCompatActivity {
    FirebaseAuth fAuth;
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> adapter;
    ImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        //get this firebase instance
        fAuth = FirebaseAuth.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        profilePic = findViewById(R.id.account_image);
        StorageReference storageRef = storage.getReference();
        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final StorageReference ref = storageRef.child("Images/"+userID+".jpg");
        ref.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get().load(uri).into(profilePic);
        }).addOnFailureListener(exception -> Log.d("Test", " Failed!"));
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


        //initialize sign out button
        Button signOutButton;
        signOutButton = findViewById(R.id.account_signOutButton);
        signOutButton.setOnClickListener(v -> {
            fAuth.signOut();
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        });


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
        homeButton = findViewById(R.id.account_feedButton);
        homeButton.setOnClickListener(v -> {
            //route to home
            startActivity(new Intent(getApplicationContext(), Home.class));
        });

        //Input post text
        EditText post = findViewById(R.id.post_text);

        //set current date
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        //post button
        Button buttonTest = findViewById(R.id.account_postButton);
        buttonTest.setOnClickListener(v -> {
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
                                    user.put("userID", userID);
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
