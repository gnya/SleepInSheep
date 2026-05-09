package io.github.gnya.sheep.mixins.screen;

import io.github.gnya.sheep.api.mixins.IMixinLocalPlayer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LocalPlayer.class)
@Implements(@Interface(iface = IMixinLocalPlayer.class, prefix = "sheep$"))
public abstract class LocalPlayerMixin {
  @Unique private boolean private$useBedSheepUI = false;

  public boolean sheep$getUseBedSheepUI() {
    return this.private$useBedSheepUI;
  }

  public void sheep$setUseBedSheepUI(boolean value) {
    this.private$useBedSheepUI = value;
  }
}
