package re.imc.geysermodelenginepackgenerator.generator;

import com.google.gson.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Geometry {

    String modelId;
    JsonObject json;
    List<String> bones = new ArrayList<>();

    String path;
    public void load(String json) {
        this.json = new JsonParser().parse(json).getAsJsonObject();
    }
    public void setId(String id) {
        getInternal().get("description").getAsJsonObject().addProperty("identifier", id);
    }

    public JsonObject getInternal() {
        return json.get("minecraft:geometry").getAsJsonArray().get(0)
                .getAsJsonObject();
    }

    public void modify() {

        JsonArray array = getInternal().get("bones").getAsJsonArray();
        Iterator<JsonElement> iterator = array.iterator();
        while (iterator.hasNext()) {
            JsonElement element = iterator.next();
            if (element.isJsonObject()) {
                String name = element.getAsJsonObject().get("name").getAsString().toLowerCase(Locale.ROOT);

                element.getAsJsonObject().remove("name");
                element.getAsJsonObject().addProperty("name", name);

                if (name.equals("hitbox") ||
                        name.equals("mount") ||
                        name.startsWith("p_") ||
                        name.startsWith("b_") ||
                        name.startsWith("ob_")) {
                    iterator.remove();
                } else bones.add(name);
            }
        }
        setId("geometry.modelengine_" + modelId);
    }

}
