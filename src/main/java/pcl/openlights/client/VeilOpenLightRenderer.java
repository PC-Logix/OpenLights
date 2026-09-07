package pcl.openlights.client;

import com.mojang.blaze3d.vertex.PoseStack;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.data.PointLightData;
import foundry.veil.api.client.render.light.renderer.LightRenderHandle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ChunkEvent;
import pcl.openlights.OpenLightBlockEntity;
import pcl.openlights.OpenLights;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

final class VeilOpenLightRenderer implements BlockEntityRenderer<OpenLightBlockEntity> {
    private static final Map<OpenLightBlockEntity, LightRenderHandle<PointLightData>> LIGHTS =
            new IdentityHashMap<>();

    static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(OpenLights.OPEN_LIGHT_BLOCK_ENTITY.get(), context -> new VeilOpenLightRenderer());
        NeoForge.EVENT_BUS.addListener(VeilOpenLightRenderer::clientTick);
        NeoForge.EVENT_BUS.addListener(VeilOpenLightRenderer::chunkLoaded);
        NeoForge.EVENT_BUS.addListener(VeilOpenLightRenderer::chunkUnloaded);
        NeoForge.EVENT_BUS.addListener(VeilOpenLightRenderer::loggedOut);
    }

    @Override
    public void render(OpenLightBlockEntity light, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        update(light);
    }

    private static void clientTick(ClientTickEvent.Post event) {
        ClientLevel level = Minecraft.getInstance().level;
        Iterator<Map.Entry<OpenLightBlockEntity, LightRenderHandle<PointLightData>>> iterator =
                LIGHTS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<OpenLightBlockEntity, LightRenderHandle<PointLightData>> entry = iterator.next();
            OpenLightBlockEntity light = entry.getKey();
            if (level == null || light.isRemoved() || light.getLevel() != level || light.brightness() == 0) {
                entry.getValue().free();
                iterator.remove();
            } else if (!entry.getValue().isValid()) {
                LightRenderHandle<PointLightData> replacement = create(light);
                if (replacement != null) {
                    entry.setValue(replacement);
                }
            } else {
                updateData(light, entry.getValue());
            }
        }
    }

    private static void chunkLoaded(ChunkEvent.Load event) {
        if (event.getLevel() instanceof ClientLevel && event.getChunk() instanceof LevelChunk chunk) {
            for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                if (blockEntity instanceof OpenLightBlockEntity light) {
                    update(light);
                }
            }
        }
    }

    private static void chunkUnloaded(ChunkEvent.Unload event) {
        if (event.getLevel() instanceof ClientLevel && event.getChunk() instanceof LevelChunk chunk) {
            for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                if (blockEntity instanceof OpenLightBlockEntity light) {
                    remove(light);
                }
            }
        }
    }

    private static void loggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
        LIGHTS.values().forEach(LightRenderHandle::free);
        LIGHTS.clear();
    }

    private static void update(OpenLightBlockEntity light) {
        if (light.brightness() == 0) {
            remove(light);
            return;
        }

        LightRenderHandle<PointLightData> handle = LIGHTS.get(light);
        if (handle == null || !handle.isValid()) {
            handle = create(light);
            if (handle == null) {
                return;
            }
            LIGHTS.put(light, handle);
        }

        updateData(light, handle);
    }

    private static LightRenderHandle<PointLightData> create(OpenLightBlockEntity light) {
        if (VeilRenderSystem.renderer() == null) {
            return null;
        }
        LightRenderHandle<PointLightData> handle = VeilRenderSystem.renderer().getLightRenderer()
                .addLight(new PointLightData());
        updateData(light, handle);
        return handle;
    }

    private static void updateData(OpenLightBlockEntity light, LightRenderHandle<PointLightData> handle) {
        handle.getLightData()
                .setPosition(light.getBlockPos().getX() + 0.5, light.getBlockPos().getY() + 0.5,
                        light.getBlockPos().getZ() + 0.5)
                .setColor(light.color())
                .setBrightness(1.0F)
                .setRadius(light.brightness())
                .setOcclusionEnabled(true);
    }

    private static void remove(OpenLightBlockEntity light) {
        LightRenderHandle<PointLightData> handle = LIGHTS.remove(light);
        if (handle != null) {
            handle.free();
        }
    }

    private VeilOpenLightRenderer() {
    }
}
