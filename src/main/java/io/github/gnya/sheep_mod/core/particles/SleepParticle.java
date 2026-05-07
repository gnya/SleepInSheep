package io.github.gnya.sheep_mod.core.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class SleepParticle extends SingleQuadParticle {
  protected SleepParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
    super(level, x, y, z, sprites.first());

    this.gravity = 0.0F;
    this.yd = 0.005F;
    this.quadSize *= 0.4F;
    this.alpha = 0.0F;
    this.lifetime = 60;
  }

  @Override
  public void tick() {
    super.tick();

    float remaining = Math.abs(this.lifetime - this.age);
    float linearIn = Math.min(10, this.age) / 10.0F;
    float linearOut = Math.min(15, remaining) / 15.0F;

    this.alpha = (float) Mth.smoothstep(Math.min(linearIn, linearOut));
  }

  @Override
  protected @NonNull Layer getLayer() {
    return Layer.TRANSLUCENT;
  }

  public static class Provider implements ParticleProvider<SimpleParticleType> {
    private final SpriteSet sprites;

    public Provider(final SpriteSet sprites) {
      this.sprites = sprites;
    }

    @Override
    public @Nullable Particle createParticle(
        @NonNull SimpleParticleType options,
        @NonNull ClientLevel level,
        double x,
        double y,
        double z,
        double xAux,
        double yAux,
        double zAux,
        @NonNull RandomSource random) {
      return new SleepParticle(level, x, y, z, this.sprites);
    }
  }
}
