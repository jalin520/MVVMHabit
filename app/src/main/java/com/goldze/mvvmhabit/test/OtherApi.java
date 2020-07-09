package com.goldze.mvvmhabit.test;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * @Author: zhouxiaolin
 * @CreateDate: 2020/6/3 11:49
 * @Description: 其他第三方api
 */
public interface OtherApi {
    String HEALTH_CODE_URL = "http://www.maiweiyun.com/";
    // 健康码账号密码
    String HEALTH_CODE_NAME = "mwsq";
    String HEALTH_CODE_PWD = "8888";


    /**
     * 获取健康码 HealCodeRequestBody
     *
     * @param requestBody
     * @return
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("personjkm.jsp")
    Observable<HealCodeResult> checkPersonHealth(@Body RequestBody requestBody);
}
