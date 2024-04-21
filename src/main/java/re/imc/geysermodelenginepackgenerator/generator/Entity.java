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
                    "default": "animation.%entity_id%.idle",
                    "look_at_target": "%look_at_target%"
                   
                  },
                  "scripts": {
                    "animate": [
                      "default",
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

    Properties properties = new Properties();

    public Entity(String modelId) {
        this.modelId = modelId;
    }

    public void modify() {
        json = TEMPLATE.replace("%entity_id%", modelId)
                .replace("%geometry%", "geometry.modelengine_" + modelId)
                .replace("%texture%", "textures/entity/" + modelId)
                .replace("%look_at_target%",  "animation." + modelId + ".look_at_target")
                .replace("%material%", properties.getProperty("material", "entity_alphatest"))
                .replace("%render_controller%", properties.getProperty("render_controller", "controller.render.default"))


        ;
    }



}
