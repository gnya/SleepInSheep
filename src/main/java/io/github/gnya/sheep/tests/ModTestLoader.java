package io.github.gnya.sheep.tests;

import io.github.gnya.sheep.SheepMod;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public class ModTestLoader {
  public static final DeferredRegister<Consumer<GameTestHelper>> TEST_FUNCTIONS =
      DeferredRegister.create(Registries.TEST_FUNCTION, SheepMod.MODID);

  public static void register(Class<?> clazz) {
    for (var method : clazz.getMethods()) {
      if (method.getParameterCount() == 1
          && method.getParameterTypes()[0] == GameTestHelper.class) {
        TEST_FUNCTIONS.register(
            (clazz.getSimpleName() + "/" + method.getName()).toLowerCase(),
            () ->
                helper -> {
                  try {
                    method.invoke(null, helper);
                  } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                  }
                });
      }
    }
  }

  public static void init(BusGroup busGroup) {
    register(ModTests.class);
    TEST_FUNCTIONS.register(busGroup);
  }
}
