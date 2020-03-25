package com.kzaemrio.anread.adapter;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SimpleOffsetItemDecoration extends RecyclerView.ItemDecoration {
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (parent.getAdapter() != null && parent.getAdapter().getItemCount() > 0) {
            boolean isFirst = parent.getChildAdapterPosition(view) == 0;
            boolean isLast = parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1;
            outRect.top = isFirst ? parent.getWidth() / 16 : 0;
            outRect.bottom = isLast ? parent.getWidth() / 8 : 0;
        }
    }
}
