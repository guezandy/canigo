package com.lunadeveloper.canigo.scroll;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import com.lunadeveloper.canigo.R;


/**
 * Horizontal Scroll View that will center on the most visible element.
 *
 * This implementation assumes that the elements are of all equal width.
 *
 * Created by andrew 7/4/2016
 */

public class CenteringHorizontalScrollView extends HorizontalScrollView {

    private Runnable mScrollerTask;
    private int mInitialPosition;
    private Context mContext;
    private View mCurrentView;

    private int currentIdx=0;
    private int mNewCheck = 100;
    private static final String TAG = "CenteringHorizontalScrollView";
    private DisplayMetrics mMetrics;
    private float mItemWidth = -1;

    private OnScrollStoppedListener onScrollStoppedListener;

    /**
     * Defines the callback for when the scrolling stops.  This will give you the 'selected view' once scrolling is finished.
     */
    public interface OnScrollStoppedListener{
        void onScrollStopped(View view, int index);
    }

    public CenteringHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mItemWidth = (int)getResources().getDimension(R.dimen.item_selector_width);
        this.mContext = context;
        this.mMetrics = context.getResources().getDisplayMetrics();
        this.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    CenteringHorizontalScrollView.this.startScrollerTask();
                }
                return false;
            }
        });

        mScrollerTask = new Runnable() {

            public void run() {

                int newPosition = getScrollY();
                if(mInitialPosition - newPosition == 0){//has stopped
                    setCenter();
                    if(onScrollStoppedListener!=null){
                        onScrollStoppedListener.onScrollStopped(mCurrentView, currentIdx);
                    }
                }else{
                    mInitialPosition = getScrollY();
                    CenteringHorizontalScrollView.this.postDelayed(mScrollerTask, mNewCheck);
                }
            }
        };
    }

    /**
     * The ScrollStoppedListener that will receive the scroll stopped callback.  The callback will be fired
     * once the scroll has centered the right view.
     * @param listener
     */
    public void setOnScrollStoppedListener(CenteringHorizontalScrollView.OnScrollStoppedListener listener){
        onScrollStoppedListener = listener;
    }


    /**
     * Call this when a scroll is initiated.
     */
    public void startScrollerTask(){
        mInitialPosition = getScrollY();
        CenteringHorizontalScrollView.this.postDelayed(mScrollerTask, mNewCheck);
    }

    /**
     * This will center the closest view to the middle of the screen.
     */
    public void setCenter() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenWidth = displaymetrics.widthPixels;
        ViewGroup parent = (ViewGroup) getChildAt(0);

        float x = getScrollX();
        //float widthInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mItemWidthInDip, mMetrics);
        float offset = x == 0 ? 1 : (x/mItemWidth)+((screenWidth/2)/mItemWidth);

        Log.d("OFFSET", ""+((int)offset));

        mCurrentView = parent.getChildAt(((int)offset));
        currentIdx = (int)offset;

        if(mCurrentView != null) {
            int scrollX = (mCurrentView.getLeft() - (screenWidth / 2))
                    + (mCurrentView.getWidth() / 2);
            this.smoothScrollTo(scrollX, 0);
        }
    }


}