package re.imc.geysermodelenginepackgenerator.generator;

import java.util.UUID;

public class PackManifest {
    public static final String TEMPLATE = """
            {
              "format_version": 1,
              "header": {
                "name": "ModelEngine",
                "description": "ModelEngine For Geyser",
                "uuid": "%uuid-1%",
                "version": [0, 0, 1]
              },
              "modules": [
                {
                  "type": "resources",
                  "description": "ModelEngine",
                  "uuid": "%uuid-2%",
                  "version": [0, 0, 1]
                }
              ]
            }
            """;

    public static String generate() {
        return TEMPLATE.replace("%uuid-1%", UUID.randomUUID().toString())
                .replace("%uuid-2%", UUID.randomUUID().toString());
    }
}
