package com.example.womo.exp8;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {
    ListView list;
    CursorAdapter myAdapter;
    Button addBtn;
    MyDB myDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDB = new MyDB(this);
        setContentView(R.layout.activity_main);
        initListView();
        initButton();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        updateList();
    }

    private void initButton() {
        addBtn = (Button) findViewById(R.id.add_button);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    private void initListView() {
        list = (ListView) findViewById(R.id.list);
        myAdapter = new SimpleCursorAdapter(this, R.layout.list_item, myDB.queryAllData(),
                new String[] {"name", "birth", "gift"},
                new int[] {R.id.name, R.id.birthday, R.id.gift},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        list.setAdapter(myAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final MyDB.Record rc = myDB.toRecord((Cursor)myAdapter.getItem(position));
                final View v = LayoutInflater.from(MainActivity.this).
                        inflate(R.layout.dialog_layout, null);
                final EditText edit_birth = (EditText) v.findViewById(R.id.edit_birth);
                final EditText edit_gift = (EditText) v.findViewById(R.id.edit_gift);
                ((TextView) v.findViewById(R.id.name)).setText(rc.name);
                ((TextView) v.findViewById(R.id.number)).setText(getPhoneNumber(rc.name));
                edit_birth.setText(rc.birth);
                edit_gift.setText(rc.gift);
                new AlertDialog.Builder(MainActivity.this)
                        .setView(v)
                        .setTitle("(￣_,￣ )")
                        .setNegativeButton("放弃修改", null)
                        .setPositiveButton("保存修改", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                rc.birth = edit_birth.getText().toString();
                                rc.gift = edit_gift.getText().toString();
                                myDB.updateRecord(rc);
                                updateList();
                            }
                        })
                        .create().show();
            }
        });
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final MyDB.Record rc = myDB.toRecord((Cursor)myAdapter.getItem(position));
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("是否删除？")
                        .setNegativeButton("否",null)
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                myDB.deleteRecord(rc);
                                updateList();
                            }
                        }).create().show();
                return true;
            }
        });
    }

    void updateList() {
        myAdapter.changeCursor(myDB.queryAllData());
    }

    private String getPhoneNumber(String name) {
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" = \""+name+"\"", null, null);
        if (cursor.moveToFirst()) {
            int isHas = Integer.parseInt(cursor.getString(cursor.getColumnIndex(
                    ContactsContract.Contacts.HAS_PHONE_NUMBER)));
            if (isHas != 0) {
                String _id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                cursor.close();
                cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ "="+_id, null,null);
                String phoneNumber = "";
                while (cursor.moveToNext()) {
                    phoneNumber += cursor.getString(cursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER)) + " ";
                }
                cursor.close();
                return phoneNumber;
            }
        }
        return "无";
    }

}
