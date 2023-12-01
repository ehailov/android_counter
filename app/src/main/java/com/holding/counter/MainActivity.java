package com.holding.counter;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView mData;
    private TextView mTch1;
    private TextView mTch2;
    private TextView mCh1;
    private TextView mCh2;
    private TextView mAdc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mData = (TextView) findViewById(R.id.Data);
        mTch1 = (TextView) findViewById(R.id.Tch1);
        mTch2 = (TextView) findViewById(R.id.Tch2);
        mCh1 = (TextView) findViewById(R.id.Ch1);
        mCh2 = (TextView) findViewById(R.id.Ch2);
        mAdc = (TextView) findViewById(R.id.Adc);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        startCounter();
    }

    private void startCounter() {
        Counter.subscription(getApplicationContext(), new CounterResult() {
            @Override
            public void onSuccess(CounterModel data) {
                updateCounter(data);
            }

            @Override
            public void onFailure(String errorMessage) {
                showTost(errorMessage);
            }

            @Override
            public void onMessage(String message) {
                showTost(message);
            }
        });
    }

    private void updateCounter(CounterModel data) {
        mData.setText(data.getTimeString());
        mTch1.setText(data.t1);
        mTch2.setText(data.t2);
        mCh1.setText(data.ch1);
        mCh2.setText(data.ch2);
        mAdc.setText(data.adc);
    }

    private void showTost(String text) {
        Toast.makeText(getBaseContext(), text, Toast.LENGTH_LONG).show();
    }

}