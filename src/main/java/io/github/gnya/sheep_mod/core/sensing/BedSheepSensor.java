package io.github.gnya.sheep_mod.core.sensing;

import io.github.gnya.sheep_mod.api.mixins.IMixinSheep;
import io.github.gnya.sheep_mod.core.SheepModMemoryModuleTypes;
import java.util.List;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.animal.sheep.Sheep;
import org.jspecify.annotations.NonNull;

public class BedSheepSensor extends Sensor<LivingEntity> {
  @Override
  public @NonNull Set<MemoryModuleType<?>> requires() {
    return Set.of(
        MemoryModuleType.NEAREST_LIVING_ENTITIES, SheepModMemoryModuleTypes.NEAREST_BED_SHEEP);
  }

  @Override
  protected void doTick(final @NonNull ServerLevel level, final @NonNull LivingEntity body) {
    body.getBrain()
        .getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES)
        .ifPresent(entities -> this.setNearestBedSheep(body, entities));
  }

  protected void setNearestBedSheep(final LivingEntity body, final List<LivingEntity> entities) {
    var sheep =
        entities.stream()
            .filter(e -> e instanceof Sheep && ((IMixinSheep) e).canSleepIn())
            .findFirst();

    body.getBrain().setMemory(SheepModMemoryModuleTypes.NEAREST_BED_SHEEP, sheep);
  }
}
