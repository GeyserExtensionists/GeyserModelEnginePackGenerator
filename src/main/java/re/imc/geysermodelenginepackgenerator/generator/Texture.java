package re.imc.geysermodelenginepackgenerator.generator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class Texture {

    String modelId;
    String path;
    Set<String> bindingBones;
    Path originalPath;

}
