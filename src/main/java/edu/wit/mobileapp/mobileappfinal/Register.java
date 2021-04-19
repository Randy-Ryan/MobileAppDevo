package edu.wit.mobileapp.mobileappfinal;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import static edu.wit.mobileapp.mobileappfinal.R.id.profilePic;

public class Register extends AppCompatActivity {

    //initialize class variables
    EditText mFullName, mEmail, mPassword;
    Button mRegisterBtn;
    TextView mLoginBtn;
    FirebaseAuth fAuth;
    public static String userID;
    ImageView profilePic;
    Button profileButton;

    private static final int IMAGE = 1;
    private Uri selectedImage;
    public static StorageReference mStorageref;
    public static StorageReference userProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mStorageref= FirebaseStorage.getInstance().getReference("Images");
        userProfilePic = FirebaseStorage.getInstance().getReference("Images/" + this.userID);

        //link class variables with layout display variables in xml
        mFullName   = findViewById(R.id.register_fullName);
        mEmail      = findViewById(R.id.register_email);
        mPassword   = findViewById(R.id.register_password);
        mRegisterBtn= findViewById(R.id.register_button);
        mLoginBtn   = findViewById(R.id.register_text2);
        profilePic = (ImageView) findViewById(R.id.profilePic);
        profileButton = (Button) findViewById(R.id.profile_button);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileChooser();
            }
        });





        fAuth = FirebaseAuth.getInstance();




        //when create account button is clicked
        mRegisterBtn.setOnClickListener(v -> {

            final String email = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();
            final String fullName = mFullName.getText().toString();

            //check if fields are empty and display the errors
            if (TextUtils.isEmpty(email)) {
                mEmail.setError("Email is required for registration.");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                mPassword.setError("Password is required for registration.");
                return;
            }

            if (TextUtils.isEmpty(fullName)) {
                mPassword.setError("Full name is required for registration.");
                return;
            }

            //check if password is "strong"
            //can further implement this password requirement check
            if (password.length() < 6) {
                mPassword.setError("Password Must be >= 6 Characters");
                return;
            }




            // register the user in firebase
            fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    //set the entered full name as the displayname/username
                    FirebaseUser fireUser = fAuth.getCurrentUser();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(fullName).setPhotoUri(selectedImage).build();
                    fireUser.updateProfile(profileUpdates);
                    this.userID = fireUser.getUid();

                    //send verification link to email
                    FirebaseUser fuser = fAuth.getCurrentUser();
                    fuser.sendEmailVerification().addOnSuccessListener(aVoid ->
                            Toast.makeText(Register.this, "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Log.v("randytest", "onFailure: Email not sent " + e.getMessage()));

                    Toast.makeText(Register.this, "User Created.", Toast.LENGTH_SHORT).show();

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Map<String, Object> user = new HashMap<>();
                    user.put("Username", fullName);
                    user.put("userID", this.userID);
                    user.put("Email", email);
                    user.put("Password", password);
                    user.put("postMessage", "");
                    if(!(selectedImage == null)) {
                        fileUploader();
                    }

                    // Add a new document with a generated ID
                    //creating new data instance in cloud firestore
                    db.collection("users").document(this.userID).set(user);

                    //route to home page after registration is complete
                    //should we add a loading screen to route to in between pages?
                    //loading screen could become useful for future implementations
                    startActivity(new Intent(getApplicationContext(), Account.class));
                    finish();
                }else {
                    Toast.makeText(Register.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        //routes to login page
        mLoginBtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Login.class)));


    }
    private void fileChooser() {
        Intent intent = new Intent ();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImage = data.getData();
            profilePic.setImageURI(selectedImage);
        }
    }

    public String getExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        Log.v("Key", "" + mimeTypeMap.getExtensionFromMimeType(cr.getType(uri)));
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private void fileUploader() {
        StorageReference Ref = mStorageref.child(this.userID + "." + getExtension(selectedImage));


        Ref.putFile(selectedImage)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Toast.makeText(Register.this, "Image uploaded", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }


}