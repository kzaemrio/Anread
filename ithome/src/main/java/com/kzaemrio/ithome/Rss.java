package com.kzaemrio.ithome;

import android.text.TextUtils;

import com.kzaemrio.ithome.model.Item;

import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Rss {

    private static final String url = "https://www.ithome.com/rss/";

    @Inject
    OkHttpClient client;

    @Inject
    public Rss() {
    }

    public List<Item> requestItemList() {
        try {
            return streamToItemList(networkInputStream(url));
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Item> streamToItemList(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        ParseHandler parseHandler = new ParseHandler(url);
        parser.parse(inputStream, parseHandler);
        return parseHandler.getItemArray();
    }

    public InputStream networkInputStream(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        return Objects.requireNonNull(response.body()).byteStream();
    }

    private static class ParseHandler extends DefaultHandler {

        private final String mChannelUrl;
        private final Box mBox;

        private boolean item;
        private boolean title;
        private boolean link;
        private boolean description;
        private boolean pubDate;

        private String mChannelName;
        private final LinkedList<Item> mList = new LinkedList<>();
        private Item.Builder mBuilder;

        public ParseHandler(String channelUrl) {
            mChannelUrl = channelUrl;
            mBox = new Box();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            switch (qName) {
                case "item":
                    if (mBuilder == null) {
                        mBuilder = Item.Builder.create().withChannelUrl(mChannelUrl);
                    }
                    item = true;
                    break;
                case "title":
                    title = true;
                    break;
                case "link":
                    link = true;
                    break;
                case "description":
                    description = true;
                    break;
                case "pubDate":
                    pubDate = true;
                    break;
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            if (item) {
                if (title || link || description || pubDate) {
                    mBox.append(String.valueOf(ch, start, length));
                }
            } else {
                if (mChannelName == null && title) {
                    mBox.append(String.valueOf(ch, start, length));
                }
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            switch (qName) {
                case "item":
                    if (!mBuilder.isSkip()) {
                        mList.add(mBuilder.withChannelName(mChannelName).build());
                    }
                    item = false;
                    break;
                case "title":
                    if (mChannelName == null) {
                        mChannelName = mBox.flush();
                    } else {
                        if (item) {
                            mBuilder.withTitle(mBox.flush());
                        }
                    }
                    title = false;
                    break;
                case "link":
                    if (item) {
                        mBuilder.withLink(mBox.flush());
                    }
                    link = false;
                    break;
                case "description":
                    if (item) {
                        mBuilder.withDes(mBox.flush());
                    }
                    description = false;
                    break;
                case "pubDate":
                    if (item) {
                        mBuilder.withPubDate(getPubDate(mBox.flush()));
                    }
                    pubDate = false;
                    break;
            }
        }

        public List<Item> getItemArray() {
            return mList;
        }

        static long getPubDate(String trim) {
            ZonedDateTime originalZonedDateTime = getZonedDateTime(trim);
            ZonedDateTime fixedZonedDateTime = originalZonedDateTime.withZoneSameInstant(ZoneId.systemDefault());
            return fixedZonedDateTime.toInstant().toEpochMilli();
        }


        static ZonedDateTime getZonedDateTime(String time) {
            try {
                return ZonedDateTime.parse(time, DateTimeFormatter.ISO_DATE);
            } catch (Exception e) {
                return ZonedDateTime.parse(time, DateTimeFormatter.RFC_1123_DATE_TIME);
            }
        }


        static class Box {
            private final List<String> mList = new LinkedList<>();
            private int mSize;

            public void append(String text) {
                if (!TextUtils.isEmpty(text)) {
                    mList.add(text);
                    mSize += text.length();
                }
            }

            public String flush() {
                StringBuilder builder = new StringBuilder(mSize);
                for (String s : mList) {
                    builder.append(s);
                }

                mList.clear();
                mSize = 0;
                return builder.toString();
            }
        }
    }
}
