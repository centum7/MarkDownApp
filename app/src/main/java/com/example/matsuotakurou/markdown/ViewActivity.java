package com.example.matsuotakurou.markdown;


import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

import com.example.matsuotakurou.markdown.database.MyContentProvider;
import com.example.matsuotakurou.markdown.database.MyMemoContract;

import org.markdown4j.Markdown4jProcessor;

import java.io.IOException;


public class ViewActivity extends AppCompatActivity {


    private boolean isNewMemo = true;
    private long memoId;

    WebView webView;

    private TextView myMemoTitle;

    private TextView myMemoUpdated;
    private String title = "";
    private String body = "";
    private String htmlbody="";
    private String updated = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        myMemoTitle = (TextView) findViewById(R.id.myMemoTitle);
        myMemoUpdated = (TextView) findViewById(R.id.myMemoUpdated);
        //webview
         webView = (WebView)findViewById(R.id.htmlview);


        Intent intent = getIntent();




        memoId = intent.getLongExtra("key", 0L);
        isNewMemo = memoId == 0L ? true : false;

    //webview

        Uri uri = ContentUris.withAppendedId(MyContentProvider.CONTENT_URI, memoId);
        String[] projection = new String[] {
                MyMemoContract.Memos.COLUMN_TITLE,
                MyMemoContract.Memos.COLUMN_BODY,
                MyMemoContract.Memos.COLUMN_HTMLBODY,
                MyMemoContract.Memos.COLUMN_UPDATED
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
            updated = "Updated: " + cursor.getString(cursor.getColumnIndex(MyMemoContract.Memos.COLUMN_UPDATED));
        }

        try {
            htmlbody = new Markdown4jProcessor().process(body);
            Log.i("------markdown-----Edit", htmlbody);
        } catch (IOException e) {
            e.printStackTrace();
        }


        myMemoTitle.setText(title);
        webView.loadData(htmlbody, "text/html; charset=UTF-8", null);
        myMemoUpdated.setText(updated);
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
                intent.putExtra("editUpdate",updated);

                intent.putExtra("key",memoId);


                startActivity(intent);

            case R.id.action_delete:

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("Delete Memo");
                alertDialog.setMessage("Are you sure to delete this memo?");
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Uri uri = ContentUris.withAppendedId(MyContentProvider.CONTENT_URI, memoId);
                        String selection = MyMemoContract.Memos.COLUMN_ID + " = ?";
                        String[] selectionArgs = new String[] { Long.toString(memoId) };
                        getContentResolver().delete(
                                uri,
                                selection,
                                selectionArgs
                        );
                        Intent intent = new Intent(ViewActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
                alertDialog.create().show();
                break;




        }





        return super.onOptionsItemSelected(item);
    }
}