package com.goldze.mvvmhabit.test;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;


public class GsonParser {
    private static final String TAG = GsonParser.class.getSimpleName();

    public static Gson getGson() {
        return new Gson();
    }



    static class CustomerConverter<T> implements Converter<ResponseBody, T> {
        private Type mType;

        public void setType(Type type) {
            mType = type;
        }

        @Override
        public T convert(ResponseBody value) throws IOException {
            String jsonString = value.string();
            Log.d(TAG, "convert mType: " + mType);
            if (mType == HealCodeResult.class) {
                return (T) getHealCodeResultFromJson(jsonString);
            }
            return null;
        }
    }

    public static HealCodeResult getHealCodeResultFromJson(String jsonString) {
        HealCodeResult info = getGson().fromJson(jsonString, HealCodeResult.class);
        return info;
    }

    public static class CustomConverterFactory extends Converter.Factory {

        public static final CustomConverterFactory INSTANCE = new CustomConverterFactory();

        public static CustomConverterFactory create() {
            return INSTANCE;
        }

        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            CustomerConverter converter = null;
            if (type == HealCodeResult.class) {
                converter = new CustomerConverter<HealCodeResult>();
            }

            if (converter != null) {
                converter.setType(type);
            }
            return converter;
        }

    }
}
