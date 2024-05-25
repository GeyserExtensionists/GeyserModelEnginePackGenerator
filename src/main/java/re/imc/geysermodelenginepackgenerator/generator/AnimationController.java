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
                    							"idle": "q.is_item_name_any('slot.armor.head', 0, 'minecraft:stone')"
                    						}
                    					]
                    				},
                    				"idle": {
                    					"animations": [
                    						"idle"
                    					],
                    					"transitions": [
                    						{
                    							"spawn": "q.is_item_name_any('slot.armor.head', 0, 'minecraft:iron_block')"
                    						},
                    						{
                    							"walk": "q.is_item_name_any('slot.armor.head', 0, 'minecraft:redstone')"
                    						},
                    						{
                    							"stop": "q.is_item_name_any('slot.armor.head', 0, 'minecraft:air')"
                    						}
                    					]
                    				},
                    				"walk": {
                    					"animations": [
                    						"walk"
                    					],
                    					"transitions": [
                    						{
                    							"spawn": "q.is_item_name_any('slot.armor.head', 0, 'minecraft:iron_block')"
                    						},
                    						{
                    							"stop": "q.is_item_name_any('slot.armor.head', 0, 'minecraft:air')"
                    						},
                    						{
                    							"idle": "q.is_item_name_any('slot.armor.head', 0, 'minecraft:stone')"
                    						}
                    					]
                    				},
                    				"stop": {
                    					"transitions": [
                    						{
                    							"idle": "q.is_item_name_any('slot.armor.head', 0, 'minecraft:stone')"
                    						},
                    						{
                    							"spawn": "q.is_item_name_any('slot.armor.head', 0, 'minecraft:iron_block')"
                    						},
                    						{
                    							"walk": "q.is_item_name_any('slot.armor.head', 0, 'minecraft:redstone')"
                    						}
                    					]
                    				}
                    			}
                    		}
                    	}
                    }""";
}
