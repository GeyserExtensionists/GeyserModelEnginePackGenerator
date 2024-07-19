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
                    							"idle": "query.property('modelengine:anim') == 2"
                    						},
                                            {
                    							"walk": "query.property('modelengine:anim') == 3"
                    						},
                    						{
                    							"stop": "query.property('modelengine:anim') == 0"
                    						}
                    					]
                    				},
                    				"idle": {
                    					"animations": [
                    						"idle"
                    					],
                    					"transitions": [
                    						{
                    							"spawn": "query.property('modelengine:anim') == 1"
                    						},
                    						{
                    							"walk": "query.property('modelengine:anim') == 3"
                    						},
                    						{
                    							"stop": "query.property('modelengine:anim') == 0"
                    						}
                    					]
                    				},
                    				"walk": {
                    					"animations": [
                    						"walk"
                    					],
                    					"transitions": [
                    						{
                    							"spawn": "query.property('modelengine:anim') == 1"
                    						},
                    						{
                    							"stop": "query.property('modelengine:anim') == 0"
                    						},
                    						{
                    							"idle": "query.property('modelengine:anim') == 2"
                    						}
                    					]
                    				},
                    				"stop": {
                    					"transitions": [
                    						{
                    							"idle": "query.property('modelengine:anim') == 2"
                    						},
                    						{
                    							"spawn": "query.property('modelengine:anim') == 1"
                    						},
                    						{
                    							"walk": "query.property('modelengine:anim') == 3"
                    						}
                    					]
                    				}
                    			}
                    		}
                    	}
                    }""";
}
