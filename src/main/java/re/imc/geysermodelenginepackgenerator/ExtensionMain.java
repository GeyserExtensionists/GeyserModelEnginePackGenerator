package re.imc.geysermodelenginepackgenerator;

import me.zimzaza4.geyserutils.geyser.GeyserUtils;
import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.geyser.api.event.lifecycle.GeyserLoadResourcePacksEvent;
import org.geysermc.geyser.api.event.lifecycle.GeyserPreInitializeEvent;
import org.geysermc.geyser.api.extension.Extension;
import re.imc.geysermodelenginepackgenerator.generator.Entity;
import re.imc.geysermodelenginepackgenerator.generator.Geometry;
import re.imc.geysermodelenginepackgenerator.util.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipOutputStream;

public class ExtensionMain implements Extension {

    private File source;

    Path generatedPackZip;

    @Subscribe
    public void onLoad(GeyserPreInitializeEvent event) {
        source = dataFolder().resolve("input").toFile();
        source.mkdirs();

        File generatedPack = dataFolder().resolve("generated_pack").toFile();

        GeneratorMain.startGenerate(source, generatedPack);

        generatedPackZip = dataFolder().resolve("generated_pack.zip");

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(generatedPackZip))) {
            // 压缩文件夹
            ZipUtil.compressFolder(generatedPack, null, zipOutputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }

         for (String entity : GeneratorMain.entityMap.keySet()) {
             String id = "modelengine:" + entity;
             GeyserUtils.addCustomEntity(id);

             Geometry geometry = GeneratorMain.geometryMap.get(entity);
             geometry.getBones().forEach(bone -> {
                 GeyserUtils.addProperty(id, entity + ":" + bone, Boolean.class);
             });

             GeyserUtils.addProperty(id, "modelengine:anim_idle", Boolean.class);
             GeyserUtils.addProperty(id, "modelengine:anim_spawn", Boolean.class);
             GeyserUtils.addProperty(id, "modelengine:anim_walk", Boolean.class);
             GeyserUtils.addProperty(id, "modelengine:anim_stop", Boolean.class);

             GeyserUtils.registerProperties(id);
         }

    }


    @Subscribe
    public void onPackLoad(GeyserLoadResourcePacksEvent event) {
        if (Boolean.parseBoolean(System.getProperty("geyser-model-engine-auto-load-pack", "true"))) {
            event.resourcePacks().add(generatedPackZip);
        }
    }


}
