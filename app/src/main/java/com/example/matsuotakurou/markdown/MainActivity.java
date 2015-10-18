package com.example.matsuotakurou.markdown;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

//import android.support.design.widget.FloatingActionButton;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {
    private SimpleCursorAdapter mAdapter;
    public final static String EXTRA_MYID = "com.example.matsuotakurou.markdown.MYID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(R.string.toolbar_app_name);
        setSupportActionBar(toolbar);

        String[] from = {
                MyMemoContract.Memos.COLUMN_TITLE,
        };
        int[] to = {
                android.R.id.text1,
        };

        mAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_1,
                null,
                from,
                to,
                0
        );

        ListView myListView = (ListView) findViewById(R.id.myListView);
        myListView.setAdapter(mAdapter);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long myid) {
                Intent intent = new Intent(MainActivity.this, ViewActivity.class);
                intent.putExtra("key", myid);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(0, null, this);

        final Intent intent = new Intent(this, EditActivity.class);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setBackgroundTintList(ColorStateList.valueOf(Color.BLUE));
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(intent);
            }
        });
    }



    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                MyMemoContract.Memos.COLUMN_ID,
                MyMemoContract.Memos.COLUMN_TITLE,
                MyMemoContract.Memos.COLUMN_UPDATED,
        };
        return new CursorLoader(
                this,
                MyContentProvider.CONTENT_URI,
                projection,
                null,
                null,
                "updated desc"
        );
    }

    @Override
    public void onLoadFinished(Loader loader, Object cursor) {
        mAdapter.swapCursor((android.database.Cursor) cursor);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mAdapter.swapCursor(null);
    }
}

