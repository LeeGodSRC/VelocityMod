package com.github.leegodsrc.velocitymod.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.ChatComponentText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {

    @Shadow
    private Minecraft gameController;

    @Inject(method = "handleEntityVelocity", at = @At("RETURN"))
    public void handleEntityVelocity(S12PacketEntityVelocity packetIn, CallbackInfo ci) {
        if (packetIn.getEntityID() != Minecraft.getMinecraft().thePlayer.getEntityId())
            return;

        List<EntityPlayer> entities = Minecraft.getMinecraft().theWorld.playerEntities;
        double x = (double)packetIn.getMotionX() / 8000.0;
        double y = (double)packetIn.getMotionY() / 8000.0;
        double z = (double)packetIn.getMotionZ() / 8000.0;
        double dist = Double.POSITIVE_INFINITY;
        for (EntityPlayer entity : entities) {
            double dx = Math.max(Math.abs(Minecraft.getMinecraft().thePlayer.posX - entity.posX) - 0.4, 0);
            double dz = Math.max(Math.abs(Minecraft.getMinecraft().thePlayer.posZ - entity.posZ) - 0.4, 0);
            double localDist = Math.sqrt(dx * dx + dz * dz);
            if (localDist > 0 && localDist < dist) {
                dist = localDist;
            }
        }
        double horizontal = Math.abs(Math.sqrt(x * x + z * z));
        this.gameController.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("Horizontal: " + horizontal + ", X: " + x + ", Y: " + y + ", Z: " + z + ", Dist: " + dist));
    }

}
