package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class MapperUtility {

    private static final Gson gson = new GsonBuilder().create();

    public static List<Log> readJsonStream(final InputStream in) throws IOException {
        return gson.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8),
                new TypeToken<List<Log>>() {
                }.getType());
    }

    public static Log readJsonAsObjectStream(final InputStream in) throws IOException {
        return gson.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8),
                new TypeToken<Log>() {
                }.getType());
    }

    public static Map writeJsonStream(final Log log) throws IOException {
        return new ObjectMapper().convertValue(log, Map.class);
        /*gson.toJsonTree(log, Log.class);
        final String s = gson.toJson(log, Log.class);
        return null;*/
    }


}
