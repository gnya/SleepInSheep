package io.github.gnya.sheep_mod.core.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class SheepParticle extends SingleQuadParticle {
  protected float dRoll;

  protected SheepParticle(
      final ClientLevel level,
      final double x,
      final double y,
      final double z,
      final double xa,
      final double ya,
      final double za,
      final SheepParticleOptions options,
      final SpriteSet sprites) {
    super(level, x, y, z, sprites.get(level.getRandom()));

    this.gravity = 0.005F;
    this.xd = xa;
    this.yd = ya;
    this.zd = za;
    this.roll = this.random.nextFloat() * 360.0F;
    this.oRoll = this.roll;
    this.dRoll = this.random.nextBoolean() ? -0.04F : 0.04F;
    this.quadSize *= options.getScale() * 0.5F;

    Vector3f color = options.getColor();
    this.rCol = (this.random.nextFloat() * 0.1F + 0.9F) * color.x();
    this.gCol = (this.random.nextFloat() * 0.1F + 0.9F) * color.y();
    this.bCol = (this.random.nextFloat() * 0.1F + 0.9F) * color.z();
    this.alpha = 0.0F;

    this.lifetime = 60;
  }

  @Override
  public void tick() {
    super.tick();
    this.oRoll = this.roll;

    if (!this.removed) {
      this.dRoll *= this.friction;

      if (this.onGround) {
        this.roll *= 0.7F;
      }

      float remaining = Math.abs(this.lifetime - this.age);
      float linearIn = Math.min(10, this.age) / 10.0F;
      float linearOut = Math.min(15, remaining) / 15.0F;

      this.alpha = (float) Mth.smoothstep(Math.min(linearIn, linearOut));
    }
  }

  @Override
  protected @NonNull Layer getLayer() {
    return Layer.TRANSLUCENT;
  }

  public static class Provider implements ParticleProvider<SheepParticleOptions> {
    private final SpriteSet sprites;

    public Provider(final SpriteSet sprites) {
      this.sprites = sprites;
    }

    @Override
    public @Nullable Particle createParticle(
        @NonNull SheepParticleOptions options,
        @NonNull ClientLevel level,
        double x,
        double y,
        double z,
        double xAux,
        double yAux,
        double zAux,
        @NonNull RandomSource random) {
      return new SheepParticle(level, x, y, z, xAux, yAux, zAux, options, this.sprites);
    }
  }
}
