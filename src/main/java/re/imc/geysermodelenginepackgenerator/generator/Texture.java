package re.imc.geysermodelenginepackgenerator.generator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;

@Getter
@Setter
@AllArgsConstructor
public class Texture {

    String modelId;
    Path path;
}
