package com.kzaemrio.anread.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.ReplacementSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kzaemrio.anread.R;
import com.kzaemrio.anread.databinding.BinderContentBinding;
import com.kzaemrio.anread.databinding.BinderTimeHeaderItemBinding;
import com.kzaemrio.anread.databinding.BinderTimeItemBinding;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class ItemListAdapter extends ListAdapter<StrId, RecyclerView.ViewHolder> {

    private Consumer<String> mItemConsumer = s -> {
    };

    public ItemListAdapter() {
        super(new DiffUtil.ItemCallback<StrId>() {
            @Override
            public boolean areItemsTheSame(@NonNull StrId oldItem, @NonNull StrId newItem) {
                return Objects.equals(oldItem.strId(), newItem.strId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull StrId oldItem, @NonNull StrId newItem) {
                return true;
            }
        });
    }

    public void setItemConsumer(Consumer<String> itemConsumer) {
        mItemConsumer = itemConsumer;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).type();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TimeHeaderItem.TYPE:
                return TimeHeaderHolder.create(parent);
            case TimeItem.TYPE:
                return TimeHolder.create(parent);
            default:
                return ContentHolder.create(parent);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TimeHeaderItem.TYPE:
                ((TimeHeaderHolder) holder).bind(((TimeHeaderItem) getItem(position)));
                break;
            case TimeItem.TYPE:
                ((TimeHolder) holder).bind(((TimeItem) getItem(position)));
                break;
            default:
                ((ContentHolder) holder).bind(((ContentItem) getItem(position)), mItemConsumer);
        }
    }

    private static class TimeHeaderHolder extends RecyclerView.ViewHolder {

        public static RecyclerView.ViewHolder create(ViewGroup parent) {
            return new TimeHeaderHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.binder_time_header_item,
                    parent,
                    false
            ));
        }

        private final BinderTimeHeaderItemBinding mBinding;

        private TimeHeaderHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = BinderTimeHeaderItemBinding.bind(itemView);
        }

        public void bind(TimeHeaderItem item) {
            mBinding.time.setText(item.getTime());
        }
    }

    private static class TimeHolder extends RecyclerView.ViewHolder {

        public static RecyclerView.ViewHolder create(ViewGroup parent) {
            return new TimeHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.binder_time_item,
                    parent,
                    false
            ));
        }

        private final BinderTimeItemBinding mBinding;
        private final SpannableStringBuilder mBuilder;
        private final CharacterStyle mTimeColor;
        private final CharacterStyle mChannelColor;
        private final CharacterStyle mSpaceSize;

        private TimeHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = BinderTimeItemBinding.bind(itemView);
            mBuilder = new SpannableStringBuilder();
            Context context = itemView.getContext();
            mTimeColor = new ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorAccent));
            mChannelColor = new ForegroundColorSpan(ContextCompat.getColor(context, R.color.text_color_header));
            mSpaceSize = new SpaceSpan(context.getResources().getDimensionPixelSize(R.dimen.space_small), 0);
        }

        public void bind(TimeItem item) {
            String time = item.getTime();
            String channelName = item.getChannelName();

            mBuilder.clear();
            mBuilder.append(time);
            mBuilder.append("\0");
            mBuilder.append(channelName);
            mBuilder.setSpan(mTimeColor, 0, time.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mBuilder.setSpan(mChannelColor, mBuilder.length() - channelName.length(), mBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mBuilder.setSpan(mSpaceSize, time.length(), time.length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            mBinding.text.setText(mBuilder);
        }
    }

    private static class ContentHolder extends RecyclerView.ViewHolder {

        public static RecyclerView.ViewHolder create(ViewGroup parent) {
            return new ContentHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.binder_content,
                    parent,
                    false
            ));
        }

        private final BinderContentBinding mBinding;
        private final SpannableStringBuilder mBuilder;
        private final CharacterStyle mTitleColor;
        private final CharacterStyle mContentColor;
        private final CharacterStyle mTitleSize;
        private final CharacterStyle mContentSize;
        private final CharacterStyle mSpaceSize;

        private ContentHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = BinderContentBinding.bind(itemView);
            mBuilder = new SpannableStringBuilder();
            Context context = itemView.getContext();
            mTitleColor = new ForegroundColorSpan(ContextCompat.getColor(context, R.color.text_color_title));
            mContentColor = new ForegroundColorSpan(ContextCompat.getColor(context, R.color.text_color_content));
            mTitleSize = new AbsoluteSizeSpan(context.getResources().getDimensionPixelSize(R.dimen.text_size_title));
            mContentSize = new AbsoluteSizeSpan(context.getResources().getDimensionPixelSize(R.dimen.text_size_content));
            mSpaceSize = new SpaceSpan(0, context.getResources().getDimensionPixelSize(R.dimen.space_small));
        }

        public void bind(ContentItem item, Consumer<String> clickConsumer) {
            String title = item.getTitle();
            String des = item.getDes();

            mBuilder.clear();
            mBuilder.append(title);
            mBuilder.append("\n");
            mBuilder.append("\0");
            mBuilder.append(des);
            mBuilder.setSpan(mTitleColor, 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mBuilder.setSpan(mTitleSize, 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            mBuilder.setSpan(mContentColor, title.length(), mBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mBuilder.setSpan(mContentSize, title.length(), mBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mBuilder.setSpan(mSpaceSize, mBuilder.length() - des.length() - 1, mBuilder.length() - des.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            mBinding.text.setText(mBuilder);
            itemView.setOnClickListener(v -> clickConsumer.accept(item.getLink()));
        }
    }

    private static class SpaceSpan extends ReplacementSpan {

        private final int mWidth;
        private final int mHeight;

        public SpaceSpan(int width, int height) {
            mWidth = width;
            mHeight = height;
        }

        @Override
        public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
            if (fm != null) {
                fm.ascent = -mHeight;
                fm.top = -mHeight;
            }
            return mWidth;
        }

        @Override
        public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        }
    }
}
