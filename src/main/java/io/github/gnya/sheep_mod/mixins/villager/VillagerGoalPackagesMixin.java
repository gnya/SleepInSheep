package io.github.gnya.sheep_mod.mixins.villager;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import io.github.gnya.sheep_mod.api.mixins.IMixinSheep;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.InteractWith;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTarget;
import net.minecraft.world.entity.ai.behavior.VillagerGoalPackages;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(VillagerGoalPackages.class)
public abstract class VillagerGoalPackagesMixin {
  @Unique
  private static List<Pair<? extends BehaviorControl<? extends LivingEntity>, Integer>>
      private$modifyPackage(
          List<Pair<? extends BehaviorControl<? extends LivingEntity>, Integer>> weightedBehaviors,
          @Local(argsOnly = true) float speedModifier) {
    // 村人がHappyな羊を追いかけるようにする
    return Stream.concat(
            Stream.of(
                Pair.of(
                    InteractWith.of(
                        EntityType.SHEEP,
                        32,
                        _ -> true,
                        mob -> ((IMixinSheep) mob).isHappy(),
                        MemoryModuleType.INTERACTION_TARGET,
                        speedModifier,
                        2),
                    16)),
            weightedBehaviors.stream())
        .collect(ImmutableList.toImmutableList());
  }

  @ModifyArg(
      method = "getPlayPackage",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/world/entity/ai/behavior/RunOne;<init>(Ljava/util/Map;Ljava/util/List;)V"),
      index = 1)
  private static List<Pair<? extends BehaviorControl<? extends LivingEntity>, Integer>>
      modifyGetPlayPackage(
          List<Pair<? extends BehaviorControl<? extends LivingEntity>, Integer>> weightedBehaviors,
          @Local(argsOnly = true) float speedModifier) {
    return VillagerGoalPackagesMixin.private$modifyPackage(weightedBehaviors, speedModifier);
  }

  @ModifyArg(
      method = "getIdlePackage",
      at =
          @At(
              value = "INVOKE",
              target = "Lnet/minecraft/world/entity/ai/behavior/RunOne;<init>(Ljava/util/List;)V"))
  private static List<Pair<? extends BehaviorControl<? extends LivingEntity>, Integer>>
      modifyGetIdlePackage(
          List<Pair<? extends BehaviorControl<? extends LivingEntity>, Integer>> weightedBehaviors,
          @Local(argsOnly = true) float speedModifier) {
    return VillagerGoalPackagesMixin.private$modifyPackage(weightedBehaviors, speedModifier);
  }

  @ModifyArg(
      method = "getFullLookBehavior",
      at =
          @At(
              value = "INVOKE",
              target = "Lnet/minecraft/world/entity/ai/behavior/RunOne;<init>(Ljava/util/List;)V"),
      index = 0)
  private static List<Pair<? extends BehaviorControl<? extends LivingEntity>, Integer>>
      modifyGetFullLookBehavior(
          List<Pair<? extends BehaviorControl<? extends LivingEntity>, Integer>>
              weightedBehaviors) {
    // 村人がHappyな羊を見るようにする
    return Stream.concat(
            Stream.of(
                Pair.of(
                    SetEntityLookTarget.create(
                        mob -> mob.is(EntityType.SHEEP) && ((IMixinSheep) mob).isHappy(), 8.0F),
                    16)),
            weightedBehaviors.stream())
        .collect(ImmutableList.toImmutableList());
  }
}
