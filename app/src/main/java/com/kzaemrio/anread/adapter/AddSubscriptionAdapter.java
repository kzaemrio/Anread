package com.kzaemrio.anread.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.kzaemrio.anread.R;
import com.kzaemrio.anread.databinding.AdapterAddChannelBinding;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class AddSubscriptionAdapter extends SingleViewAdapter {

    private final Runnable mOnAddSubscriptionClickAction;

    public AddSubscriptionAdapter(Runnable onAddSubscriptionClickAction) {
        super(R.layout.adapter_add_channel);
        mOnAddSubscriptionClickAction = onAddSubscriptionClickAction;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = super.onCreateViewHolder(parent, viewType);
        AdapterAddChannelBinding bind = DataBindingUtil.bind(holder.itemView);
        Objects.requireNonNull(bind);
        View.OnClickListener onClickListener = v -> mOnAddSubscriptionClickAction.run();
        bind.text.setOnClickListener(onClickListener);
        bind.icon.setOnClickListener(onClickListener);
        return holder;
    }
}
