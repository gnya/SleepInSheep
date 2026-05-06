package io.github.gnya.sheep_mod.core;

import com.mojang.serialization.MapCodec;
import io.github.gnya.sheep_mod.particles.SheepParticle;
import io.github.gnya.sheep_mod.particles.SheepParticleOptions;
import io.github.gnya.sheep_mod.particles.SleepParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import org.jspecify.annotations.NonNull;

public class SheepModParticleTypes {
  public static final SimpleParticleType SLEEP_PARTICLE = register("sleep", false);

  public static final ParticleType<SheepParticleOptions> SHEEP_PARTICLE =
      register("sheep", false, SheepParticleOptions.CODEC, SheepParticleOptions.STREAM_CODEC);

  public static <O extends ParticleOptions> ParticleType<O> register(
      final String name,
      final boolean overrideLimiter,
      final MapCodec<O> codec,
      final StreamCodec<RegistryFriendlyByteBuf, O> streamCodec) {
    return register(
        name,
        new ParticleType<O>(overrideLimiter) {
          @Override
          public @NonNull MapCodec<O> codec() {
            return codec;
          }

          @Override
          public @NonNull StreamCodec<RegistryFriendlyByteBuf, O> streamCodec() {
            return streamCodec;
          }
        });
  }

  public static SimpleParticleType register(final String name, final boolean overrideLimiter) {
    return register(name, new SimpleParticleType(overrideLimiter));
  }

  public static <T extends ParticleType<? extends ParticleOptions>> T register(
      final String name, final T value) {
    SheepModRegistries.PARTICLE_TYPES.register(name, () -> value);

    return value;
  }

  public static void init() {
    RegisterParticleProvidersEvent.BUS.addListener(
        event -> {
          event.registerSpriteSet(SLEEP_PARTICLE, SleepParticle.Provider::new);
          event.registerSpriteSet(SHEEP_PARTICLE, SheepParticle.Provider::new);
        });
  }
}
