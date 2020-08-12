// TestAidlInterface.aidl
package com.goldze.mvvmhabit;

// Declare any non-default types here with import statements
import com.goldze.mvvmhabit.TestAidlListener;
interface TestAidlInterface {
    int getCount();
    // 设置监听
    void registerListener(TestAidlListener listener);
}
