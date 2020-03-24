package com.kzaemrio.anread.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.drakeet.multitype.ItemViewBinder;
import com.kzaemrio.anread.R;
import com.kzaemrio.anread.databinding.BinderContentBinding;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

public class ContentItemBinder extends ItemViewBinder<ContentItem, ContentItemBinder.Holder> {

    private final Consumer<String> mItemConsumer;

    public ContentItemBinder() {
        this(item -> {
        });
    }

    public ContentItemBinder(Consumer<String> itemConsumer) {
        mItemConsumer = itemConsumer;
    }

    @NotNull
    @Override
    public Holder onCreateViewHolder(@NotNull LayoutInflater layoutInflater, @NotNull ViewGroup viewGroup) {
        return new Holder(layoutInflater.inflate(R.layout.binder_content, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NotNull Holder holder, ContentItem contentItem) {
        holder.mBinding.title.setText(contentItem.getTitle());
        holder.mBinding.des.setText(contentItem.getDes());
        holder.itemView.setOnClickListener(v -> mItemConsumer.accept(contentItem.getLink()));
    }

    public static class Holder extends RecyclerView.ViewHolder {

        private final BinderContentBinding mBinding;

        public Holder(@NonNull View itemView) {
            super(itemView);
            mBinding = BinderContentBinding.bind(itemView);
        }
    }
}
