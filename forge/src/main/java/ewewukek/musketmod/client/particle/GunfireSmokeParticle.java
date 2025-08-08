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
    private static final float INITIAL_SPEED_MUL = 14.0F;
    private static final double MIN_DRAG_SPEED = 0.001;
    private static final float DEVIATION_FUDGE_FACTOR = 3.2E-5F;
    private static final float SMOKE_SCALE = 6.0F;

    protected final float initialQuadSize;
    protected final Vec3 initialVel;

    protected GunfireSmokeParticle(
            ClientLevel level,
            double posX, double posY, double posZ,
            double vX, double vY, double vZ
    ) {
        super(level, posX, posY, posZ);

        this.scale(SMOKE_SCALE);
        this.initialQuadSize = this.quadSize;
        this.setSize((0.25F / 3.0F) * SMOKE_SCALE, (0.25F / 3.0F) * SMOKE_SCALE);

        this.gravity = 1.3E-5F;

        // The velocity passed into this constructor is NOT normalized (its length being under 1).
        // It's saved to retrieve the initial speed later, as well as the path to deviate from when applying drag.
        this.initialVel = new Vec3(vX, vY, vZ);

        // initial velocity is merely setting the direction of the smoke; the final magnitude of the velocity is not
        // applied until later.
        this.xd = vX;
        this.yd = vY;
        this.zd = vZ;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age == 0) {
            Vec3 vel = this.initialVel.add(
                    this.random.nextIntBetweenInclusive(-100, 100) * DEVIATION_FUDGE_FACTOR,
                    this.random.nextIntBetweenInclusive(-100, 100) * DEVIATION_FUDGE_FACTOR,
                    this.random.nextIntBetweenInclusive(-100, 100) * DEVIATION_FUDGE_FACTOR
            ).normalize().scale(this.initialVel.length());

            this.xd = vel.x * INITIAL_SPEED_MUL;
            this.yd = vel.y * INITIAL_SPEED_MUL;
            this.zd = vel.z * INITIAL_SPEED_MUL;
        }

        this.quadSize *= (this.quadSize / QUAD_GROWTH_FACTOR) + 1.0F;

        this.age++;

        if (this.alpha <= 0) {
            this.remove();
            return;
        }

        Vec3 vel = new Vec3(this.xd, this.yd, this.zd);

        if (vel.lengthSqr() >= MIN_DRAG_SPEED) {
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

        this.alpha = this.initialQuadSize / this.quadSize;
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
                    level, posX, posY, posZ, vX, vY, vZ
            );

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
                    level, posX, posY, posZ, vX, vY, vZ);

            gunfireSmokeParticle.setAlpha(0.97F);
            gunfireSmokeParticle.pickSprite(this.sprites);

            return gunfireSmokeParticle;
        }
    }
}
