package re.imc.geysermodelenginepackgenerator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import re.imc.geysermodelenginepackgenerator.generator.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GeneratorMain {
    public static final Map<String, Entity> entityMap = new HashMap<>();
    public static final  Map<String, Animation> animationMap = new HashMap<>();
    public static final  Map<String, Geometry> geometryMap = new HashMap<>();
    public static final  Map<String, Texture> textureMap = new HashMap<>();
    public static final Gson GSON = new GsonBuilder()
            .create();


    public static void main(String[] args) {
        File source = new File(args.length > 0 ? args[0] : "input");

        File output = new File("output");

        startGenerate(source, output);
    }

    public static void generateFromFolder(String currentPath, File folder) {
        if (folder.listFiles() == null) {
            return;
        }
        String modelId = folder.getName().toLowerCase();

        Entity entity = new Entity(modelId);
        boolean canAdd = false;
        for (File e : folder.listFiles()) {
            if (e.isDirectory()) {
                generateFromFolder(currentPath + folder.getName() + "/", e);
            }
            if (e.getName().endsWith(".png")) {
                textureMap.put(modelId, new Texture(modelId, currentPath, e.toPath()));
            }
            if (e.getName().endsWith(".json")) {
                try {
                    String json = Files.readString(e.toPath());
                    if (isAnimationFile(json)) {
                        Animation animation = new Animation();
                        animation.setPath(currentPath);
                        animation.load(json);
                        animation.setModelId(modelId);
                        animationMap.put(modelId, animation);
                    }

                    if (isGeometryFile(json)) {
                        Geometry geometry = new Geometry();
                        geometry.load(json);
                        geometry.setPath(currentPath);
                        geometry.setModelId(modelId);
                        geometryMap.put(modelId, geometry);
                        canAdd = true;
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        if (canAdd) {
            File config = new File(folder, "config.properties");
            try {
                if (config.exists()) {
                    entity.getProperties().load(new FileReader(config));
                } else {
                    entity.getProperties().setProperty("enable-part-visibility", "false");
                    entity.getProperties().store(new FileWriter(config), "For some reasons, the part visibility render controller may cause client crash");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            entity.setPath(currentPath);
            entityMap.put(modelId, entity);
        }
    }
    public static void startGenerate(File source, File output) {


        for (File file1 : source.listFiles()) {
            if (file1.isDirectory()) {
                if (file1.listFiles() == null) {
                    continue;
                }
                generateFromFolder("", file1);
            }
        }

        File animationsFolder = new File(output, "animations");
        File entityFolder = new File(output, "entity");
        File modelsFolder = new File(output, "models/entity");
        File texturesFolder = new File(output, "textures/entity");
        File animationControllersFolder = new File(output, "animation_controllers");
        File renderControllersFolder = new File(output, "render_controllers");


        File manifestFile = new File(output, "manifest.json");


        output.mkdirs();
        if (!manifestFile.exists()) {
            try {
                Files.writeString(manifestFile.toPath(),
                        PackManifest.generate(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        animationsFolder.mkdirs();
        entityFolder.mkdirs();
        modelsFolder.mkdirs();
        texturesFolder.mkdirs();
        animationControllersFolder.mkdirs();
        renderControllersFolder.mkdirs();

        for (Map.Entry<String, Animation> entry : animationMap.entrySet()) {
            entry.getValue().modify();
            Geometry geo = geometryMap.get(entry.getKey());
            if (geo != null) {
                entry.getValue().addHeadBind(geo);
            }
            Path path = animationsFolder.toPath().resolve(entry.getValue().getPath() + entry.getKey() + ".animation.json");
            path.toFile().getParentFile().mkdirs();

            if (path.toFile().exists()) {
                continue;
            }
            try {
                Files.writeString(path, GSON.toJson(entry.getValue().getJson()), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (Map.Entry<String, Geometry> entry : geometryMap.entrySet()) {
            entry.getValue().modify();
            Path path = modelsFolder.toPath().resolve(entry.getValue().getPath() + entry.getKey() + ".geo.json");
            path.toFile().getParentFile().mkdirs();

            if (path.toFile().exists()) {
                continue;
            }
            try {
                Files.writeString(path, GSON.toJson(entry.getValue().getJson()), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (Map.Entry<String, Texture> entry : textureMap.entrySet()) {
            Path path = texturesFolder.toPath().resolve(entry.getValue().getPath() + entry.getKey() + ".png");
            path.toFile().getParentFile().mkdirs();

            if (path.toFile().exists()) {
                continue;
            }
            try {
                Files.copy(entry.getValue().getOriginalPath(), path, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (Map.Entry<String, Entity> entry : entityMap.entrySet()) {
            Entity entity = entry.getValue();
            entity.getProperties().setProperty("render_controller", "controller.render." + entry.getKey());
            entity.modify();

            Path entityPath = entityFolder.toPath().resolve(entity.getPath() + entry.getKey() + ".entity.json");
            entityPath.toFile().getParentFile().mkdirs();
            if (entityPath.toFile().exists()) {
                continue;
            }
            try {
                Files.writeString(entityPath, entity.getJson(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // render controller part

            String id = entity.getModelId();
            if (!geometryMap.containsKey(id)) continue;
            RenderController controller = new RenderController(id, geometryMap.get(id).getBones());

            Path renderPath = new File(renderControllersFolder, "controller.render." + id + ".json").toPath();
            if (renderPath.toFile().exists()) {
                continue;
            }
            try {
                Files.writeString(renderPath, controller.generate(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File controller = new File(animationControllersFolder, "modelengine.animation_controller.json");
        if (!controller.exists()) {
            try {
                Files.writeString(controller.toPath(), AnimationController.TEMPLATE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static boolean isGeometryFile(String json) {
        try {
            return new JsonParser().parse(json).getAsJsonObject().has("minecraft:geometry");
        } catch (Throwable e) {
            return false;
        }
    }

    private static boolean isAnimationFile(String json) {
        try {
            return new JsonParser().parse(json).getAsJsonObject().has("animations");
        } catch (Throwable e) {
            return false;
        }
    }

}