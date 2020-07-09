package com.goldze.mvvmhabit.ui.test;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;
import android.widget.TextView;

import com.goldze.mvvmhabit.R;

/**
 * 永远滚动的跑马灯
 */
public class MarqueeTextView extends TextView {
    // 滚动1000分辨率需要的毫秒
    private int mRndDuration = 10000;
    private int mRepeatCount = -1;// 滚动重复次数 ，-1表示无限重复，0 表示不重复只执行一次
    private boolean mRestore = true;// 停止滚动停止后是否还原
    public static final int DIRECTION_FIT_HORIZONTAL = 0;//水平上适配，超出从右到左边滚动
    public static final int DIRECTION_FIT_VERTICAL = 1;//垂直适配，超出从下到上滚动
    public static final int DIRECTION_AUTO_HORIZONTAL = 2;//必然的从右到左边
    public static final int DIRECTION_AUTO_VERTICAL = 3;//必然的从下到上规定
    private int mDirection = DIRECTION_FIT_HORIZONTAL;// 滑动规则

    // 滚动控制器计算机
    private Scroller mSlr;
    private int currentRepeatCount = 0;// 当前重复次数计数器
    // 暂停时的X偏移量
    private int mXPaused = 0;
    // 暂停时的Y偏移量
    private int mYPaused = 0;
    // 是否暂停
    private boolean mPaused = true;

