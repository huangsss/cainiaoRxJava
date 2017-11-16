package com.huangj.cainiaorxjava;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 二.RxJava的线程调度;
 * Created by HuangJ on 2017/11/13.23:50
 */

public class ScheduleTestActivity extends AppCompatActivity {
    Integer i = 10;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    public void ScheduleTest() {

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                //执行一些网络操作.在这里执行

            }
        }).subscribeOn(Schedulers.io())//指定Observable所在线程,后续的Observer也会在指定的线程;io-网络等耗时操作,computer计算的操作;
                .observeOn(AndroidSchedulers.mainThread())//重新指定Observer所在线程;
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
