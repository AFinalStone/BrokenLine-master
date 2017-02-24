package com.shi.androidstudio.brokenline;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 *
 * @author
 * @time 2017/2/22 18:35
 */
public class MainActivity extends AppCompatActivity {

    SimpleView_04 simpleView_04;

    TextView tv_subVoice;
    TextView tv_addVoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        simpleView_04 = (SimpleView_04) findViewById(R.id.simpleView_04);
        tv_subVoice = (TextView) findViewById(R.id.tv_subVoice);
        tv_addVoice = (TextView) findViewById(R.id.tv_addVoice);
        tv_subVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpleView_04.subVoice();
            }
        });

        tv_addVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpleView_04.addVoice();
            }
        });
    }
}
