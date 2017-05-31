package pcl.openlights.tileentity;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.EnumSkyBlock;

/**
 * @author Caitlyn
 *
 */
public class OpenLightTE extends TileEntity implements SimpleComponent {

	public int color = 0xFFFFFF;
	public int brightness = 0;

	public OpenLightTE() { }

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

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setInteger("color", Integer.parseInt(getColor(), 16));
		nbt.setInteger("brightness", getBrightness().getBlock().getDamageValue(worldObj, pos));
	}

	@Callback
	public Object[] greet(Context context, Arguments args) {
		return new Object[] { "Lasciate ogne speranza, voi ch'entrate" };
	}

	@Callback(direct=true)
	public Object[] setColor(Context context, Arguments args) {
		color = args.checkInteger(0);
		//worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
		//worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		getDescriptionPacket();
		return new Object[] { "Ok" };
	}

	@Callback(direct=true)
	public Object[] setBrightness(Context context, Arguments args) {
		//context.pause(1);
		brightness = args.checkInteger(0);
		if (brightness > 15) {
			return new Object[] { "Error, brightness should be between 0, and 15" };
		}
		//worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, brightness, 3);
		//worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		getDescriptionPacket();
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
		//return worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		return worldObj.getBlockState(pos);
	}

	public int getLampColor() {
		return color;
	}

	@Override
	public net.minecraft.network.Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		this.writeToNBT(tag);
		return new S35PacketUpdateTileEntity(pos, 1, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		NBTTagCompound tagCom = pkt.getNbtCompound();
		this.readFromNBT(tagCom);
		this.worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
		this.worldObj.markBlockForUpdate(getPos());
		//this.worldObj.setLightValue(EnumSkyBlock.Block, xCoord, yCoord, zCoord, getBrightness());
		//this.worldObj.updateLightByType(EnumSkyBlock.Block, pos);
	}
}
