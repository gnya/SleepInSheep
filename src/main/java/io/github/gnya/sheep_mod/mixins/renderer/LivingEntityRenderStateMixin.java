package io.github.gnya.sheep_mod.mixins.renderer;

import io.github.gnya.sheep_mod.api.mixins.IMixinLivingEntityRenderState;
import io.github.gnya.sheep_mod.api.mixins.SheepSleeper;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntityRenderState.class)
@Implements(@Interface(iface = IMixinLivingEntityRenderState.class, prefix = "sheep_mod$"))
public abstract class LivingEntityRenderStateMixin {
  @Unique private SheepSleeper.SleepType private$sleepInSheepType;

  @Unique private float private$vehicleSheepYRot;

  public SheepSleeper.SleepType sheep_mod$getSleepInSheepType() {
    return this.private$sleepInSheepType;
  }

  public void sheep_mod$setSleepInSheepType(final SheepSleeper.SleepType type) {
    this.private$sleepInSheepType = type;
  }

  public boolean sheep_mod$isSleepInSheep() {
    return this.private$sleepInSheepType != SheepSleeper.SleepType.NONE;
  }

  public float sheep_mod$getBedSheepYRot() {
    return this.private$vehicleSheepYRot;
  }

  public void sheep_mod$setBedSheepYRot(final float value) {
    this.private$vehicleSheepYRot = value;
  }
}
