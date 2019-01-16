package com.brook.app.android;

import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.brook.app.android.eventbus.R;
import com.brook.app.android.supportlibrary.eventbus.EventBus;
import com.brook.app.android.supportlibrary.eventbus.Subscribe;
import com.brook.app.android.supportlibrary.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);

        text = findViewById(R.id.text);

        text.postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(1, 2);
            }
        }, 3000);
    }

    @Keep
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiver(int a, int b) {
        text.setText("结果=" + (a + b));
    }
}
