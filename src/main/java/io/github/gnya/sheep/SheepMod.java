package io.github.gnya.sheep;

import com.mojang.logging.LogUtils;
import io.github.gnya.sheep.core.ModRegistries;
import java.lang.reflect.InvocationTargetException;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.ForgeGameTestHooks;
import org.slf4j.Logger;

@Mod(SheepMod.MODID)
public class SheepMod {
  public static final String MODID = "sheep";
  public static final Logger LOGGER = LogUtils.getLogger();

  public SheepMod(FMLJavaModLoadingContext context) {
    var busGroup = context.getModBusGroup();

    // テスト関連の登録（テスト実行時のみ）
    //noinspection UnstableApiUsage
    if (ForgeGameTestHooks.isGametestEnabled()) {
      String className = this.getClass().getPackageName() + ".tests.ModTestLoader";

      try {
        Class.forName(className).getMethod("init", BusGroup.class).invoke(null, busGroup);
      } catch (ClassNotFoundException
          | NoSuchMethodException
          | InvocationTargetException
          | IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }

    // レジストリ関連の登録
    ModRegistries.init(busGroup);
  }
}
