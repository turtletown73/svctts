package net.scoobis.svctts;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = SvcTtsMod.MOD_ID)
public class ModConfig implements ConfigData {
    @ConfigEntry.Category("general")
    String provider = "freetts";
}
