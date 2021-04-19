package edu.wit.mobileapp.mobileappfinal;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Search<T> extends AppCompatActivity {
    EditText searchLink;

    public static class GridItem {
        public Uri image;
        public String title;
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


            return view;
        }
    }

    public void setFeed(String s) {

        FirebaseStorage storage = FirebaseStorage.getInstance();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        List<GridItem> list = new ArrayList<>();
        //view feed
        GridView gridView = findViewById(R.id.search_view);
        GridItemAdapter adapter;
        adapter = new GridItemAdapter(this, 0, list);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            GridItem listItem = (GridItem) gridView.getItemAtPosition(position);
            String str = listItem.title;
            Uri img = listItem.image;

            Intent myIntent = new Intent(gridView.getContext(), UserAccount.class);
            myIntent.putExtra("firstKeyName", str);
            myIntent.putExtra("secondKeyName", img.toString());
            startActivity(myIntent);
        });

        //initializing gridView & setting adapter

        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //change this if statement to a close match instead
                            if (document.getData().get("Username").toString().equals(s)) {
                                GridItem item = new GridItem();
                                //set this posts data on the feed
                                StorageReference storageRef = storage.getReference();
                                StorageReference ref = storageRef.child("Images/" + document.getData().get("userID").toString() + ".jpg");
                                ref.getDownloadUrl().addOnSuccessListener(uri -> {
                                    //change to uri
                                    item.image = uri;
                                    //change to username
                                    item.title = document.getData().get("Username").toString();

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
        setContentView(R.layout.activity_search);

        //link class variables with layout display variables in xml
        searchLink  = findViewById(R.id.search_input);



        Button homeButton;
        homeButton = findViewById(R.id.search_feedButton);
        homeButton.setOnClickListener(v -> {
            //route to home
            startActivity(new Intent(getApplicationContext(), Home.class));
        });

        //initialize search button
        ImageView searchButton;
        searchButton = findViewById(R.id.search_img);
        searchButton.setOnClickListener(v -> {
            //route to account page
            String search = searchLink.getText().toString();
            setFeed(search);

        });
        //functionality to search for accounts
        //go to their account page
        //their account page should show their profile pic and their posts



    }
}
