package com.kzaemrio.ithome;

import android.content.Context;
import android.content.Intent;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.core.content.ContextCompat;

import com.kzaemrio.ithome.model.Item;

import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import javax.inject.Inject;

public class WebHelper {
    @Inject
    public WebHelper() {
    }

    void shareItem(Context context, Item item) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String value = item.getTitle() + "\n---\n" + item.getLink();
        sendIntent.putExtra(Intent.EXTRA_TEXT, value);
        sendIntent.setType("text/plain");
        if (sendIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(Intent.createChooser(sendIntent, context.getString(R.string.action_share)));
        }
    }

    WebView cacheEnabledWeb(WebView webView) {
        WebSettings settings = webView.getSettings();
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        return webView;
    }

    void showDetail(WebView web, Item item) {
        String pubDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(item.getPubDate()), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm"));
        String html = "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />" +
                "<h3>" + item.getTitle() + "</h3>" +
                "<p/>" +
                String.format("<p style=\"color:#%06X\">", 0xFFFFFF & ContextCompat.getColor(web.getContext(), R.color.text_color_label)) + item.getChannelName() + "\t" + pubDate + "</p>" +
                "<p/>" +
                item.getDes();

        web.loadDataWithBaseURL(
                "file:///android_asset/",
                html,
                "text/html",
                "UTF-8",
                null
        );
    }
}
