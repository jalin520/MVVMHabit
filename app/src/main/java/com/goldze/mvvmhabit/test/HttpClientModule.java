package com.goldze.mvvmhabit.test;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import me.goldze.mvvmhabit.BuildConfig;
import me.goldze.mvvmhabit.http.interceptor.BaseInterceptor;
import me.goldze.mvvmhabit.http.interceptor.logging.Level;
import me.goldze.mvvmhabit.http.interceptor.logging.LoggingInterceptor;
import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.internal.platform.Platform;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpClientModule {
    private static final String TAG = HttpClientModule.class.getSimpleName();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private Context mContext;

    public HttpClientModule(Context context) {
        this.mContext = context;
    }


    /**
     * 创建 Retrofit 指定 url
     *
     * @param url
     * @return
     */
    public Retrofit createRetrofit(String url) {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd hh:mm:ss")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonParser.CustomConverterFactory.create())
                .client(getOkHttpClient())
                .build();

        return retrofit;
    }

    /**
     * 自定义 url，自定义 headers
     *
     * @param url
     * @param headers
     * @return
     */
    public Retrofit createRetrofit(String url, Map<String, String> headers) {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd hh:mm:ss")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonParser.CustomConverterFactory.create())
                .client(getOkHttpClient(headers))
                .build();

        return retrofit;
    }


    private final static int READ_TIMEOUT = 100;

    private final static int CONNECT_TIMEOUT = 60;

    private final static int WRITE_TIMEOUT = 60;

    private OkHttpClient getOkHttpClient() {
        return getOkHttpClient(null);
    }

    private OkHttpClient getOkHttpClient(Map<String, String> headers) {
        //定制OkHttp
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                //读取超时
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                //连接超时
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                //写入超时
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                //自定义连接池最大空闲连接数和等待时间大小，否则默认最大5个空闲连接
                .connectionPool(new ConnectionPool(32, 5, TimeUnit.MINUTES))
                .addInterceptor(new LoggingInterceptor
                        .Builder()//构建者模式
                        .loggable(BuildConfig.DEBUG) //是否开启日志打印
                        .setLevel(Level.BASIC) //打印的等级
                        .log(Platform.INFO) // 打印类型
                        .request("Request") // request的Tag
                        .response("Response")// Response的Tag
                        .addHeader("log-header", "I am the log request header.") // 添加打印头, 注意 key 和 value 都不能是中文
                        .build()
                );

        if (headers != null && !headers.isEmpty()) {
            httpClientBuilder.addInterceptor(new BaseInterceptor(headers));
        }
        return httpClientBuilder.build();
    }

}
