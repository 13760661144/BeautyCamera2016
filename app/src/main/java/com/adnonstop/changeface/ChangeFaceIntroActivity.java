package com.adnonstop.changeface;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


public class ChangeFaceIntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String source = intent.getStringExtra("source");
        boolean third = false;
        if(source != null && source.equals("third"))
        {
            third = true;
        }
        Helper.openIntroPage(this, third);
        this.finish();
    }
}
