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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


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

        //set the text variables at the top of home page
        TextView t1;
        t1 = findViewById(R.id.home_text1);
        t1.setText("Welcome to That Game Chat!");

        TextView t2;
        t2 = findViewById(R.id.home_text2);
        t2.setText("Play Tic Tac Toe to earn points");

        TextView t3;
        t3 = findViewById(R.id.home_text3);
        t3.setText("Or join a chat room below");

        //create image & place it at /res/drawable
        //not working right now for some reason
        //would be good to get icons in there
        Bitmap defaultImage;
        defaultImage = BitmapFactory.decodeResource(getResources(), R.drawable.image_profile);

        //get today's date and format for string output
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        //create chat rooms and add to list array
        List<T> list = new ArrayList<>();

        GridItem item = new GridItem();
        item.image = defaultImage;
        item.title = "Chat room 1";
        item.date = "Online";
        list.add((T)item);

        GridItem item2 = new GridItem();
        item2.image = defaultImage;
        item2.title = "Chat room 2";
        item2.date = "Online";
        list.add((T)item2);

        GridItem item3 = new GridItem();
        item3.image = defaultImage;
        item3.title = "Chat room 3";
        item3.date = "Online";
        list.add((T)item3);

        //initialize shop button
        ImageView shopButton;
        shopButton = findViewById(R.id.home_shop_button);
        shopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //route to shop page
                startActivity(new Intent(getApplicationContext(), Shop.class));
            }
        });

        //initialize tic tac toe button
        ImageView tttButton;
        tttButton = findViewById(R.id.home_ttt_button);
        tttButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //route to ttt page
                startActivity(new Intent(getApplicationContext(), Feed.class));
            }
        });

        //initialize account button
        ImageView accountButton;
        accountButton = findViewById(R.id.home_account_button);
        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //route to account page
                startActivity(new Intent(getApplicationContext(), Account.class));
            }
        });
        //get this firebase instance
        fAuth = FirebaseAuth.getInstance();
        //initialize sign out button
        Button signOutButton;
        signOutButton = findViewById(R.id.home_signout_button);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });

        //initializing adapter
        GridItemAdapter adapter;
        adapter = new GridItemAdapter(this, 0, list);

        //initializing gridView & setting adapter
        final GridView gridView = findViewById(R.id.GridView01);
        gridView.setAdapter(adapter);

        //when a chat room is clicked
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Intent myIntent = new Intent(gridView.getContext(), Messages.class);

                //chat room 1
                //pass respective Scaledrone channel id
                if (id == 0){
                    myIntent.putExtra("firstKeyName","qAsa1lwae0kcHtyo");
                    startActivity(myIntent);
                }

                //chat room 2
                //pass respective Scaledrone channel id
                if (id == 1){
                    myIntent.putExtra("firstKeyName","uYBPPresEC6GNq2r");
                    startActivity(myIntent);
                }

                //chat room 3
                //pass respective Scaledrone channel id
                if (id == 2){
                    myIntent.putExtra("firstKeyName","MiBNOgaJ9bXeMTcD");
                    startActivity(myIntent);
                }
            }
        });
    }
}
