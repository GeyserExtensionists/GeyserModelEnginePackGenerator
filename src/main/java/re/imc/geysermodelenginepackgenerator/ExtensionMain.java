package re.imc.geysermodelenginepackgenerator;

import me.zimzaza4.geyserutils.geyser.GeyserUtils;
import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.geyser.api.event.lifecycle.GeyserLoadResourcePacksEvent;
import org.geysermc.geyser.api.event.lifecycle.GeyserPreInitializeEvent;
import org.geysermc.geyser.api.extension.Extension;
import re.imc.geysermodelenginepackgenerator.util.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipOutputStream;

public class ExtensionMain implements Extension {

    private File source;

    @Subscribe
    public void onLoad(GeyserPreInitializeEvent event) {
        source = dataFolder().resolve("input").toFile();
        source.mkdirs();

        File[] files = source.listFiles();
        if (files != null) {
            for (File file : files) {
                String id = "modelengine:" + file.getName().toLowerCase();
                GeyserUtils.addCustomEntity(id);
            }
        }
    }


    @Subscribe
    public void onPackLoad(GeyserLoadResourcePacksEvent event) {

        File generatedPack = dataFolder().resolve("generated_pack").toFile();

        GeneratorMain.startGenerate(source, generatedPack);

        Path generatedPackZip = dataFolder().resolve("generated_pack.zip");

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(generatedPackZip))) {
            // 压缩文件夹
            ZipUtil.compressFolder(generatedPack, generatedPack.getName(), zipOutputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }
        event.resourcePacks().add(generatedPackZip);
    }
}
