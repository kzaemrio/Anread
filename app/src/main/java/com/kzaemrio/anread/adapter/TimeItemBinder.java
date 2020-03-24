package com.kzaemrio.anread.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.drakeet.multitype.ItemViewBinder;
import com.kzaemrio.anread.R;
import com.kzaemrio.anread.databinding.BinderTimeItemBinding;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TimeItemBinder extends ItemViewBinder<TimeItem, TimeItemBinder.Holder> {
    @NotNull
    @Override
    public Holder onCreateViewHolder(@NotNull LayoutInflater layoutInflater, @NotNull ViewGroup viewGroup) {
        return new Holder(layoutInflater.inflate(R.layout.binder_time_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NotNull Holder holder, TimeItem timeItem) {
        holder.mBinding.time.setText(timeItem.getTime());
        holder.mBinding.name.setText(timeItem.getChannelName());
    }

    public static class Holder extends RecyclerView.ViewHolder {

        private final BinderTimeItemBinding mBinding;

        public Holder(@NonNull View itemView) {
            super(itemView);
            mBinding = BinderTimeItemBinding.bind(itemView);
        }
    }
}
