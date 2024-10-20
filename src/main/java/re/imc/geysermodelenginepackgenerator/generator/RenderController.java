package re.imc.geysermodelenginepackgenerator.generator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.*;

public class RenderController {

    public static final Set<String> NEED_REMOVE_WHEN_SORT = Set.of("pbody_", "plarm_", "prarm_", "plleg_", "prleg_", "phead_", "p_");
    String modelId;
    Map<String, Bone> bones;
    Entity entity;

    public RenderController(String modelId, Map<String, Bone> bones, Entity entity) {
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

        Set<Bone> processedBones = new HashSet<>();
        for (String key : entity.textureMap.keySet()) {

            Texture texture = entity.textureMap.get(key);
            Set<String> uvBonesId = entity.getModelConfig().bingingBones.get(key);
            ModelConfig.AnimTextureOptions anim = entity.getModelConfig().getAnimTextures().get(key);

            JsonObject controller = new JsonObject();

            renderControllers.add("controller.render." + modelId + "_" + key, controller);

            controller.addProperty("geometry", "Geometry.default");

            JsonArray materials = new JsonArray();
            String material = entity.getModelConfig().getTextureMaterials().get(key);
            JsonObject materialItem = new JsonObject();
            if (material != null) {
                materialItem.addProperty("*", "Material." + material);
            } else if (anim != null) {
                materialItem.addProperty("*", "Material.anim");
                JsonObject uvAnim = new JsonObject();
                controller.add("uv_anim", uvAnim);
                JsonArray offset = new JsonArray();
                offset.add(0.0);
                offset.add("math.mod(math.floor(q.life_time * " + anim.fps + ")," + anim.frames + ") / " + anim.frames);
                uvAnim.add("offset", offset);
                JsonArray scale = new JsonArray();
                scale.add(1.0);
                scale.add("1 / " + anim.frames);
                uvAnim.add("scale", scale);
            } else {
                materialItem.addProperty("*", "Material.default");
            }
            materials.add(materialItem);
            controller.add("materials", materials);

            JsonArray textures = new JsonArray();
            textures.add("Texture." + key);
            controller.add("textures", textures);

            // if (enable) {
            JsonArray partVisibility = new JsonArray();
            JsonObject visibilityDefault = new JsonObject();
            visibilityDefault.addProperty("*", false);
            partVisibility.add(visibilityDefault);
            int i = 0;
            List<String> sorted = new ArrayList<>(bones.keySet());
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

            Set<String> uvAllBones = new HashSet<>();
            for (String uvBone : uvBonesId) {
                if (uvBone.equals("*")) {
                    uvAllBones.addAll(bones.keySet());
                }
                if (!bones.containsKey(uvBone)) {
                    continue;
                }
                for (Bone child : bones.get(uvBone).allChildren) {
                    uvAllBones.add(child.getName());
                }
                uvAllBones.add(uvBone);
            }


            for (String boneName : sorted) {
                boneName = originalId.get(boneName);
                JsonObject visibilityItem = new JsonObject();
                Bone bone = bones.get(boneName);

                if (!processedBones.contains(bone) && (uvAllBones.contains(boneName) || uvBonesId.contains("*"))) {
                    int index = i;
                    if (boneName.startsWith("uv_")) {
                        index = sorted.indexOf(bone.parent);
                    }
                    int n = (int) Math.pow(2, (index % 24));

                    visibilityItem.addProperty(boneName, "math.mod(math.floor(query.property('modelengine:bone" + i / 24 + "') / " + n + "), 2) == 1");
                    partVisibility.add(visibilityItem);
                    if (!uvBonesId.contains("*")) {
                        processedBones.add(bone);
                    }
                }
                if (!boneName.startsWith("uv_")) {
                    i++;
                }
            }
            controller.add("part_visibility", partVisibility);
            //}
        }

        return root.toString();
    }

}
