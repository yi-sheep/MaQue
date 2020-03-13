package com.gaoxianglong.maque.views;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ScrollView;

import androidx.annotation.RequiresApi;

import com.gaoxianglong.maque.utils.TranslucentListener;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ChangedScrollView extends ScrollView {
    public static final String TAG = "ChangedScrollView";
    private TranslucentListener mTranslucentListener;
    private ScrollviewListener mScrollviewListener;

    public ChangedScrollView(Context context) {
        this(context, null);
    }

    public ChangedScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChangedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ChangedScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * 接口回调
     */
    public interface ScrollviewListener {
        void onScrollchanged(ChangedScrollView scrollView, int x, int y, int oldx, int oldy);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mScrollviewListener != null) {
            mScrollviewListener.onScrollchanged(this,l,t,oldl,oldt);
        }
    }

    public void setTranslucentListener(ScrollviewListener scrollviewListener) {
        mScrollviewListener = scrollviewListener;
            //获取ScrollView滑出的高度
            int scrollY = getScrollY();
            // 获取屏幕高度
            int screenHeight = getContext().getResources().getDisplayMetrics().heightPixels;
            // 这里定义的规则 也就是有效滑动距离为屏幕二分之一
            if (scrollY <= screenHeight / 2f) {
                Log.d(TAG, "ScrollView滑出高度:" + scrollY);
                Log.d(TAG, "screenHeight:" + screenHeight);
                Log.d(TAG, "alpha:" + (1 - scrollY / (screenHeight / 3f)));
                // alpha = 滑动高度 / (screenHeight/3f)
                // 渐变的过程 1~0
            }

    }
}
