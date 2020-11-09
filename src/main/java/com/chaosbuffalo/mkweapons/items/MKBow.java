package com.chaosbuffalo.mkweapons.items;

import com.chaosbuffalo.mkcore.GameConstants;
import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.items.weapon.IMKBow;
import com.chaosbuffalo.mkweapons.items.weapon.effects.ranged.IRangedWeaponEffect;
import com.chaosbuffalo.mkweapons.items.weapon.tier.MKTier;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stats.Stats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class MKBow extends BowItem implements IMKBow {
    private final List<IRangedWeaponEffect> weaponEffects;
    private final MKTier tier;
    private final float baseDrawTime;
    private final float baseLaunchVel;

    public MKBow(ResourceLocation location, Properties builder, MKTier tier, float baseDrawTime,
                 float baseLaunchVel, IRangedWeaponEffect... weaponEffects) {
        super(builder);
        setRegistryName(location);
        this.baseDrawTime = baseDrawTime;
        this.baseLaunchVel = baseLaunchVel;
        this.weaponEffects = Arrays.asList(weaponEffects);
        this.tier = tier;
        this.addPropertyOverride(new ResourceLocation("pull"), (itemStack, world, entity) -> {
            if (entity == null) {
                return 0.0F;
            } else {
                return !(entity.getActiveItemStack().getItem() instanceof MKBow) ? 0.0F :
                        (float)(itemStack.getUseDuration() - entity.getItemInUseCount()) / getDrawTime(itemStack, entity);
            }
        });
    }

    public float getDrawTime(ItemStack item, LivingEntity entity){
        float time = baseDrawTime;
        for (IRangedWeaponEffect weaponEffect : getWeaponEffects()){
            time = weaponEffect.modifyDrawTime(time, item, entity);
        }
        return time;
    }

    public float getPowerFactor(int useTicks, ItemStack stack, LivingEntity entity){
        float powerFactor = (float)(useTicks) / getDrawTime(stack, entity);
        powerFactor = (powerFactor * powerFactor + powerFactor * 2.0F) / 3.0F;
        if (powerFactor > 1.0F) {
            powerFactor = 1.0F;
        }
        return powerFactor;
    }

    public float getLaunchVelocity(ItemStack stack, LivingEntity entity){
        float vel = baseLaunchVel;
        for (IRangedWeaponEffect weaponEffect : getWeaponEffects()){
            vel = weaponEffect.modifyLaunchVelocity(vel, stack, entity);
        }
        return vel;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)entityLiving;
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
                if (!((double)powerFactor < 0.1D)) {
                    boolean hasAmmo = player.abilities.isCreativeMode || (ammoStack.getItem() instanceof ArrowItem && ((ArrowItem)ammoStack.getItem()).isInfinite(ammoStack, stack, player));
                    if (!worldIn.isRemote) {
                        ArrowItem arrowItem = (ArrowItem)(ammoStack.getItem() instanceof ArrowItem ? ammoStack.getItem() : Items.ARROW);
                        AbstractArrowEntity arrowEntity = arrowItem.createArrow(worldIn, ammoStack, player);
                        arrowEntity = customeArrow(arrowEntity);
                        arrowEntity.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F,
                                powerFactor * getLaunchVelocity(stack, entityLiving), 1.0F);
                        if (powerFactor == 1.0F) {
                            arrowEntity.setIsCritical(true);
                        }

                        int powerLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
                        if (powerLevel > 0) {
                            arrowEntity.setDamage(arrowEntity.getDamage() + (double)powerLevel * 0.5D + 0.5D);
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

    @Override
    public AbstractArrowEntity customeArrow(AbstractArrowEntity arrow) {
        // set item stack on cap here
        if (arrow.getShooter() instanceof LivingEntity){
            MKWeapons.getArrowCapability(arrow).ifPresent(cap ->
                    cap.setShootingWeapon(((LivingEntity) arrow.getShooter()).getHeldItemMainhand()));
        }
        arrow.setDamage(arrow.getDamage() + getMKTier().getAttackDamage());
        return super.customeArrow(arrow);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (getMKTier().getAttackDamage() > 0){
            tooltip.add(new StringTextComponent(I18n.format("mkweapons.bow_extra_damage.description",
                    getMKTier().getAttackDamage())).applyTextStyle(TextFormatting.GRAY));
        }
        for (IRangedWeaponEffect weaponEffect : getWeaponEffects()){
            weaponEffect.addInformation(stack, worldIn, tooltip, flagIn);
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
}
