package re.imc.geysermodelenginepackgenerator.generator;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ModelConfig {

    @SerializedName("head_rotation")
    boolean enableHeadRotation = true;
    @SerializedName("material")
    String material = "entity_alphatest_change_color_one_sided";
    @SerializedName("blend_transition")
    boolean enableBlendTransition = true;
    @SerializedName("binding_bones")
    Map<String, Set<String>> bingingBones = new HashMap<>();
    @SerializedName("anim_textures")
    Map<String, AnimTextureOptions> animTextures = new HashMap<>();
    @SerializedName("texture_materials")
    Map<String, String> textureMaterials = new HashMap<>();

    public Map<String, String> getTextureMaterials() {
        return textureMaterials != null ? textureMaterials : Map.of();
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class AnimTextureOptions {
        float fps;
        int frames;
    }
}
