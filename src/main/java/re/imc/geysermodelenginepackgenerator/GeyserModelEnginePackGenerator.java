package re.imc.geysermodelenginepackgenerator;

import com.google.gson.Gson;
import lombok.Getter;
import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.geyser.api.command.Command;
import org.geysermc.geyser.api.command.CommandSource;
import org.geysermc.geyser.api.event.lifecycle.GeyserDefineCommandsEvent;
import org.geysermc.geyser.api.event.lifecycle.GeyserLoadResourcePacksEvent;
import org.geysermc.geyser.api.event.lifecycle.GeyserPreInitializeEvent;
import org.geysermc.geyser.api.extension.Extension;
import org.geysermc.geyser.api.extension.ExtensionLogger;
import re.imc.geysermodelenginepackgenerator.config.Config;
import re.imc.geysermodelenginepackgenerator.generator.Entity;
import re.imc.geysermodelenginepackgenerator.util.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipOutputStream;

public class GeyserModelEnginePackGenerator implements Extension {

    private File source;

    public static ExtensionLogger logger;
    Path generatedPackZip;
    @Getter
    Config config = new Config();

    @Subscribe
    public void onLoad(GeyserPreInitializeEvent event) {
        source = dataFolder().resolve("input").toFile();
        source.mkdirs();
        logger = logger();
        loadConfig();

    }

    @Subscribe
    public void onDefineCommand(GeyserDefineCommandsEvent event) {
        event.register(Command.builder(this)
                .name("reload")
                .source(CommandSource.class)
                .executableOnConsole(true)
                .description("GeyserModelPackGenerator Reload Command")
                .suggestedOpOnly(true)
                .permission("geysermodelenginepackgenerator.admin")
                .executor((source, command, args) -> {
                    loadConfig();
                    source.sendMessage("GeyserModelEnginePackGenerator reloaded!");
                })
                .build());
    }

    public void loadConfig() {
        File generatedPack = dataFolder().resolve("generated_pack").toFile();
        File configFile = dataFolder().resolve("config.json").toFile();

        Gson gson = new Gson();

        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Files.writeString(configFile.toPath(), gson.toJson(config));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            config = gson.fromJson(Files.readString(configFile.toPath()), Config.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        PackGenerator.startGenerate(source, generatedPack);

        generatedPackZip = dataFolder().resolve("generated_pack.zip");

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(generatedPackZip))) {
            ZipUtil.compressFolder(generatedPack, null, zipOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Entity entity : PackGenerator.entityMap.values()) {
            entity.register();
        }

    }



    @Subscribe
    public void onPackLoad(GeyserLoadResourcePacksEvent event) {
        if (Boolean.parseBoolean(System.getProperty("geyser-model-engine-auto-load-pack", "true")) && config.isAutoLoadPack()) {
            event.resourcePacks().add(generatedPackZip);
        }
    }


}
