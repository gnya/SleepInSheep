package io.github.gnya.sheep_mod.mixins.screen;

import io.github.gnya.sheep_mod.api.mixins.IMixinClientboundSetPassengersPacket;
import io.github.gnya.sheep_mod.api.mixins.IMixinLocalPlayer;
import java.util.BitSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin extends ClientCommonPacketListenerImpl {
  @Shadow private ClientLevel level;

  private ClientPacketListenerMixin(
      Minecraft minecraft, Connection connection, CommonListenerCookie cookie) {
    // ダミーコンストラクタ
    super(minecraft, connection, cookie);
  }

  @Inject(method = "handleSetEntityPassengersPacket", at = @At("HEAD"))
  public void handleSetEntityPassengersPacket(
      ClientboundSetPassengersPacket packet, CallbackInfo ci) {
    LocalPlayer player = this.minecraft.player;

    if (player != null) {
      int[] passengerId = packet.getPassengers();
      BitSet isSleepInSheep = ((IMixinClientboundSetPassengersPacket) packet).getIsSleepInSheep();

      // 羊用のUIを無効にする
      ((IMixinLocalPlayer) player).setUseBedSheepUI(false);

      for (int i = 0; i < passengerId.length; i++) {
        Entity passenger = this.level.getEntity(passengerId[i]);

        if (passenger == player && isSleepInSheep.get(i)) {
          // 羊用のUIを有効にする
          ((IMixinLocalPlayer) player).setUseBedSheepUI(true);
        }
      }
    }
  }

  @ModifyVariable(
      method = "handleSetEntityPassengersPacket",
      at = @At("LOAD"),
      name = "wasPlayerMounted")
  public boolean modifyHandleSetEntityPassengersPacket(boolean wasPlayerMounted) {
    LocalPlayer player = this.minecraft.player;

    // 羊の上で寝ているときにはオーバーレイのメッセージを出さない
    return wasPlayerMounted || (player != null && ((IMixinLocalPlayer) player).getUseBedSheepUI());
  }
}
