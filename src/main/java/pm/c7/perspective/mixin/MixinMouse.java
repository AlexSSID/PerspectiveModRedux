package pm.c7.perspective.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import pm.c7.perspective.PerspectiveMod;

@Mixin(Mouse.class)
public class MixinMouse {
    @ModifyArgs(
        method = "updateMouse",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/tutorial/TutorialManager;onUpdateMouse(DD)V"
        )
    )
    private void perspective$updatePitchYaw(Args args) {
        if (PerspectiveMod.INSTANCE.perspectiveEnabled) {
            double deltaX = args.get(0);
            double deltaY = args.get(1);

            PerspectiveMod.INSTANCE.cameraYaw += (float) (deltaX / 8.0F);
            PerspectiveMod.INSTANCE.cameraPitch += (float) ((deltaY * (MinecraftClient.getInstance().options.getInvertYMouse().getValue() ? -1 : 1)) / 8.0F);

            if (Math.abs(PerspectiveMod.INSTANCE.cameraPitch) > 90.0F) {
                PerspectiveMod.INSTANCE.cameraPitch = PerspectiveMod.INSTANCE.cameraPitch > 0.0F ? 90.0F : -90.0F;
            }
        }
    }

    @Redirect(
        method = "updateMouse",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"
        )
    )
    private void perspective$preventPlayerMovement(ClientPlayerEntity player, double deltaX, double deltaY) {
        if (!PerspectiveMod.INSTANCE.perspectiveEnabled) {
            player.changeLookDirection(deltaX, deltaY);
        }
    }
}
