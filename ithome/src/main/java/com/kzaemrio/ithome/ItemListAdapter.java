package com.kzaemrio.ithome;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.kzaemrio.ithome.xml.XMLLexer;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;
import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Objects;

public class ItemListAdapter extends ListAdapter<ItemListAdapter.ViewItem, ItemListAdapter.Holder> {

    private Consumer<Item> mItemConsumer = s -> {
    };

    public ItemListAdapter() {
        super(new DiffUtil.ItemCallback<ViewItem>() {
            @Override
            public boolean areItemsTheSame(@NonNull ViewItem oldViewItem, @NonNull ViewItem newViewItem) {
                return Objects.equals(oldViewItem.mItem.getLink(), newViewItem.mItem.getLink());
            }

            @Override
            public boolean areContentsTheSame(@NonNull ViewItem oldViewItem, @NonNull ViewItem newViewItem) {
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
        return Holder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        ViewItem viewItem = getItem(position);
        holder.bind(viewItem);
        holder.itemView.setOnClickListener(v -> mItemConsumer.accept(viewItem.mItem));
    }

    public static class Holder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView time;
        private final TextView label;
        private final TextView content;

        public Holder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            time = itemView.findViewById(R.id.time);
            label = itemView.findViewById(R.id.label);
            content = itemView.findViewById(R.id.content);
        }

        public static Holder create(ViewGroup parent) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.binder_content,
                    parent,
                    false
            ));
        }

        public void bind(ViewItem viewItem) {
            title.setText(viewItem.mTitle);
            time.setText(viewItem.mTime);
            label.setText(viewItem.mLabel);
            content.setText(viewItem.mContent);
        }
    }

    public static class ViewItem {
        private final Item mItem;
        private final String mTime;
        private final String mLabel;
        private final String mTitle;
        private final String mContent;

        private ViewItem(Item item, String time, String label, String title, String content) {
            mItem = item;
            mTime = time;
            mLabel = label;
            mTitle = title;
            mContent = content;
        }

        public Item getItem() {
            return mItem;
        }

        public static ViewItem create(Item item) {
            return new ViewItem(
                    item,
                    ZonedDateTime.ofInstant(Instant.ofEpochMilli(item.getPubDate()), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("HH:mm")),
                    item.getChannelName(),
                    item.getTitle(),
                    parseItemDes(item.getDes())
            );
        }
    }

    private static String parseItemDes(String des) {
        StringBuilder builder = new StringBuilder();
        TokenSource lexer = new XMLLexer(CharStreams.fromString(des));

        for (Token token = lexer.nextToken(); token.getType() != Token.EOF; token = lexer.nextToken()) {
            if (token.getType() == XMLLexer.TEXT) {
                builder.append(token.getText());
            }
            if (builder.length() > 50) {
                break;
            }
        }
        return builder.toString();
    }
}
