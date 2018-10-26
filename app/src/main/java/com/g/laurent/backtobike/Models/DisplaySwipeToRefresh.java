package com.g.laurent.backtobike.Models;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;


public class DisplaySwipeToRefresh extends SwipeRefreshLayout {

    public DisplaySwipeToRefresh(Context context) {
        super(context);
    }

    public DisplaySwipeToRefresh(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                super.onTouchEvent(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                return false;
            case MotionEvent.ACTION_CANCEL:
                super.onTouchEvent(ev);
                break;
            case MotionEvent.ACTION_UP:
                return false;
            default:
                break;
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
        return true;
    }
}
