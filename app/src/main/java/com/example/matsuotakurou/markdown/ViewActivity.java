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
import android.widget.TextView;

import org.markdown4j.Markdown4jProcessor;

import java.io.IOException;


public class ViewActivity extends AppCompatActivity {


    private boolean isNewMemo = true;
    private long memoId;

    private WebView mWebView;

    private TextView myMemoTitle;

    private String title = "";
    private String body = "";
    private String htmlbody="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        myMemoTitle = (TextView) findViewById(R.id.myMemoTitle);
        mWebView = (WebView)findViewById(R.id.htmlview);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("プレビュー");
        setSupportActionBar(toolbar);


        Intent intent = getIntent();

        title = intent.getStringExtra("editTitle");
        body = intent.getStringExtra("editBody");

        memoId = intent.getLongExtra("key", 0L);
        isNewMemo = memoId == 0L ? true : false;

    //webview

        Uri uri = ContentUris.withAppendedId(MyContentProvider.CONTENT_URI, memoId);
        String[] projection = new String[] {
                MyMemoContract.Memos.COLUMN_TITLE,
                MyMemoContract.Memos.COLUMN_BODY,
                MyMemoContract.Memos.COLUMN_HTMLBODY
        };
        String selection = MyMemoContract.Memos.COLUMN_ID + " = ?";
        String[] selectionArgs = new String[] { Long.toString(memoId) };
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
            htmlbody = cursor.getString(cursor.getColumnIndex(MyMemoContract.Memos.COLUMN_HTMLBODY));
        }

        try {
            htmlbody = new Markdown4jProcessor().process(body);
            Log.i("------markdown-----Edit", htmlbody);
        } catch (IOException e) {
            e.printStackTrace();
        }


        myMemoTitle.setText(title);
        mWebView.loadData(htmlbody, "text/html; charset=UTF-8", null);

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view, menu);
        if (isNewMemo) {
            menu.getItem(0).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
            /*下記部分を記述 <

             */
        switch (item.getItemId()) {
            case R.id.action_edit:
                    /*
                    editActivityにtitleとbodyを渡して遷移させる。
                     */
                Intent intent = new Intent(ViewActivity.this, EditActivity.class);
                intent.putExtra("editTitle", title);
                intent.putExtra("editBody", body);



                intent.putExtra("key",memoId);


                startActivity(intent);
                finish();

//            case R.id.action_delete:
//
//                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
//                alertDialog.setTitle("Delete Memo");
//                alertDialog.setMessage("Are you sure to delete this memo?");
//                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Uri uri = ContentUris.withAppendedId(MyContentProvider.CONTENT_URI, memoId);
//                        String selection = MyMemoContract.Memos.COLUMN_ID + " = ?";
//                        String[] selectionArgs = new String[] { Long.toString(memoId) };
//                        getContentResolver().delete(
//                                uri,
//                                selection,
//                                selectionArgs
//                        );
//                        Intent intent = new Intent(ViewActivity.this, MainActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
//                    }
//                });
//                alertDialog.create().show();
//                break;




        }





        return super.onOptionsItemSelected(item);
    }
}