package com.huangj.cainiaorxjava;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by HuangJ on 2017/11/18.22:59
 * 四.RxJava之中的变换操作符;
 */

public class ConversionOperatorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion);
    }

    public void onClick(View view) {
        isMap();
        switch (view.getId()) {
            case R.id.isMap:
                isMap();
                break;
            case R.id.isFloatMap:
                isFloatMap();
                break;
        }
    }

    /**
     * Map操作符: 对原始的Observable发射的每一项数据应用一个你选择的函数,然后返回这个结果的Observable.
     */
    private void isMap() {
        //a.新建一个Int类型的observable;
        Observable<Integer> observable = Observable.just(1);
        //b.通过Map转换成一个String类型;
        Observable<String> observableForMap = observable.map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                return 1+"变换后的String";
            }
        });
        observableForMap.subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d("print", "accept: 转换后的String"+s);
            }
        });

        /**
         * 链式调用;
         */
      /*  Observable.just(2).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
            //随意做什么; return String 即可;
                return integer+"2222";
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d("print", "accept: 222"+s);
            }
        });*/
    }

    private void isFloatMap() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
