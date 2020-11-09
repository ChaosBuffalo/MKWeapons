package com.chaosbuffalo.mkweapons.mixins;


import com.chaosbuffalo.mkweapons.items.weapon.IMKMeleeWeapon;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public class NoSweepMixin {

    @Redirect(at = @At(value = "INVOKE", target="Lnet/minecraft/entity/player/PlayerEntity;getHeldItem(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"),
            method = "attackTargetEntityWithCurrentItem(Lnet/minecraft/entity/Entity;)V")
    private ItemStack proxyGetItem(PlayerEntity playerEntity, Hand hand){
        ItemStack stack = playerEntity.getHeldItem(hand);
        if (stack.getItem() instanceof IMKMeleeWeapon){
            return ((IMKMeleeWeapon) stack.getItem()).allowSweep() ? stack : ItemStack.EMPTY;
        }
        return stack;
    }
}
