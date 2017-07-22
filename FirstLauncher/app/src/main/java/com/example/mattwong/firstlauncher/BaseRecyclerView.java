package com.example.mattwong.firstlauncher;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.util.*;

/**
 * Created by Matt Wong on 7/19/2017.
 */

public abstract class BaseRecyclerView extends RecyclerView implements RecyclerView.OnItemTouchListener{

    private static final int SCROLL_DELTA_THRESHOLD_DP = 4;

    int mDy =0;
    private float mDeltaThreshold;

    protected BaseRecyclerView mScrollbar;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private int mDownX;
    private int mDownY;
    private int mLastY;
    private Rect mBackgroundPadding = new Rect();

    public BaseRecyclerView(Context context) {this(context, null);}
    public BaseRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, null, 0);
    }

    public BaseRecyclerView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        mDeltaThreshold = getResources().getDisplayMetrics().density * SCROLL_DELTA_THRESHOLD_DP;

        ScrollListener listener = new ScrollListener();
        addOnScrollListener(listener);
    }

    private class ScrollListener extends OnScrollListener {
        public ScrollListener(){
            //do nothing
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy){
        mDy=dy;
        onUpdateScrollbar(dy);
    }

    @Override
    protected void OnFinishInflate(){
        super.onFinishInflate();
        addOnItemTouchListner(this);
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent ev) {handleTouchEvent(ev);}


    private boolean handleTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownX = x;
                mDownY = mLastY = y;
                if(shouldStopScroll(ev)){
                    stopScroll();
                }
                mScrollbar.handleTouchEvent(ev, mDownX, mDownY, mLastY);
                break;
            case MotionEvent.ACTION_MOVE:
                mLastY=y;
                mScrollbar.handleTouchEvent(ev, mDownX, mDownY, mLastY);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                onFastScrollCompleted();
                mScrollbar.handleTouchEvent(ev,mDownX,mDownY,mLastY);
                break;
        }
        return mScrollbar.isDraggingThumb();
    }

    protected abstract void handleTouchEvent(MotionEvent ev, int mDownX, int mDownY, int mLastY);

    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept){
        //needed implementation for m build
    }

    protected boolean shouldStopScroll(MotionEvent ev) {
        if (ev.getAction()==MotionEvent.ACTION_DOWN){
            if((Math.abs(mDy) < mDeltaThreshold && getScrollState() != RecyclerView.SCROLL_STATE_IDLE)){
                return true;
            }
        }
        return false;
    }

    public void updateBackgroundPadding (Rect padding) { mBackgroundPadding.set(padding);}

    public Rect getBackgroundPadding() {
        return mBackgroundPadding;
    }

    public int getMaxScrollbarWidth() {
        return mScrollbar.getThumbMaxWidth();
    }

    protected int getVisibleHeight() {
        int visibleHeight = getHeight() - mBackgroundPadding.top - mBackgroundPadding.bottom;
        return visibleHeight;
    }


    protected abstract int getAvailableScrollHeight();

    protected int getAvailableScrollBarHeight() {
        int availableScrollBarHeight = getVisibleHeight() - mScrollbar.getThumbHeight();
        return availableScrollBarHeight;
    }

    public int getFastScrollerTrackColor(int defaultTrackColor) {
        return defaultTrackColor;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        onUpdateScrollbar(0);
        mScrollbar.draw(canvas);
    }

    protected void synchronizeScrollBarThumbOffsetToViewScroll(int scrollY,
                                                               int availableScrollHeight) {
        // Only show the scrollbar if there is height to be scrolled
        int availableScrollBarHeight = getAvailableScrollBarHeight();
        if (availableScrollHeight <= 0) {
            mScrollbar.setThumbOffset(-1, -1);
            return;
        }

        // Calculate the current scroll position, the scrollY of the recycler view accounts for the
        // view padding, while the scrollBarY is drawn right up to the background padding (ignoring
        // padding)
        int scrollBarY = mBackgroundPadding.top +
                (int) (((float) scrollY / availableScrollHeight) * availableScrollBarHeight);

        // Calculate the position and size of the scroll bar
        mScrollbar.setThumbOffset(getScrollBarX(), scrollBarY);
    }
    protected int getScrollBarX() {
        if (Utilities.isRtl(getResources())) {
            return mBackgroundPadding.left;
        } else {
            return getWidth() - mBackgroundPadding.right - mScrollbar.getThumbWidth();
        }
    }
    public abstract int getCurrentScrollY();

    protected abstract String scrollToPositionAtProgress(float touchFraction);

    protected abstract void onUpdateScrollbar(int dy);

    protected void onFastScrollCompleted() {
    }
}

