package com.goldze.mvvmhabit.test;

import android.util.Log;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * @Author: zhouxiaolin
 * @CreateDate: 2020/6/4 9:02
 * @Description:
 */
public class InterceptorHelper {
    public static final String TAG = InterceptorHelper.class.getSimpleName();

    /**
     * @Description 网络拦截器
     **/
    public static Interceptor getInterceptor() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {


                Request request = chain.request();
                Response response = chain.proceed(request);

                ResponseBody responseBody = response.body();
                long contentLength = responseBody.contentLength();

                if (!bodyEncoded(response.headers())) {
                    BufferedSource source = responseBody.source();
                    source.request(Long.MAX_VALUE); // Buffer the entire body.
                    Buffer buffer = source.buffer();

                    Charset charset = UTF8;
                    MediaType contentType = responseBody.contentType();
                    if (contentType != null) {
                        try {
                            charset = contentType.charset(UTF8);
                        } catch (UnsupportedCharsetException e) {
                            return response;
                        }
                    }

                    if (!isPlaintext(buffer)) {
                        return response;
                    }

                    if (contentLength != 0) {
                        String result = buffer.clone().readString(charset);
                        Log.d(TAG, " response.url():" + response.request().url());
                        Log.d(TAG, " response.body():" + result);
                        //得到所需的string，开始判断是否异常
                        //***********************do something*****************************

                    }

                }


                return response;
            }
        };
        return interceptor;
    }


    private static final Charset UTF8 = Charset.forName("UTF-8");

    private static boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }

    private static boolean isPlaintext(Buffer buffer) throws EOFException {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }
}
