package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import model.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

class MapperUtility {

    public static final SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS",
            Locale.ENGLISH);
    private static final Gson gson = new GsonBuilder().setDateFormat(dateformat.toPattern()).create();

    static List<Log> readLogJsonStream(final InputStream in) throws IOException {
        List<Log> list = new ArrayList<>();
        JsonReader reader = new JsonReader(new InputStreamReader(in));
        reader.beginObject();
        while(!"hits".equals(reader.nextName())) {
            reader.skipValue();
        }
        reader.beginObject();
        while (!"hits".equals(reader.nextName())) {
            reader.skipValue();
        }
        reader.beginArray();
        while (reader.hasNext()) {
            reader.beginObject();
            list.add(readResponseForObject(reader));
            reader.endObject();
        }
        return list;
    }

    static Log readLogJsonAsObjectStream(final InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in));
        reader.beginObject();
        return readResponseForObject(reader);
    }

    private static Log readResponseForObject(JsonReader reader) throws IOException {
        while(!"_source".equals(reader.nextName())) {
            reader.skipValue();
        }
        return gson.fromJson(reader, new TypeToken<Log>() {}.getType());
    }

    static String writeJsonStream(final Log log) throws IOException {
        return gson.toJson(log, Log.class);
    }

    static String getLogPropertiesForCreate() {
        return  "{" +
                "\"mappings\" : {\n" +
                "        \"log\" : {\n" +
                "            \"properties\" : {\n" +
                "                \"data\" : { \"type\" : \"text\" },\n" +
                "                \"type\" : { \"type\" : \"text\" },\n" +
                "                \"result\" : { \"type\" : \"text\" },\n" +
                "                \"callstack\" : { \"type\" : \"text\" }\n" +

               // "                \"serverID\" : { \"type\" : \"number\" }\n" +

                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";
    }
}
