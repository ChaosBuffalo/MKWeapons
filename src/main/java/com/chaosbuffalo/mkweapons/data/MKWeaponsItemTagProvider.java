package com.chaosbuffalo.mkweapons.data;

import com.chaosbuffalo.mkweapons.init.MKWeaponsItems;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;

public class MKWeaponsItemTagProvider extends ItemTagsProvider {

    public MKWeaponsItemTagProvider(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider, String modId,
                                    @Nullable ExistingFileHelper existingFileHelper) {
        super(dataGenerator, blockTagProvider, modId, existingFileHelper);
    }

    @Override
    protected void registerTags() {
        getOrCreateBuilder(accessory("ring")).add(MKWeaponsItems.CopperRing);
    }

    private static Tag.INamedTag<Item> accessory(String name) {
        return ItemTags.createOptional(new ResourceLocation("curios", name));
    }

    @Override
    public String getName() {
        return "MK Weapons Item Tags";
    }
}
