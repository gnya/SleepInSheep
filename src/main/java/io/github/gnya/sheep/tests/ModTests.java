package io.github.gnya.sheep.tests;

import io.github.gnya.sheep.api.mixins.IMixinSheep;
import io.github.gnya.sheep.api.mixins.SheepSleeper;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;

public final class ModTests {
  private static void assertPlayerSleeping(GameTestHelper helper, Player player) {
    helper.assertTrue(player.isSleeping(), "sleeper.isSleeping()");
    helper.assertTrue(((SheepSleeper) player).isSleepInSheep(), "sleeper.isSleepInSheep()");
    helper.assertValueEqual(player.getPose(), Pose.SLEEPING, "sleeper.getPose()");
  }

  private static void assertPlayerNotSleeping(GameTestHelper helper, Player player) {
    helper.assertFalse(player.isSleeping(), "!sleeper.isSleeping()");
    helper.assertFalse(((SheepSleeper) player).isSleepInSheep(), "!sleeper.isSleepInSheep()");
    helper.assertValueEqual(player.getPose(), Pose.STANDING, "sleeper.getPose()");
  }

  @SuppressWarnings("unused")
  public static void sheep_scaling(GameTestHelper helper) {
    var center = new BlockPos(2, 2, 2);
    var sheep = helper.spawn(EntityType.SHEEP, center);
    var player = helper.makeMockPlayer(GameType.SURVIVAL);

    // 羊の状態をHappyに、羊の羊毛を赤色にして状態が反映されるように1tick待つ
    ((IMixinSheep) sheep).setHappy(true);
    sheep.setColor(DyeColor.RED);
    helper.runAfterDelay(
        1,
        () -> {
          // 当たり判定が2倍、体力が4倍になっているか確認する
          helper.assertValueEqual(sheep.getBbWidth(), 0.9F * 2.0F, "sheep.getBbWidth()");
          helper.assertValueEqual(sheep.getBbHeight(), 1.3F * 2.0F, "sheep.getBbHeight()");
          helper.assertValueEqual(sheep.getHealth(), 8.0F * 4.0F, "sheep.getHealth()");

          // 手にハサミを持った状態で右クリックして毛が刈られているか確認する
          player.setItemInHand(InteractionHand.MAIN_HAND, Items.SHEARS.getDefaultInstance());
          player.interactOn(sheep, InteractionHand.MAIN_HAND, sheep.position());
          helper.assertTrue(sheep.isSheared(), "sheep.isSheared()");
          helper.assertFalse(((IMixinSheep) sheep).canSleepIn(), "!sheep.canSleepIn()");

          // 状態が反映されるように1tick待つ
          helper.runAfterDelay(
              1,
              () -> {
                // 毛を刈られたときのドロップが4倍になっているか？
                // NOTE 死亡時のドロップについてはテストを省略します
                // NOTE LootTableSeedが固定なので毎回8個ドロップします
                helper.assertItemEntityCountIs(Items.RED_WOOL, center, 2.0, 2 * 4);

                helper.succeed();
              });
        });
  }

  @SuppressWarnings("unused")
  public static void sheep_color(GameTestHelper helper) {
    var center = new BlockPos(2, 2, 2);
    var sheep = helper.spawn(EntityType.SHEEP, center);
    var player = helper.makeMockPlayer(GameType.SURVIVAL);

    // 羊の状態をHappyにして状態が反映されるように1tick待つ
    ((IMixinSheep) sheep).setHappy(true);
    helper.runAfterDelay(
        1,
        () -> {
          // 手に染料を持った状態で右クリックして羊が染まったか確認する
          player.setItemInHand(InteractionHand.MAIN_HAND, Items.RED_DYE.getDefaultInstance());
          player.interactOn(sheep, InteractionHand.MAIN_HAND, sheep.position());
          helper.assertValueEqual(sheep.getColor(), DyeColor.RED, "sheep.getColor()");

          helper.succeed();
        });
  }

  @SuppressWarnings("unused")
  public static void sheep_child(GameTestHelper helper) {
    var center = new BlockPos(2, 2, 2);
    var sheep = helper.spawn(EntityType.SHEEP, center);
    var player = helper.makeMockPlayer(GameType.SURVIVAL);

    // 羊の状態をHappyに、羊の年齢を子どもにして状態が反映されるように1tick待つ
    ((IMixinSheep) sheep).setHappy(true);
    sheep.setAge(-100);
    helper.runAfterDelay(
        1,
        () -> {
          // 羊を右クリックして眠れないか確認する
          player.interactOn(sheep, InteractionHand.MAIN_HAND, sheep.position());
          assertPlayerNotSleeping(helper, player);

          helper.succeed();
        });
  }

  @SuppressWarnings("unused")
  public static void sheep_sleep(GameTestHelper helper) {
    var center = new BlockPos(2, 2, 2);
    var player = helper.makeMockPlayer(GameType.SURVIVAL);
    var sleeper = helper.makeMockPlayer(GameType.SURVIVAL);
    var sheep = helper.spawn(EntityType.SHEEP, center);

    // 羊を右クリックして眠れないか確認する（Happyではない羊では眠れない）
    sleeper.interactOn(sheep, InteractionHand.MAIN_HAND, sheep.position());
    assertPlayerNotSleeping(helper, sleeper);

    // 羊の状態をHappyにして状態が反映されるように1tick待つ
    ((IMixinSheep) sheep).setHappy(true);
    helper.runAfterDelay(
        1,
        () -> {
          // 羊の状態をHappyに設定できたか？
          helper.assertTrue(((IMixinSheep) sheep).isHappy(), "sheep.isHappy()");

          // 羊を右クリックして眠れるか確認する
          sleeper.interactOn(sheep, InteractionHand.MAIN_HAND, sheep.position());
          assertPlayerSleeping(helper, sleeper);

          // 羊を右クリックして眠れないか確認する（誰かがすでに寝ている場合は眠れない）
          player.interactOn(sheep, InteractionHand.MAIN_HAND, sheep.position());
          helper.assertFalse(player.isSleeping(), "!player.isSleeping()");

          helper.succeed();
        });
  }
}
