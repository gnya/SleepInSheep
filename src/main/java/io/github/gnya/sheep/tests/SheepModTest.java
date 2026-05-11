package io.github.gnya.sheep.tests;

import io.github.gnya.sheep.api.mixins.IMixinSheep;
import io.github.gnya.sheep.api.mixins.SheepSleeper;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.GameType;

public final class SheepModTest {
  // TODO これは仮のテストなので、今後今までに対処した内容をもとにテストを書く
  public static void test(GameTestHelper helper) {
    var player = helper.makeMockPlayer(GameType.SURVIVAL);
    var sheep = helper.spawn(EntityType.SHEEP, new BlockPos(2, 2, 2));

    ((IMixinSheep) sheep).setHappy(true);
    ((SheepSleeper) player).startSleeping(sheep);

    helper.assertTrue(((IMixinSheep) sheep).isHappy(), "sheep.isHappy()");
    helper.assertTrue(((IMixinSheep) sheep).canSleepIn(), "sheep.canSleepIn()");

    helper.assertTrue(player.isSleeping(), "player.isSleeping()");
    helper.assertTrue(player.getPose() == Pose.SLEEPING, "player.getPose() == Pose.SLEEPING");
    helper.assertTrue(((SheepSleeper) player).isSleepInSheep(), "player.isSleepInSheep()");

    sheep.setSheared(true);
    helper.assertTrue(sheep.isSheared(), "sheep.isSheared()");

    helper.assertFalse(((IMixinSheep) sheep).canSleepIn(), "!sheep.canSleepIn()");

    helper.runAfterDelay(
        10,
        () -> {
          helper.assertFalse(((SheepSleeper) player).isSleepInSheep(), "!player.isSleepInSheep()");

          helper.succeed();
        });
  }
}
