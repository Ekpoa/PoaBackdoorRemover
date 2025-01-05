package poa.poabackdoorcleaner.commands;

import lombok.SneakyThrows;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import poa.poabackdoorcleaner.util.Remover;

public class RemoveBackdoor implements CommandExecutor {
    @SneakyThrows
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Remover.cleanPlugins();
        return false;
    }
}
