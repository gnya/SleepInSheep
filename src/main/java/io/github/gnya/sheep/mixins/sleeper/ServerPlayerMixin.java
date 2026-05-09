package io.github.gnya.sheep.mixins.sleeper;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import io.github.gnya.sheep.SheepMod;
import io.github.gnya.sheep.api.mixins.PlayableSheepSleeper;
import io.github.gnya.sheep.api.mixins.SheepSleeper;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Unit;
import net.minecraft.world.attribute.BedRule;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.*;

@Mixin(ServerPlayer.class)
@Implements(@Interface(iface = PlayableSheepSleeper.class, prefix = "sheep$"))
public abstract class ServerPlayerMixin extends Player {
  public ServerPlayerMixin(Level level, GameProfile gameProfile) {
    super(level, gameProfile);
  }

  @Shadow
  public abstract @NonNull ServerLevel level();

  public void sheep$startSleeping(final Sheep sheep) {
    this.sheep$ServerPlayer$startSleeping(sheep);
  }

  public void sheep$ServerPlayer$startSleeping(final Sheep sheep) {
    SheepMod.LOGGER.debug("ServerPlayer$startSleeping");

    this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
    ((SheepSleeper) this).LivingEntity$startSleeping(sheep);
  }

  public Either<BedSleepingProblem, Unit> sheep$startSleepInBed(final Sheep sheep) {
    return this.sheep$ServerPlayer$startSleepInBed(
        sheep, ((SheepSleeper) this)::ServerPlayer$startSleeping);
  }

  public Either<BedSleepingProblem, Unit> sheep$ServerPlayer$startSleepInBed(
      final Sheep sheep, final Consumer<Sheep> startSleeping) {
    SheepMod.LOGGER.debug("ServerPlayer$startSleepInBed");

    if (!sheep.getPassengers().isEmpty()) {
      this.sendOverlayMessage(Component.translatable("block.minecraft.bed.occupied"));

      return Either.left(Player.BedSleepingProblem.OTHER_PROBLEM);
    } else if (!this.isSleeping() && this.isAlive()) {
      Vec3 pos = sheep.position();
      BedRule rule =
          this.level().environmentAttributes().getValue(EnvironmentAttributes.BED_RULE, pos);

      if (!rule.canSleep(this.level())) {
        return Either.left(rule.asProblem());
      } else {
        if (!this.isCreative()) {
          double hRange = 8.0;
          double vRange = 5.0;
          List<Monster> monsters =
              this.level()
                  .getEntitiesOfClass(
                      Monster.class,
                      new AABB(
                          pos.x() - hRange,
                          pos.y() - vRange,
                          pos.z() - hRange,
                          pos.x() + hRange,
                          pos.y() + vRange,
                          pos.z() + hRange),
                      monster -> monster.isPreventingPlayerRest(this.level(), this));

          if (!monsters.isEmpty()) {
            return Either.left(BedSleepingProblem.NOT_SAFE);
          }
        }

        var result = ((PlayableSheepSleeper) this).Player$startSleepInBed(sheep, startSleeping);

        result.ifRight(
            _ -> {
              this.awardStat(Stats.SLEEP_IN_BED);
              CriteriaTriggers.SLEPT_IN_BED.trigger((ServerPlayer) (Object) this);
            });

        if (!this.level().canSleepThroughNights()) {
          this.sendOverlayMessage(Component.translatable("sleep.not_possible"));
        }

        this.level().updateSleepingPlayerList();

        return result;
      }
    } else {
      return Either.left(Player.BedSleepingProblem.OTHER_PROBLEM);
    }
  }
}
