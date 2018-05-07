package com.example.ivanovnv.rxjavatraining;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

    Button mButton;
    Button mButtonObserve;
    Observable<String> mObservable;
    TextView mTextView;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = findViewById(R.id.bt_action);
        mButtonObserve = findViewById(R.id.bt_observe);
        mTextView = findViewById(R.id.tv_result);

        mObservable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter emitter) throws Exception {
                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        emitter.onNext("click at: " + Calendar.getInstance().getTime().toString());
                    }
                });
            }
        }).share();

        mButtonObserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mObservable.observeOn(Schedulers.newThread())
                        .flatMap(new Function<String, ObservableSource<String>>() {
                            @Override
                            public ObservableSource<String> apply(String s) throws Exception {
                                Log.d("TAG", "flatMap: threadId:" + Thread.currentThread().getId());
                                TimeUnit.SECONDS.sleep(5);
                                return Observable.just(s + "\nshow at:" + Calendar.getInstance().getTime().toString());
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                Log.d("TAG", "subscribe: threadId:" + Thread.currentThread().getId());
                                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        mObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        mTextView.setText(s);
                    }
                });

//        mButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mObservable.subscribeOn(Schedulers.newThread())
//                        .flatMap(new Function<String, ObservableSource<String>>() {
//                            @Override
//                            public ObservableSource<String> apply(String s) throws Exception {
//                                TimeUnit.SECONDS.sleep(5);
//                                return Observable.just(s + "\n" + Calendar.getInstance().getTime().toString());
//                            }
//                        })
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new Consumer<String>() {
//                            @Override
//                            public void accept(String s) throws Exception {
//                                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
//                            }
//                        });
//            }
//        });
    }
}
