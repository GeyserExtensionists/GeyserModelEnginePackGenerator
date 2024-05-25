package re.imc.geysermodelenginepackgenerator.generator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Properties;

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
                    "default": "%material%"
                  },
                  "textures": {
                    "default": "%texture%"
                  },
                  "geometry": {
                    "default": "%geometry%"
                  },
                  "animations": {
                  
                    "idle": "animation.%entity_id%.idle",
                    "spawn": "animation.%entity_id%.%spawn%",
                    "walk": "animation.%entity_id%.%walk%",
                    "look_at_target": "%look_at_target%",
                    "modelengine_controller": "controller.animation.modelengine"
                  },
                  "scripts": {
                    "animate": [
                      "modelengine_controller",
                      "look_at_target"
                    ]
                  },
                  "render_controllers": [
                    "%render_controller%"
                  ]
                }
              }
            }
            """;


    String modelId;
    String json;
    boolean hasHeadAnimation = false;
    boolean hasWalkAnimation = false;
    boolean hasSpawnAnimation = false;
    String path;

    Properties properties = new Properties();

    public Entity(String modelId) {
        this.modelId = modelId;
    }

    public void modify() {

        String walk;
        String spawn;
        walk = spawn = "idle";
        if (hasWalkAnimation) {
            walk = "walk";
        }
        if (hasSpawnAnimation) {
            spawn = "spawn";
        }
        json = TEMPLATE.replace("%entity_id%", modelId)
                .replace("%geometry%", "geometry.modelengine_" + modelId)
                .replace("%texture%", "textures/entity/" + path + modelId)
                .replace("%look_at_target%",  "animation." + modelId + ".look_at_target")
                .replace("%walk%", walk)
                .replace("%spawn%", spawn)
                .replace("%material%", properties.getProperty("material", "entity_alphatest"))
                .replace("%render_controller%", properties.getProperty("render_controller", "controller.render.default"));


    }



}
