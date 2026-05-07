package io.github.gnya.sheep_mod.mixins.villager;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.gnya.sheep_mod.core.SheepModSensorTypes;
import java.util.Collection;
import java.util.stream.Stream;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.npc.villager.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Brain.Provider.class)
public abstract class BrainProviderMixin {
  @ModifyArg(
      method = "makeBrain",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/world/entity/ai/Brain;<init>(Ljava/util/Collection;Ljava/util/Collection;Ljava/util/List;Lnet/minecraft/world/entity/ai/memory/MemoryMap;Lnet/minecraft/util/RandomSource;)V"),
      index = 1)
  public <E extends LivingEntity>
      Collection<SensorType<? extends Sensor<? super E>>> modifyMakeBrain(
          Collection<SensorType<? extends Sensor<? super E>>> sensorTypes,
          final @Local(argsOnly = true) E body) {
    // 村人の場合のみNEAREST_BED_SHEEPのセンサーを追加する
    if (body instanceof Villager) {
      sensorTypes =
          Stream.concat(sensorTypes.stream(), Stream.of(SheepModSensorTypes.NEAREST_BED_SHEEP))
              .collect(ImmutableList.toImmutableList());
    }

    return sensorTypes;
  }
}
