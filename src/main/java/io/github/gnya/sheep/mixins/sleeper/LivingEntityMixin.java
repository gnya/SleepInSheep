package io.github.gnya.sheep.mixins.sleeper;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.gnya.sheep.SheepMod;
import io.github.gnya.sheep.api.mixins.IMixinSheep;
import io.github.gnya.sheep.api.mixins.SheepSleeper;
import io.github.gnya.sheep.core.ModParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
@Implements(@Interface(iface = SheepSleeper.class, prefix = "sheep$"))
public abstract class LivingEntityMixin extends Entity {
  @Unique
  private static final EntityDataAccessor<Integer> DATA_SLEEP_IN_SHEEP =
      SynchedEntityData.defineId(LivingEntityMixin.class, EntityDataSerializers.INT);

  private LivingEntityMixin(EntityType<?> type, Level level) {
    // ダミーコンストラクタ
    super(type, level);
  }

  // LivingEntityでstopRiding()がOverrideされてるためこの定義が必要です
  @Shadow
  public abstract void stopRiding();

  @Shadow
  public abstract boolean isSleeping();

  @Shadow
  public abstract boolean isAlive();

  public SheepSleeper.SleepType sheep$getSleepInSheepType() {
    return SheepSleeper.SleepType.values()[this.entityData.get(DATA_SLEEP_IN_SHEEP)];
  }

  @Unique
  private void private$setSleepInSheepType(SheepSleeper.SleepType type) {
    this.entityData.set(DATA_SLEEP_IN_SHEEP, type.ordinal());
  }

  public boolean sheep$isSleepInSheep() {
    return this.sheep$getSleepInSheepType() != SheepSleeper.SleepType.NONE;
  }

  public @Nullable Sheep sheep$getBedSheep() {
    if (this.sheep$isSleepInSheep()) {
      return (Sheep) this.getVehicle();
    } else {
      return null;
    }
  }

  @ModifyReturnValue(method = "checkBedExists", at = @At("RETURN"))
  private boolean modifyCheckBedExists(boolean exists) {
    Sheep sheep = this.sheep$getBedSheep();

    return sheep != null ? ((IMixinSheep) sheep).canSleepIn() : exists;
  }

  @ModifyReturnValue(method = "isSleeping", at = @At("RETURN"))
  public boolean modifyIsSleeping(boolean sleep) {
    return sleep || this.sheep$isSleepInSheep();
  }

  public void sheep$startSleeping(final Sheep sheep) {
    this.sheep$LivingEntity$startSleeping(sheep);
  }

  public void sheep$LivingEntity$startSleeping(final Sheep sheep) {
    if (!((IMixinSheep) sheep).canSleepIn()
        || this.isSleeping()
        || !this.canRide(sheep)
        || !sheep.getPassengers().isEmpty()) {
      return;
    }

    SheepMod.LOGGER.debug("LivingEntity$startSleeping");

    this.private$setSleepInSheepType(
        this.random.nextBoolean()
            ? SheepSleeper.SleepType.FACE_UP
            : SheepSleeper.SleepType.FACE_DOWN);

    if (this.startRiding(sheep, false, true)) {
      sheep.playSound(SoundEvents.WOOL_HIT, 1.0F, this.random.triangle(1.0F, 0.2F));
    } else {
      this.private$setSleepInSheepType(SheepSleeper.SleepType.NONE);
    }
  }

  @Inject(method = "stopSleeping", at = @At("HEAD"), cancellable = true)
  public void stopSleeping(CallbackInfo ci) {
    if (!this.sheep$isSleepInSheep()) {
      return;
    }

    SheepMod.LOGGER.debug("stopSleeping: %s".formatted(this));

    this.stopRiding();
    this.private$setSleepInSheepType(SheepSleeper.SleepType.NONE);
    ci.cancel();
  }

  @Inject(method = "defineSynchedData", at = @At("TAIL"))
  protected void defineSynchedDataMixin(SynchedEntityData.Builder entityData, CallbackInfo ci) {
    entityData.define(DATA_SLEEP_IN_SHEEP, SheepSleeper.SleepType.NONE.ordinal());
  }

  @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
  protected void addAdditionalSaveDataMixin(ValueOutput output, CallbackInfo ci) {
    output.putInt("SleepInSheep", this.sheep$getSleepInSheepType().ordinal());
  }

  @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
  protected void readAdditionalSaveDataMixin(ValueInput input, CallbackInfo ci) {
    int ordinal = input.getIntOr("SleepInSheep", SheepSleeper.SleepType.NONE.ordinal());

    this.private$setSleepInSheepType(SheepSleeper.SleepType.values()[ordinal]);
  }

  // RendererなどでPoseを見られるので羊で寝ている場合はPose.SLEEPを返すようにする
  @Override
  public @NonNull Pose getPose() {
    if (this.sheep$isSleepInSheep()) {
      return Pose.SLEEPING;
    } else {
      return super.getPose();
    }
  }

  @Override
  public @NonNull Vec3 getVehicleAttachmentPoint(final @NonNull Entity vehicle) {
    if (this.sheep$isSleepInSheep() && vehicle instanceof Sheep sheep) {
      // 羊の上で寝たときの頭の相対位置を返します
      // x: 0.0
      // y: -(8.0 + 1.75) / 16 - 0.6
      // z: (8.0 + 1.75 - ZOffset(0.5)) / 16 - EyeHeight(1.62) / SheepScale(2.0)
      return new Vec3(0.0, -0.009375, -0.231875)
          .scale(sheep.getScale())
          .yRot((float) Math.toRadians(-sheep.yBodyRot));
    } else {
      return super.getVehicleAttachmentPoint(vehicle);
    }
  }

  @Inject(method = "tick", at = @At("TAIL"))
  public void tick(CallbackInfo ci) {
    if (this.level().isClientSide()
        && this.isAlive()
        && this.isSleeping()
        && this.tickCount % 30 == 0) {
      // 寝ているエンティティからいびきのパーティクルを出す
      this.level()
          .addParticle(
              ModParticleTypes.SLEEP_PARTICLE,
              this.getRandomX(0.7),
              this.getRandomY() + 0.3,
              this.getRandomZ(0.7),
              0.0,
              0.0,
              0.0);
    }
  }
}
