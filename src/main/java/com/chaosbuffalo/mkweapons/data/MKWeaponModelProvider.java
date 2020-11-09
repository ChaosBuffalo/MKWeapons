package com.chaosbuffalo.mkweapons.data;

import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.init.MKWeaponsItems;
import com.chaosbuffalo.mkweapons.items.MKBow;
import com.chaosbuffalo.mkweapons.items.MKMeleeWeapon;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;

import java.util.*;

public class MKWeaponModelProvider extends ItemModelProvider {

    public MKWeaponModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, MKWeapons.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (MKMeleeWeapon weapon : MKWeaponsItems.WEAPONS){
            makeWeaponModel(weapon);
        }
        for (MKBow bow : MKWeaponsItems.BOWS){
            makeBowModels(bow);
        }
    }

    private void makeBowModels(MKBow bow){
        String path = bow.getRegistryName().getPath();
        List<String> subModels = Arrays.asList("pulling_0", "pulling_1", "pulling_2");
        for (String subModel : subModels){
            String subPath = String.format("%s_%s", path, subModel);
            getBuilder(subPath)
                    .parent(getExistingFile(modLoc(String.format("item/longbow_base_%s", subModel))))
                    .texture("0", modLoc(String.format("items/%s_tool", bow.getMKTier().getName())))
                    .texture("particle", modLoc(String.format("items/%s_tool", bow.getMKTier().getName())));
        }
        ItemModelBuilder builder = getBuilder(path)
                .parent(getExistingFile(modLoc("item/longbow_base")))
                .texture("0", modLoc(String.format("items/%s_tool", bow.getMKTier().getName())))
                .texture("particle", modLoc(String.format("items/%s_tool", bow.getMKTier().getName())));
        int index = 0;
        Map<String, Tuple<Integer, Double>> subModelKeys = new HashMap<>();
        subModelKeys.put("pulling_0", new Tuple<>(1, -1.0));
        subModelKeys.put("pulling_1", new Tuple<>(1, 0.65));
        subModelKeys.put("pulling_2", new Tuple<>(1, 0.9));
        for (String subModel : subModels){
            Tuple<Integer, Double> predicates = subModelKeys.get(subModel);
            ItemModelBuilder.OverrideBuilder override = builder.override().model(getExistingFile(modLoc(String.format("item/longbow_%s_%s",
                    bow.getMKTier().getName(), subModel))))
                    .predicate(new ResourceLocation("pulling"), predicates.getA());
            if (predicates.getB() > 0){
                override.predicate(new ResourceLocation("pull"), predicates.getB().floatValue());
            }
            index++;
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
