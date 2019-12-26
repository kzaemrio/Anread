package com.kzaemrio.anread.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import com.kzaemrio.anread.R;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {

    private final int mMargin;
    private final Paint mPaint;

    public SimpleDividerItemDecoration(Context context) {
        mMargin = context.getResources().getDimensionPixelSize(R.dimen.space_small);
        mPaint = new Paint();
        mPaint.setColor(ContextCompat.getColor(context, R.color.text_color_header));
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        if (parent.getAdapter() != null && parent.getAdapter().getItemCount() > 1) {
            for (int i = 0; i < parent.getChildCount(); i++) {
                View view = parent.getChildAt(i);
                if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
                    c.drawRect(
                            mMargin,
                            view.getBottom() - 1,
                            view.getRight() - mMargin,
                            view.getBottom(),
                            mPaint
                    );
                }
            }
        }
    }
}
