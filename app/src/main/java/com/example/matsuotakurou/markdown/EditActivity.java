package com.example.matsuotakurou.markdown;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import org.markdown4j.Markdown4jProcessor;

import java.io.IOException;


public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {
    private boolean isNewMemo = true;
    private long memoId;

    private EditText myMemoTitle;
    private EditText myMemoBody;
    private String title = "";
    private String body = "";
    private String htmlbody = ""; // TODO 変数名はキャメルケースで記述する

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        myMemoTitle = (EditText) findViewById(R.id.myMemoTitle);
        myMemoBody = (EditText) findViewById(R.id.myMemoBody);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        // TODO ユーザに見せるような文言はstrings.xmlに記述
        toolbar.setTitle("編集");
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        Log.i("check_Edit1", Long.toString(memoId));

        title = intent.getStringExtra("editTitle");
        body = intent.getStringExtra("editBody");
        memoId = intent.getLongExtra("key", memoId);

        Log.i("check_Edit2", Long.toString(memoId));

        // TODO 冗長なコードなので下記のとおりにするとよい
        // isNewMemo = memoId == 0L;
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
        }
        myMemoTitle.setText(title);
        myMemoBody.setText(body);

        getLoaderManager().initLoader(0, null, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO 不要なコメントは消す
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO 不要なコメントは消す
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        item.getItemId(); // TODO 不要なコード
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


                // TODO 空文字判定は以下のように出来る
                // TextUtils.isEmpty(title);
                if (title.equals("")) {
                    // TODO ユーザに見せるような文言はstrings.xmlに記述
                    Toast.makeText(
                            this,
                            "タイトルを入力してください",
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
                    Log.d("intent edit action_save", String.valueOf(memoId));
                    Intent intent = new Intent(EditActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                break;

            case R.id.action_delete:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                // TODO ユーザに見せるような文言はstrings.xmlに記述
                alertDialog.setTitle("削除の確認");
                // TODO ユーザに見せるような文言はstrings.xmlに記述
                alertDialog.setMessage("本当に削除してもよいですか");
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Uri uri = ContentUris.withAppendedId(MyContentProvider.CONTENT_URI, memoId);
                        String selection = MyMemoContract.Memos.COLUMN_ID + " = ?";
                        String[] selectionArgs = new String[]{Long.toString(memoId)};
                        getContentResolver().delete(
                                uri,
                                selection,
                                selectionArgs
                        );
                        finish();
                    }
                });
                alertDialog.create().show();
                break;

            case R.id.action_from_edit_to_view:
                /*1--------dbに保存--------1*/

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
                    // TODO ユーザに見せるような文言はstrings.xmlに記述
                    Toast.makeText(
                            this,
                            "タイトルを入力してください。",
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
                    /*1----------------1*/

                    /*2-----編集画面に遷移-------2*/

                    Intent intent = new Intent(EditActivity.this, ViewActivity.class);
                    intent.putExtra("editTitle", title);
                    intent.putExtra("editBody", body);
                    intent.putExtra("EDIT_WEBVIEW", htmlbody);
//                    intent.putExtra("key",memoId);
                    Log.d("intent edit action_from_edit_to_view", String.valueOf(memoId));

                    startActivity(intent);
                    finish();

                    /*2----------------2*/

                }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
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
    public void onLoadFinished(Loader loader, Object data) {
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }
}