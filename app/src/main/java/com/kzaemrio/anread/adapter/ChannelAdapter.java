package com.kzaemrio.anread.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kzaemrio.anread.R;
import com.kzaemrio.anread.databinding.AdapterChannelBinding;
import com.kzaemrio.anread.model.Channel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.Holder> {

    private final List<Channel> mList;
    private final Consumer<Channel> mConsumer;

    public ChannelAdapter(List<Channel> list, Consumer<Channel> consumer) {
        mList = list;
        mConsumer = consumer;
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
        holder.itemView.setOnClickListener(v -> mConsumer.accept(channel));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class Holder extends RecyclerView.ViewHolder {

        private final AdapterChannelBinding mBinding;

        private Holder(@NonNull View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
        }

        private void bind(Channel channel) {
            mBinding.text.setText(channel.getTitle());
        }
    }
}
