package poa.poabackdoorcleaner;

import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;
import poa.poabackdoorcleaner.commands.RemoveBackdoor;
import poa.poabackdoorcleaner.util.Remover;

public final class PoaBackdoorCleaner extends JavaPlugin {

    @SneakyThrows
    @Override
    public void onEnable() {
       getCommand("removebackdoor").setExecutor(new RemoveBackdoor());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
