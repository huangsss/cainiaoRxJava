package com.huangj.cainiaorxjava;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
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

    Button clickButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion);
        mEditText = findViewById(R.id.editText);
        isDebounce();

        //防止按钮多次点击;
        clickButton = findViewById(R.id.isThrottleFirst);

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.isMap:
                isMap();
                break;
            case R.id.isFloatMap:
                isFloatMap();
                break;
            case R.id.isThrottleFirst:
//                isThrottleFirst();
                break;
            case R.id.isConcatMap:
//                isThrottleFirst();
                isConcatMap();
                break;
            case R.id.isBuffer:
                isBuffer();
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

    /**
     * 作用：将被观察者发送的事件序列进行 拆分 & 单独转换，再合并成一个新的事件序列，最后再进行发送
     */
    private void isFloatMap() {

        Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
            }
        }).flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                final List<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add("我是事件 " + integer + "拆分后的子事件" + i);
                    // 通过flatMap中将被观察者生产的事件序列先进行拆分，再将每个事件转换为一个新的发送三个String事件
                    // 最终合并，再发送给被观察者
                }
                return Observable.fromIterable(list);//发送List;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d("print", "accept: 233" + s);
            }
        });

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
     * 防止连续点击按钮; 不应这样写: 具体怎么写还不会>..17.11.23
     * ThrottleFirst;
     * 允许设置一个时间长度,之后它会发送固定时间长度内的第一个事件,而屏蔽其他事件,在达到间隔时间时,可以发送下一个事件.
     */
    public void isThrottleFirst() {

        Observable.just(0).throttleFirst(5, TimeUnit.SECONDS)
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


    private void isConcatMap() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
            }
        }).concatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                final List<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add("我是事件 " + integer + "拆分后的子事件" + i);
                    // 通过concatMap中将被观察者生产的事件序列先进行拆分，再将每个事件转换为一个新的发送三个String事件
                    // 最终合并，再发送给被观察者
                }
                return Observable.fromIterable(list);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d("print", "accept: " + s);
            }
        });
    }

    /**
     * - 作用
     * 定期从 被观察者（Obervable）需要发送的事件中 获取一定数量的事件 & 放到缓存区中，最终发送
     * - 应用场景
     * 缓存被观察者发送的事件
     * - 具体使用
     * 那么，Buffer（）每次是获取多少个事件放到缓存区中的呢？下面我将通过一个例子来说明
     */
    private void isBuffer() {
        Observable.just(1,2,3,4,5,6)
                .buffer(2,2)
                // 设置缓存区大小 & 步长
                // 缓存区大小 = 每次从被观察者中获取的事件数量
                // 步长 = 每次获取新事件的数量
                .subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(List<Integer> integers) throws Exception {
                        Log.d("print", "accept: buffer-Size" +integers.size());
                        for (Integer integer : integers) {
                            Log.d("print", "accept: Buffer-integer" +integer);
                        }
                    }
                });
    }
}
