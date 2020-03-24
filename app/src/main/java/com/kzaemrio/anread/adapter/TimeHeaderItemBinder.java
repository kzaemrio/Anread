package com.kzaemrio.anread.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.drakeet.multitype.ItemViewBinder;
import com.kzaemrio.anread.R;
import com.kzaemrio.anread.databinding.BinderTimeHeaderItemBinding;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TimeHeaderItemBinder extends ItemViewBinder<TimeHeaderItem, TimeHeaderItemBinder.Holder> {
    @NotNull
    @Override
    public Holder onCreateViewHolder(@NotNull LayoutInflater layoutInflater, @NotNull ViewGroup viewGroup) {
        return new Holder(layoutInflater.inflate(R.layout.binder_time_header_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NotNull Holder holder, TimeHeaderItem timeHeaderItem) {
        holder.mBinding.time.setText(timeHeaderItem.getTime());
    }

    public static class Holder extends RecyclerView.ViewHolder {

        private final BinderTimeHeaderItemBinding mBinding;

        public Holder(@NonNull View itemView) {
            super(itemView);
            mBinding = BinderTimeHeaderItemBinding.bind(itemView);
        }
    }
}
