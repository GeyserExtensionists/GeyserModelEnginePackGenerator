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
public class TextureConfig {

    @SerializedName("binding_bones")
    Map<String, Set<String>> bingingBones = new HashMap<>();
    @SerializedName("anim_textures")
    Map<String, AnimTextureOptions> animTextures = new HashMap<>();

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class AnimTextureOptions {
        boolean animUv;
        float fps;
        int frames;
    }
}
