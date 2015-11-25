package jp.gcreate.sample.samplearccustomview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import jp.gcreate.library.widget.circlebarbutton.CircleBarButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CircleBarButton target = (CircleBarButton) findViewById(R.id.circle);
        findViewById(R.id.button).setOnClickListener(view -> target.onFinishedToRestart());
    }
}
