package edu.wit.mobileapp.mobileappfinal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class Account extends AppCompatActivity {
    int balance;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        //get this firebase instance
        fAuth = FirebaseAuth.getInstance();

        //get username from firebase account
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
                                    TextView bal;
                                    bal = findViewById(R.id.account_balance);
                                    bal.setText("Balance: " + balance + " pts");
                                }
                            }
                        } else {
                            Log.v("randytest", "Error getting documents.", task.getException());
                        }
                    }
                });



        //photoUrl =  fAuth.getCurrentUser().getPhotoUrl();
        //uid =  fAuth.getCurrentUser().getUid();
        String email =  fAuth.getCurrentUser().getEmail();
        boolean emailVerified =  fAuth.getCurrentUser().isEmailVerified();

        //set TextView variables
        TextView t1;
        t1 = findViewById(R.id.account_text1);
        t1.setText("Username: " + username);

        TextView t2;
        t2 = findViewById(R.id.account_text2);
        t2.setText("Account email: " + email);

        TextView t3;
        t3 = findViewById(R.id.account_text3);
        t3.setText("Email verified? " + emailVerified);


        //initialize home button
        Button homeButton;
        homeButton = findViewById(R.id.account_homebutton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //route to home
                startActivity(new Intent(getApplicationContext(), Home.class));
            }
        });

    }
}
