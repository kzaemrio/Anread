package com.kzaemrio.anread.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kzaemrio.anread.R;
import com.kzaemrio.anread.databinding.AdapterMainItemBinding;
import com.kzaemrio.anread.model.Item;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class MainAdapter extends ListAdapter<Item, MainAdapter.Holder> {

    private Consumer<Item> mItemConsumer = item -> {
    };

    public MainAdapter() {
        super(new DiffUtil.ItemCallback<Item>() {
            @Override
            public boolean areItemsTheSame(@NonNull Item oldItem, @NonNull Item newItem) {
                return Objects.equals(oldItem.mLink, newItem.mLink);
            }

            @Override
            public boolean areContentsTheSame(@NonNull Item oldItem, @NonNull Item newItem) {
                return true;
            }
        });
    }

    public void setItemConsumer(Consumer<Item> itemConsumer) {
        mItemConsumer = itemConsumer;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.adapter_main_item,
                parent,
                false
        );
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Item item = getItem(position);
        holder.bind(item);
        holder.itemView.setOnClickListener(v -> {
            mItemConsumer.accept(item);
        });
    }

    static class Holder extends RecyclerView.ViewHolder {

        private final AdapterMainItemBinding mBind;

        private Holder(@NonNull View itemView) {
            super(itemView);
            mBind = AdapterMainItemBinding.bind(itemView);
        }

        private void bind(Item item) {
            mBind.title.setText(item.mTitle);
            mBind.label.setText(item.mChannelName);
            mBind.time.setText(item.mPubDateItem);
            mBind.content.setText(item.mDesItem);
        }
    }
}
