package re.imc.geysermodelenginepackgenerator.config;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Config {
    @SerializedName("auto_load_pack")
    boolean autoLoadPack = true;
}
