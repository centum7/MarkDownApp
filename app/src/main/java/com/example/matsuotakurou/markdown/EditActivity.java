package com.example.matsuotakurou.markdown;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.markdown4j.Markdown4jProcessor;

import java.io.IOException;

public class EditActivity extends AppCompatActivity {

    private boolean isNewMemo = true;
    private long memoId;

    private EditText myMemoTitle;
    private EditText myMemoBody;
    private TextView myMemoUpdated;
    private String title = "";
    private String body = "";
    private String updated = "";
    private String htmlbody = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        myMemoTitle = (EditText) findViewById(R.id.myMemoTitle);
        myMemoBody = (EditText) findViewById(R.id.myMemoBody);
        myMemoUpdated = (TextView) findViewById(R.id.myMemoUpdated);

/*下記部分を記述 <

 */

//        final ActionBar actionBar = getActionBar();
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//
//        actionBar.addTab(actionBar.newTab()
//                .setText("First")
//                .setTabListener(new TabListener<BlankFragment>(
//                        this, "tag1", BlankFragment.class)));
//        actionBar.addTab(actionBar.newTab()
//                .setText("Second")
//                .setTabListener(new TabListener<BlankFragment_view>(
//                        this, "tag2", BlankFragment_view.class)));



        Intent intent = getIntent();

        Log.i("check_Edit1", Long.toString(memoId));

        title = intent.getStringExtra("editTitle");
        body = intent.getStringExtra("editBody");
        updated = intent.getStringExtra("editUpdate");
        memoId = intent.getLongExtra("key", memoId);

        Log.i("check_Edit2", Long.toString(memoId));
//
//        myMemoTitle.setText(title);
//        myMemoBody.setText(body);
//        myMemoUpdated.setText(updated);

//        String keyword = i.getStringExtra(“KEYWORD”);
//        boolean isAnd = i.getBooleanExtra(“AND”, true);

//                  >

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
        myMemoTitle.setText(title);
        myMemoBody.setText(body);
        myMemoUpdated.setText(updated);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        item.getItemId();
        switch (item.getItemId()) {
            case R.id.action_save:
                title = myMemoTitle.getText().toString().trim();
                body = myMemoBody.getText().toString().trim();

                Log.i("------markdown-----Edit", "start");
                try {
                    htmlbody = new Markdown4jProcessor().process(body);
                    Log.i("------markdown-----Edit", htmlbody);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                if (title.equals("")) {
                    Toast.makeText(
                            this,
                            "Please enter title",
                            Toast.LENGTH_LONG
                    ).show();
                } else {
                    ContentValues values = new ContentValues();
                    values.put(MyMemoContract.Memos.COLUMN_TITLE, title);
                    values.put(MyMemoContract.Memos.COLUMN_BODY, body);
                    values.put(MyMemoContract.Memos.COLUMN_HTMLBODY, htmlbody);
                    Log.i("-----markdown------edit", MyMemoContract.Memos.COLUMN_HTMLBODY.toString());
                    if (isNewMemo) {
                        // insert
                        getContentResolver().insert(MyContentProvider.CONTENT_URI, values);
                    } else {
                        // updated
                        values.put(
                                MyMemoContract.Memos.COLUMN_UPDATED,
                                android.text.format.DateFormat.format(
                                        "yyyy-MM-dd kk:mm:ss",
                                        new java.util.Date()
                                ).toString()
                        );
                        Log.i("check_Edit3", Long.toString(memoId));
                        Uri uri = ContentUris.withAppendedId(MyContentProvider.CONTENT_URI, memoId);
                        String selection = MyMemoContract.Memos.COLUMN_ID + " = ?";
                        String[] selectionArgs = new String[]{Long.toString(memoId)};
                        getContentResolver().update(
                                uri,
                                values,
                                selection,
                                selectionArgs
                        );
                    }
                    Intent intent = new Intent(EditActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                break;

            case R.id.action_tab:
                Intent intent = new Intent(EditActivity.this, TabEdit.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

        }

        return super.onOptionsItemSelected(item);
    }
}