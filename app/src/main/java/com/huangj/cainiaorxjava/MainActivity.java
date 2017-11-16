package com.huangj.cainiaorxjava;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * 一.RxJava的基础操作;
 * RxJava 是一个在 Java VM 上使用可观测的序列来组成异步的、基于事件的程序的库
 * RxJava 是一个 基于事件流、实现异步操作的库
 * 基于事件流的链式调用，使得RxJava：
 * 逻辑简洁
 * 实现优雅
 * 使用简单
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void click(View view) {
        Observable<String> observable = getObservable();
        /**
         * 第一种关联;
         */
//        Observer<String> observer = getObserver();
//        observable.subscribe(observer);

        /**
         * 第二种关联;
         */
        observable.subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d("print", "accept: " + s);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.d("print", "accept: Consumer");
            }
        }, new Action() {
            @Override
            public void run() throws Exception {
                Log.d("print", "run:  Action");
            }
        });
    }

    public Observable<String> getObservable() {
        //第一种创建;
    /*    return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("第一个数据");
                e.onNext("第二个数据");
                e.onComplete();
            }
        });*/

        /**
         * 第二种创建;just(T...)：直接将传入的参数依次发送出来; 注：最多只能发送10个参数
         */

    /*return Observable.just("第二种创建的第一个数据","第二种创建的第一个数据");
         将会依次调用：
         onNext("A");
         onNext("B");
         onNext("C");
         onCompleted();*/
/**
 * 第三种创建;传数组...from(T[]) / from(Iterable<? extends T>) : 将传入的数组 / Iterable 拆分成具体对象后，依次发送出来
 * 作用
 快速创建1个被观察者对象（Observable）
 发送事件的特点：直接发送 传入的数组数据
 会将数组中的数据转换为Observable对象
 */
    /*    String[] words = {"A", "B", "C"};
        Observable.fromArray(words);*/
        // 将会依次调用：
        // onNext("A");
        // onNext("B");
        // onNext("C");
        // onCompleted();

        //第四种创建;只发送一次;
        return Observable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "第四种创建的第一个数据";
            }
        });


    }

    public Observer<String> getObserver() {

        Observer<String> observer = new Observer<String>() {
            Disposable mDisposable;//可以通过此判断是否有联系或者切断与Observable的联系;
            // 可采用 Disposable.dispose() 切断观察者 与 被观察者 之间的连接

            // 观察者接收事件前，默认最先调用复写 onSubscribe（）
            @Override
            public void onSubscribe(Disposable d) {
                mDisposable = d;
//                d.isDisposed(); 是否存在联系;
                Log.d("print", "onSubscribe: ");
            }

            // 当被观察者生产Next事件 & 观察者接收到时，会调用该复写方法 进行响应
            @Override
            public void onNext(String s) {

                Log.d("print", "onNext: " + s);
              /*  if (s.equals("第一个数据")){
                    mDisposable.dispose();//切断关系;
                }*/
            }

            // 当被观察者生产Error事件& 观察者接收到时，会调用该复写方法 进行响应
            @Override
            public void onError(Throwable e) {
                Log.d("print", "onError: ");
            }

            // 当被观察者生产Complete事件& 观察者接收到时，会调用该复写方法 进行响应
            @Override
            public void onComplete() {
                Log.d("print", "onComplete: ");
            }
        };

        //第二种创建Observer;
        Subscriber<String> observer2 = new Subscriber<String>() {

            // 观察者接收事件前，默认最先调用复写 onSubscribe（）
            @Override
            public void onSubscribe(Subscription s) {

            }

            // 当被观察者生产Next事件 & 观察者接收到时，会调用该复写方法 进行响应
            @Override
            public void onNext(String s) {

            }

            // 当被观察者生产Next事件 & 观察者接收到时，会调用该复写方法 进行响应
            @Override
            public void onError(Throwable t) {

            }

            // 当被观察者生产Complete事件& 观察者接收到时，会调用该复写方法 进行响应
            @Override
            public void onComplete() {

            }
        };
        //<--特别注意：2种方法的区别，即Subscriber 抽象类与Observer 接口的区别 -->
        // 相同点：二者基本使用方式完全一致（实质上，在RxJava的 subscribe 过程中，Observer总是会先被转换成Subscriber再使用）
        // 不同点：Subscriber抽象类对 Observer 接口进行了扩展，新增了两个方法：[未找到 QAQ]
        // 1. onStart()：在还未响应事件前调用，用于做一些初始化工作
        // 2. unsubscribe()：用于取消订阅。在该方法被调用后，观察者将不再接收 & 响应事件
        // 调用该方法前，先使用 isUnsubscribed() 判断状态，确定被观察者Observable是否还持有观察者Subscriber的引用，如果引用不能及时释放，就会出现内存泄露


        return observer;
    }

    public void click2(View view) {
        //链式调用;
        Observable.just(1).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d("print", "accept: 链式调用" + integer);
            }
        });
    }

    /**
     * // RxJava的链式操作
     Observable.create(new ObservableOnSubscribe<Integer>() {
     // 1. 创建被观察者 & 生产事件
     @Override public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
     emitter.onNext(1);
     emitter.onNext(2);
     emitter.onNext(3);
     emitter.onComplete();
     }
     }).subscribe(new Observer<Integer>() {
     // 2. 通过通过订阅（subscribe）连接观察者和被观察者
     // 3. 创建观察者 & 定义响应事件的行为
     @Override public void onSubscribe(Disposable d) {
     Log.d(TAG, "开始采用subscribe连接");
     }
     // 默认最先调用复写的 onSubscribe（）

     @Override public void onNext(Integer value) {
     Log.d(TAG, "对Next事件"+ value +"作出响应"  );
     }

     @Override public void onError(Throwable e) {
     Log.d(TAG, "对Error事件作出响应");
     }

     @Override public void onComplete() {
     Log.d(TAG, "对Complete事件作出响应");
     }

     });
     }
     }
     注：整体方法调用顺序：观察者.onSubscribe（）> 被观察者.subscribe（）> 观察者.onNext（）>观察者.onComplete()
     */

}

