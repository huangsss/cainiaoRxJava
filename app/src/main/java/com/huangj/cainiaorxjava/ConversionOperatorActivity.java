package com.huangj.cainiaorxjava;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by HuangJ on 2017/11/18.22:59
 * 四.RxJava之中的变换操作符;
 */

public class ConversionOperatorActivity extends AppCompatActivity {

    EditText mEditText;
    private String mTrim;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion);
        mEditText = findViewById(R.id.editText);
        isDebounce();
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
            case R.id.isThrottleFirst:
                isThrottleFirst();
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
                return 1 + "变换后的String";
            }
        });
        observableForMap.subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d("print", "accept: 转换后的String" + s);
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
        //

    }

    /**
     * --过滤操作符
     * 仅在过了一段指定时间后还没发送数据才发射数据;
     */
    private void isDebounce() {
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //数据发生变化;
                Log.d("print", "afterTextChanged: " + editable.toString());

                mTrim = editable.toString().trim();//String类型;

                Observable.just(mTrim)//a.发送输入的文字得到Observable,
                        /**
                         * b.debouce 每过一段时间,发送一次数据; [ 这样就不用每次输入或者删除数据都去请求数据 ]
                         */
                        .debounce(500, TimeUnit.MILLISECONDS)
                        .subscribeOn(AndroidSchedulers.mainThread())//监听Text变化需要在主线程
                        //另外:  发送String之前判断是否为空; 过滤操作符 filter;
                        .filter(new Predicate<String>() {
                            @Override
                            public boolean test(String s) throws Exception {
                                return mTrim.length() > 0;
                            }
                        })
                        //c.通过发送String数据,发送网络请求得到List集合;
                        .flatMap(new Function<String, ObservableSource<List<String>>>() {
                            @Override
                            public ObservableSource<List<String>> apply(String s) throws Exception {
                                // 网络请求开始搜索;
                                List<String> mList = new ArrayList<>();
                                mList.add("one");
                                mList.add("two");
                                mList.add("three");
                                return Observable.just(mList);//返回一个List;
                            }
                        })
                        /* 用法与flatMap 用法完全一致,只是发射最近发射的数据;用来解决EditText请求数据的时候,前一次的数据未及时返回,有发送了后面的数据导致数据错乱;
                        .switchMap(new Function<List<String>, ObservableSource<List<String>>>() {
                            @Override
                            public ObservableSource<List<String>> apply(List<String> strings) throws Exception {
                                // 网络请求开始搜索;
                                List<String> mList = new ArrayList<>();
                                mList.add("one");
                                mList.add("two");
                                mList.add("three");
                                return Observable.just(mList);//返回一个List;
                            }
                        })*/

                        .subscribeOn(Schedulers.io())//网络请求在子线程
                        .observeOn(AndroidSchedulers.mainThread())//主线程更新UI;
                        //d.通过返回的list
                        .subscribe(new Consumer<List<String>>() {
                            @Override
                            public void accept(List<String> strings) throws Exception {
                                //得到List;
                                Log.d("print", "accept: " + strings.toString());
                            }
                        });
            }
        });
    }

    /**
     * 防止连续点击按钮;
     *  ThrottleFirst;
     *  允许设置一个时间长度,之后它会发送固定时间长度内的第一个事件,而屏蔽其他事件,在达到间隔时间时,可以发送下一个事件.
     */
    public void isThrottleFirst() {

        Observable.just(0).throttleFirst(1,TimeUnit.SECONDS)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d("print", "onNext: ---点击事件---");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
