package re.imc.geysermodelenginepackgenerator.generator;

import com.google.gson.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.geysermc.geyser.api.extension.ExtensionLogger;
import re.imc.geysermodelenginepackgenerator.ExtensionMain;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Geometry {


    String modelId;
    String geometryId;
    JsonObject json;
    Map<String, Bone> bones = new HashMap<>();

    String path;
    public void load(String json) {
        this.json = new JsonParser().parse(json).getAsJsonObject();
    }
    public void setId(String id) {
        geometryId = id;
        getInternal().get("description").getAsJsonObject().addProperty("identifier", id);
    }

    public void setTextureWidth(int w) {
        getInternal().get("description").getAsJsonObject().addProperty("texture_width", w);
    }

    public void setTextureHeight(int h) {
        getInternal().get("description").getAsJsonObject().addProperty("texture_height", h);
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

                String parent = element.getAsJsonObject().has("parent") ? element.getAsJsonObject().get("parent").getAsString() : null;
                element.getAsJsonObject().remove("name");

                element.getAsJsonObject().addProperty("name", name);

                if (name.equals("hitbox") ||
                        name.equals("shadow") ||
                        name.equals("mount") ||
                        name.startsWith("b_") ||
                        name.startsWith("ob_")) {
                    iterator.remove();
                } else bones.put(name, new Bone(name, parent, new HashSet<>(), new HashSet<>()));
            }

            for (Bone bone : bones.values()) {
                if (bone.parent != null) {
                    Bone parent = bones.get(bone.parent);
                    if (parent != null) {
                        parent.children.add(bone);
                        addAllChildren(parent, bone);
                    }
                }
            }
        }
        setId("geometry.meg_" + modelId);
    }

    public void addAllChildren(Bone p, Bone c) {
        p.allChildren.add(c);
        Bone parent = bones.get(p.parent);
        if (parent != null) {
            addAllChildren(parent, c);
        }
    }
}
