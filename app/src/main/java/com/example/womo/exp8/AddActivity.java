package com.example.womo.exp8;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddActivity extends AppCompatActivity {
    EditText name;
    EditText birth;
    EditText gift;
    Button addBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        initViews();
        initButton();
    }

    private void initViews() {
        name = (EditText) findViewById(R.id.edit_name);
        birth = (EditText) findViewById(R.id.edit_birth);
        gift = (EditText) findViewById(R.id.edit_gift);
        addBtn = (Button) findViewById(R.id.add_button);
    }

    private void initButton() {
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDB db = new MyDB(AddActivity.this);
                MyDB.Record rc = db.new Record();
                rc.name = name.getText().toString();
                rc.birth= birth.getText().toString();
                rc.gift = gift.getText().toString();
                if (rc.name.equals("")) {
                    showToast("名字为空，请完善");
                } else if (db.isNameDuplicate(rc.name)) {
                    showToast("名字重复，请检查");
                } else {
                    db.addRecord(rc);
                    AddActivity.this.finish();
                }
            }
        });
    }

    void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
