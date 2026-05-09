package io.github.gnya.sheep.mixins.sleeper;

import com.mojang.datafixers.util.Either;
import io.github.gnya.sheep.SheepMod;
import io.github.gnya.sheep.api.mixins.PlayableSheepSleeper;
import io.github.gnya.sheep.api.mixins.SheepSleeper;
import java.util.function.Consumer;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.*;

@Mixin(Player.class)
@Implements(@Interface(iface = PlayableSheepSleeper.class, prefix = "sheep$"))
public abstract class PlayerMixin extends Avatar {
  @Shadow private int sleepCounter;

  protected PlayerMixin(EntityType<? extends LivingEntity> type, Level level) {
    // ダミーコンストラクタ
    super(type, level);
  }

  public Either<Player.BedSleepingProblem, Unit> sheep$startSleepInBed(final Sheep sheep) {
    return this.sheep$Player$startSleepInBed(
        sheep, ((SheepSleeper) this)::LivingEntity$startSleeping);
  }

  public Either<Player.BedSleepingProblem, Unit> sheep$Player$startSleepInBed(
      final Sheep sheep, final Consumer<Sheep> startSleeping) {
    SheepMod.LOGGER.debug("Player$startSleepInBed");

    startSleeping.accept(sheep);
    this.sleepCounter = 0;
    return Either.right(Unit.INSTANCE);
  }
}
