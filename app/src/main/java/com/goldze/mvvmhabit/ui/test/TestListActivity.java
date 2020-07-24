package com.goldze.mvvmhabit.ui.test;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.goldze.mvvmhabit.R;
import com.goldze.mvvmhabit.data.source.http.service.DemoApiService2;
import com.goldze.mvvmhabit.entity.VillageStructuresResult;
import com.goldze.mvvmhabit.multicast.MultiAudioActivity;
import com.goldze.mvvmhabit.test.HttpClient;
import com.goldze.mvvmhabit.utils.DemoBean;
import com.goldze.mvvmhabit.utils.ExcelUtil;
import com.goldze.mvvmhabit.utils.RetrofitClientTest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class TestListActivity extends AppCompatActivity {
    public static final String TAG = TestListActivity.class.getSimpleName();

    RecyclerView recyclerView;

    List<TestListBean> testList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_list);
        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadTestListData();
        loadExcelTest();
        testMarquee();
        testTTS();
        testMultiAudio();
        final TestListAdapter testListAdapter = new TestListAdapter(testList);
        testListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                TestListBean testListBean = testList.get(position);
                testListBean.onClickListener.onClick(view);
            }
        });
        recyclerView.setAdapter(testListAdapter);
    }

    // todo 设置测试类
    public void loadTestListData() {
        testList.add(new TestListBean("获取健康码", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("username", "mwsq");
                jsonObject.addProperty("password", "cf79ae6addba60ad018347359bd144d2");
                jsonObject.addProperty("zjhm", "441421199204152426");
                jsonObject.addProperty("xm", "周晓琳");
                ObservableOnSubscribe<String> test = new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                        HttpClient.test();
                        String value1 = HttpClient.testArea();
                        Log.d("test", "value1:" + value1);
                        String value2 = HttpClient.getHealthCode("http://www.maiweiyun.com/personjkm.jsp", jsonObject);
                        Log.d("test", "value2-" + value2);
                        emitter.onNext(value2);
                    }
                };
                Observable.create(test)
                        .subscribeOn(Schedulers.io())//请求网络 在调度者的io线程
                        .observeOn(AndroidSchedulers.mainThread()) //在主线程中更新ui
                        .subscribe(new Observer<String>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                Log.d("test", "onSubscribe");

                            }

                            @Override
                            public void onNext(String s) {
                                Log.d("test", "onNext");
                                Log.d("test", s);

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d("test", "onError");
                                e.printStackTrace();

                            }

                            @Override
                            public void onComplete() {
                                Log.d("test", "onComplete");

                            }
                        });
            }
        }));
        testList.add(new TestListBean("获取楼宇结构", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DemoApiService2 apiService = RetrofitClientTest.getInstance().create(DemoApiService2.class);
                apiService.getTenantList("T001", "T001")
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.newThread())
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(Disposable disposable) throws Exception {

                            }
                        })
                        .subscribe(new Observer<List<VillageStructuresResult>>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(List<VillageStructuresResult> villageStructuresEntities) {
                                Log.d("test", new Gson().toJson(villageStructuresEntities));

                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();

                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        }));
        testList.add(new TestListBean("testRxJava onNext", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Observable.create(new ObservableOnSubscribe<String>() {//create创建产生的实例类型ObservableCreate<T>
                            @Override
                            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                                Log.i(TAG, "subscribe  " + Thread.currentThread().getId() + ":" + Thread.currentThread().getName());
                                emitter.onNext("hello");
                                emitter.onNext("world");
                                emitter.onComplete();
                            }
                        }).subscribeOn(Schedulers.newThread())//subscribeOn创建产生的实例类型ObservableSubscribeOn<T>
                                .observeOn(AndroidSchedulers.mainThread()) //observeOn创建产生的实例类型ObservableObserveOn<T>
                                .subscribe(new Observer<String>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {
                                        Log.i(TAG, "onSubscribe " + Thread.currentThread().getId() + ":" + Thread.currentThread().getName());
                                    }

                                    @Override
                                    public void onNext(String s) {
                                        Log.i(TAG, "onNext  " + Thread.currentThread().getId() + ":" + Thread.currentThread().getName());
                                        Log.i(TAG, s);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.e(TAG, "onError  " + Thread.currentThread().getId() + ":" + Thread.currentThread().getName());
                                        e.printStackTrace();
                                    }

                                    @Override
                                    public void onComplete() {
                                        Log.i(TAG, "onComplete  " + Thread.currentThread().getId() + ":" + Thread.currentThread().getName());
                                        Log.i(TAG, "onComplete");
                                    }
                                });
                    }
                }).start();
            }
        }));


    }

    public void loadExcelTest() {
        testList.add(new TestListBean("2excel", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String filePath = getApplication().getExternalFilesDir(null).getPath();
                final String filename = "demo.xls";
                Log.e(TAG, "fileName:" + filePath + "/" + filename);
                String[] colName = new String[]{"name", "age", "size"};
                final ExcelUtil util = new ExcelUtil();
                util.initExcel(filePath, filename, colName);
                Observable.create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                        List<DemoBean> demoBeans = new ArrayList<>();
                        demoBeans.add(new DemoBean("jalin", 20, 1));
                        demoBeans.add(new DemoBean("admin", 100, 1));
                        demoBeans.add(new DemoBean("张三", 30, 0));
                        util.writeObjListToExcel(demoBeans, filePath + "/" + filename);
                        emitter.onNext(true);
                        emitter.onComplete();

                    }
                }).subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {

                    }
                });
            }
        }));

    }

    public void testMarquee() {
        testList.add(new TestListBean("跑马灯", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TestListActivity.this, MarqueeTestActivity.class));
            }
        }));
    }

    public void testTTS() {
        testList.add(new TestListBean("文字转换语音", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TestListActivity.this, TTSActivity.class));
            }
        }));

    }

    public void testMultiAudio() {
        testList.add(new TestListBean("多点广播测试", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TestListActivity.this, MultiAudioActivity.class));

            }
        }));
    }

    public class TestListAdapter extends BaseQuickAdapter<TestListBean, BaseViewHolder> {

        public TestListAdapter(@Nullable List<TestListBean> data) {
            super(R.layout.item_test_title, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, TestListBean item) {
            helper.setText(R.id.tv_title, item.title);

        }
    }

    public static class TestListBean {
        String title;
        View.OnClickListener onClickListener;

        public TestListBean(String title, View.OnClickListener onClickListener) {
            this.title = title;
            this.onClickListener = onClickListener;
        }
    }
}