    public MarqueeTextView(Context context) {
        this(context, null);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        // customize the TextView
        setEllipsize(null);
        if (attrs != null) {
            TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.MarqueeTextView);
            if (typeArray != null) {
                mRndDuration = typeArray.getInt(R.styleable.MarqueeTextView_rndDuration, mRndDuration);
                mRepeatCount = typeArray.getInt(R.styleable.MarqueeTextView_repeatCount, mRepeatCount);
                mRestore = typeArray.getBoolean(R.styleable.MarqueeTextView_restore, mRestore);
                mDirection = typeArray.getInt(R.styleable.MarqueeTextView_direction, mDirection);
                typeArray.recycle();
            }
        }
    }

    /**
     * 开始从原始位置滚动文本
     */
    public void startScroll() {
        // 从最右边开始
        mXPaused = -1 * getWidth();
        // 从最底部开始
        mYPaused = -1 * getHeight();
        // 这里要设置停顿一下
        mPaused = true;
        // 重置重复次数==0
        currentRepeatCount = 0;
        resumeScroll();
    }


    /**
     * 继续滚动
     */
    public void resumeScroll() {
        if (!mPaused)
            return;
        // 它有时不会滚动，在构造函数中调用setHorizontallyScrolling。
        setHorizontallyScrolling(true);
        if (mDirection == DIRECTION_FIT_HORIZONTAL || mDirection == DIRECTION_AUTO_HORIZONTAL) {
            // 单行
            setSingleLine(true);
            int width = getWidth();
            int textWidth = getTextWidth();
            if (width >= textWidth && mDirection == DIRECTION_FIT_HORIZONTAL) {
                // 不需要滚动
                setGravity(Gravity.CENTER);
                scrollTo(0, 0);
                stopScroll();
            } else {
                // 使用线性插值稳定滚动
                mSlr = new Scroller(this.getContext(), new LinearInterpolator());
                setScroller(mSlr);
                setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                int scrollingLen = width + textWidth;// 滚动的宽度
                int distance = scrollingLen - width - mXPaused;
                // 计算滚动一圈需要的毫秒数
                int duration = (new Double(mRndDuration / 1000.0 * scrollingLen)).intValue();
                mSlr.startScroll(mXPaused, 0, distance, 0, duration);
                Log.e("test", String.format("mRndDuration = %d,duration = %d,distance=%d,mXPaused=%d,scrollingLen=%d", mRndDuration, duration, distance, mXPaused, scrollingLen));
                mPaused = false;
            }
        } else if (mDirection == DIRECTION_FIT_VERTICAL || mDirection == DIRECTION_AUTO_VERTICAL) {
            // 多行
            setSingleLine(false);
            int height = getHeight();
            int textHeight = getTextHeight();
            if (height >= textHeight && mDirection == DIRECTION_FIT_VERTICAL) {
                // 不需要滚动
                setGravity(Gravity.CENTER);
                scrollTo(0, 0);
                stopScroll();
            } else {
                // 需要滚动
                // 使用线性插值稳定滚动
                mSlr = new Scroller(this.getContext(), new LinearInterpolator());
                setScroller(mSlr);
                setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
                int scrollingLen = height + textHeight;//滚动的高度
                int distance = scrollingLen - height - mYPaused;
                // 计算滚动一圈需要的毫秒数
                int duration = (new Double(mRndDuration / 1000.0 * scrollingLen)).intValue();
                mSlr.startScroll(0, mYPaused, 0, distance, duration);
                Log.e("test", String.format("mRndDuration = %d,duration = %d,distance=%d,mYPaused=%d,scrollingLen=%d", mRndDuration, duration, distance, mYPaused, scrollingLen));
                mPaused = false;
            }
        }

        invalidate();
    }

    /**
     * 计算文本的宽度(以像素为单位)
     *
     * @return the  text width  in pixels
     */
    private int getTextWidth() {
        TextPaint tp = getPaint();
        Rect rect = new Rect();
        String strTxt = getText().toString();
        tp.getTextBounds(strTxt, 0, strTxt.length(), rect);
        return rect.width();
    }

    /**
     * 计算文本的高度(以像素为单位)
     *
     * @return the text height in pixels
     */
    private int getTextHeight() {
        TextPaint tp = getPaint();
        Rect rect = new Rect();
        String strTxt = getText().toString();
        tp.getTextBounds(strTxt, 0, strTxt.length(), rect);
        // 计算行数
        int row = getWidth() > 0 ? rect.width() / getWidth() + 1 : 1;
        float lineSpacingMultiplier = getLineSpacingMultiplier() <= 0 ? 1 : getLineSpacingMultiplier();
        int height = (int) (rect.height() * row * lineSpacingMultiplier * 1.2);
        return height;
    }

    /**
     * 暂停滚动
     */
    public void pauseScroll() {
        if (null == mSlr)
            return;

        if (mPaused)
            return;

        mPaused = true;

        // abortAnimation sets the current X to be the final X,
        // and sets isFinished to be true
        // so current position shall be saved
        mXPaused = mSlr.getCurrX();
        mYPaused = mSlr.getCurrY();

        mSlr.abortAnimation();
    }

    /**
     * 停止滚动,回到
     */
    public void stopScroll() {
        mPaused = true;
        if (mSlr != null) {
            mSlr.computeScrollOffset();
            mSlr = null;
            if (mRestore) {
                scrollTo(0, 0);
            }
        }
        setScroller(null);
        invalidate();
    }


    /*
     * 重写computeScroll以重新启动滚动，设置文本永远滚动
     */
    @Override
    public void computeScroll() {
        super.computeScroll();

        if (null == mSlr) return;
        if (mSlr.isFinished() && (!mPaused)) {
            if ((mRepeatCount == -1 || currentRepeatCount < mRepeatCount)) {
                // 重复滚动
                // 从最右边开始
                mXPaused = -1 * getWidth();
                // 从最底部开始
                mYPaused = -1 * getHeight();
                // 这里要设置停顿一下
                mPaused = true;
                // 重复次数+1
                currentRepeatCount++;
                resumeScroll();
            } else {
                // 停止滚动
                stopScroll();
            }
        }
    }

    public int getRndDuration() {
        return mRndDuration;
    }

    public void setRndDuration(int duration) {
        this.mRndDuration = duration;
    }

    public boolean isPaused() {
        return mPaused;
    }

    public void setRepeatCount(int repeatCount) {
        this.mRepeatCount = repeatCount;
    }

    public int getRepeatCount() {
        return mRepeatCount;
    }

    public void setDirection(int direction) {
        this.mDirection = direction;
    }

    public int getDirection() {
        return mDirection;
    }

    public boolean isRestore() {
        return mRestore;
    }

    public void setRestore(boolean mRestore) {
        this.mRestore = mRestore;
    }
}
