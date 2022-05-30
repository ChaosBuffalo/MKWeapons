package com.chaosbuffalo.mkweapons.items.effects.ranged;

import com.chaosbuffalo.mkcore.MKCore;
import com.chaosbuffalo.mkcore.abilities.MKAbility;
import com.chaosbuffalo.mkcore.core.IMKEntityData;
import com.chaosbuffalo.mkcore.core.MKAttributes;
import com.chaosbuffalo.mkweapons.ClientUtils;
import com.chaosbuffalo.mkweapons.MKWeapons;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class RangedSkillScalingEffect extends BaseRangedWeaponEffect{
    public static final ResourceLocation NAME = new ResourceLocation(MKWeapons.MODID, "weapon_effect.ranged_skill_scaling");
    public static final UUID skillScaling = UUID.fromString("e4e8a04a-6c8e-43f6-9599-99a84f207c60");
    private double baseDamage;
    private Attribute skill;

    public RangedSkillScalingEffect() {
        super(NAME, TextFormatting.GRAY);
    }

    public RangedSkillScalingEffect(double baseDamage, Attribute skill){
        this();
        this.baseDamage = baseDamage;
        this.skill = skill;
    }

    @Override
    public void onProjectileHit(LivingHurtEvent event, DamageSource source, LivingEntity livingTarget, LivingEntity livingSource, IMKEntityData sourceData, AbstractArrowEntity arrow, ItemStack bow) {
        MKCore.getPlayer(livingSource).ifPresent(x -> x.getSkills().tryScaledIncreaseSkill(skill, 0.5));
    }

//    @Override
//    public double modifyArrowDamage(double inDamage, LivingEntity shooter, AbstractArrowEntity arrow) {
//        float skillLevel = MKAbility.getSkillLevel(shooter, skill);
//        double bonus = skillLevel * baseDamage;
//        return inDamage + bonus;
//    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
        tooltip.add(new TranslationTextComponent(skill.getAttributeName()).mergeStyle(color));
        if (Screen.hasShiftDown()){
            float skillLevel = ClientUtils.getClientSkillLevel(skill);
            double bonus = skillLevel * baseDamage;
            tooltip.add(new TranslationTextComponent("mkweapons.weapon_effect.ranged_skill_scaling.description",
                    new TranslationTextComponent(skill.getAttributeName()), MKAbility.NUMBER_FORMATTER.format(bonus)));
        }
    }

    @Override
    public void onEntityEquip(LivingEntity entity) {
        float skillLevel = MKAbility.getSkillLevel(entity, skill);
        ModifiableAttributeInstance attr = entity.getAttribute(MKAttributes.RANGED_DAMAGE);
        if (attr != null){
            if (attr.getModifier(skillScaling) == null){
                attr.applyNonPersistentModifier(new AttributeModifier(skillScaling, "skill scaling", skillLevel * baseDamage, AttributeModifier.Operation.ADDITION));
            }
        }
    }

    @Override
    public void onSkillChange(PlayerEntity player) {
        onEntityUnequip(player);
        onEntityEquip(player);
    }

    @Override
    public void onEntityUnequip(LivingEntity entity) {
        ModifiableAttributeInstance attr = entity.getAttribute(MKAttributes.RANGED_DAMAGE);
        if (attr != null) {
            attr.removeModifier(skillScaling);
        }
    }

    @Override
    public <D> void readAdditionalData(Dynamic<D> dynamic) {
        baseDamage = dynamic.get("baseDamage").asDouble(0.0);
        dynamic.get("skill").asString().result().ifPresent(x -> {
            skill = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(x));
        });
    }

    @Override
    public <D> void writeAdditionalData(DynamicOps<D> ops, ImmutableMap.Builder<D, D> builder) {
        builder.put(ops.createString("baseDamage"), ops.createDouble(baseDamage));
        builder.put(ops.createString("skill"), ops.createString(skill.getRegistryName().toString()));
    }
}
