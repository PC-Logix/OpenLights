package pcl.openlights;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

final class ControllerLinking {
    private static final String SELECTION_TAG = "openlights:selected_controller";
    private static final String DIMENSION_TAG = "dimension";
    private static final String POSITION_TAG = "position";
    private static final String CONTROLLER_ID_TAG = "controller_id";

    private ControllerLinking() {
    }

    static void select(Player player, OpenLightsControllerBlockEntity controller) {
        Level level = controller.getLevel();
        if (level == null) {
            return;
        }

        CompoundTag selection = new CompoundTag();
        selection.putString(DIMENSION_TAG, level.dimension().location().toString());
        selection.putLong(POSITION_TAG, controller.getBlockPos().asLong());
        selection.putUUID(CONTROLLER_ID_TAG, controller.controllerId());
        player.getPersistentData().put(SELECTION_TAG, selection);
        player.displayClientMessage(Component.translatable(
                "message.openlights.controller_selected",
                controller.getBlockPos().getX(),
                controller.getBlockPos().getY(),
                controller.getBlockPos().getZ()), false);
    }

    static void bindSelected(Player player, OpenLightBlockEntity light) {
        Level level = light.getLevel();
        if (level == null || level.isClientSide) {
            return;
        }

        CompoundTag playerData = player.getPersistentData();
        if (!playerData.contains(SELECTION_TAG, Tag.TAG_COMPOUND)) {
            reportExistingOrMissing(player, light);
            return;
        }

        CompoundTag selection = playerData.getCompound(SELECTION_TAG);
        if (!selection.hasUUID(CONTROLLER_ID_TAG)
                || !level.dimension().location().toString().equals(selection.getString(DIMENSION_TAG))) {
            player.displayClientMessage(Component.translatable("message.openlights.controller_unavailable"), false);
            return;
        }

        BlockPos controllerPos = BlockPos.of(selection.getLong(POSITION_TAG));
        if (!level.hasChunkAt(controllerPos)
                || !(level.getBlockEntity(controllerPos) instanceof OpenLightsControllerBlockEntity controller)
                || !controller.matches(selection.getUUID(CONTROLLER_ID_TAG))) {
            player.displayClientMessage(Component.translatable("message.openlights.controller_unavailable"), false);
            return;
        }

        int id = controller.bindLight(light, player.getName().getString());
        if (id < 0) {
            player.displayClientMessage(Component.translatable(
                    "message.openlights.controller_full",
                    OpenLightsControllerBlockEntity.MAX_LIGHTS), false);
            return;
        }

        player.displayClientMessage(Component.translatable(
                "message.openlights.light_bound",
                id,
                controllerPos.getX(),
                controllerPos.getY(),
                controllerPos.getZ()), false);
    }

    private static void reportExistingOrMissing(Player player, OpenLightBlockEntity light) {
        if (light.hasController()) {
            BlockPos controllerPos = light.controllerPos();
            player.displayClientMessage(Component.translatable(
                    "message.openlights.light_id",
                    light.controllerLightId(),
                    controllerPos.getX(),
                    controllerPos.getY(),
                    controllerPos.getZ()), false);
        } else {
            player.displayClientMessage(Component.translatable("message.openlights.no_controller_selected"), false);
        }
    }
}
