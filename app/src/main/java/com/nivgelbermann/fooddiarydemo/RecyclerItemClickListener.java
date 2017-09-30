package com.nivgelbermann.fooddiarydemo;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Niv on 28-Sep-17.
 */
class RecyclerItemClickListener extends RecyclerView.SimpleOnItemTouchListener {
    private static final String TAG = "RecyclerItemClickListen";

    // callback interface
    interface OnRecyclerClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    // reference to the callaback object
    // (object that implements the callback interface)
    private final OnRecyclerClickListener mListener;
    private final GestureDetectorCompat mGestureDetector;

    /**
     * Class constructor.
     * Defines the callbacks to the implementing-object's callback interface methods
     * (onItemClick() and onLongItemClick()).
     *
     * @param context      The context in which this class is created.
     * @param recyclerView The {@link RecyclerView} that makes use of this class.
     * @param listener     An object implementing the callback interface.
     */
    public RecyclerItemClickListener(Context context,
                                     final RecyclerView recyclerView,
                                     OnRecyclerClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.d(TAG, "onSingleTapUp: called");

                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childView != null && mListener != null) {
                    Log.d(TAG, "onSingleTapUp: calling mListener.onItemClick()");
                    mListener.onItemClick(childView, recyclerView.getChildAdapterPosition(childView));
                }

                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.d(TAG, "onLongPress: called");

                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childView != null && mListener != null) {
                    Log.d(TAG, "onLongPress: calling mListener.onItemLongClick()");
                    mListener.onItemLongClick(childView, recyclerView.getChildAdapterPosition(childView));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        Log.d(TAG, "onInterceptTouchEvent: starts");
        if (mGestureDetector != null) {
            boolean result = mGestureDetector.onTouchEvent(e);
            Log.d(TAG, "onInterceptTouchEvent(): returned: " + result);
            return result;
        } else {
            Log.d(TAG, "onInterceptTouchEvent(): returned: false");
            return false;
        }
    }

}
