package ewewukek.musketmod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

// TODO - rework physics of these.
// TODO - speed of particle dependent on config value of projectile speed
@OnlyIn(Dist.CLIENT)
public class GunfireSmokeParticle extends TextureSheetParticle {
    private static final float SPREAD = 5000.0F;

    protected GunfireSmokeParticle(
            ClientLevel level,
            double posX, double posY, double posZ,
            double vX, double vY, double vZ,
            int lifetime
    ) {
        super(level, posX, posY, posZ);

        this.scale(3.0F);
        this.setSize(2.5F, 2.5F);

        this.lifetime = this.random.nextInt(80) + lifetime;

        this.gravity = 3.0E-4F;

        this.xd = vX;
        this.yd = vY;
        this.zd = vZ;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ < this.lifetime && !(this.alpha <= 0.0F)) {
            this.xd += this.random.nextFloat() / SPREAD * (float)(this.random.nextBoolean() ? 1 : -1);
            this.zd += this.random.nextFloat() / SPREAD * (float)(this.random.nextBoolean() ? 1 : -1);

            this.yd += this.random.nextFloat() / SPREAD * (float)(this.random.nextBoolean() ? 1 : -1);
            this.yd += this.gravity;

            this.move(this.xd, this.yd, this.zd);

            if (this.age >= this.lifetime - 60 && this.alpha > 0.01F) {
                this.alpha -= 0.015F;
            }
        } else {
            this.remove();
        }
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class TwoHandedGunProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public TwoHandedGunProvider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @ParametersAreNonnullByDefault
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double posX, double posY, double posZ, double vX, double vY, double vZ) {
            GunfireSmokeParticle gunfireSmokeParticle = new GunfireSmokeParticle(
                    level, posX, posY, posZ, vX, vY, vZ, 600
            );

            gunfireSmokeParticle.setAlpha(1.00F);
            gunfireSmokeParticle.pickSprite(this.sprites);

            return gunfireSmokeParticle;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class OneHandedGunProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public OneHandedGunProvider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @ParametersAreNonnullByDefault
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double posX, double posY, double posZ, double vX, double vY, double vZ) {
            GunfireSmokeParticle gunfireSmokeParticle = new GunfireSmokeParticle(
                    level, posX, posY, posZ, vX, vY, vZ, 700
            );

            gunfireSmokeParticle.setAlpha(1.00F);
            gunfireSmokeParticle.pickSprite(this.sprites);

            return gunfireSmokeParticle;
        }
    }
}
