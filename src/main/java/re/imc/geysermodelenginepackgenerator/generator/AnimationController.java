package re.imc.geysermodelenginepackgenerator.generator;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnimationController {

    public static final String CONTROLLER_TEMPLATE =
            """
                   {
                       "initial_state": "stop",
                        "states": {
                             "play": {
                                  "animations": [
                                       "%anim%"
                                  ],
                                  "blend_transition": 0.1,
                                  "transitions": [{ "stop": "%query% == 0"}]
                             },
                             "stop": {
                                  "blend_transition": 0.1,
                                  "transitions": [{ "play": "%query% != 0"}]
                             }
                        }
                     }""";
    public static final String TEMPLATE =
            """
                    {
                    	"format_version": "1.10.0",
                    	"animation_controllers": {
                    		"controller.animation.modelengine": {
                    			"initial_state": "spawn",
                    			"states": {
                    				"spawn": {
                    					"animations": [
                    						"spawn"
                    					],
                    					"transitions": [
                    						{
                    							"idle": "q.variant == 1"
                    						}
                    					],
                    					"blend_transition": 0.2
                    				},
                    				"idle": {
                    					"animations": [
                    						"idle"
                    					],
                    					"transitions": [
                    						{
                    							"spawn": "q.variant == 0"
                    						},
                    						{
                    							"walk": "q.variant == 2"
                    						},
                    						{
                    							"stop": "q.variant == 3"
                    						}
                    					],
                    					"blend_transition": 0.2
                    				},
                    				"walk": {
                    					"animations": [
                    						"walk"
                    					],
                    					"transitions": [
                    						{
                    							"spawn": "q.variant == 0"
                    						},
                    						{
                    							"stop": "q.variant == 3"
                    						},
                    						{
                    							"idle": "q.variant == 1"
                    						}
                    					],
                    					"blend_transition": 0.2
                    				},
                    				"stop": {
                    					"transitions": [
                    						{
                    							"idle": "q.variant == 1"
                    						},
                    						{
                    							"spawn": "q.variant == 0"
                    						},
                    						{
                    							"walk": "q.variant == 2"
                    						}
                    					],
                    					"blend_transition": 0.2
                    				}
                    			}
                    		}
                    	}
                    }""";

    @Getter
    JsonObject json;

    public void load(Animation animation) {
        JsonObject root = new JsonObject();
        json = root;
        root.addProperty("format_version", "1.10.0");

        JsonObject animationControllers = new JsonObject();
        root.add("animation_controllers", animationControllers);

        List<String> sorted = new ArrayList<>(animation.animationIds);
        int i = 0;

        Collections.sort(sorted);
        for (String id : sorted) {

            int n = (int) Math.pow(2, (i % 24));
            JsonObject controller = new JsonParser().parse(CONTROLLER_TEMPLATE.replace("%anim%", id).replace("%query%", "math.mod(math.floor(query.property('modelengine:anim" + i / 24 + "') / " + n + "), 2)")).getAsJsonObject();
            animationControllers.add("controller.animation." + animation.modelId + "." + id, controller);
            i++;
        }
    }

    /*
    public static final String TEMPLATE =
            """
                    {
                    	"format_version": "1.10.0",
                    	"animation_controllers": {
                    		"controller.animation.modelengine": {
                    			"initial_state": "spawn",
                    			"states": {
                    				"spawn": {
                    					"animations": [
                    						"spawn"
                    					],
                    					"transitions": [
                    						{
                    							"idle": "q.variant == 1"
                    						}
                    					]
                    				},
                    				"idle": {
                    					"animations": [
                    						"idle"
                    					],
                    					"transitions": [
                    						{
                    							"spawn": "q.variant == 0"
                    						},
                    						{
                    							"walk": "q.variant == 2"
                    						},
                    						{
                    							"stop": "q.variant == 3"
                    						}
                    					]
                    				},
                    				"walk": {
                    					"animations": [
                    						"walk"
                    					],
                    					"transitions": [
                    						{
                    							"spawn": "q.variant == 0"
                    						},
                    						{
                    							"stop": "q.variant == 3"
                    						},
                    						{
                    							"idle": "q.variant == 1"
                    						}
                    					]
                    				},
                    				"stop": {
                    					"transitions": [
                    						{
                    							"idle": "q.variant == 1"
                    						},
                    						{
                    							"spawn": "q.variant == 0"
                    						},
                    						{
                    							"walk": "q.variant == 2"
                    						}
                    					]
                    				}
                    			}
                    		}
                    	}
                    }""";

     */
}
