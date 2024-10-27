package re.imc.geysermodelenginepackgenerator.generator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Bone {
    String name;
    String parent;
    Set<Bone> children = new HashSet<>();
    Set<Bone> allChildren = new HashSet<>();
}
