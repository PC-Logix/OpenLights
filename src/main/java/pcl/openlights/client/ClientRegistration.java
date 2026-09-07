package pcl.openlights.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import pcl.openlights.OpenLightBlockEntity;
import pcl.openlights.OpenLights;

@EventBusSubscriber(modid = OpenLights.MOD_ID, value = Dist.CLIENT)
public final class ClientRegistration {
    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tintIndex) -> {
            if (level != null && pos != null && level.getBlockEntity(pos) instanceof OpenLightBlockEntity light) {
                return 0xFF000000 | light.color();
            }
            return 0xFFFFFFFF;
        }, OpenLights.OPEN_LIGHT.get());
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> 0xFFFFFFFF, OpenLights.OPEN_LIGHT_ITEM.get());
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        if (ModList.get().isLoaded("veil")) {
            VeilOpenLightRenderer.register(event);
        }
    }

    private ClientRegistration() {
    }
}
