package io.github.gnya.sheep_mod;

import com.mojang.logging.LogUtils;
import io.github.gnya.sheep_mod.core.SheepModRegistries;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(SheepMod.MODID)
public class SheepMod {
  public static final String MODID = "sheep_mod";
  public static final Logger LOGGER = LogUtils.getLogger();

  public SheepMod(FMLJavaModLoadingContext context) {
    SheepModRegistries.init(context);
  }
}
