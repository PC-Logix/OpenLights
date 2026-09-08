package pcl.openlights;

import net.minecraft.world.InteractionResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = OpenLights.MOD_ID)
public final class OpenLightsInteractionHandler {
    private OpenLightsInteractionHandler() {
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getEntity().isShiftKeyDown() || event.getLevel().isClientSide) {
            return;
        }

        if (event.getLevel().getBlockEntity(event.getPos()) instanceof OpenLightsControllerBlockEntity controller) {
            ControllerLinking.select(event.getEntity(), controller);
        } else if (event.getLevel().getBlockEntity(event.getPos()) instanceof OpenLightBlockEntity light) {
            ControllerLinking.bindSelected(event.getEntity(), light);
        } else {
            return;
        }

        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }
}
