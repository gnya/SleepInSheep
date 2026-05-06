package io.github.gnya.sheep_mod.particles;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.gnya.sheep_mod.core.SheepModParticleTypes;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ScalableParticleOptionsBase;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

public class SheepParticleOptions extends ScalableParticleOptionsBase {
  public static final MapCodec<SheepParticleOptions> CODEC =
      RecordCodecBuilder.mapCodec(
          i ->
              i.group(
                      ExtraCodecs.RGB_COLOR_CODEC.fieldOf("color").forGetter(o -> o.color),
                      SCALE.fieldOf("scale").forGetter(ScalableParticleOptionsBase::getScale))
                  .apply(i, SheepParticleOptions::new));

  public static final StreamCodec<RegistryFriendlyByteBuf, SheepParticleOptions> STREAM_CODEC =
      StreamCodec.composite(
          ByteBufCodecs.INT,
          o -> o.color,
          ByteBufCodecs.FLOAT,
          ScalableParticleOptionsBase::getScale,
          SheepParticleOptions::new);

  private final int color;

  public SheepParticleOptions(final int color, final float scale) {
    super(scale);
    this.color = color;
  }

  @Override
  public @NonNull ParticleType<SheepParticleOptions> getType() {
    return SheepModParticleTypes.SHEEP_PARTICLE;
  }

  public Vector3f getColor() {
    return ARGB.vector3fFromRGB24(this.color);
  }
}
