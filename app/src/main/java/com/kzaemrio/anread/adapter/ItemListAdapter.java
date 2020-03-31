package com.kzaemrio.anread.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kzaemrio.anread.R;
import com.kzaemrio.anread.databinding.BinderContentBinding;
import com.kzaemrio.anread.databinding.BinderTimeHeaderItemBinding;
import com.kzaemrio.anread.databinding.BinderTimeItemBinding;

import java.util.Objects;

import androidx.annotation.NonNull;
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

        private TimeHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = BinderTimeItemBinding.bind(itemView);
        }

        public void bind(TimeItem item) {
            mBinding.time.setText(item.getTime());
            mBinding.name.setText(item.getChannelName());
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

        private ContentHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = BinderContentBinding.bind(itemView);
        }

        public void bind(ContentItem item, Consumer<String> clickConsumer) {
            mBinding.title.setText(item.getTitle());
            mBinding.des.setText(item.getDes());
            itemView.setOnClickListener(v -> clickConsumer.accept(item.getLink()));
        }
    }
}
