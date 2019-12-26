package com.kzaemrio.anread.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kzaemrio.anread.R;
import com.kzaemrio.anread.databinding.AdapterSubscriptionBinding;
import com.kzaemrio.anread.model.Subscription;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.Holder> {

    private final List<Subscription> mList;
    private final Consumer<Subscription> mConsumer;

    public SubscriptionAdapter(List<Subscription> list, Consumer<Subscription> consumer) {
        mList = list;
        mConsumer = consumer;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.adapter_subscription,
                parent,
                false
        );
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Subscription subscription = mList.get(position);
        holder.bind(subscription);
        holder.itemView.setOnClickListener(v -> mConsumer.accept(subscription));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class Holder extends RecyclerView.ViewHolder {

        private final AdapterSubscriptionBinding mBinding;

        private Holder(@NonNull View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
        }

        private void bind(Subscription subscription) {
            mBinding.text.setText(subscription.getTitle());
        }
    }
}
