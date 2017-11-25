package com.huangj.retrofit_rxjava.netNestDemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.huangj.cainiaorxjava.R;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by huangasys on 2017/11/23.18:54
 * http://www.jianshu.com/p/5f5d61f04f96 [参考博客]
 * 需要进行嵌套网络请求：即在第1个网络请求成功后，继续再进行一次网络请求
 * 如 先进行 用户注册 的网络请求, 待注册成功后回再继续发送 用户登录 的网络请求
 */

public class NetNestActivity extends AppCompatActivity {

    private Observable<RegisterClassBean> mRegisterCall;
    private Observable<LoginClassBean> mLoginCall;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_netnest);
        /**
         * 实现功能：发送嵌套网络请求（将英文翻译成中文，翻译两次）
         */
        // a.设置Retrofit对象;
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl("http://fy.iciba.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //b.创建网络请求实例;
        Api api = mRetrofit.create(Api.class);
        //c.获取 注册登录 的Observable;

        mRegisterCall = api.getRegisterCall();

        mLoginCall = api.getLoginCall();

    }

    public void onClick(View view){
        mRegisterCall.subscribeOn(Schedulers.io())//被观察者切换线程发起网络请求;
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<RegisterClassBean>() {
                    //第一次为观察者需要在主线程得到信息;
                    @Override
                    public void accept(RegisterClassBean registerClassBean) throws Exception {
                        //得到RegisterClassBean对象;可以对注册的做操作;
                        Log.d("print", "accept: 第一次网络请求成功");
                        registerClassBean.show();
                    }
                })
                /**
                 * （新被观察者，同时也是新观察者）切换到IO线程去发起登录请求
                 特别注意：因为flatMap是对初始被观察者作变换，所以对于旧被观察者，它是新观察者，所以通过observeOn切换线程
                 但对于初始观察者，它则是新的被观察者
                 */
                .observeOn(Schedulers.io())

                //使用Register对象去获取得到Login对象;
                .flatMap(new Function<RegisterClassBean, ObservableSource<LoginClassBean>>() { // 作变换，即作嵌套网络请求
                    @Override
                    public ObservableSource<LoginClassBean> apply(RegisterClassBean registerClassBean) throws Exception {
                        //如果注册失败则返回 在flatMap中根据RegisterClassBean判断 注册失败 返回 Observable.empty()
                        if (registerClassBean.getStatus() != 1) {
                            //注册失败
                            Log.d("print", "apply: 注册失败");
                            return Observable.empty();
                        }
                        // 将网络请求1转换成网络请求2，即发送网络请求2
                        //发送第二个网络请求;
                        return mLoginCall;
                    }
                })
                //返回主线程进行登陆成功后得到的信息的操作;
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<LoginClassBean>() {
                    @Override
                    public void accept(LoginClassBean loginClassBean) throws Exception {
                        Log.d("print", "accept: 第二次网络请求成功");
                        loginClassBean.show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("print", "accept: 登录失败-失败原因--" +throwable);
                    }
                });
    }
}
