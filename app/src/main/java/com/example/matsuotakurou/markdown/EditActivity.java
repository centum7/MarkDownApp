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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.markdown4j.Markdown4jProcessor;

import java.io.IOException;


public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {
    private boolean isNewMemo = true;
    private long memoId;

    private EditText mMyMemoTitle;
    private EditText mMyMemoBody;
    private String mTitle = "";
    private String mBody = "";
    private String mHtmlBody = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        mMyMemoTitle = (EditText) findViewById(R.id.myMemoTitle);
        mMyMemoBody = (EditText) findViewById(R.id.myMemoBody);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);

        toolbar.setTitle(R.string.edit_toolbar_title);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();



        mTitle = intent.getStringExtra("editTitle");
        mBody = intent.getStringExtra("editBody");
        memoId = intent.getLongExtra("key", memoId);

         isNewMemo = memoId == 0L;

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
            mTitle = cursor.getString(cursor.getColumnIndex(MyMemoContract.Memos.COLUMN_TITLE));
            mBody = cursor.getString(cursor.getColumnIndex(MyMemoContract.Memos.COLUMN_BODY));
        }

        FloatingActionsMenu floatingActionsMenu = (FloatingActionsMenu) findViewById(R.id.multiple_actions);

        com.getbase.floatingactionbutton.FloatingActionButton storeInCreateViewFloatingActionButton
                = (FloatingActionButton) findViewById(R.id.store_button_in_create_view);

        FloatingActionButton floatingActionButton1 = (FloatingActionButton) findViewById(R.id.store_button);

        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTitle = mMyMemoTitle.getText().toString().trim();
                mBody = mMyMemoBody.getText().toString().trim();

                Log.i("------markdown-----Edit", "start");
                try {
                    mHtmlBody = new Markdown4jProcessor().process(mBody);
                    Log.i("------markdown-----Edit", mHtmlBody);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (TextUtils.isEmpty(mTitle)) {
                    Toast.makeText(
                            EditActivity.this,
                            R.string.input_title,
                            Toast.LENGTH_LONG
                    ).show();
                } else {
                    ContentValues values = new ContentValues();
                    values.put(MyMemoContract.Memos.COLUMN_TITLE, mTitle);
                    values.put(MyMemoContract.Memos.COLUMN_BODY, mBody);
                    values.put(MyMemoContract.Memos.COLUMN_HTMLBODY, mHtmlBody);
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
            }
        });

        FloatingActionButton floatingActionButton2 = (FloatingActionButton) findViewById(R.id.x1_button);
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditActivity.this);
                alertDialog.setTitle(R.string.check_title_delete);
                alertDialog.setMessage(R.string.final_check_delete);
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

            }
        });


        FloatingActionButton floatingActionButton3 = (FloatingActionButton) findViewById(R.id.alert_button);
        floatingActionButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTitle = mMyMemoTitle.getText().toString().trim();
                mBody = mMyMemoBody.getText().toString().trim();

                Log.i("------markdown-----Edit", "start");
                try {
                    mHtmlBody = new Markdown4jProcessor().process(mBody);
                    Log.i("------markdown-----Edit",mHtmlBody);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (mTitle.equals("")) {
                    Toast.makeText(
                            EditActivity.this,
                            R.string.input_title,
                            Toast.LENGTH_LONG
                    ).show();
                } else {
                    ContentValues values = new ContentValues();
                    values.put(MyMemoContract.Memos.COLUMN_TITLE, mTitle);
                    values.put(MyMemoContract.Memos.COLUMN_BODY, mBody);
                    values.put(MyMemoContract.Memos.COLUMN_HTMLBODY, mHtmlBody);
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
                    intent.putExtra("editTitle", mTitle);
                    intent.putExtra("editBody", mBody);
                    intent.putExtra("EDIT_WEBVIEW", mHtmlBody);
//                    intent.putExtra("key",memoId);
                    Log.d("intent edit action_from_edit_to_view", String.valueOf(memoId));

                    startActivity(intent);
                    finish();

                    /*2----------------2*/

                }
            }
        });

        storeInCreateViewFloatingActionButton.setVisibility(View.VISIBLE);
        floatingActionsMenu.setVisibility(View.VISIBLE);



        mMyMemoTitle.setText(mTitle);
        mMyMemoBody.setText(mBody);

        getLoaderManager().initLoader(0, null, this);
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