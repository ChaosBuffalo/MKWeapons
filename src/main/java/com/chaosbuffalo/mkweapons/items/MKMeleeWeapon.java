package com.chaosbuffalo.mkweapons.items;

import com.chaosbuffalo.mkcore.MKCore;
import com.chaosbuffalo.mkcore.MKCoreRegistry;
import com.chaosbuffalo.mkcore.abilities.MKAbility;
import com.chaosbuffalo.mkcore.core.MKAttributes;
import com.chaosbuffalo.mkcore.item.IImplementsBlocking;
import com.chaosbuffalo.mkcore.item.ILimitItemTooltip;
import com.chaosbuffalo.mkcore.item.IReceivesSkillChange;
import com.chaosbuffalo.mkcore.utils.EntityUtils;
import com.chaosbuffalo.mkweapons.capabilities.IWeaponData;
import com.chaosbuffalo.mkweapons.capabilities.WeaponsCapabilities;
import com.chaosbuffalo.mkweapons.items.effects.melee.IMeleeWeaponEffect;
import com.chaosbuffalo.mkweapons.items.weapon.IMKMeleeWeapon;
import com.chaosbuffalo.mkweapons.items.weapon.tier.MKTier;
import com.chaosbuffalo.mkweapons.items.weapon.types.IMeleeWeaponType;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MKMeleeWeapon extends SwordItem implements IMKMeleeWeapon, ILimitItemTooltip, IImplementsBlocking, IReceivesSkillChange {
    private final IMeleeWeaponType weaponType;
    private final MKTier mkTier;
    private final List<IMeleeWeaponEffect> weaponEffects;
    protected Multimap<Attribute, AttributeModifier> modifiers;
    protected static final UUID ATTACK_REACH_MODIFIER = UUID.fromString("f74aa80c-43b8-4d00-a6ce-8d52694ff20c");
    protected static final UUID CRIT_CHANCE_MODIFIER = UUID.fromString("9b9c4389-0036-4beb-9dcc-5e11928ff499");
    protected static final UUID CRIT_MULT_MODIFIER = UUID.fromString("11fc07d2-7844-44f2-94ad-02479cff424d");
    protected static final UUID MAX_POISE_MODIFIER = UUID.fromString("fbc2bba2-27d6-4de8-8962-2febb418c718");
    protected static final UUID BLOCK_EFFICIENCY_MODIFIER = UUID.fromString("da287a85-0c12-459c-97a5-faea98bc3d6f");

    public MKMeleeWeapon(ResourceLocation weaponName, MKTier tier, IMeleeWeaponType weaponType, Properties builder) {
        super(tier, Math.round(weaponType.getDamageForTier(tier) - tier.getAttackDamage()), weaponType.getAttackSpeed(), builder);
        this.weaponType = weaponType;
        this.mkTier = tier;
        this.weaponEffects = new ArrayList<>();
        recalculateModifiers();
        weaponEffects.addAll(tier.getMeleeWeaponEffects());
        weaponEffects.addAll(weaponType.getWeaponEffects());
        setRegistryName(weaponName);
    }

    protected void recalculateModifiers(){
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER,
                "Weapon modifier", getAttackDamage(), AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER,
                "Weapon modifier", getWeaponType().getAttackSpeed(), AttributeModifier.Operation.ADDITION));
        builder.put(MKAttributes.ATTACK_REACH, new AttributeModifier(ATTACK_REACH_MODIFIER,
                "Weapon modifier", getWeaponType().getReach(), AttributeModifier.Operation.ADDITION));
        builder.put(MKAttributes.MELEE_CRIT, new AttributeModifier(CRIT_CHANCE_MODIFIER,
                "Weapon modifier", getWeaponType().getCritChance(), AttributeModifier.Operation.ADDITION));
        builder.put(MKAttributes.MELEE_CRIT_MULTIPLIER, new AttributeModifier(CRIT_MULT_MODIFIER,
                "Weapon modifier", getWeaponType().getCritMultiplier(), AttributeModifier.Operation.ADDITION));
        builder.put(MKAttributes.MAX_POISE, new AttributeModifier(MAX_POISE_MODIFIER,
                "Weapon Modifier", getWeaponType().getMaxPoise(), AttributeModifier.Operation.ADDITION));
        builder.put(MKAttributes.BLOCK_EFFICIENCY, new AttributeModifier(BLOCK_EFFICIENCY_MODIFIER,
                "Weapon Modifier", getWeaponType().getBlockEfficiency(), AttributeModifier.Operation.ADDITION));
        modifiers = builder.build();
    }

    @Override
    public float getAttackDamage() {
        return Math.round(getWeaponType().getDamageForTier(getMKTier()) - getMKTier().getAttackDamage());
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
        return equipmentSlot == EquipmentSlotType.MAINHAND ? this.modifiers : super.getAttributeModifiers(equipmentSlot);
    }

    @Override
    public void reload(){
        weaponEffects.clear();
        recalculateModifiers();
        weaponEffects.addAll(getMKTier().getMeleeWeaponEffects());
        weaponEffects.addAll(getWeaponType().getWeaponEffects());
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        return false;
    }

    @Override
    public List<IMeleeWeaponEffect> getWeaponEffects() {
        return weaponEffects;
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        MKCore.getEntityData(attacker).ifPresent(cap -> {
            if (!target.isActiveItemStackBlocking()){
                if (cap.getCombatExtension().getEntityTicksSinceLastSwing() >= EntityUtils.getCooldownPeriod(attacker)){
                    for (IMeleeWeaponEffect effect : getWeaponEffects(stack)){
                        effect.onHit(this, stack, target, attacker);
                    }
                }
            }
        });

        return super.hitEntity(stack, target, attacker);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        return stack.getCapability(WeaponsCapabilities.WEAPON_DATA_CAPABILITY).map(x -> x.getAttributeModifiers(slot))
                .orElse(getAttributeModifiers(slot));
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        ItemStack offhand = playerIn.getHeldItemOffhand();
        if (offhand.getItem() instanceof ShieldItem){
            return ActionResult.resultPass(itemstack);
        }
        if (MKCore.getPlayer(playerIn).map(x -> x.getStats().isPoiseBroke()).orElse(false)){
            return ActionResult.resultPass(itemstack);
        } else {
            playerIn.setActiveHand(handIn);
            return ActionResult.resultConsume(itemstack);
        }

    }

    @Nullable
    @Override
    public CompoundNBT getShareTag(ItemStack stack) {
        // This needs to be handled carefully.
        // If an SEntityEquipmentPacket is created in LivingEntity#func_241342_a_ and sent to all tracking entities
        // the eventual ItemStack serialization may be performed by multiple network threads, causing a
        // ConcurrentModificationException in CompoundNBT if they insert data into the ItemStack's tag simultaneously.
        // Work around this by returning a new CompoundNBT for each call, embedding the original share tag and putting
        // our data next to it without modifying the actual ItemStack tag

        CompoundNBT newTag = new CompoundNBT();
        CompoundNBT original = super.getShareTag(stack);
        if (original != null) {
            newTag.put("share", original);
        }
        stack.getCapability(WeaponsCapabilities.WEAPON_DATA_CAPABILITY).ifPresent(weaponData ->
                newTag.put("weaponCap", weaponData.serializeNBT()));
        return newTag;
    }

    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundNBT shareTag) {
        if (shareTag == null)
            return;

        if (shareTag.contains("share")) {
            super.readShareTag(stack, shareTag.getCompound("share"));
        }
        if (shareTag.contains("weaponCap")) {
            stack.getCapability(WeaponsCapabilities.WEAPON_DATA_CAPABILITY).ifPresent(weaponData ->
                    weaponData.deserializeNBT(shareTag.getCompound("weaponCap")));
        }
    }

