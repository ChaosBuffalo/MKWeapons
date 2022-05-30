package com.chaosbuffalo.mkweapons.items.effects.melee;

import com.chaosbuffalo.mkcore.MKCore;
import com.chaosbuffalo.mkcore.abilities.MKAbility;
import com.chaosbuffalo.mkweapons.ClientUtils;
import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.items.weapon.IMKMeleeWeapon;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class SkillScalingEffect extends BaseMeleeWeaponEffect{
    public static final ResourceLocation NAME = new ResourceLocation(MKWeapons.MODID, "weapon_effect.skill_scaling");
    public static final UUID skillScaling = UUID.fromString("5db76231-686d-417e-952b-92f33c4c1b37");
    private double baseDamage;
    private Attribute skill;

    public SkillScalingEffect() {
        super(NAME, TextFormatting.GRAY);
    }

    public SkillScalingEffect(double baseDamage, Attribute skill){
        this();
        this.baseDamage = baseDamage;
        this.skill = skill;
    }

    @Override
    public void onHit(IMKMeleeWeapon weapon, ItemStack stack, LivingEntity target, LivingEntity attacker) {
        MKCore.getPlayer(attacker).ifPresent(x -> x.getSkills().tryScaledIncreaseSkill(skill, 0.5));
    }

    @Override
    public void onSkillChange(PlayerEntity player) {
        onEntityUnequip(player);
        onEntityEquip(player);
    }

    @Override
    public void onEntityEquip(LivingEntity entity) {
        float skillLevel = MKAbility.getSkillLevel(entity, skill);
        ModifiableAttributeInstance attr = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attr != null){
            if (attr.getModifier(skillScaling) == null){
                attr.applyNonPersistentModifier(new AttributeModifier(skillScaling, "skill scaling", skillLevel * baseDamage, AttributeModifier.Operation.ADDITION));
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
        tooltip.add(new TranslationTextComponent(skill.getAttributeName()).mergeStyle(color));
        if (Screen.hasShiftDown()){
            float skillLevel = ClientUtils.getClientSkillLevel(skill);
            double bonus = skillLevel * baseDamage;
                tooltip.add(new TranslationTextComponent("mkweapons.weapon_effect.skill_scaling.description",
                        new TranslationTextComponent(skill.getAttributeName()), MKAbility.NUMBER_FORMATTER.format(bonus)));
        }
    }

    @Override
    public void onEntityUnequip(LivingEntity entity) {
        ModifiableAttributeInstance attr = entity.getAttribute(Attributes.ATTACK_DAMAGE);
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
