package com.kzaemrio.anread.adapter;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.kzaemrio.anread.R;
import com.kzaemrio.anread.databinding.AdapterMainItemBinding;
import com.kzaemrio.anread.model.Item;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Item> mList;

    public MainAdapter(List<Item> list) {
        mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.adapter_main_item,
                parent,
                false
        )) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AdapterMainItemBinding bind = DataBindingUtil.bind(holder.itemView);

        Item item = mList.get(position);
        bind.title.setText(item.mTitle);
        bind.label.setText(item.mChannelName);
        bind.time.setText(item.mPubDate);
        bind.content.setText(Html.fromHtml(item.mDes.substring(item.mDes.indexOf("<p>") + "<p>".length(), item.mDes.indexOf("</p>"))));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
