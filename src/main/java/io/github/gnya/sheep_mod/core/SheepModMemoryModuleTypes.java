package io.github.gnya.sheep_mod.core;

import java.util.Optional;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class SheepModMemoryModuleTypes {
  public static final MemoryModuleType<LivingEntity> NEAREST_BED_SHEEP = register("bed_sheep");

  public static <O> MemoryModuleType<O> register(final String name) {
    var type = new MemoryModuleType<O>(Optional.empty());

    SheepModRegistries.MEMORY_MODULE_TYPES.register(name, () -> type);

    return type;
  }

  public static void init() {}
}
