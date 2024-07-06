package re.imc.geysermodelenginepackgenerator.generator;

public class AnimationController {
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
                    							"idle": "query.property('modelengine:anim_idle')"
                    						}
                    					]
                    				},
                    				"idle": {
                    					"animations": [
                    						"idle"
                    					],
                    					"transitions": [
                    						{
                    							"spawn": "query.property('modelengine:anim_spawn')"
                    						},
                    						{
                    							"walk": "query.property('modelengine:anim_walk')"
                    						},
                    						{
                    							"stop": "query.property('modelengine:anim_stop')"
                    						}
                    					]
                    				},
                    				"walk": {
                    					"animations": [
                    						"walk"
                    					],
                    					"transitions": [
                    						{
                    							"spawn": "query.property('modelengine:anim_spawn')"
                    						},
                    						{
                    							"stop": "query.property('modelengine:anim_stop')"
                    						},
                    						{
                    							"idle": "query.property('modelengine:anim_idle')"
                    						}
                    					]
                    				},
                    				"stop": {
                    					"transitions": [
                    						{
                    							"idle": "query.property('modelengine:anim_idle')"
                    						},
                    						{
                    							"spawn": "query.property('modelengine:anim_spawn')"
                    						},
                    						{
                    							"walk": "query.property('modelengine:anim_walk')"
                    						}
                    					]
                    				}
                    			}
                    		}
                    	}
                    }""";
}
