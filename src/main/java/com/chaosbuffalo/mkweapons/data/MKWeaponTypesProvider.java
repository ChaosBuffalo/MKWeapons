package com.chaosbuffalo.mkweapons.data;

import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.items.weapon.types.IMeleeWeaponType;
import com.chaosbuffalo.mkweapons.items.weapon.types.MeleeWeaponTypes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;

public class MKWeaponTypesProvider implements IDataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final DataGenerator generator;

    public MKWeaponTypesProvider(DataGenerator generator) {
        this.generator = generator;
    }

    @Override
    public void act(@Nonnull DirectoryCache cache) {
        Path outputFolder = this.generator.getOutputFolder();
        MeleeWeaponTypes.WEAPON_TYPES.keySet().forEach(key -> {
            IMeleeWeaponType type = MeleeWeaponTypes.getWeaponType(key);
            MKWeapons.LOGGER.info("Dumping weapon {}", key);
            Path path = outputFolder.resolve("data/" + key.getNamespace() + "/melee_weapon_types/" + key.getPath() + ".json");
            try {
                JsonElement element = type.serialize(JsonOps.INSTANCE);
                IDataProvider.save(GSON, cache, element, path);
            } catch (IOException e) {
                MKWeapons.LOGGER.error("Couldn't write weapon type {}", path, e);
            }
        });
    }

    @Nonnull
    @Override
    public String getName() {
        return "MKWeapons Weapon Types";
    }
}
