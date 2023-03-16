package com.chaosbuffalo.mkweapons.items;

import com.chaosbuffalo.mkcore.MKCoreRegistry;
import com.chaosbuffalo.mkcore.abilities.MKAbility;
import com.chaosbuffalo.mkcore.core.MKAttributes;
import com.chaosbuffalo.mkcore.item.IReceivesSkillChange;
import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.capabilities.IWeaponData;
import com.chaosbuffalo.mkweapons.capabilities.WeaponsCapabilities;
import com.chaosbuffalo.mkweapons.items.effects.ranged.IRangedWeaponEffect;
import com.chaosbuffalo.mkweapons.items.effects.ranged.RangedSkillScalingEffect;
import com.chaosbuffalo.mkweapons.items.weapon.IMKRangedWeapon;
import com.chaosbuffalo.mkweapons.items.weapon.tier.MKTier;
import com.google.common.collect.Multimap;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MKBow extends BowItem implements IMKRangedWeapon, IReceivesSkillChange {
    private final List<IRangedWeaponEffect> weaponEffects = new ArrayList<>();
    private final MKTier tier;
    private final float baseDrawTime;
    private final float baseLaunchVel;

    public MKBow(ResourceLocation location, Properties builder, MKTier tier, float baseDrawTime,
                 float baseLaunchVel, IRangedWeaponEffect... weaponEffects) {
        super(builder);
        setRegistryName(location);
        this.baseDrawTime = baseDrawTime;
        this.baseLaunchVel = baseLaunchVel;
        this.weaponEffects.addAll(Arrays.asList(weaponEffects));
        this.weaponEffects.add(new RangedSkillScalingEffect(5.0 + tier.getAttackDamage(), MKAttributes.MARKSMANSHIP));
        this.tier = tier;
    }

    public float getDrawTime(ItemStack item, LivingEntity entity) {
        float time = baseDrawTime;
        for (IRangedWeaponEffect weaponEffect : getWeaponEffects(item)) {
            time = weaponEffect.modifyDrawTime(time, item, entity);
        }
        return time;
    }

    @Nullable
    @Override
    public CompoundNBT getShareTag(ItemStack stack) {
        // See comment in MKMeleeWeapon#getShareTag
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

    public float getPowerFactor(int useTicks, ItemStack stack, LivingEntity entity) {
        float powerFactor = (float) (useTicks) / getDrawTime(stack, entity);
        powerFactor = (powerFactor * powerFactor + powerFactor * 2.0F) / 3.0F;
        if (powerFactor > 1.0F) {
            powerFactor = 1.0F;
        }
        return powerFactor;
    }

    public float getLaunchVelocity(ItemStack stack, LivingEntity entity) {
        float vel = baseLaunchVel;
        for (IRangedWeaponEffect weaponEffect : getWeaponEffects(stack)) {
            vel = weaponEffect.modifyLaunchVelocity(vel, stack, entity);
        }
        return vel;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        return stack.getCapability(WeaponsCapabilities.WEAPON_DATA_CAPABILITY).map(x -> x.getAttributeModifiers(slot))
                .orElse(getAttributeModifiers(slot));
    }


    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entityLiving;
            boolean doesntNeedAmmo = player.abilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(
                    Enchantments.INFINITY, stack) > 0;
            ItemStack ammoStack = player.findAmmo(stack);

            int useTicks = this.getUseDuration(stack) - timeLeft;
            useTicks = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, worldIn, player, useTicks, !ammoStack.isEmpty() || doesntNeedAmmo);
            if (useTicks < 0) return;

            if (!ammoStack.isEmpty() || doesntNeedAmmo) {
                if (ammoStack.isEmpty()) {
                    ammoStack = new ItemStack(Items.ARROW);
                }

                float powerFactor = getPowerFactor(useTicks, stack, entityLiving);
                if (!((double) powerFactor < 0.1D)) {
                    boolean hasAmmo = player.abilities.isCreativeMode || (ammoStack.getItem() instanceof ArrowItem && ((ArrowItem) ammoStack.getItem()).isInfinite(ammoStack, stack, player));
                    if (!worldIn.isRemote) {
                        ArrowItem arrowItem = (ArrowItem) (ammoStack.getItem() instanceof ArrowItem ? ammoStack.getItem() : Items.ARROW);
                        AbstractArrowEntity arrowEntity = arrowItem.createArrow(worldIn, ammoStack, player);
                        arrowEntity = customArrow(arrowEntity, stack);
                        arrowEntity.setDirectionAndMovement(entityLiving, entityLiving.rotationPitch, entityLiving.rotationYaw,
                                0.0F, powerFactor * getLaunchVelocity(stack, entityLiving), 1.0F);
                        if (powerFactor == 1.0F) {
                            arrowEntity.setIsCritical(true);
                        }

                        int powerLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
                        if (powerLevel > 0) {
                            arrowEntity.setDamage(arrowEntity.getDamage() + (double) powerLevel * 0.5D + 0.5D);
                        }

                        int punchLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
                        if (punchLevel > 0) {
                            arrowEntity.setKnockbackStrength(punchLevel);
                        }

                        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0) {
                            arrowEntity.setFire(100);
                        }

                        stack.damageItem(1, player, (ent) -> ent.sendBreakAnimation(ent.getActiveHand()));
                        if (hasAmmo || player.abilities.isCreativeMode && (ammoStack.getItem() == Items.SPECTRAL_ARROW || ammoStack.getItem() == Items.TIPPED_ARROW)) {
                            arrowEntity.pickupStatus = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
                        }
                        worldIn.addEntity(arrowEntity);
                    }

                    worldIn.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(),
                            SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F,
                            1.0F / (random.nextFloat() * 0.4F + 1.2F) + powerFactor * 0.5F);
                    if (!hasAmmo && !player.abilities.isCreativeMode) {
                        ammoStack.shrink(1);
                        if (ammoStack.isEmpty()) {
                            player.inventory.deleteStack(ammoStack);
                        }
                    }
                    player.addStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }


    public AbstractArrowEntity customArrow(AbstractArrowEntity arrow, ItemStack stack) {
        // set item stack on cap here
        Entity shooter = arrow.getShooter();
        double damage = arrow.getDamage();
        damage += getMKTier().getAttackDamage();
        if (shooter instanceof LivingEntity) {
            MKWeapons.getArrowCapability(arrow).ifPresent(cap ->
                    cap.setShootingWeapon(((LivingEntity) shooter).getHeldItemMainhand()));
            for (IRangedWeaponEffect weaponEffect : getWeaponEffects(stack)) {
                damage = weaponEffect.modifyArrowDamage(damage, (LivingEntity) shooter, arrow);
            }
        }
        arrow.setDamage(damage);
        return super.customArrow(arrow);
    }

    public void addToTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
        if (getMKTier().getAttackDamage() > 0) {
            tooltip.add(new TranslationTextComponent("mkweapons.bow_extra_damage.description",
                    getMKTier().getAttackDamage()).mergeStyle(TextFormatting.GRAY));
        }
        for (IRangedWeaponEffect weaponEffect : getWeaponEffects(stack)) {
            weaponEffect.addInformation(stack, worldIn, tooltip);
        }
    }


    @Override
    public MKTier getMKTier() {
        return tier;
    }

    @Override
    public List<IRangedWeaponEffect> getWeaponEffects() {
        return weaponEffects;
    }

    @Override
    public List<IRangedWeaponEffect> getWeaponEffects(ItemStack item) {
        return item.getCapability(WeaponsCapabilities.WEAPON_DATA_CAPABILITY).map(cap -> {
            if (cap.hasRangedWeaponEffects()) {
                return cap.getRangedEffects();
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
    public void onSkillChange(ItemStack itemStack, PlayerEntity playerEntity) {
        getWeaponEffects(itemStack).forEach(x -> x.onSkillChange(playerEntity));
    }
}
