package com.chaosbuffalo.mkweapons.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;

public class WeaponsCommands {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(LootGenCommand.register());
    }

    public static void registerArguments() {
        ArgumentTypes.register("loot_tier", LootTierArgument.class, new ArgumentSerializer<>(LootTierArgument::definition));
        ArgumentTypes.register("loot_slot", LootSlotArgument.class, new ArgumentSerializer<>(LootSlotArgument::definition));
    }
}
