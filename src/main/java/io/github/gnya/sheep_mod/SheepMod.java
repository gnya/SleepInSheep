package io.github.gnya.sheep_mod;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import io.github.gnya.sheep_mod.particles.SheepParticle;
import io.github.gnya.sheep_mod.particles.SheepParticleOptions;
import io.github.gnya.sheep_mod.particles.SleepParticle;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

@Mod(SheepMod.MODID)
public class SheepMod {
  public static final String MODID = "sheep_mod";
  public static final Logger LOGGER = LogUtils.getLogger();

  public static final DeferredRegister<ParticleType<?>> PARTICLES =
      DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MODID);

  public static final RegistryObject<SimpleParticleType> SLEEP_PARTICLE =
      PARTICLES.register("sleep", () -> new SimpleParticleType(false));

  public static final RegistryObject<ParticleType<SheepParticleOptions>> SHEEP_PARTICLE =
      PARTICLES.register(
          "sheep",
          () ->
              new ParticleType<>(false) {
                @Override
                public @NonNull MapCodec<SheepParticleOptions> codec() {
                  return SheepParticleOptions.CODEC;
                }

                @Override
                public @NonNull StreamCodec<RegistryFriendlyByteBuf, SheepParticleOptions>
                    streamCodec() {
                  return SheepParticleOptions.STREAM_CODEC;
                }
              });

  public SheepMod(FMLJavaModLoadingContext context) {
    PARTICLES.register(context.getModBusGroup());

    RegisterParticleProvidersEvent.BUS.addListener(
        event -> {
          event.registerSpriteSet(SLEEP_PARTICLE.get(), SleepParticle.Provider::new);
          event.registerSpriteSet(SHEEP_PARTICLE.get(), SheepParticle.Provider::new);
        });
  }
}
