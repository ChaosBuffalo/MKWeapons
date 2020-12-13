package com.chaosbuffalo.mkweapons.command;

import com.chaosbuffalo.mkweapons.items.randomization.LootTierManager;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.ResourceLocation;

import java.util.concurrent.CompletableFuture;

public class LootTierArgument implements ArgumentType<ResourceLocation> {
    public LootTierArgument() {
    }

    public static LootTierArgument definition() {
        return new LootTierArgument();
    }

    public ResourceLocation parse(StringReader reader) throws CommandSyntaxException {
        return ResourceLocation.read(reader);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.suggest(LootTierManager.LOOT_TIERS.keySet().stream()
                .map(ResourceLocation::toString), builder);
    }
}
