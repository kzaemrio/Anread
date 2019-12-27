package com.kzaemrio.anread.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kzaemrio.anread.R;
import com.kzaemrio.anread.databinding.AdapterChannelBinding;
import com.kzaemrio.anread.model.Channel;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.Holder> {

    private final List<Channel> mList;
    private final Consumer<Channel> mItemClickConsumer;
    private final Consumer<Channel> mDeleteClickConsumer;

    public ChannelAdapter(List<Channel> list, Consumer<Channel> itemClickConsumer, Consumer<Channel> longClickConsumer) {
        mList = list;
        mItemClickConsumer = itemClickConsumer;
        mDeleteClickConsumer = longClickConsumer;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.adapter_channel,
                parent,
                false
        );
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Channel channel = mList.get(position);
        holder.bind(channel);
        holder.itemView.setOnClickListener(v -> mItemClickConsumer.accept(channel));
        holder.delete.setOnClickListener(v -> mDeleteClickConsumer.accept(channel));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class Holder extends RecyclerView.ViewHolder {

        private final AdapterChannelBinding mBinding;
        public final View delete;

        private Holder(@NonNull View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
            Objects.requireNonNull(mBinding);
            delete = mBinding.delete;
        }

        private void bind(Channel channel) {
            mBinding.text.setText(channel.getTitle());
        }
    }
}
