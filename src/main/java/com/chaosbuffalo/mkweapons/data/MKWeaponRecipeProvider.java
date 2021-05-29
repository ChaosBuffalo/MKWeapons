package com.chaosbuffalo.mkweapons.data;

import com.chaosbuffalo.mkweapons.init.MKWeaponsItems;
import com.chaosbuffalo.mkweapons.items.MKBow;
import com.chaosbuffalo.mkweapons.items.MKMeleeWeapon;
import com.chaosbuffalo.mkweapons.items.weapon.IMKMeleeWeapon;
import com.chaosbuffalo.mkweapons.items.weapon.types.IMeleeWeaponType;
import com.chaosbuffalo.mkweapons.items.weapon.types.MeleeWeaponTypes;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Tuple;
import net.minecraftforge.common.Tags;

import java.util.*;
import java.util.function.Consumer;

public class MKWeaponRecipeProvider extends RecipeProvider {

    public static class WeaponRecipe {
        private final List<String> pattern;
        private final List<Tuple<Character, Item>> itemKeys;

        public WeaponRecipe(List<String> pattern, List<Tuple<Character, Item>> itemKeys){
            this.pattern = pattern;
            this.itemKeys = itemKeys;
        }

        public List<String> getPattern() {
            return pattern;
        }

        public boolean hasHaft(){
            for (Tuple<Character, Item> tuple : itemKeys){
                if (tuple.getA().equals('H')){
                    return true;
                }
            }
            return false;
        }

        public boolean hasStick(){
            for (Tuple<Character, Item> tuple : itemKeys){
                if (tuple.getA().equals('S')){
                    return true;
                }
            }
            return false;
        }

        public List<Tuple<Character, Item>> getItemKeys() {
            return itemKeys;
        }
    }

    public static final Map<IMeleeWeaponType, WeaponRecipe> weaponRecipes = new HashMap<>();
    static {
        weaponRecipes.put(MeleeWeaponTypes.DAGGER_TYPE, new WeaponRecipe(
                Arrays.asList("I", "S"),
                Arrays.asList(new Tuple<>('S', Items.STICK))));
        weaponRecipes.put(MeleeWeaponTypes.BATTLEAXE_TYPE, new WeaponRecipe(
                Arrays.asList("III", "IHI", " H "),
                Arrays.asList(new Tuple<>('H', MKWeaponsItems.Haft))));
        weaponRecipes.put(MeleeWeaponTypes.GREATSWORD_TYPE, new WeaponRecipe(
                Arrays.asList(" I ", " I ", "IHI"),
                Arrays.asList(new Tuple<>('H', MKWeaponsItems.Haft))));
        weaponRecipes.put(MeleeWeaponTypes.KATANA_TYPE, new WeaponRecipe(
                Arrays.asList("  I", " I ", "H  "),
                Arrays.asList(new Tuple<>('H', MKWeaponsItems.Haft))));
        weaponRecipes.put(MeleeWeaponTypes.LONGSWORD_TYPE, new WeaponRecipe(
                Arrays.asList(" I ", " I ", " H "),
                Arrays.asList(new Tuple<>('H', MKWeaponsItems.Haft))));
        weaponRecipes.put(MeleeWeaponTypes.SPEAR_TYPE, new WeaponRecipe(
                Arrays.asList("  I", " H ", "H  "),
                Arrays.asList(new Tuple<>('H', MKWeaponsItems.Haft))));
        weaponRecipes.put(MeleeWeaponTypes.WARHAMMER_TYPE, new WeaponRecipe(
                Arrays.asList(" II", " HI", "H  "),
                Arrays.asList(new Tuple<>('H', MKWeaponsItems.Haft))));
        weaponRecipes.put(MeleeWeaponTypes.MACE_TYPE, new WeaponRecipe(
                Arrays.asList(" I ", " H ", " H "),
                Arrays.asList(new Tuple<>('H', MKWeaponsItems.Haft))));
        weaponRecipes.put(MeleeWeaponTypes.STAFF_TYPE, new WeaponRecipe(
                Arrays.asList("  I", " H ", "I  "),
                Arrays.asList(new Tuple<>('H', MKWeaponsItems.Haft))));

    }

    public MKWeaponRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        getHaftRecipe().build(consumer);
        for (MKMeleeWeapon weapon : MKWeaponsItems.WEAPONS){
            getRecipe(weapon).build(consumer);
        }
        for (MKBow bow : MKWeaponsItems.BOWS){
            getLongbowRecipe(bow).build(consumer);
        }
    }

    private ShapedRecipeBuilder getHaftRecipe(){
        return ShapedRecipeBuilder.shapedRecipe(MKWeaponsItems.Haft, 3)
                .key('S', Items.STICK)
                .key('L', Tags.Items.LEATHER)
                .patternLine("SSS")
                .patternLine(" L ")
                .patternLine("SSS")
                .addCriterion("has_stick", this.hasItem(Items.STICK))
                .addCriterion("has_leather", this.hasItem(Tags.Items.LEATHER));
    }

    private ShapedRecipeBuilder getLongbowRecipe(MKBow bow){
        return ShapedRecipeBuilder.shapedRecipe(bow)
                .key('H', MKWeaponsItems.Haft)
                .key('I', bow.getMKTier().getMajorIngredient())
                .key('S', Tags.Items.STRING)
                .patternLine(" IS")
                .patternLine("H S")
                .patternLine(" IS")
                .addCriterion("has_haft", this.hasItem(MKWeaponsItems.Haft))
                .addCriterion("has_string", this.hasItem(Tags.Items.STRING))
                .addCriterion("has_ingot", this.hasItem(bow.getMKTier().getTag()))
                ;
    }

    private ShapedRecipeBuilder getRecipe(MKMeleeWeapon weapon) {
        ShapedRecipeBuilder recipeBuilder = ShapedRecipeBuilder.shapedRecipe(weapon);
        recipeBuilder.key('I', weapon.getMKTier().getMajorIngredient());;
        WeaponRecipe weaponRecipe = weaponRecipes.get(weapon.getWeaponType());
        for (Tuple<Character, Item> key : weaponRecipe.getItemKeys()) {
            recipeBuilder.key(key.getA(), key.getB());
        }
        for (String line : weaponRecipe.getPattern()){
            recipeBuilder.patternLine(line);
        }
        if (weaponRecipe.hasHaft()){
            recipeBuilder.addCriterion("has_haft", this.hasItem(MKWeaponsItems.Haft));
        }
        if (weaponRecipe.hasStick()){
            recipeBuilder.addCriterion("has_stick", this.hasItem(Items.STICK));
        }
        recipeBuilder.addCriterion("has_ingot", this.hasItem(weapon.getMKTier().getTag()));
        return recipeBuilder;
    }
}
