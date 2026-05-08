package io.github.gnya.sheep_mod.mixins.screen;

import io.github.gnya.sheep_mod.api.IMixinLocalPlayer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LocalPlayer.class)
@Implements(@Interface(iface = IMixinLocalPlayer.class, prefix = "sheep_mod$"))
public abstract class LocalPlayerMixin {
  @Unique private boolean private$useBedSheepUI = false;

  public boolean sheep_mod$getUseBedSheepUI() {
    return this.private$useBedSheepUI;
  }

  public void sheep_mod$setUseBedSheepUI(boolean value) {
    this.private$useBedSheepUI = value;
  }
}
