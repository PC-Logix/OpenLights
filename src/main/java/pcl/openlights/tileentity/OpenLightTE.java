package pcl.openlights.tileentity;

import javax.annotation.Nullable;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pcl.openlights.blocks.LightBlock;

/**
 * @author Caitlyn
 *
 */
public class OpenLightTE extends TileEntity implements SimpleComponent {

	private int color = 0xFFFFFF;
	private int brightness = 0;

	public OpenLightTE() {}

	@Override
	public String getComponentName() {
		return "openlight";
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		color = nbt.getInteger("color");
		brightness = nbt.getInteger("brightness");
	}

	@SuppressWarnings("deprecation")
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setInteger("color", Integer.parseInt(getColor(), 16));
		nbt.setInteger("brightness", getBrightness().getLightValue());
		return nbt;
	}

	@Callback
	public Object[] greet(Context context, Arguments args) {
		return new Object[] { "Lasciate ogne speranza, voi ch'entrate" };
	}

	@Callback(direct=true)
	public Object[] setColor(Context context, Arguments args) {
		//if (args.checkInteger(0) > 0xFFFFFF || args.checkInteger(0) < 0x000000) {
			color = args.checkInteger(0);
			getUpdateTag();
			world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 2);
			world.markBlockRangeForRenderUpdate(getPos(), getPos());
			markDirty();
			return new Object[] { "Ok" };
		//} else {
		//	return new Object[] { "Valid range is 0x000000 to 0xFFFFFF" };
		//}
	}

	@Callback(direct=true)
	public Object[] setBrightness(Context context, Arguments args) {
		//context.pause(1);
		brightness = args.checkInteger(0);
		if (brightness > 15 || brightness < 0) {
			return new Object[] { "Error, brightness should be between 0, and 15" };
		}
		IBlockState state = world.getBlockState(pos);
		world.setBlockState(pos, state.withProperty(LightBlock.BRIGHTNESS, brightness));
		world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 2);
		getUpdateTag();
		world.markBlockRangeForRenderUpdate(getPos(), getPos());
		return new Object[] { "Ok" };
	}

	@Callback
	public Object[] getColor(Context context, Arguments args) {
		return new Object[] { getColor() };
	}

	@Callback
	public Object[] getBrightness(Context context, Arguments args) {
		return new Object[] { getBrightness() };
	}

	public String getColor() {
		return String.format("%06X", (0xFFFFFF & this.color));
	}

	public IBlockState getBrightness() {
		return world.getBlockState(pos);
	}

	public int getLampColor() {
		return color;
	}

	public int getBrightnessVal(){ return brightness; }

	@Override
	@Nullable
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound tagCom = super.getUpdateTag();
		this.writeToNBT(tagCom);
		return tagCom;
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		this.readFromNBT(tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
			readFromNBT(packet.getNbtCompound());
			IBlockState state = this.world.getBlockState(this.pos);
			this.world.notifyBlockUpdate(pos, state, state, 3);
	}
	
	public boolean writeNBTToDescriptionPacket()
	{
		return true;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
	{
		return (oldState.getBlock() != newState.getBlock());
	}
}
