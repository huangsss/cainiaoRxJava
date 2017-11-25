package com.huangj.retrofit_rxjava.netNestDemo;


import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Created by huangasys on 2017/11/25.10:22
 */

public interface Api {

    //模拟注册
    @GET("ajax.php?a=fy&f=auto&t=auto&w=hi%20register")
    Observable<RegisterClassBean> getRegisterCall();

    //模拟登录
    @GET("ajax.php?a=fy&f=auto&t=auto&w=hi%20login")
    Observable<LoginClassBean> getLoginCall();
}
