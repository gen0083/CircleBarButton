package jp.gcreate.sample.samplearccustomview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.concurrent.*;

import jp.gcreate.library.widget.circlebarbutton.CircleBarButton;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final CircleBarButton target = (CircleBarButton) findViewById(R.id.circle);
        final int count = 100;
        target.setOnClickListener(view -> {
            Observable.interval(10, TimeUnit.MILLISECONDS)
                    .take(count + 1)
                    .map(times -> count - times)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(time -> {
                        float angle = ((float)time / (float)count) * 360f;
                        Log.d("sample", "observable : time=" + time + ",angle=" + angle);
                        target.rewriteCircle(angle);
                    });
        });
        findViewById(R.id.button).setOnClickListener(view -> target.onFinishedToRestart());
    }
}
