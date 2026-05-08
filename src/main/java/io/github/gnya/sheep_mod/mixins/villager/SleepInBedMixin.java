package io.github.gnya.sheep_mod.mixins.villager;

import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.gnya.sheep_mod.api.mixins.IMixinSheep;
import io.github.gnya.sheep_mod.api.mixins.SheepSleeper;
import io.github.gnya.sheep_mod.core.SheepModMemoryModuleTypes;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.SleepInBed;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SleepInBed.class)
public abstract class SleepInBedMixin extends Behavior<LivingEntity> {
  private SleepInBedMixin(Map<MemoryModuleType<?>, MemoryStatus> entryCondition) {
    // ダミーコンストラクタ
    super(entryCondition);
  }

  @Redirect(
      method = "<init>",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lcom/google/common/collect/ImmutableMap;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap;"))
  private static <K, V> ImmutableMap<MemoryModuleType<?>, MemoryStatus> redirectOnInit(
      K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
    // NOTE 引数の数が固定になってしまうのでバニラのコードに変更があった場合はここも変更する必要があります
    return ImmutableMap.<MemoryModuleType<?>, MemoryStatus>builder()
        .put((MemoryModuleType<?>) k1, (MemoryStatus) v1)
        .put((MemoryModuleType<?>) k2, (MemoryStatus) v2)
        .put((MemoryModuleType<?>) k3, (MemoryStatus) v3)
        .put((MemoryModuleType<?>) k4, (MemoryStatus) v4)
        .put((MemoryModuleType<?>) k5, (MemoryStatus) v5)
        .put(MemoryModuleType.HOME, MemoryStatus.REGISTERED)
        .put(SheepModMemoryModuleTypes.NEAREST_BED_SHEEP, MemoryStatus.REGISTERED)
        .buildKeepingLast();
  }

  @Unique
  private Optional<GlobalPos> private$getTargetPos(Brain<?> brain) {
    var target = brain.getMemory(MemoryModuleType.HOME);

    if (target.isEmpty()) {
      var sheep = brain.getMemory(SheepModMemoryModuleTypes.NEAREST_BED_SHEEP);

      if (sheep.isPresent()) {
        target =
            Optional.of(GlobalPos.of(sheep.get().level().dimension(), sheep.get().blockPosition()));
      }
    }

    return target;
  }

  @Redirect(
      method = "checkExtraStartConditions",
      at =
          @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isPassenger()Z"))
  public boolean redirectCheckExtraStartConditions(LivingEntity body) {
    Brain<?> brain = body.getBrain();

    // HOMEもNEAREST_BED_SHEEPも値を持っていないならスキップする
    return body.isPassenger()
        || (!brain.hasMemoryValue(MemoryModuleType.HOME)
            && !brain.hasMemoryValue(SheepModMemoryModuleTypes.NEAREST_BED_SHEEP));
  }

  @Redirect(
      method = "checkExtraStartConditions",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/world/entity/ai/Brain;getMemory(Lnet/minecraft/world/entity/ai/memory/MemoryModuleType;)Ljava/util/Optional;",
              ordinal = 0))
  protected Optional<GlobalPos> redirectCheckExtraStartConditions(
      Brain<?> brain, MemoryModuleType<GlobalPos> type) {
    // HOMEかNEAREST_BED_SHEEPの値は持っているのでEmptyにはならない
    return this.private$getTargetPos(brain);
  }

  @Redirect(
      method = "checkExtraStartConditions",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/tags/TagKey;)Z"))
  protected boolean redirectCheckExtraStartConditions(
      BlockState blockState, TagKey<Block> tag, @Local(name = "brain") Brain<?> brain) {
    if (!brain.hasMemoryValue(MemoryModuleType.HOME)) {
      return true;
    }

    return blockState.is(tag);
  }

  @Redirect(
      method = "checkExtraStartConditions",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;"))
  protected Comparable<Boolean> redirectCheckExtraStartConditions(
      BlockState blockState, Property<Boolean> property, @Local(name = "brain") Brain<?> brain) {
    if (!brain.hasMemoryValue(MemoryModuleType.HOME)) {
      var sheep = brain.getMemory(SheepModMemoryModuleTypes.NEAREST_BED_SHEEP).orElseThrow();

      return !((IMixinSheep) sheep).canSleepIn() || !sheep.getPassengers().isEmpty();
    }

    return blockState.getValue(property);
  }

  @Redirect(
      method = "canStillUse",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/world/entity/ai/Brain;getMemory(Lnet/minecraft/world/entity/ai/memory/MemoryModuleType;)Ljava/util/Optional;"))
  protected Optional<GlobalPos> redirectCanStillUse(
      Brain<?> brain, MemoryModuleType<GlobalPos> type) {
    return this.private$getTargetPos(brain);
  }

  @ModifyReturnValue(method = "canStillUse", at = @At("RETURN"))
  protected boolean modifyCanStillUse(boolean result, @Local(argsOnly = true) LivingEntity body) {
    var sheep = body.getBrain().getMemory(SheepModMemoryModuleTypes.NEAREST_BED_SHEEP);

    if (sheep.isEmpty()) {
      return result;
    } else {
      // 羊の上で寝る場合は村人の位置がずれることはないので条件が簡単になる
      return body.getBrain().isActive(Activity.REST);
    }
  }

  @Redirect(
      method = "start",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/world/entity/ai/Brain;getMemory(Lnet/minecraft/world/entity/ai/memory/MemoryModuleType;)Ljava/util/Optional;",
              ordinal = 2))
  protected Optional<GlobalPos> redirectStart(Brain<?> brain, MemoryModuleType<GlobalPos> type) {
    // checkExtraStartConditionのあとに呼ばれるのでEmptyになることはない
    return this.private$getTargetPos(brain);
  }

  @Redirect(
      method = "start",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/world/entity/LivingEntity;startSleeping(Lnet/minecraft/core/BlockPos;)V"))
  protected void redirectStart(LivingEntity body, BlockPos bedPosition) {
    Brain<?> brain = body.getBrain();

    if (!brain.hasMemoryValue(MemoryModuleType.HOME)) {
      // TODO NEAREST_BED_SHEEPをLivingEntityからSheepに変えてみる
      var sheep = brain.getMemory(SheepModMemoryModuleTypes.NEAREST_BED_SHEEP).orElseThrow();

      ((SheepSleeper) body).startSleeping((Sheep) sheep);
    } else {
      body.startSleeping(bedPosition);
    }
  }
}
