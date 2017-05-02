package com.jayavijayjayavelu.news_gateway;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static ArrayList<String> items = new ArrayList<>();
    public ArrayList<NewsArticle> totalArticleListMain = new ArrayList<>();
    public static ArrayList<NewsSource> totalSourceList;
    public static ArrayList<NewsArticle> totalArticleListTemp;
    public static MyPageAdapter pageAdapter;
    private List<Fragment> fragments;
    private ViewPager pager;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    public static String category = "";
    public static String article = "";
    public static int positionMain;
    public static String photoURL="";
    static final String SERVICE_DATA = "SERVICE_DATA";
    static final String NEWS_SOURCE = "NEWS_SOURCE";
    static final String NEWS_ARTICLE = "NEWS_ARTICLE";

    private SampleReceiver sampleReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting() && items.isEmpty()) {
            Intent intent = new Intent(MainActivity.this, NewsService.class);
            startService(intent);
            sampleReceiver = new SampleReceiver();

            IntentFilter filter3 = new IntentFilter(NEWS_SOURCE);
            registerReceiver(sampleReceiver, filter3);

        } else if(netInfo == null) {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            final TextView tv = new TextView(MainActivity.this);
            tv.setText("Data cannot be accessed/loaded without Internet Connection.");
            tv.setTextColor(Color.BLACK);
            tv.setTextSize(15);
            tv.setPadding(50, 20, 0, 30);
            alertDialog.setView(tv);
            alertDialog.setTitle("No Network Connection");
            alertDialog.setIcon(android.R.drawable.ic_menu_search);
            alertDialog.show();
        }
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list, items));
        mDrawerList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectItem(position);
                    }
                }
        );
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        fragments = getFragments();
        pageAdapter = new MyPageAdapter(getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);
    }

    public void fragmentInitialize(){
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list, items));
        mDrawerList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectItem(position);
                    }
                }
        );
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void selectItem(int position) {
        positionMain = position;
        Toast.makeText(this, items.get(position), Toast.LENGTH_SHORT).show();
        article = items.get(position);
        setTitle(article);
        String dataURL2Temp =
                "https://newsapi.org/v1/articles?source=&apiKey=17756624c11144828e89e36b625bb2d0";
        dataURL2Temp = dataURL2Temp.substring(0,39)+totalSourceList.get(position).getId()+dataURL2Temp.substring(39);
        NewsService newsService = new NewsService();
        newsService.dataURL = dataURL2Temp;
        stopService(new Intent(this, NewsService.class));
        Intent intent = new Intent(this, NewsService.class);
        startService(intent);
        IntentFilter filter1 = new IntentFilter(NEWS_ARTICLE);
        registerReceiver(sampleReceiver, filter1);
    }

    public void articleInitializer(ArrayList<NewsArticle> totalArticleList){
        totalArticleListMain.clear();
        totalArticleListMain = totalArticleList;
        reDoFragments(positionMain);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void reDoFragments(int idx) {
        for (int i = 0; i < pageAdapter.getCount(); i++)
            pageAdapter.notifyChangeInPosition(i);

        fragments.clear();
        int count = totalArticleListMain.size();
        for (int i = 0; i < count; i++) {
            fragments.add(MyFragment.newInstance(this, totalArticleListMain.get(i).getTitle(),
                    totalArticleListMain.get(i).getUrltoImage(), totalArticleListMain.get(i).getDescription(),
                    totalArticleListMain.get(i).getUrl(), totalArticleListMain.get(i).getAuthor(),
                    totalArticleListMain.get(i).getPublishedAt(), i, count));
        }
        pageAdapter.notifyDataSetChanged();
        pager.setCurrentItem(0);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (item.toString().equals("all")) {
            setTitle("News Gateway");
            String dataURL2Temp =
                    "https://newsapi.org/v1/sources?language=en&country=us&apiKey=17756624c11144828e89e36b625bb2d0";
            NewsService newsService = new NewsService();
            newsService.dataURL = dataURL2Temp;
            Intent intent = new Intent(MainActivity.this, NewsService.class);
            startService(intent);
            IntentFilter filter3 = new IntentFilter(NEWS_SOURCE);
            registerReceiver(sampleReceiver, filter3);
        } else {
            category = item.toString();
            String dataURL2Temp =
                    "https://newsapi.org/v1/sources?language=en&country=us&category=&apiKey=17756624c11144828e89e36b625bb2d0";
            dataURL2Temp = dataURL2Temp.substring(0,63)+category+dataURL2Temp.substring(63);
            NewsService newsService = new NewsService();
            newsService.dataURL = dataURL2Temp;
            Intent intent = new Intent(MainActivity.this, NewsService.class);
            startService(intent);
            IntentFilter filter3 = new IntentFilter(NEWS_SOURCE);
            registerReceiver(sampleReceiver, filter3);
        }
        return true;
    }

    private List<Fragment> getFragments() {
        List<Fragment> fList = new ArrayList<Fragment>();
        return fList;
    }

     class MyPageAdapter extends FragmentPagerAdapter {
        private long baseId = 0;


        public MyPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public long getItemId(int position) {
            return baseId + position;
        }
        public void notifyChangeInPosition(int n) {
            baseId += getCount() + n;
        }

         @Override
         public void finishUpdate(ViewGroup container) {
             try{
                 super.finishUpdate(container);
             } catch (NullPointerException nullPointerException){
                 System.out.println("Catch the NullPointerException in FragmentPagerAdapter.finishUpdate");
             }
         }

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(sampleReceiver);
        Intent intent = new Intent(MainActivity.this, NewsService.class);
        stopService(intent);
        super.onDestroy();
    }

    class SampleReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case NEWS_SOURCE:
                    String data = "";
                    if (intent.hasExtra(SERVICE_DATA)) {
                        data = intent.getStringExtra(SERVICE_DATA);
                        totalSourceList = parseJSON1(data);
                        fragmentInitialize();
                    }
                    break;
                case NEWS_ARTICLE:
                    String value = "";
                    if (intent.hasExtra(SERVICE_DATA)) {
                        value = intent.getStringExtra(SERVICE_DATA);
                        totalArticleListMain.clear();
                        totalArticleListTemp = parseJSON2(value);
                        articleInitializer(totalArticleListTemp);
                        fragmentInitialize();
                    }
                    break;
            }
        }
    }

    private ArrayList<NewsSource> parseJSON1(String s) {
        String id;
        String name;
        String url;
        String category;
        ArrayList<NewsSource> sourceTemp = new ArrayList<>();
        items.clear();
        try {
            JSONObject jObjMain = new JSONObject(s);
            JSONArray jSource = new JSONArray(jObjMain.getString("sources"));
            JSONObject jSingleSource;
            for(int i =0;i < jSource.length();i++){
                jSingleSource = new JSONObject(jSource.get(i).toString());
                id = jSingleSource.getString("id");
                name = jSingleSource.getString("name");
                url = jSingleSource.getString("url");
                category = jSingleSource.getString("category");
                sourceTemp.add(new NewsSource(id, name, url, category));
                items.add(name);
            }
        }catch (Exception e){
        }
        return sourceTemp;
    }

    private ArrayList<NewsArticle> parseJSON2(String s) {
        ArrayList<NewsArticle> articleTemp = new ArrayList<>();
        String author;
        String title;
        String description;
        String url;
        String urltoImage;
        String publishedAt;
        try {
            JSONObject jObjMain = new JSONObject(s);
            JSONObject jObjValue;
            JSONArray jSource = new JSONArray(jObjMain.getString("articles"));
            for (int i =0 ;i < jSource.length(); i++){
                jObjValue = new JSONObject(jSource.get(i).toString());
                author = jObjValue.getString("author");
                title = jObjValue.getString("title");
                description = jObjValue.getString("description");
                url = jObjValue.getString("url");
                urltoImage = jObjValue.getString("urlToImage");
                publishedAt = jObjValue.getString("publishedAt");
                articleTemp.add(new NewsArticle(author,title,description,url,urltoImage,publishedAt));
            }
        }catch (Exception e){
        }
        return articleTemp;
    }

}