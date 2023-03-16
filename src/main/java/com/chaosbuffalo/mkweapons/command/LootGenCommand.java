package com.chaosbuffalo.mkweapons.command;

import com.chaosbuffalo.mkcore.GameConstants;
import com.chaosbuffalo.mkweapons.items.randomization.LootConstructor;
import com.chaosbuffalo.mkweapons.items.randomization.LootTier;
import com.chaosbuffalo.mkweapons.items.randomization.LootTierManager;
import com.chaosbuffalo.mkweapons.items.randomization.slots.LootSlot;
import com.chaosbuffalo.mkweapons.items.randomization.slots.LootSlotManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;

import java.util.concurrent.CompletableFuture;

public class LootGenCommand {
    public static LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("gen_loot")
                .then(Commands.argument("loot_tier", LootTierArgument.definition())
                        .suggests(LootGenCommand::suggestLootTiers)
                        .then(Commands.argument("loot_slot", LootSlotArgument.definition())
                                .suggests(LootGenCommand::suggestLootSlots)
                                .then(Commands.argument("difficulty", DoubleArgumentType.doubleArg(GameConstants.MIN_DIFFICULTY, GameConstants.MAX_DIFFICULTY))
                                        .executes(LootGenCommand::summon))));
    }

    static CompletableFuture<Suggestions> suggestLootTiers(final CommandContext<CommandSource> context,
                                                           final SuggestionsBuilder builder) throws CommandSyntaxException {
        return ISuggestionProvider.suggest(LootTierManager.LOOT_TIERS.keySet().stream()
                .map(ResourceLocation::toString), builder);
    }

    static CompletableFuture<Suggestions> suggestLootSlots(final CommandContext<CommandSource> context,
                                                           final SuggestionsBuilder builder) throws CommandSyntaxException {
        return ISuggestionProvider.suggest(LootSlotManager.SLOTS.keySet().stream()
                .map(ResourceLocation::toString), builder);
    }

    static int summon(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().asPlayer();
        ResourceLocation tierName = ctx.getArgument("loot_tier", ResourceLocation.class);
        ResourceLocation slotName = ctx.getArgument("loot_slot", ResourceLocation.class);
        double difficulty = ctx.getArgument("difficulty", Double.class);
        LootTier tier = LootTierManager.getTierFromName(tierName);
        LootSlot slot = LootSlotManager.getSlotFromName(slotName);
        if (tier != null) {
            if (slot != null) {
                LootConstructor constructor = tier.generateConstructorForSlot(player.getRNG(), slot);
                if (constructor != null) {
                    ItemStack stack = constructor.constructItem(player.getRNG(), difficulty);
                    if (!stack.isEmpty()) {
                        boolean wasAdded = player.addItemStackToInventory(stack);
                        if (!wasAdded) {
                            player.dropItem(stack, false);
                        }
                    }
                } else {
                    player.sendMessage(new StringTextComponent("No LootConstructor generated."), Util.DUMMY_UUID);
                }
            } else {
                player.sendMessage(new StringTextComponent("Loot Slot Not Found."), Util.DUMMY_UUID);
            }
        } else {
            player.sendMessage(new StringTextComponent("Loot Tier Not Found."), Util.DUMMY_UUID);
        }
        return Command.SINGLE_SUCCESS;
    }
}