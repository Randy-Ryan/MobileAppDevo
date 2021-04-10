package edu.wit.mobileapp.mobileappfinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.auth.FirebaseAuth;

public class Home<T> extends AppCompatActivity {
    FirebaseAuth fAuth;

    public static class GridItem {
        public Bitmap image;
        public String title;
        public String date;
    }

    public class GridItemAdapter extends ArrayAdapter<T> {

        private final LayoutInflater mInflater;

        @Override
        public T getItem(int i) {
            return super.getItem(i);
        }

        public GridItemAdapter(Context context, int rid, List<T> list) {
            super(context, rid, list);
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
            image.setImageBitmap(item.image);

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        Bitmap defaultImage;
        defaultImage = BitmapFactory.decodeResource(getResources(), R.drawable.image_profile);

        //create chat rooms and add to list array
        List<T> list = new ArrayList<>();

        GridItem item = new GridItem();
        item.image = defaultImage;
        item.title = "Chat Room";
        item.date = "Online";
        list.add((T)item);

        //initialize post button
        ImageView postButton;
        postButton = findViewById(R.id.home_post_button);
        postButton.setOnClickListener(v -> {
            //route to post page
            startActivity(new Intent(getApplicationContext(), Post.class));
        });

        //initialize feed button
        ImageView feedButton;
        feedButton = findViewById(R.id.home_feed_button);
        feedButton.setOnClickListener(v -> {
            //route to feed page
            startActivity(new Intent(getApplicationContext(), Feed.class));
        });

        //initialize account button
        ImageView accountButton;
        accountButton = findViewById(R.id.home_account_button);
        accountButton.setOnClickListener(v -> {
            //route to account page
            startActivity(new Intent(getApplicationContext(), Account.class));
        });

        //get this firebase instance
        fAuth = FirebaseAuth.getInstance();
        //initialize sign out button
        Button signOutButton;
        signOutButton = findViewById(R.id.home_signout_button);
        signOutButton.setOnClickListener(v -> {
            fAuth.signOut();
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        });

        //initializing adapter
        GridItemAdapter adapter;
        adapter = new GridItemAdapter(this, 0, list);

        //initializing gridView & setting adapter
        final GridView gridView = findViewById(R.id.GridView01);
        gridView.setAdapter(adapter);

        //when chat room is clicked
        gridView.setOnItemClickListener((parent, v, position, id) -> {
            Intent myIntent = new Intent(gridView.getContext(), Messages.class);
            //chat room 1
            //pass respective Scaledrone channel id
            if (id == 0){
                myIntent.putExtra("firstKeyName","qAsa1lwae0kcHtyo");
                startActivity(myIntent);
            }
        });
    }
}
