package com.example.matsuotakurou.markdown;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;



public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    private SimpleCursorAdapter adapter;
    public final static String EXTRA_MYID = "com.example.matsuotakurou.markdown.MYID";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        String[] from = {
                MyMemoContract.Memos.COLUMN_TITLE,
                MyMemoContract.Memos.COLUMN_UPDATED,

        };
        int[] to = {
                android.R.id.text1,
                android.R.id.text2
        };

        adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                null,
                from,
                to,
                0
        );

        ListView myListView = (ListView) findViewById(R.id.myListView);
        myListView.setAdapter(adapter);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long myid) {

                Intent intent = new Intent(MainActivity.this, ViewActivity.class);
                intent.putExtra("key", myid);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(0, null, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_add) {
            Intent intent = new Intent(this, EditActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        adapter.swapCursor((android.database.Cursor) cursor);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        adapter.swapCursor(null);
    }
}