package carpetextra.mixins;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayNetworkHandler.class)
public class BlockPosCheckMixin_accurateBlockPlacement {
	@Redirect(method = "onPlayerInteractBlock",
	at = @At(value = "INVOKE",
		target = "Lnet/minecraft/util/math/Vec3d;subtract(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;"),
		require = 0
	)
	private Vec3d removeHitPosCheck(Vec3d hitVec, Vec3d blockCenter){
		return Vec3d.ZERO;
	}
}