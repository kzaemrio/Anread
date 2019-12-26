package com.kzaemrio.anread.adapter;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kzaemrio.anread.R;
import com.kzaemrio.anread.databinding.AdapterMainItemBinding;
import com.kzaemrio.anread.model.Item;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.Holder> {

    private final List<Item> mList;
    private final Consumer<Item> mItemConsumer;

    public MainAdapter(List<Item> list, Consumer<Item> itemConsumer) {
        mList = list;
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
        Item item = mList.get(position);
        holder.bind(item);
        holder.itemView.setOnClickListener(v -> {
            mItemConsumer.accept(item);
            item.mIsRead = 1;
            holder.bind(item);
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class Holder extends RecyclerView.ViewHolder {

        private final AdapterMainItemBinding mBind;

        private Holder(@NonNull View itemView) {
            super(itemView);
            mBind = DataBindingUtil.bind(itemView);
        }

        private void bind(Item item) {
            mBind.title.setText(item.mTitle);
            mBind.label.setText(item.mChannelName);
            mBind.time.setText(item.mPubDate);
            mBind.content.setText(Html.fromHtml(item.mDes.substring(item.mDes.indexOf("<p>") + "<p>".length(), item.mDes.indexOf("</p>"))));

            boolean notRead = item.mIsRead == 0;

            color(mBind.title, notRead, R.color.text_color_title);
            color(mBind.label, notRead, R.color.colorAccent);
            color(mBind.time, notRead, R.color.text_color_title);
            color(mBind.content, notRead, R.color.text_color_content);
        }

        private void color(TextView title, boolean notRead, int colorId) {
            int id = notRead ? colorId : R.color.text_color_header;
            int color = ContextCompat.getColor(title.getContext(), id);
            title.setTextColor(color);
        }
    }
}
