package service.simplelog;

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

    static List<SimpleLogItem> readSimpleLogJsonStream(final InputStream in, final StringBuilder scrollId)
            throws IOException {
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

    /**
     * @param reader JsonReader
     * @return SimpleLogItem entity
     * @throws IOException Exception during read
     */
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

    /**
     * @param log SimpleLog entity
     * @return Json string of log entity
     */
    static String writeJsonStream(final SimpleLog log) {
        return gson.toJson(log, SimpleLog.class);
    }

    /**
     * @param list SimpleLog list
     * @return Json string that contains that list
     */
    static String writeJsonObjectList(final List<SimpleLog> list) {
        final StringBuilder sb = new StringBuilder();
        list.forEach(log -> {
            sb.append("{ \"index\":{} }\n");
            sb.append(writeJsonStream(log)).append("\n");
        });
        sb.append("\n");
        return sb.toString();
    }

    /**
     * POST /indexname/typename/id/_update?pretty
     *
     * @param log Log entity for updating
     * @return Json string for document update
     */
    static String writeJsonStreamForUpdate(final Log log) {
        return "{\n" +
                "\"doc\" : " +
                gson.toJson(log, Log.class) + "\n" +
                "}";
    }

    /**
     * @return Json string for create SimpleLog index
     */
    static String getSimpleLogProperiesForCreate() {
        return "{" +
                "\"mappings\" : {\n" +
                "        \"log\" : {\n" +
                "            \"properties\" : {\n" +
                "                \"result\" : { \"type\" : \"text\" },\n" +
                "                \"date\" : { \"type\" : \"date\"," +
                "                             \"format\": \"yyyy-MM-dd HH:mm:ss,SSS\" }\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";
    }

    /**
     * POST /_search/scroll
     *
     * @param min Lifetime of scroll in minute
     * @param id  Id of scroll
     * @return Json string to get a scroll
     */
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
