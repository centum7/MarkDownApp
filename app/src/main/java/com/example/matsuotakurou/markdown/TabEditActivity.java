package com.example.matsuotakurou.markdown;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.example.matsuotakurou.markdown.R;
import com.example.matsuotakurou.markdown.database.MyContentProvider;
import com.example.matsuotakurou.markdown.database.MyMemoContract;
import com.example.matsuotakurou.markdown.fragment.SlidingTabLayout;
import com.example.matsuotakurou.markdown.fragment.ViewPagerAdapter;

public class TabEditActivity extends AppCompatActivity {

    // Declaring Your View and Variables

    Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[]={"Home","Events"};
    int Numboftabs =2;

    private boolean isNewMemo = true;
    private long memoId;


    private String title = "";
    private String body = "";
    private String updated = "";
    private String htmlbody = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_edit);

        //EditActivity copy

        Intent intent = getIntent();

        Log.i("check_Edit1", Long.toString(memoId));

        title = intent.getStringExtra("editTitle");
        body = intent.getStringExtra("editBody");
        updated = intent.getStringExtra("editUpdate");
        memoId = intent.getLongExtra("key", memoId);

        Log.i("check_Edit2", Long.toString(memoId));

        isNewMemo = memoId == 0L ? true : false;

        if (intent.getStringExtra("editBody") == null) {
            // new memo
            Log.i("cheak", "new");

        } else {
            // edit memo
            Log.i("cheak", "edit");
            isNewMemo = false;
        }

        Uri uri = ContentUris.withAppendedId(MyContentProvider.CONTENT_URI, memoId);
        String[] projection = new String[]{
                MyMemoContract.Memos.COLUMN_TITLE,
                MyMemoContract.Memos.COLUMN_BODY,
                MyMemoContract.Memos.COLUMN_UPDATED

        };
        String selection = MyMemoContract.Memos.COLUMN_ID + " = ?";
        String[] selectionArgs = new String[]{Long.toString(memoId)};
        Cursor cursor = getContentResolver().query(
                uri,
                projection,
                selection,
                selectionArgs,
                null
        );
        while (cursor.moveToNext()) {
            title = cursor.getString(cursor.getColumnIndex(MyMemoContract.Memos.COLUMN_TITLE));
            body = cursor.getString(cursor.getColumnIndex(MyMemoContract.Memos.COLUMN_BODY));
            updated = "Updated: " + cursor.getString(cursor.getColumnIndex(MyMemoContract.Memos.COLUMN_UPDATED));
        }




        // Creating The Toolbar and setting it as the Toolbar for the activity

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);


        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, Numboftabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}