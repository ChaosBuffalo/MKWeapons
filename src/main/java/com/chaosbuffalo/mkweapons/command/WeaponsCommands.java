package com.chaosbuffalo.mkweapons.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;

public class WeaponsCommands {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(LootGenCommand.register());
    }
}
