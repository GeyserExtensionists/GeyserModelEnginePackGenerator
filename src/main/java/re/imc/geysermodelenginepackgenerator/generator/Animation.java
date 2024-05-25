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
import java.util.Iterator;
import java.util.Map;

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

    String path;

    public void load(String json) {
        this.json = new JsonParser().parse(json).getAsJsonObject();
    }

    public void modify() {
        JsonObject newAnimations = new JsonObject();
        for (Map.Entry<String, JsonElement> element : json.get("animations").getAsJsonObject().entrySet()) {
            if (element.getKey().equals("spawn")) {
                GeneratorMain.entityMap
                        .get(modelId).setHasSpawnAnimation(true);
            }
            if (element.getKey().equals("walk")) {
                GeneratorMain.entityMap
                        .get(modelId).setHasWalkAnimation(true);
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
