package dev.rgbmc.multimythic;

import org.bukkit.plugin.java.JavaPlugin;

public final class MultiMythic extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().warning("MultiMythic 仅支持MythicMobs 4.14.2 在其他版本上(仅4.X)运行 可能会出现异常");
        getLogger().warning("MultiMythic 不支持MythicMobs 5.X");
        getLogger().warning("===========================================");
        getLogger().warning("已支持的版本有: 1.19.1, 1.19.2");
        getLogger().warning("此插件为实验品 具有不稳定性 请谨慎尝试");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
