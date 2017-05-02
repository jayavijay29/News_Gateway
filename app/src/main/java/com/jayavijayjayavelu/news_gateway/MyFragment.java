package com.jayavijayjayavelu.news_gateway;

/**
 * Created by jayavijayjayavelu on 4/22/17.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class MyFragment extends Fragment {
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    public static MainActivity mainActivity;
    public static Context context;
    ImageView imageView1;

    public static final MyFragment newInstance(MainActivity ma, String title, String image,
                                               String description, String url,
                                               String author, String time, int current, int total)
    {
        mainActivity = ma;
        context = ma;
        MyFragment f = new MyFragment();
        Bundle bdl = new Bundle(1);
        bdl.putString("title", title);
        bdl.putString("imageURL",image);
        bdl.putString("desc",description);
        bdl.putString("link", url);
        bdl.putString("author",author);
        bdl.putString("time",time);
        bdl.putInt("currentPage",current);
        bdl.putInt("totalPage",total);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String message = getArguments().getString("title");
        String photoUrl = getArguments().getString("imageURL");
        String description = getArguments().getString("desc");
        int current = getArguments().getInt("currentPage") + 1;
        int total = getArguments().getInt("totalPage");
        View v = inflater.inflate(R.layout.myfragment_layout, container, false);
        TextView titleTextView = (TextView)v.findViewById(R.id.textView);
        titleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = getArguments().getString("link");
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        titleTextView.setText(message);
        ImageView imageView = (ImageView)v.findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = getArguments().getString("link");
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        imageView1 = imageView;
        loadImage(photoUrl);
        TextView desTextView = (TextView)v.findViewById(R.id.textView3);
        if(description!=(null))
            desTextView.setText(description);
        else
            desTextView.setText("");
        desTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = getArguments().getString("link");
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        TextView authorTextView = (TextView)v.findViewById(R.id.author);
        if(!getArguments().getString("author").equals("null"))
            authorTextView.setText(getArguments().getString("author"));
        else
            authorTextView.setText("Anonymous");
        TextView timeTextView = (TextView)v.findViewById(R.id.date);
        try {
            String time = getArguments().getString("time");
            time = time.substring(0,10)+"-"+time.substring(11,16);
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
            SimpleDateFormat target = new SimpleDateFormat("EEE dd,yyyy HH:mm");
            if(!getArguments().getString("time").equals("null"))
                timeTextView.setText(target.format(format.parse(time)).toString());
            else
                timeTextView.setText("");
        }catch (Exception e){
            System.out.println(e);
        }
        TextView countTextView = (TextView)v.findViewById(R.id.textView2);
        countTextView.setText(String.valueOf(current)+" of "+String.valueOf(total));
        return v;
    }

    private void loadImage(String photoUrl) {
        Log.d(TAG, "loadImage: " + photoUrl);
        Picasso picasso = new Picasso.Builder(context)
                .listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        Log.d(TAG, "onImageLoadFailed: ");
                        picasso.load(R.drawable.missingimage)
                                .into(imageView1);
                    }
                })
                .build();

        picasso.load(photoUrl)
                .error(R.drawable.missingimage)
                .placeholder(R.drawable.placeholder)
                .into(imageView1);
    }

}