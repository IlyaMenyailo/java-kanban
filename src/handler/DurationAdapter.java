package handler;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.Duration;

public class DurationAdapter implements JsonSerializer<Duration>, JsonDeserializer<Duration> {
    @Override
    public JsonElement serialize(Duration duration, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(duration.toMinutes());
    }

    @Override
    public Duration deserialize(JsonElement json, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        return Duration.ofMinutes(json.getAsLong());
    }
}