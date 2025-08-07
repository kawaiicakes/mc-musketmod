package ewewukek.musketmod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
public class GunfireSmokeParticle extends TextureSheetParticle {
    private static final float SPREAD = 3700.0F;
    private static final float QUAD_GROWTH_FACTOR = 1004.0F;
    private static final float INITIAL_SPEED = 14.0F;
    private static final double MIN_DRAG_SPEED = 0.001;

    protected final float initialQuadSize;

    protected GunfireSmokeParticle(
            ClientLevel level,
            double posX, double posY, double posZ,
            double vX, double vY, double vZ,
            float scale, int lifetime
    ) {
        super(level, posX, posY, posZ);

        this.scale(scale);
        this.initialQuadSize = this.quadSize;
        this.setSize((2.5F / 3.0F) * scale, (2.5F / 3.0F) * scale);

        this.lifetime = this.random.nextInt(80) + lifetime;

        this.gravity = 1.3E-5F;

        // initial velocity is merely setting the direction of the smoke.
        this.xd = vX;
        this.yd = vY;
        this.zd = vZ;
    }

    /**
     * Oh my God, this is nesting hell.... Welp. Quick and dirty makes it to release, I guess.
     */
    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age == 0) {
            this.xd *= INITIAL_SPEED;
            this.yd *= INITIAL_SPEED;
            this.zd *= INITIAL_SPEED;

            // TODO - add more initial spread here
        }

        this.quadSize *= (this.quadSize / QUAD_GROWTH_FACTOR) + 1.0F;

        if (this.age++ < this.lifetime && !(this.alpha <= 0.0F)) {
            Vec3 vel = new Vec3(this.xd, this.yd, this.zd);
            if (vel.lengthSqr() >= MIN_DRAG_SPEED) {
                // TODO - exacerbate random spread here; smoke should curve along a line as it slows
                double scale = Mth.lerp(0.75, 0.0, vel.length());
                vel = vel.normalize().scale(scale);

                this.xd = vel.x;
                this.yd = vel.y;
                this.zd = vel.z;
            } else {
                // Brownian motion simulation lol
                this.xd += this.random.nextFloat() / SPREAD * (float)(this.random.nextBoolean() ? 1 : -1);
                this.zd += this.random.nextFloat() / SPREAD * (float)(this.random.nextBoolean() ? 1 : -1);
            }

            this.yd += this.gravity;

            this.move(this.xd, this.yd, this.zd);

            // TODO - I'm not a fan of this. Perhaps alpha should change as a function of the quad size
            if (this.age >= this.lifetime - 60) {
                if (this.alpha > 0.01F) {
                    this.alpha -= 0.010F;
                }
            } else {
                this.alpha -= 1.0E-6F;
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
                    level, posX, posY, posZ, vX, vY, vZ, 5.0F,
                    600);

            gunfireSmokeParticle.setAlpha(0.97F);
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
                    level, posX, posY, posZ, vX, vY, vZ, 5.0F, 700);

            gunfireSmokeParticle.setAlpha(0.97F);
            gunfireSmokeParticle.pickSprite(this.sprites);

            return gunfireSmokeParticle;
        }
    }
}
