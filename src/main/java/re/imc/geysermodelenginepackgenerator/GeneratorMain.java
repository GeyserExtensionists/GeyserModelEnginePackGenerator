package re.imc.geysermodelenginepackgenerator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import re.imc.geysermodelenginepackgenerator.generator.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

public class GeneratorMain {
    public static final Map<String, Entity> entityMap = new HashMap<>();
    public static final  Map<String, Animation> animationMap = new HashMap<>();
    public static final  Map<String, Geometry> geometryMap = new HashMap<>();
    public static final  Map<String, Texture> textureMap = new HashMap<>();
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();


    public static void main(String[] args) {
        File source = new File(args.length > 0 ? args[0] : "input");

        File output = new File("output");

        startGenerate(source, output);
    }

    public static void startGenerate(File source, File output) {


        for (File file1 : source.listFiles()) {
            if (file1.isDirectory()) {
                if (file1.listFiles() == null) {
                    continue;
                }
                String modelId = file1.getName();

                entityMap.put(modelId, new Entity(modelId));
                for (File e : file1.listFiles()) {
                    if (e.getName().endsWith(".png")) {
                        textureMap.put(modelId, new Texture(modelId, e.toPath()));
                    }

                    if (e.getName().endsWith(".json")) {
                        try {
                            String json = Files.readString(e.toPath());
                            if (isAnimationFile(json)) {
                                Animation animation = new Animation();
                                animation.load(json);
                                animation.setModelId(modelId);
                                animationMap.put(modelId, animation);
                            }

                            if (isGeometryFile(json)) {
                                System.out.println("G");
                                Geometry geometry = new Geometry();
                                geometry.load(json);
                                geometry.setModelId(modelId);
                                geometryMap.put(modelId, geometry);
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }

        File animationsFolder = new File(output, "animations");
        File entityFolder = new File(output, "entity");
        File modelsFolder = new File(output, "models/entity");
        File texturesFolder = new File(output, "textures/entity");


        boolean generateManifest = false;
        if (!entityFolder.exists()) {
            generateManifest = true;
        }
        File[] files = entityFolder.listFiles();
        if (files == null || files.length < entityMap.size()) {
            generateManifest = true;
        }

        if (generateManifest) {
            output.mkdirs();
            Path path = new File(output, "manifest.json").toPath();
            try {
                Files.writeString(path,
                        PackManifest.generate(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        animationsFolder.mkdirs();
        entityFolder.mkdirs();
        modelsFolder.mkdirs();
        texturesFolder.mkdirs();

        for (Map.Entry<String, Animation> stringAnimationEntry : animationMap.entrySet()) {
            stringAnimationEntry.getValue().modify();
            Geometry geo = geometryMap.get(stringAnimationEntry.getKey());
            if (geo != null) {
                stringAnimationEntry.getValue().addHeadBind(geo);
            }
            Path path = animationsFolder.toPath().resolve(stringAnimationEntry.getKey() + ".animation.json");
            try {
                Files.writeString(path, GSON.toJson(stringAnimationEntry.getValue().getJson()), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (Map.Entry<String, Geometry> stringGeometryEntry : geometryMap.entrySet()) {
            stringGeometryEntry.getValue().modify();
            Path path = modelsFolder.toPath().resolve(stringGeometryEntry.getKey() + ".geo.json");
            try {
                Files.writeString(path, GSON.toJson(stringGeometryEntry.getValue().getJson()), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (Map.Entry<String, Texture> stringTextureEntry : textureMap.entrySet()) {
            Path path = texturesFolder.toPath().resolve(stringTextureEntry.getKey() + ".png");
            try {
                Files.copy(stringTextureEntry.getValue().getPath(), path, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (Map.Entry<String, Entity> stringEntityEntry : entityMap.entrySet()) {
            stringEntityEntry.getValue().modify();
            Path path = entityFolder.toPath().resolve(stringEntityEntry.getKey() + ".entity.json");

            try {
                Files.writeString(path, stringEntityEntry.getValue().getJson(), StandardCharsets.UTF_8);
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