package io.github.gnya.sheep;

import com.mojang.logging.LogUtils;
import io.github.gnya.sheep.core.ModRegistries;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(SheepMod.MODID)
public class SheepMod {
  public static final String MODID = "sheep";
  public static final Logger LOGGER = LogUtils.getLogger();

  public SheepMod(FMLJavaModLoadingContext context) {
    ModRegistries.init(context);
  }
}
