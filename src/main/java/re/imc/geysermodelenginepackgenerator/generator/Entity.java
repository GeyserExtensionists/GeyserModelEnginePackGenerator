package re.imc.geysermodelenginepackgenerator.generator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.zimzaza4.geyserutils.geyser.GeyserUtils;
import re.imc.geysermodelenginepackgenerator.GeneratorMain;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Entity {
    public static final String TEMPLATE = """
            {
              "format_version": "1.10.0",
              "minecraft:client_entity": {
                "description": {
                  "identifier": "modelengine:%entity_id%",
                  "materials": {
                    "default": "%material%",
                    "anim": "entity_alphatest_anim_change_color"
                  },
                  "textures": {
                  },
                  "geometry": {
                    "default": "%geometry%"
                  },
                  "animations": {
                    "look_at_target": "%look_at_target%"
                  },
                  "scripts": {
                    "animate": [
                      "look_at_target"
                    ]
                  },
                  "render_controllers": [
                  ]
                }
              }
            }
            """;


    String modelId;
    JsonObject json;
    boolean hasHeadAnimation = false;
    Animation animation;
    Geometry geometry;
    RenderController renderController;
    String path;
    Map<String, Texture> textureMap = new HashMap<>();
    TextureConfig textureConfig;


    Properties config = new Properties();



    public Entity(String modelId) {
        this.modelId = modelId;
    }

    public void modify() {

        json = new JsonParser().parse(TEMPLATE.replace("%entity_id%", modelId)
                .replace("%geometry%", "geometry.modelengine_" + modelId)
                .replace("%texture%", "textures/entity/" + path + modelId)
                .replace("%look_at_target%",  Boolean.parseBoolean(config.getProperty("head-rotation", "true".toLowerCase())) ? "animation." + modelId + ".look_at_target" : "animation.none")
                .replace("%material%", config.getProperty("material", "entity_alphatest_change_color"))).getAsJsonObject();

        JsonObject description = json.get("minecraft:client_entity").getAsJsonObject().get("description").getAsJsonObject();
        JsonObject jsonAnimations = description.get("animations").getAsJsonObject();
        JsonObject jsonTextures = description.get("textures").getAsJsonObject();
        JsonArray jsonRenderControllers = description.get("render_controllers").getAsJsonArray();


        for (String name : textureMap.keySet()) {
            jsonTextures.addProperty(name,"textures/entity/" + path + modelId + "/" + name);
            jsonRenderControllers.add("controller.render." + modelId + "_" + name);
        }

        JsonArray animate = description.get("scripts").getAsJsonObject().get("animate").getAsJsonArray();

        if (animation != null) {
            for (String animation : animation.animationIds) {
                String controller = "controller.animation." + modelId + "." + animation;
                animate.add(animation + "_control");
                jsonAnimations.addProperty(animation, "animation." + modelId + "." + animation);
                jsonAnimations.addProperty(animation + "_control", controller);
            }
        }
    }

    public void register() {

        String id = "modelengine:" + modelId;
        GeyserUtils.addCustomEntity(id);
        if (geometry == null) {
            return;
        }
        for (int i = 0; i < Math.ceil(geometry.getBones().size() / 24f); i++) {
            GeyserUtils.addProperty(id, "modelengine:bone" + i, Integer.class);
        }

        if (animation != null) {
            for (int i = 0; i < Math.ceil(animation.animationIds.size() / 24f); i++) {
                GeyserUtils.addProperty(id, "modelengine:anim" + i, Integer.class);
            }
        }
        GeyserUtils.registerProperties(id);
    }
}
