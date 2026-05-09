package io.github.gnya.sheep.mixins.renderer;

import io.github.gnya.sheep.api.mixins.IMixinLivingEntityRenderState;
import io.github.gnya.sheep.api.mixins.SheepSleeper;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntityRenderState.class)
@Implements(@Interface(iface = IMixinLivingEntityRenderState.class, prefix = "sheep$"))
public abstract class LivingEntityRenderStateMixin {
  @Unique private SheepSleeper.SleepType private$sleepInSheepType;

  @Unique private float private$vehicleSheepYRot;

  public SheepSleeper.SleepType sheep$getSleepInSheepType() {
    return this.private$sleepInSheepType;
  }

  public void sheep$setSleepInSheepType(final SheepSleeper.SleepType type) {
    this.private$sleepInSheepType = type;
  }

  public boolean sheep$isSleepInSheep() {
    return this.private$sleepInSheepType != SheepSleeper.SleepType.NONE;
  }

  public float sheep$getBedSheepYRot() {
    return this.private$vehicleSheepYRot;
  }

  public void sheep$setBedSheepYRot(final float value) {
    this.private$vehicleSheepYRot = value;
  }
}
