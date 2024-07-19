package re.imc.geysermodelenginepackgenerator.generator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import re.imc.geysermodelenginepackgenerator.GeneratorMain;

import java.util.List;

public class RenderController {

    String modelId;
    List<String> bones;

    public RenderController(String modelId, List<String> bones) {
        this.modelId = modelId;
        this.bones = bones;
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
        boolean enable = Boolean.parseBoolean(entity.getProperties().getProperty("enable-part-visibility", "false"));
        if (enable) {
            JsonArray partVisibility = new JsonArray();
            JsonObject visibilityDefault = new JsonObject();
            visibilityDefault.addProperty("*", true);
            partVisibility.add(visibilityDefault);

            for (String bone : bones) {
                JsonObject visibilityItem = new JsonObject();
                visibilityItem.addProperty(bone, "query.property('" + modelId + ":" + bone + "')");
                partVisibility.add(visibilityItem);
            }
            controller.add("part_visibility", partVisibility);
        }
        return root.toString();
    }

}
