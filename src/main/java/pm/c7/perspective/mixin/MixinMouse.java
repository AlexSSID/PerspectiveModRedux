package pm.c7.perspective.mixin;

import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import pm.c7.perspective.PerspectiveMod;

@Mixin(Mouse.class)
public class MixinMouse {
    @Redirect(
        method = "updateMouse",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"
        )
    )
    private void perspective$preventPlayerMovement(ClientPlayerEntity player, double deltaX, double deltaY) {
        if (PerspectiveMod.INSTANCE.perspectiveEnabled) {
            float previousYaw = player.getYaw();
            float previousPitch = player.getPitch();
            float previousHeadYaw = player.headYaw;
            float previousBodyYaw = player.bodyYaw;
            float previousPrevYaw = player.prevYaw;
            float previousPrevPitch = player.prevPitch;
            float previousPrevHeadYaw = player.prevHeadYaw;
            float previousPrevBodyYaw = player.prevBodyYaw;

            player.changeLookDirection(deltaX, deltaY);

            float yawDelta = player.getYaw() - previousYaw;
            float pitchDelta = player.getPitch() - previousPitch;

            PerspectiveMod.INSTANCE.cameraYaw = MathHelper.wrapDegrees(PerspectiveMod.INSTANCE.cameraYaw + yawDelta);
            PerspectiveMod.INSTANCE.cameraPitch = MathHelper.clamp(PerspectiveMod.INSTANCE.cameraPitch + pitchDelta, -90.0F, 90.0F);

            player.setYaw(previousYaw);
            player.setPitch(previousPitch);
            player.prevYaw = previousPrevYaw;
            player.prevPitch = previousPrevPitch;
            player.headYaw = previousHeadYaw;
            player.prevHeadYaw = previousPrevHeadYaw;
            player.bodyYaw = previousBodyYaw;
            player.prevBodyYaw = previousPrevBodyYaw;
        } else {
            player.changeLookDirection(deltaX, deltaY);
        }
    }
}
