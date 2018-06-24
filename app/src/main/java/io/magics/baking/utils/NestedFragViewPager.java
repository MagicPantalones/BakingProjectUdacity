package io.magics.baking.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import io.magics.baking.R;

public class NestedFragViewPager extends ViewPager {

    private boolean swipeable;

    public NestedFragViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NestedFragViewPager);
        try {
            swipeable = a.getBoolean(R.styleable.NestedFragViewPager_swipeable, true);
        } finally {
            a.recycle();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return swipeable && super.onInterceptTouchEvent(ev);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return swipeable && super.onTouchEvent(ev);
    }
}
