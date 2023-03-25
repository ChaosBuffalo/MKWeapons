package com.chaosbuffalo.mkweapons.mixins;


import com.chaosbuffalo.mkweapons.items.weapon.IMKMeleeWeapon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Player.class)
public class PlayerAttackMixins {

    // don't perform aoe sweep if we're an mk melee weapon
    @Redirect(at = @At(value = "INVOKE", target="Lnet/minecraft/entity/player/PlayerEntity;getHeldItem(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"),
            method = "attackTargetEntityWithCurrentItem(Lnet/minecraft/entity/Entity;)V")
    private ItemStack proxyGetItem(Player playerEntity, InteractionHand hand){
        ItemStack stack = playerEntity.getItemInHand(hand);
        if (stack.getItem() instanceof IMKMeleeWeapon){
            return ((IMKMeleeWeapon) stack.getItem()).allowSweep() ? stack : ItemStack.EMPTY;
        }
        return stack;
    }

    // disables reset cooldown as we handle it ourselves later
    @Redirect(at = @At(value= "INVOKE", target="Lnet/minecraft/entity/player/PlayerEntity;resetCooldown()V"),
        method = "attackTargetEntityWithCurrentItem(Lnet/minecraft/entity/Entity;)V")
    private void proxyResetCooldown(Player entity){

    }
}
