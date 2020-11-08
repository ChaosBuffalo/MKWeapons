package com.chaosbuffalo.mkweapons.data;

import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.init.MKWeaponsItems;
import com.chaosbuffalo.mkweapons.items.MKMeleeWeapon;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelProvider;

public class MKWeaponModelProvider extends ItemModelProvider {

    public MKWeaponModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, MKWeapons.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (MKMeleeWeapon weapon : MKWeaponsItems.WEAPONS){
            makeWeaponModel(weapon);
        }
    }

    private void makeWeaponModel(MKMeleeWeapon weapon) {
        String path = weapon.getRegistryName().getPath();
        getBuilder(path)
                .parent(getExistingFile(modLoc(String.format("item/%s_base", weapon.getWeaponType().getName()))))
                .texture("0", modLoc(String.format("items/%s_tool", weapon.getMKTier().getName())))
                .texture("particle", modLoc(String.format("items/%s_tool", weapon.getMKTier().getName())));
    }

}
