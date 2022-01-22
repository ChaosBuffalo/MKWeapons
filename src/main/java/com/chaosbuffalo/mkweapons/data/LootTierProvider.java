package com.chaosbuffalo.mkweapons.data;

import com.chaosbuffalo.mkcore.core.MKAttributes;
import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.init.MKWeaponsItems;
import com.chaosbuffalo.mkweapons.items.MKMeleeWeapon;
import com.chaosbuffalo.mkweapons.items.randomization.LootTier;
import com.chaosbuffalo.mkweapons.items.randomization.options.AttributeOption;
import com.chaosbuffalo.mkweapons.items.randomization.slots.LootSlot;
import com.chaosbuffalo.mkweapons.items.randomization.slots.LootSlotManager;
import com.chaosbuffalo.mkweapons.items.randomization.slots.RandomizationSlotManager;
import com.chaosbuffalo.mkweapons.items.randomization.templates.RandomizationTemplate;
import com.chaosbuffalo.mkweapons.items.weapon.tier.MKTier;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class LootTierProvider implements IDataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final DataGenerator generator;

    public LootTierProvider(DataGenerator generator) {
        this.generator = generator;
    }

    @Override
    public void act(@Nonnull DirectoryCache cache) {
        writeLootTier(generateTierOne(), cache);
    }

    private LootTier generateTierOne(){
        LootTier tier = new LootTier(new ResourceLocation(MKWeapons.MODID, "tier_one"));
        Set<MKTier> weaponTiers = new HashSet<>();
        weaponTiers.add(MKWeaponsItems.STONE_TIER);
        weaponTiers.add(MKWeaponsItems.WOOD_TIER);
        for (MKMeleeWeapon weapon : MKWeaponsItems.WEAPONS){
            if (weaponTiers.contains(weapon.getMKTier())){
                tier.addItemToSlot(LootSlotManager.MAIN_HAND, weapon);
            }
        }
        AttributeOption healthAttribute = new AttributeOption();
        healthAttribute.addAttributeModifier(Attributes.MAX_HEALTH, new AttributeModifier(tier.getName().toString(),
                5, AttributeModifier.Operation.ADDITION));
        healthAttribute.addApplicableSlot(LootSlotManager.MAIN_HAND);
        healthAttribute.addApplicableSlot(LootSlotManager.RINGS);
        tier.addRandomizationOption(healthAttribute);
        AttributeOption manaRegen = new AttributeOption();
        manaRegen.addAttributeModifier(MKAttributes.MANA_REGEN, new AttributeModifier(tier.getName().toString(),
                0.5, AttributeModifier.Operation.ADDITION));
        manaRegen.addApplicableSlot(LootSlotManager.MAIN_HAND);
        manaRegen.addApplicableSlot(LootSlotManager.CHEST);
        manaRegen.addApplicableSlot(LootSlotManager.HEAD);
        tier.addRandomizationOption(manaRegen);
        tier.addTemplate(new RandomizationTemplate(new ResourceLocation(MKWeapons.MODID, "simple_template"),
                RandomizationSlotManager.ATTRIBUTE_SLOT), 10);

        tier.addItemToSlot(LootSlotManager.RINGS, MKWeaponsItems.CopperRing);

        return tier;
    }

    public void writeLootTier(LootTier lootTier, @Nonnull DirectoryCache cache){
        Path outputFolder = this.generator.getOutputFolder();
        ResourceLocation key = lootTier.getName();
        Path path = outputFolder.resolve("data/" + key.getNamespace() + "/loot_tiers/" + key.getPath() + ".json");
        try {
            JsonElement element = lootTier.serialize(JsonOps.INSTANCE);
            IDataProvider.save(GSON, cache, element, path);
        } catch (IOException e){
            MKWeapons.LOGGER.error("Couldn't write loot tier {}", path, e);
        }
    }

    @Nonnull
    @Override
    public String getName() {
        return "MKWeapons Loot Tiers";
    }
}
