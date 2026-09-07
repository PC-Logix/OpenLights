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
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public final class OpenLightBlockEntity extends BlockEntityEnvironment {
    private static final String COLOR_TAG = "color";
    private static final String BRIGHTNESS_TAG = "brightness";

    private int color = 0xFFFFFF;
    private int brightness;

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
        color = value;
        sync();
        return new Object[]{getColorString()};
    }

    @Callback(doc = "function(brightness:number):number -- Set the light level from 0 to 15 and return the new level.")
    public Object[] setBrightness(Context context, Arguments args) throws Exception {
        requireArgumentCount(args, 1);
        int value = args.checkInteger(0);
        if (value < 0 || value > 15) {
            throw new Exception("Valid brightness range is 0 to 15");
        }

        brightness = value;
        if (level != null) {
            BlockState state = getBlockState();
            if (state.getValue(OpenLightBlock.BRIGHTNESS) != value) {
                level.setBlock(worldPosition, state.setValue(OpenLightBlock.BRIGHTNESS, value), Block.UPDATE_ALL);
            }
        }
        sync();
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
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        color = tag.contains(COLOR_TAG) ? tag.getInt(COLOR_TAG) & 0xFFFFFF : 0xFFFFFF;
        brightness = tag.contains(BRIGHTNESS_TAG)
                ? Math.max(0, Math.min(15, tag.getInt(BRIGHTNESS_TAG)))
                : getBlockState().getValue(OpenLightBlock.BRIGHTNESS);
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
        }
    }
}
