package com.chaosbuffalo.mkweapons.init;


import com.chaosbuffalo.mkcore.GameConstants;
import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.items.MKBow;
import com.chaosbuffalo.mkweapons.items.MKMeleeWeapon;
import com.chaosbuffalo.mkweapons.items.TestNBTWeaponEffectItem;
import com.chaosbuffalo.mkweapons.items.weapon.effects.ranged.RapidFireRangedWeaponEffect;
import com.chaosbuffalo.mkweapons.items.weapon.tier.MKTier;
import com.chaosbuffalo.mkweapons.items.weapon.types.IMeleeWeaponType;
import com.chaosbuffalo.mkweapons.items.weapon.types.MeleeWeaponType;
import com.chaosbuffalo.mkweapons.items.weapon.types.MeleeWeaponTypes;
import com.chaosbuffalo.mkweapons.items.weapon.types.WeaponTypeManager;
import net.minecraft.item.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ObjectHolder(MKWeapons.MODID)
@Mod.EventBusSubscriber(modid = MKWeapons.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MKWeaponsItems {

    public static List<MKMeleeWeapon> WEAPONS = new ArrayList<>();

    public static MKTier IRON_TIER = new MKTier(ItemTier.IRON, "iron", Tags.Items.INGOTS_IRON);
    public static MKTier WOOD_TIER = new MKTier(ItemTier.WOOD, "wood", ItemTags.PLANKS);
    public static MKTier DIAMOND_TIER = new MKTier(ItemTier.DIAMOND, "diamond", Tags.Items.GEMS_DIAMOND);
    public static MKTier GOLD_TIER = new MKTier(ItemTier.GOLD, "gold", Tags.Items.INGOTS_GOLD);
    public static MKTier STONE_TIER = new MKTier(ItemTier.STONE, "stone", Tags.Items.COBBLESTONE);

    public static List<MKBow> BOWS = new ArrayList<>();

    @ObjectHolder("haft")
    public static Item Haft;

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> evt){
        Set<Tuple<String, MKTier>> materials = new HashSet<>();
        materials.add(new Tuple<>("iron", IRON_TIER));
        materials.add(new Tuple<>("wood", WOOD_TIER));
        materials.add(new Tuple<>("diamond", DIAMOND_TIER));
        materials.add(new Tuple<>("gold", GOLD_TIER));
        materials.add(new Tuple<>("stone", STONE_TIER));
        for (Tuple<String, MKTier> mat : materials){
            for (IMeleeWeaponType weaponType : MeleeWeaponTypes.WEAPON_TYPES.values()){
                MKMeleeWeapon weapon = new MKMeleeWeapon(new ResourceLocation(MKWeapons.MODID,
                        String.format("%s_%s", weaponType.getName().getPath(), mat.getA())), mat.getB(), weaponType,
                        (new Item.Properties()).group(ItemGroup.COMBAT));
                WEAPONS.add(weapon);
                WeaponTypeManager.addMeleeWeapon(weapon);
                evt.getRegistry().register(weapon);
            }
            MKBow bow = new MKBow(new ResourceLocation(MKWeapons.MODID, String.format("longbow_%s", mat.getA())),
                    new Item.Properties().maxDamage(mat.getB().getMaxUses() * 3).group(ItemGroup.COMBAT), mat.getB(),
                    GameConstants.TICKS_PER_SECOND * 2.5f, 4.0f,
                    new RapidFireRangedWeaponEffect(7, .10f));
            BOWS.add(bow);
            evt.getRegistry().register(bow);
        }
        Item haft = new Item(new Item.Properties().group(ItemGroup.MATERIALS));
        haft.setRegistryName(MKWeapons.MODID, "haft");
        evt.getRegistry().register(haft);
        TestNBTWeaponEffectItem testNBTWeaponEffectItem = new TestNBTWeaponEffectItem(new Item.Properties());
        testNBTWeaponEffectItem.setRegistryName(MKWeapons.MODID, "test_nbt_effect");
        evt.getRegistry().register(testNBTWeaponEffectItem);
    }

    public static void registerItemProperties(){
        for (MKBow bow : BOWS){
            ItemModelsProperties.registerProperty(bow, new ResourceLocation("pull"), (itemStack, world, entity) -> {
                if (entity == null) {
                    return 0.0F;
                } else {
                    return !(entity.getActiveItemStack().getItem() instanceof MKBow) ? 0.0F :
                            (float)(itemStack.getUseDuration() - entity.getItemInUseCount()) / bow.getDrawTime(itemStack, entity);
                }
            });
            ItemModelsProperties.registerProperty(bow, new ResourceLocation("pulling"), (itemStack, world, entity) -> {
                return entity != null && entity.isHandActive() && entity.getActiveItemStack() == itemStack ? 1.0F : 0.0F;
            });
        }

        for (MKMeleeWeapon weapon : WEAPONS){
            if (MeleeWeaponTypes.WITH_BLOCKING.contains(weapon.getWeaponType()))
            ItemModelsProperties.registerProperty(weapon, new ResourceLocation("blocking"),
                    (itemStack, world, entity) -> entity != null && entity.isHandActive()
                            && entity.getActiveItemStack() == itemStack ? 1.0F : 0.0F);
        }


    }
}
