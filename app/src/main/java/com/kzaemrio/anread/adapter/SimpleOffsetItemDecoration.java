package com.kzaemrio.anread.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;

import com.kzaemrio.anread.R;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class SimpleOffsetItemDecoration extends RecyclerView.ItemDecoration {

    private final Paint mPaint;

    private final int mSpaceMedium;
    private final int mSpaceLarge;

    private final float mDp1;

    private final int mLineColor;
    private final int mBoxColor;

    public SimpleOffsetItemDecoration(Context context) {
        mPaint = new Paint();

        mSpaceMedium = context.getResources().getDimensionPixelOffset(R.dimen.space_medium);
        mSpaceLarge = context.getResources().getDimensionPixelOffset(R.dimen.space_large);

        mDp1 = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                1,
                context.getResources().getDisplayMetrics()
        );

        mLineColor = ContextCompat.getColor(context, R.color.text_color_header);
        mBoxColor = ContextCompat.getColor(context, R.color.colorAccent);
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        if (parent.getChildCount() > 0) {
            mPaint.setStrokeWidth(0);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mLineColor);
            float cx = mSpaceLarge / 2F;
            float lineWidth = mDp1 / 2;
            c.drawRect(
                    cx - lineWidth,
                    0,
                    cx + lineWidth,
                    parent.getHeight(),
                    mPaint

            );

            for (int i = 0, count = parent.getChildCount(); i < count; i++) {
                View view = parent.getChildAt(i);
                float cy = view.getTop() + view.getHeight() / 2F;
                float boxSize = mDp1 * 4;
                float boxLeft = cx - boxSize;
                float boxTop = cy - boxSize;
                float boxRight = cx + boxSize;
                float boxBottom = cy + boxSize;
                switch (parent.getChildViewHolder(view).getItemViewType()) {
                    case TimeHeaderItem.TYPE:
                        mPaint.setStrokeWidth(0);
                        mPaint.setStyle(Paint.Style.FILL);
                        mPaint.setColor(mBoxColor);
                        c.drawRect(
                                boxLeft,
                                boxTop,
                                boxRight,
                                boxBottom,
                                mPaint
                        );
                        break;
                    case TimeItem.TYPE:
                        mPaint.setStrokeWidth(mDp1);
                        mPaint.setStyle(Paint.Style.STROKE);
                        mPaint.setColor(mBoxColor);
                        c.drawRect(
                                boxLeft,
                                boxTop,
                                boxRight,
                                boxBottom,
                                mPaint
                        );
                        mPaint.setColor(Color.WHITE);
                        mPaint.setStrokeWidth(0);
                        mPaint.setStyle(Paint.Style.FILL);
                        c.drawRect(
                                boxLeft + mDp1,
                                boxTop + mDp1,
                                boxRight - mDp1,
                                boxBottom - mDp1,
                                mPaint
                        );
                        break;
                }
            }
        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.left = mSpaceLarge;
        outRect.right = mSpaceMedium;

        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = mSpaceMedium;
        }
        outRect.bottom = mSpaceMedium;
    }
}
