package net.sweenus.simplyswords.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "loot")
public class LootConfig implements ConfigData {

    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip
    public boolean enableLootDrops = true;
    @ConfigEntry.Gui.Tooltip
    public boolean enableLootInVillages = false;
    public boolean enableTheWatcher = true;
    public boolean enableWatchingWarglaive = true;
    public boolean enableLongswordOfThePlague = true;
    public boolean enableSwordOnAStick = true;
    public boolean enableBramblethorn = true;
    public boolean enableStormsEdge = true;
    public boolean enableStormbringer = true;
    public boolean enableMjolnir = true;
    public boolean enableEmberblade = true;
    public boolean enableHearthflame = true;
    public boolean enableTwistedBlade = true;
    public boolean enableSoulrender = true;
    public boolean enableSoulpyre = true;
    public boolean enableSoulkeeper = true;
    public boolean enableSoulstealer = true;
    public boolean enableFrostfall = true;
    public boolean enableMoltenEdge = true;
    public boolean enableLivyatan = true;
    public boolean enableIcewhisper = true;
    public boolean enableArcanethyst = true;
    public boolean enableThunderbrand = true;
    public boolean enableBrimstone = true;
    public boolean enableSlumberingLichblade = true;
    public boolean enableShadowsting = true;
    public boolean enableDormantRelic = true;
    public boolean enableWhisperwind = true;



}
