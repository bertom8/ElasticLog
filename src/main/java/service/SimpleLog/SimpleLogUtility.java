package service.SimpleLog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import model.Log;
import model.SimpleLog;
import model.SimpleLogItem;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class SimpleLogUtility {
    static final SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS",
            Locale.ENGLISH);
    private static final Gson gson = new GsonBuilder().setDateFormat(dateformat.toPattern()).create();


    static List<SimpleLogItem> readSimpleLogJsonStream(final InputStream in) throws IOException {
        final List<SimpleLogItem> list = new ArrayList<>();
        final JsonReader reader = new JsonReader(new InputStreamReader(in));
        reader.beginObject();
        while (!"hits".equals(reader.nextName())) {
            reader.skipValue();
        }
        reader.beginObject();
        while (!"hits".equals(reader.nextName())) {
            reader.skipValue();
        }
        reader.beginArray();
        while (reader.hasNext()) {
            reader.beginObject();
            list.add(readResponseForSimpleLogObject(reader));
            reader.endObject();
        }
        return list;
    }


    static List<SimpleLogItem> readSimpleLogJsonStream(final InputStream in, final StringBuilder scrollId) throws IOException {
        final List<SimpleLogItem> list = new ArrayList<>();
        final JsonReader reader = new JsonReader(new InputStreamReader(in));
        reader.beginObject();
        if (scrollId != null) {
            if ("_scroll_id".equals(reader.nextName())) {
                scrollId.append(reader.nextString());
            }
        }
        while (!"hits".equals(reader.nextName())) {
            reader.skipValue();
        }
        reader.beginObject();
        while (!"hits".equals(reader.nextName())) {
            reader.skipValue();
        }
        reader.beginArray();
        while (reader.hasNext()) {
            reader.beginObject();
            list.add(readResponseForSimpleLogObject(reader));
            reader.endObject();
        }
        return list;
    }

    private static SimpleLogItem readResponseForSimpleLogObject(final JsonReader reader) throws IOException {
        final SimpleLogItem item = new SimpleLogItem();
        //ID
        while (!"_id".equals(reader.nextName())) {
            reader.skipValue();
        }
        item.setId(reader.nextString());

        //Source
        while (!"_source".equals(reader.nextName())) {
            reader.skipValue();
        }
        item.setLog(gson.fromJson(reader, new TypeToken<SimpleLog>() {
        }.getType()));
        reader.nextName();
        reader.skipValue();
        return item;
    }

    /**
     * @param in InputStream
     * @return a SimpleLogItem
     * @throws IOException if something happened during reading
     */
    static SimpleLogItem readSimpleLogJsonAsObjectStream(final InputStream in) throws IOException {
        final JsonReader reader = new JsonReader(new InputStreamReader(in));
        reader.beginObject();
        return readResponseForSimpleLogObject(reader);
    }

    static String writeJsonStream(final SimpleLog log) {
        return gson.toJson(log, SimpleLog.class);
    }

    static String writeJsonStreamForUpdate(final Log log) {
        return "{\n" +
                "\"doc\" : " +
                gson.toJson(log, Log.class) + "\n" +
                "}";
    }

    static String getSimpleLogProperiesForCreate() {
        return "{" +
                "\"mappings\" : {\n" +
                "        \"log\" : {\n" +
                "            \"properties\" : {\n" +
                "                \"text\" : { \"type\" : \"text\" },\n" +
                "                \"date\" : { \"type\" : \"date\"," +
                "                             \"format\": \"yyyy-MM-dd HH:mm:ss,SSS\" }\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";
    }

    static String getScrollJSON(final int min, final String id) {
        return "{\n" +
                "    \"scroll\" : \"" + min + "m\", \n" +
                "    \"scroll_id\" : \"" + id + "\" \n" +
                "}";
    }

    /**
     * GET serverlog/log/_search?scroll=10m
     */
    static String getSearchJSON(final int size) {
        return "{\n" +
                "  \"sort\": [\n" +
                "    {\n" +
                "      \"date\": {\n" +
                "        \"order\": \"asc\"\n" +
                "      }\n" +
                "    }\n" +
                "  ], \n" +
                "  \"query\": {\n" +
                "    \"match_all\": {}\n" +
                "  }\n" +
                "  , \"size\": " + size + "\n" +
                "}";
    }

    /**
     * PUT /serverlog/_mapping/log
     */
    static String getModifyIndexForLog() {
        return "{\n" +
                "    \"log\": {\n" +
                "      \"properties\": {\n" +
                "        \"type\" : { \"type\" : \"text\" },\n" +
                "        \"callstack\" : { \"type\" : \"text\"}\n" +
                "      }\n" +
                "    }\n" +
                "}";
    }
}
