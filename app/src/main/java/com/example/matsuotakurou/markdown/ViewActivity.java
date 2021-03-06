package com.example.matsuotakurou.markdown;


import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import org.markdown4j.Markdown4jProcessor;

import java.io.IOException;


public class ViewActivity extends AppCompatActivity {
    private boolean isNewMemo = true;
    private long memoId;

    private String mTitle = "";
    private String mBody = "";
    private String mHtmlbody = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        WebView webView = (WebView) findViewById(R.id.htmlview);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);

        Intent intent = getIntent();

        mTitle = intent.getStringExtra("editTitle");
        mBody = intent.getStringExtra("editBody");

        memoId = intent.getLongExtra("key", 0L);
        isNewMemo = memoId == 0L ? true : false;

        Uri uri = ContentUris.withAppendedId(MyContentProvider.CONTENT_URI, memoId);
        String[] projection = new String[]{
                MyMemoContract.Memos.COLUMN_TITLE,
                MyMemoContract.Memos.COLUMN_BODY,
                MyMemoContract.Memos.COLUMN_HTMLBODY
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
            mTitle = cursor.getString(cursor.getColumnIndex(MyMemoContract.Memos.COLUMN_TITLE));
            mBody = cursor.getString(cursor.getColumnIndex(MyMemoContract.Memos.COLUMN_BODY));
            mHtmlbody = cursor.getString(cursor.getColumnIndex(MyMemoContract.Memos.COLUMN_HTMLBODY));
        }

        try {
            mHtmlbody = new Markdown4jProcessor().process(mBody);
            Log.i("------markdown-----Edit", mHtmlbody);
        } catch (IOException e) {
            e.printStackTrace();
        }
        toolbar.setTitle(mTitle);
        setSupportActionBar(toolbar);

        webView.loadData(mHtmlbody, "text/html; charset=UTF-8", null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view, menu);
        if (isNewMemo) {
            menu.getItem(0).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_edit:
                    /*
                    editActivityにtitleとbodyを渡して遷移させる。
                     */
                Intent intent = new Intent(ViewActivity.this, EditActivity.class);
                intent.putExtra("editTitle", mTitle);
                intent.putExtra("editBody", mBody);
                intent.putExtra("key", memoId);
                startActivity(intent);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}