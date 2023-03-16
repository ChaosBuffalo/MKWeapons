package com.chaosbuffalo.mkweapons.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;

import javax.annotation.Nullable;

public class BloodDripParticle extends SpriteTexturedParticle {

    private BloodDripParticle(ClientWorld world, double posX, double posY, double posZ) {
        super(world, posX, posY, posZ);
        this.setSize(0.01F, 0.01F);
        this.particleGravity = 0.06F;
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    protected void expire() {
        if (this.maxAge-- <= 0) {
            this.setExpired();
        }
    }

    protected void onUpdate() {
        if (this.onGround) {
            this.setExpired();
        }
    }

    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.expire();
        if (!this.isExpired) {
            this.motionY -= this.particleGravity;
            this.move(this.motionX, this.motionY, this.motionZ);
            this.onUpdate();
            if (!this.isExpired) {
                this.motionX *= 0.98F;
                this.motionY *= 0.98F;
                this.motionZ *= 0.98F;
            }
        }
    }

    public static class BloodDripFactory implements IParticleFactory<BasicParticleType> {
        protected final IAnimatedSprite spriteSet;

        public BloodDripFactory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            BloodDripParticle drip = new BloodDripParticle(worldIn, x, y, z);
            drip.selectSpriteRandomly(this.spriteSet);
            return drip;
        }
    }
}
