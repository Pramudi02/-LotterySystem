package protocol;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Map;

public class MessageParser {
    private static final Gson gson = new Gson();

    public static Request parseRequest(String json) {
        return gson.fromJson(json, Request.class);
    }

    public static Response parseResponse(String json) {
        return gson.fromJson(json, Response.class);
    }

    public static String toJson(Request request) {
        return gson.toJson(request);
    }

    public static String toJson(Response response) {
        return gson.toJson(response);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }

    public static Map<String, Object> parseJsonObject(String json) {
        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        return gson.fromJson(json, type);
    }
}
