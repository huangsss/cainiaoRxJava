package com.huangj.cainiaorxjava;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by huangasys on 2017/11/14.16:09
 * 三.RxJava的操作符;
 */

public class OperatorTestActivity extends AppCompatActivity {

    // 第一次对i赋值;
    Integer i = 10;
    long l = 100;

    TextView mTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operator_layout);
        mTextView = findViewById(R.id.text);
        mTextView.setText("测试");
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.defer:
                isDefer();
                break;
            case R.id.timer:
                isTimer();
                break;
            case R.id.interval:
                isInterval();
                break;
            case R.id.intervalRange:
                isIntervalRange();
                break;
            case R.id.range:
                isRange();
                break;
            case R.id.rangeLong:
                isRangeLong();
                break;
        }
    }

    /**
     * 1. defer();直到有观察者（Observer）订阅时，才动态创建被观察者对象（Observable） & 发送事件
     */
    public void isDefer() {

        // 2. 通过defer 定义被观察者对象
        // 注：此时被观察者对象还没创建 [ 创建时将类型定义好--  Observable<Integer>  --]
        Observable<Integer> observable = Observable.defer(new Callable<ObservableSource<? extends Integer>>() {
            @Override
            public ObservableSource<? extends Integer> call() throws Exception {
                return Observable.just(i);
            }
        });

        i = 15;

        // 注：此时，才会调用defer（）创建被观察者对象（Observable）
        observable.subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d("print", "defer---onSubscribe: 开始连接");
            }

            @Override
            public void onNext(Integer integer) {
                Log.d("print", "defer---onNext: 接收的数据" + integer);
            }

            @Override
            public void onError(Throwable e) {
                Log.d("print", "defer---onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d("print", "defer--- onComplete: ---");
            }
        });
    }

    /**
     * 2.timer();快速创建1个被观察者对象（Observable）
     * 发送事件的特点：延迟指定时间后，发送1个数值0（Long类型）
     * 延迟指定事件，发送一个0，一般用于检测
     */
    private void isTimer() {

        // 该例子 = 延迟2s后，发送一个long类型数值
        // 注：timer操作符默认运行在一个新线程上
        // 也可自定义线程调度器（第3个参数）：timer(long,TimeUnit,Scheduler)
        // .observeOn(AndroidSchedulers.mainThread());如果需要再接收的地方操作UI 则需要切换线程;
        Observable.timer(2, TimeUnit.SECONDS).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d("print", "timer---onSubscribe: ");
            }

            @Override
            public void onNext(Long aLong) {
                mTextView.setText(aLong + "");
                Log.d("print", "timer---onNext: 接收到的参数" + aLong);
            }

            @Override
            public void onError(Throwable e) {
                //出错未打印;

            }

            @Override
            public void onComplete() {
                Log.d("print", "timer---onComplete:--- ");
            }
        });
    }

    /**
     * 3.interval(); 发送的事件序列 = 从0开始、无限递增1的的整数序列  每隔段时间就发送;[不会停止发送][ - - 退出了activity却还有 - -]
     */
    private void isInterval() {

        // 参数说明：
        // 参数1 = 第1次延迟时间；
        // 参数2 = 间隔时间数字；
        // 参数3 = 时间单位；
        // 注：interval默认在computation调度器上执行
        // 也可自定义指定线程调度器（第3个参数）：interval(long,TimeUnit,Scheduler)
        Observable.interval(3, 5, TimeUnit.SECONDS).subscribe(new Observer<Long>() {
            //第一次延迟3S发送  之后每5S发送一次;不会停止;
            @Override
            public void onSubscribe(Disposable d) {
                Log.d("print", "interval---onSubscribe: ");
            }

            @Override
            public void onNext(Long aLong) {
                Log.d("print", "interval---onNext: " + aLong);
            }

            @Override
            public void onError(Throwable e) {
                Log.d("print", "interval---onError: ");
            }

            @Override
            public void onComplete() {
                Log.d("print", "interval---onComplete: ");
            }
        });
    }

    /**
     * 4. interRange();
     * 发送的事件序列 = 从0开始、无限递增1的的整数序列
     * b. 作用类似于interval（），但可指定发送的数据的数量
     * 注：interval默认在computation调度器上执行也可自定义指定线程调度器;
     */
    private void isIntervalRange() {
// 参数说明：
        // 参数1 = 事件序列起始点；
        // 参数2 = 事件数量；
        // 参数3 = 第1次事件延迟发送时间；
        // 参数4 = 间隔时间数字；
        // 参数5 = 时间单位
        Observable.intervalRange(3, 6, 3, 2, TimeUnit.SECONDS).subscribe(new Observer<Long>() {

            //从3开始 发送6个数字;
            //第一次延迟3s发送; 后面每次经过2s发送一次;
            @Override
            public void onSubscribe(Disposable d) {
                Log.d("print", "intervalRange---onSubscribe:--- ");
            }

            @Override
            public void onNext(Long aLong) {
                Log.d("print", "intervalRange---onNext:--- " + aLong);
            }

            @Override
            public void onError(Throwable e) {
                Log.d("print", "intervalRange---onError:--- ");
            }

            @Override
            public void onComplete() {
                Log.d("print", "intervalRange---onComplete:--- ");
            }
        });
    }

    /**
     * 5.range();
     * 快速创建1个被观察者对象（Observable）
     * 发送事件的特点：连续发送 1个事件序列，可指定范围
     * 发送的事件序列 = 从0开始、无限递增1的的整数序列
     * 作用类似于intervalRange（），但区别在于：无延迟发送事件
     */
    private void isRange() {
        // 参数说明：
        // 参数1 = 事件序列起始点；
        // 参数2 = 事件数量；
        // 注：若设置为负数，则会抛出异常
        Observable.range(1, 10).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d("print", "range---onSubscribe:--- ");
            }

            @Override
            public void onNext(Integer integer) {
                Log.d("print", "range---onNext: ---" + integer);
            }

            @Override
            public void onError(Throwable e) {
                Log.d("print", "range---onError: ---");
            }

            @Override
            public void onComplete() {
                Log.d("print", "range---onComplete: ---");
            }
        });
    }

    /**
     * 6. rangeLong();
     * 作用：类似于range（），区别在于该方法支持数据类型 = Long
     * 具体使用
     * 与range（）类似，此处不作过多描述
     */
    private void isRangeLong() {
        Observable.rangeLong(1, 10).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d("print", "rangeLong---onSubscribe: ---");
            }

            @Override
            public void onNext(Long aLong) {
                Log.d("print", "rangeLong---onNext: ---" + aLong);
            }

            @Override
            public void onError(Throwable e) {
                Log.d("print", "rangeLong---onError: ---");
            }

            @Override
            public void onComplete() {
                Log.d("print", "rangeLong---onComplete: ---");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("print", "---onDestroy:---已经销毁---");
    }
}

