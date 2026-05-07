package io.github.gnya.sheep_mod.core;

import io.github.gnya.sheep_mod.SheepMod;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public class SheepModRegistries {
  private static final List<Consumer<BusGroup>> REG_INITS = new ArrayList<>();

  private static final List<Runnable> TYPE_INITS = new ArrayList<>();

  public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
      register(ForgeRegistries.PARTICLE_TYPES, SheepModParticleTypes::init);

  public static final DeferredRegister<MemoryModuleType<?>> MEMORY_MODULE_TYPES =
      register(ForgeRegistries.MEMORY_MODULE_TYPES, SheepModMemoryModuleTypes::init);

  public static final DeferredRegister<SensorType<?>> SENSOR_TYPES =
      register(ForgeRegistries.SENSOR_TYPES, SheepModSensorTypes::init);

  public static <T> DeferredRegister<T> register(IForgeRegistry<T> reg, Runnable init) {
    var registry = DeferredRegister.create(reg, SheepMod.MODID);

    REG_INITS.add(registry::register);
    TYPE_INITS.add(init);

    return registry;
  }

  public static void init(FMLJavaModLoadingContext context) {
    var busGroup = context.getModBusGroup();

    REG_INITS.forEach(l -> l.accept(busGroup));
    TYPE_INITS.forEach(Runnable::run);
  }
}
