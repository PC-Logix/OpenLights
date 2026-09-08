package pcl.openlights;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.BlockEntityEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class OpenLightBlockEntity extends BlockEntityEnvironment {
    private static final String COLOR_TAG = "color";
    private static final String BRIGHTNESS_TAG = "brightness";
    private static final String CONTROLLER_ID_TAG = "controller_id";
    private static final String CONTROLLER_POSITION_TAG = "controller_position";
    private static final String CONTROLLER_LIGHT_ID_TAG = "controller_light_id";

    private int color = 0xFFFFFF;
    private int brightness;
    @Nullable
    private UUID controllerId;
    @Nullable
    private BlockPos linkedControllerPos;
    private int linkedLightId;

    public OpenLightBlockEntity(BlockPos pos, BlockState state) {
        super(OpenLights.OPEN_LIGHT_BLOCK_ENTITY.get(), pos, state);
        brightness = state.getValue(OpenLightBlock.BRIGHTNESS);
        node = Network.newNode(this, Visibility.Network)
                .withComponent("openlight", Visibility.Network)
                .create();
    }

    @Callback
    public Object[] greet(Context context, Arguments args) {
        return new Object[]{"Lasciate ogne speranza, voi ch'entrate"};
    }

    @Callback(doc = "function(color:number):string -- Set the light color as an RGB value and return its six-digit hexadecimal value.")
    public Object[] setColor(Context context, Arguments args) throws Exception {
        requireArgumentCount(args, 1);
        int value = args.checkInteger(0);
        if (value < 0x000000 || value > 0xFFFFFF) {
            throw new Exception("Valid RGB range is 0x000000 to 0xFFFFFF");
        }
        setColorValue(value);
        return new Object[]{getColorString()};
    }

    @Callback(doc = "function(brightness:number):number -- Set the light level from 0 to 15 and return the new level.")
    public Object[] setBrightness(Context context, Arguments args) throws Exception {
        requireArgumentCount(args, 1);
        int value = args.checkInteger(0);
        if (value < 0 || value > 15) {
            throw new Exception("Valid brightness range is 0 to 15");
        }

        setBrightnessValue(value);
        return new Object[]{brightness};
    }

    @Callback(doc = "function():string -- Return the light color as a six-digit RGB hexadecimal string.")
    public Object[] getColor(Context context, Arguments args) {
        return new Object[]{getColorString()};
    }

    @Callback(doc = "function():number -- Return the light level from 0 to 15.")
    public Object[] getBrightness(Context context, Arguments args) {
        return new Object[]{brightness};
    }

    public int color() {
        return color;
    }

    public int brightness() {
        return brightness;
    }

    public String getColorString() {
        return String.format("%06X", color & 0xFFFFFF);
    }

    void setColorValue(int value) {
        color = value & 0xFFFFFF;
        sync();
    }

    void setBrightnessValue(int value) {
        brightness = Math.max(0, Math.min(15, value));
        updateBlockBrightness();
        sync();
    }

    void setValues(int value, int lightLevel) {
        color = value & 0xFFFFFF;
        brightness = Math.max(0, Math.min(15, lightLevel));
        updateBlockBrightness();
        sync();
    }

    private void updateBlockBrightness() {
        if (level != null) {
            BlockState state = getBlockState();
            if (state.getValue(OpenLightBlock.BRIGHTNESS) != brightness) {
                level.setBlock(worldPosition, state.setValue(OpenLightBlock.BRIGHTNESS, brightness), Block.UPDATE_ALL);
            }
        }
    }

    boolean hasController() {
        return controllerId != null && linkedControllerPos != null && linkedLightId > 0;
    }

    @Nullable
    BlockPos controllerPos() {
        return linkedControllerPos;
    }

    int controllerLightId() {
        return linkedLightId;
    }

    boolean isBoundTo(UUID expectedControllerId, BlockPos expectedControllerPos, int expectedLightId) {
        return expectedControllerId.equals(controllerId)
                && expectedControllerPos.equals(linkedControllerPos)
                && expectedLightId == linkedLightId;
    }

    void bindToController(UUID id, BlockPos controllerPos, int lightId) {
        controllerId = id;
        linkedControllerPos = controllerPos.immutable();
        linkedLightId = lightId;
        setChanged();
    }

    void clearControllerBinding() {
        if (!hasController()) {
            return;
        }
        controllerId = null;
        linkedControllerPos = null;
        linkedLightId = 0;
        setChanged();
    }

    void detachFromController() {
        if (!hasController()) {
            return;
        }

        UUID previousControllerId = controllerId;
        BlockPos previousControllerPos = linkedControllerPos;
        int previousLightId = linkedLightId;
        clearControllerBinding();

        if (level != null && !level.isClientSide && level.hasChunkAt(previousControllerPos)
                && level.getBlockEntity(previousControllerPos) instanceof OpenLightsControllerBlockEntity controller
                && controller.matches(previousControllerId)) {
            controller.unregisterLight(previousLightId, worldPosition, true);
        }
    }

    void onBlockRemoved() {
        detachFromController();
    }

    private static void requireArgumentCount(Arguments args, int count) throws Exception {
        if (args.count() != count) {
            throw new Exception("Invalid number of arguments, expected " + count);
        }
    }

    private void sync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt(COLOR_TAG, color);
        tag.putInt(BRIGHTNESS_TAG, brightness);
        if (hasController()) {
            tag.putUUID(CONTROLLER_ID_TAG, controllerId);
            tag.putLong(CONTROLLER_POSITION_TAG, linkedControllerPos.asLong());
            tag.putInt(CONTROLLER_LIGHT_ID_TAG, linkedLightId);
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        color = tag.contains(COLOR_TAG) ? tag.getInt(COLOR_TAG) & 0xFFFFFF : 0xFFFFFF;
        brightness = tag.contains(BRIGHTNESS_TAG)
                ? Math.max(0, Math.min(15, tag.getInt(BRIGHTNESS_TAG)))
                : getBlockState().getValue(OpenLightBlock.BRIGHTNESS);

        controllerId = null;
        linkedControllerPos = null;
        linkedLightId = 0;
        if (tag.hasUUID(CONTROLLER_ID_TAG)
                && tag.contains(CONTROLLER_POSITION_TAG, Tag.TAG_LONG)
                && tag.contains(CONTROLLER_LIGHT_ID_TAG, Tag.TAG_INT)
                && tag.getInt(CONTROLLER_LIGHT_ID_TAG) > 0) {
            controllerId = tag.getUUID(CONTROLLER_ID_TAG);
            linkedControllerPos = BlockPos.of(tag.getLong(CONTROLLER_POSITION_TAG));
            linkedLightId = tag.getInt(CONTROLLER_LIGHT_ID_TAG);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt(COLOR_TAG, color);
        tag.putInt(BRIGHTNESS_TAG, brightness);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider provider) {
        color = tag.contains(COLOR_TAG) ? tag.getInt(COLOR_TAG) & 0xFFFFFF : 0xFFFFFF;
        brightness = tag.contains(BRIGHTNESS_TAG)
                ? Math.max(0, Math.min(15, tag.getInt(BRIGHTNESS_TAG)))
                : getBlockState().getValue(OpenLightBlock.BRIGHTNESS);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet,
                             HolderLookup.Provider provider) {
        CompoundTag tag = packet.getTag();
        if (tag != null) {
            handleUpdateTag(tag, provider);
            refreshClientRender();
        }
    }

    private void refreshClientRender() {
        requestModelDataUpdate();
        if (level != null && level.isClientSide) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
        }
    }
}
