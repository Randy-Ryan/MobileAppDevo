package edu.wit.mobileapp.mobileappfinal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class Shop extends AppCompatActivity {
    int balance;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        fAuth = FirebaseAuth.getInstance();
        final String username = fAuth.getCurrentUser().getDisplayName();

        //loads the current user's balance
        //issue where it takes a few seconds to first load - maybe just pass this variable
        //instead of doing this in the on create
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getData().get("Username").toString().equals(username)) {
                                    String s = document.getData().get("Balance").toString();
                                    balance = Integer.parseInt(s);
                                    TextView balanceText;
                                    balanceText = findViewById(R.id.shop_balance);
                                    balanceText.setText(username + "'s balance is: " + balance + " pts");
                                    Log.v("randytest", "" +  s);
                                    Log.v("randytest", "" + Integer.parseInt(s));
                                }
                            }
                        } else {
                            Log.w("randytest", "Error getting documents.", task.getException());
                        }
                    }
                });


        //home button
        Button homeButton;
        homeButton = findViewById(R.id.shop_homebutton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Home.class));
            }
        });


        //Input post text
        EditText post = findViewById(R.id.test1);


        //post button
        Button buttonTest = findViewById(R.id.test2);
        buttonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        if (document.getData().get("Username").toString().equals(username)) {
                                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                                            Log.v("randytest", "" + post.getText().toString());
                                            db.collection("users").document(document.getId()).update("postMessage", post.getText().toString());



                                            FirebaseUser fUser = fAuth.getCurrentUser();
                                            Map<String, Object> user = new HashMap<>();
                                            user.put("Username", username);
                                            user.put("postMessage", post.getText().toString());
                                            user.put("userID", fUser.getUid());

                                            // Add a new document with a generated ID
                                            //creating new data instance in cloud firestore
                                            db.collection("feed")
                                                    .add(user)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            Log.v("randytest", "DocumentSnapshot added with ID: " + documentReference.getId());
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.v("randytest", "Error adding document", e);
                                                        }
                                                    });
                                        }
                                    }
                                } else {
                                    Log.v("randytest", "Error getting documents.", task.getException());
                                }
                            }
                        });
            }
        });

        //accessory 1
        Button button1;
        button1 = findViewById(R.id.shop_button1);

        //linked with 25 pt button
        //remove 25 pts from balance and give user accessory 2
        //still need to implement giving the accessory to user
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                balance = balance - 25;
                Log.v("randytest", "" + post.getText().toString());
                TextView balanceText;
                balanceText = findViewById(R.id.shop_balance);
                balanceText.setText(username + "'s balance is: " + balance + " pts");
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        if (document.getData().get("Username").toString().equals(username)) {
                                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                                            db.collection("users").document(document.getId()).update("Balance", balance);
                                        }
                                    }
                                } else {
                                    Log.v("randytest", "Error getting documents.", task.getException());
                                }
                            }
                        });
            }
        });

        //accessory 2
        Button button2;
        button2 = findViewById(R.id.shop_button2);
        Log.v("testtest1", "" + button2.isShown());

        //linked with 50 pt button
        //remove 50 pts from balance and give user accessory 1
        //still need to implement giving the accessory to user
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                balance = balance - 50;
                TextView balanceText;
                balanceText = findViewById(R.id.shop_balance);
                balanceText.setText(username + "'s balance is: " + balance + " pts");
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("users")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        if (document.getData().get("Username").toString().equals(username)) {
                                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                                            db.collection("users").document(document.getId()).update("Balance", balance);
                                        }
                                    }
                                } else {
                                    Log.w("randytest", "Error getting documents.", task.getException());
                                }
                            }
                        });
            }
        });
    }
}

