package re.imc.geysermodelenginepackgenerator.generator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Iterator;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Geometry {

    String modelId;
    JsonObject json;

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
                String name = element.getAsJsonObject().get("name").getAsString();
                if (name.equals("hitbox") ||
                        name.startsWith("p_") ||
                        name.startsWith("b_") ||
                        name.startsWith("ob_")) {
                    iterator.remove();
                }
            }
        }
        setId("geometry.modelengine_" + modelId);
    }
}
