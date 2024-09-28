package re.imc.geysermodelenginepackgenerator.generator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import re.imc.geysermodelenginepackgenerator.GeneratorMain;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Animation {

    public static final String HEAD_TEMPLATE = """
             {
               "relative_to" : {
                 "rotation" : "entity"
               },
               "rotation" : [ "query.target_x_rotation - this", "query.target_y_rotation - this", 0.0 ]
            }
            """;

    String modelId;
    JsonObject json;
    @Getter
    Set<String> animationIds = new HashSet<>();

    String path;

    public void load(String string) {
        this.json = new JsonParser().parse(string).getAsJsonObject();
        JsonObject newAnimations = new JsonObject();
        for (Map.Entry<String, JsonElement> element : json.get("animations").getAsJsonObject().entrySet()) {
            animationIds.add(element.getKey());
            JsonObject animation = element.getValue().getAsJsonObject();

            if (animation.has("override_previous_animation")) {
                if (animation.get("override_previous_animation").getAsBoolean()) {
                    if (!animation.has("loop")) {
                        animation.addProperty("loop", "hold_on_last_frame");
                        // play once but override must use this to avoid strange anim
                    }
                }
            }

            if (animation.has("loop")) {
                if (animation.get("loop").getAsJsonPrimitive().isString()) {
                    if (animation.get("loop").getAsString().equals("hold_on_last_frame")) {
                        if (!animation.has("bones")) {
                            continue;
                        }
                        for (Map.Entry<String, JsonElement> bone : animation.get("bones").getAsJsonObject().entrySet()) {

                            for (Map.Entry<String, JsonElement> anim : bone.getValue().getAsJsonObject().entrySet()) {
                                float max = -1;
                                JsonObject end = null;
                                if (!anim.getValue().isJsonObject()) {
                                    continue;
                                }
                                try {
                                    for (Map.Entry<String, JsonElement> timeline : anim.getValue().getAsJsonObject().entrySet()) {
                                        float time = Float.parseFloat(timeline.getKey());
                                        if (time > max) {
                                            max = time;
                                            if (timeline.getValue().isJsonObject()) {
                                                end = timeline.getValue().getAsJsonObject();
                                            }
                                        }
                                    }
                                } catch (Throwable t) {}
                                if (end != null && end.get("lerp_mode").getAsString().equals("catmullrom")) {
                                    end.addProperty("lerp_mode", "linear");
                                }
                            }
                        }
                    }
                }
            }

            newAnimations.add("animation." + modelId + "." + element.getKey(), element.getValue());
        }
        json.add("animations", newAnimations);

    }

    public void addHeadBind(Geometry geometry) {
        JsonObject object = new JsonObject();
        object.addProperty("loop", true);
        JsonObject bones = new JsonObject();
        JsonArray array = geometry.getInternal().get("bones").getAsJsonArray();
        int i = 0;
        for (JsonElement element : array) {
            if (element.isJsonObject()) {
                String name = element.getAsJsonObject().get("name").getAsString();

                String parent = "";
                if (element.getAsJsonObject().has("parent")) {
                    parent = element.getAsJsonObject().get("parent").getAsString();
                }
                if (parent.startsWith("h_") || parent.startsWith("hi_")) {
                    continue;
                }
                if (name.startsWith("h_") || name.startsWith("hi_")) {
                    bones.add(name, new JsonParser().parse(HEAD_TEMPLATE));
                    i++;
                }
            }
        }
        if (i == 0) {
            return;
        }
        GeneratorMain.entityMap
                        .get(modelId).setHasHeadAnimation(true);

        object.add("bones", bones);
        json.get("animations").getAsJsonObject().add("animation." + modelId + ".look_at_target", object);
    }
}
