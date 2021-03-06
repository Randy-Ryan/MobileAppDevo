package edu.wit.mobileapp.mobileappfinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class Home<T> extends AppCompatActivity {

    public static class GridItem {
        public Uri image;
        public String title;
        public String date;
        public String id;
    }

    public class GridItemAdapter extends ArrayAdapter<T> {

        final LayoutInflater mInflater;

        @Override
        public T getItem(int i) {
            return super.getItem(i);
        }

        public GridItemAdapter(Context context, int rid, List<GridItem> list) {
            super(context, rid, (List<T>) list);
            mInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //retrieve data
            GridItem item = (GridItem) getItem(position);

            //Use layout file to generate view
            View view = mInflater.inflate(R.layout.activity_list_item, null);

            //setting image
            ImageView image;
            image = view.findViewById(R.id.image);
            Picasso.get().load(item.image).into(image);
            //setImage(image);

            //setting title
            TextView title;
            title = view.findViewById(R.id.title);
            title.setText(item.title);

            //setting date
            TextView date;
            date = view.findViewById(R.id.date);
            date.setText(item.date);
            return view;
        }
    }

    public void setFeed() {

        FirebaseStorage storage = FirebaseStorage.getInstance();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        List<GridItem> list = new ArrayList<>();
        //view feed
        GridView gridView = findViewById(R.id.feed_view);
        GridItemAdapter adapter;
        adapter = new GridItemAdapter(this, 0, list);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            GridItem listItem = (GridItem) gridView.getItemAtPosition(position);
            String str = listItem.title;
            Uri img = listItem.image;
            String theirID = listItem.id;
            Log.v("randyyyyy", "" + str);
            Log.v("randyyyyy", "" + img);
            Log.v("randyyyyy", "" + theirID);

            Intent myIntent = new Intent(gridView.getContext(), UserAccount.class);
            myIntent.putExtra("firstKeyName", str);
            myIntent.putExtra("secondKeyName", theirID);
            startActivity(myIntent);
        });
        //randy

        //initializing gridView & setting adapter

        db.collection("feed")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            if (document.getData().get("postMessage").toString() != null) {
                                GridItem item = new GridItem();
                                //set this posts data on the feed
                                StorageReference storageRef = storage.getReference();
                                StorageReference ref = storageRef.child("Images/" + document.getData().get("userID").toString() + ".jpg");
                                ref.getDownloadUrl().addOnSuccessListener(uri -> {
                                    //change to uri
                                    item.image = uri;
                                    //change to username
                                    item.title = document.getData().get("Username").toString();
                                    //change to postMessage
                                    item.date = document.getData().get("postMessage").toString();
                                    item.id = document.getData().get("userID").toString();
                                    adapter.add((T)item);
                                }).addOnFailureListener(exception -> Log.d("Test", " Failed!"));

                            }
                        }

                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //initialize account button
        ImageView accountButton;
        accountButton = findViewById(R.id.home_accountButton);
        accountButton.setOnClickListener(v -> {
            //route to account page
            startActivity(new Intent(getApplicationContext(), Account.class));
        });

        //initialize search button
        ImageView searchButton;
        searchButton = findViewById(R.id.home_searchButton);
        searchButton.setOnClickListener(v -> {
            //route to account page
            startActivity(new Intent(getApplicationContext(), Search.class));
        });

        setFeed();

    }
}
