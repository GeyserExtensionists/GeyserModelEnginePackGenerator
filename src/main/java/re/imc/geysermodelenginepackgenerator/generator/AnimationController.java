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