//    //rot: 0 +50, -29
//    //translation -5.75, +1.5, 0


    @Override
    public IMeleeWeaponType getWeaponType() {
        return weaponType;
    }

    @Override
    public MKTier getMKTier() {
        return mkTier;
    }

    public void addToTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
        if (getWeaponType().isTwoHanded()) {
            tooltip.add(new TranslationTextComponent("mkweapons.two_handed.name")
                    .mergeStyle(TextFormatting.GRAY));
            if (Screen.hasShiftDown()) {
                tooltip.add(new TranslationTextComponent("mkweapons.two_handed.description"));
            }
        }
        for (IMeleeWeaponEffect effect : getWeaponEffects(stack)) {
            effect.addInformation(stack, worldIn, tooltip);
        }
        MKAbility ability = getAbility(stack);
        if (ability != null) {
            tooltip.add(new TranslationTextComponent("mkweapons.grants_ability",
                    ability.getAbilityName()).mergeStyle(TextFormatting.GOLD));
        }
    }


    @Override
    public List<IMeleeWeaponEffect> getWeaponEffects(ItemStack item) {
        return item.getCapability(WeaponsCapabilities.WEAPON_DATA_CAPABILITY).map(cap -> {
            if (cap.hasMeleeWeaponEffects()){
                return cap.getMeleeEffects();
            } else {
                return weaponEffects;
            }
        }).orElse(weaponEffects);
    }

    @Nullable
    @Override
    public MKAbility getAbility(ItemStack itemStack) {
        return MKCoreRegistry.getAbility(itemStack.getCapability(WeaponsCapabilities.WEAPON_DATA_CAPABILITY)
                .map(IWeaponData::getAbilityName).orElse(MKCoreRegistry.INVALID_ABILITY));
    }

    @Override
    public Multimap<Attribute, AttributeModifier> limitTooltip(ItemStack itemStack, EquipmentSlotType equipmentSlotType, Multimap<Attribute, AttributeModifier> multimap) {
        if (multimap.containsKey(MKAttributes.BLOCK_EFFICIENCY)){
            multimap.removeAll(MKAttributes.BLOCK_EFFICIENCY);
        }
        if (multimap.containsKey(MKAttributes.MAX_POISE)){
            multimap.removeAll(MKAttributes.MAX_POISE);
        }
        if (multimap.containsKey(MKAttributes.MELEE_CRIT)){
            multimap.removeAll(MKAttributes.MELEE_CRIT);
        }
        if (multimap.containsKey(MKAttributes.MELEE_CRIT_MULTIPLIER)){
            multimap.removeAll(MKAttributes.MELEE_CRIT_MULTIPLIER);
        }
        return multimap;
    }

    @Override
    public void onSkillChange(ItemStack stack, PlayerEntity playerEntity) {
        getWeaponEffects(stack).forEach(x -> x.onSkillChange(playerEntity));
    }
}
