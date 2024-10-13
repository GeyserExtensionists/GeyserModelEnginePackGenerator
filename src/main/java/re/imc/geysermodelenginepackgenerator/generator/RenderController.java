package re.imc.geysermodelenginepackgenerator.generator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import re.imc.geysermodelenginepackgenerator.GeneratorMain;

import java.util.*;

public class RenderController {

    public static final Set<String> NEED_REMOVE_WHEN_SORT = Set.of("pbody_", "plarm_", "prarm_", "plleg_", "prleg_", "phead_", "p_");
    String modelId;
    Set<String> bones;
    Entity entity;

    public RenderController(String modelId, Set<String> bones, Entity entity) {
        this.modelId = modelId;
        this.bones = bones;
        this.entity = entity;
    }

    // look, I'm fine with your other code and stuff, but I ain't using templates for JSON lmao
    public String generate() {
        JsonObject root = new JsonObject();
        root.addProperty("format_version", "1.8.0");

        JsonObject renderControllers = new JsonObject();
        root.add("render_controllers", renderControllers);

        JsonObject controller = new JsonObject();
        renderControllers.add("controller.render." + modelId, controller);

        controller.addProperty("geometry", "Geometry.default");

        JsonArray materials = new JsonArray();
        JsonObject materialItem = new JsonObject();
        materialItem.addProperty("*", "Material.default");
        materials.add(materialItem);
        controller.add("materials", materials);

        JsonArray textures = new JsonArray();
        textures.add("Texture.default");
        controller.add("textures", textures);
        Entity entity = GeneratorMain.entityMap
                .get(modelId);
        // boolean enable = Boolean.parseBoolean(entity.getConfig().getProperty("enable-part-visibility", "true"));

        // if (enable) {
        JsonArray partVisibility = new JsonArray();
        JsonObject visibilityDefault = new JsonObject();
        visibilityDefault.addProperty("*", true);
        partVisibility.add(visibilityDefault);
        int i = 0;
        List<String> sorted = new ArrayList<>(bones);
        Map<String, String> originalId = new HashMap<>();
        ListIterator<String> iterator = sorted.listIterator();
        while (iterator.hasNext()) {
            String s = iterator.next();
            String o = s;
            for (String r : NEED_REMOVE_WHEN_SORT) {
                s = s.replace(r, "");
            }
            iterator.set(s);
            originalId.put(s, o);
        }
        Collections.sort(sorted);
        for (String bone : sorted) {
            bone = originalId.get(bone);
            JsonObject visibilityItem = new JsonObject();
            int n = (int) Math.pow(2, (i % 24));
            visibilityItem.addProperty(bone, "math.mod(math.floor(query.property('modelengine:bone" + i / 24 + "') / " + n + "), 2) == 1");
            partVisibility.add(visibilityItem);
            i++;
        }
        controller.add("part_visibility", partVisibility);
        //}
        return root.toString();
    }

}
