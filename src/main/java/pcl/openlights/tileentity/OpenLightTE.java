package pcl.openlights.tileentity;

import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;

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
		nbt.setInteger("color", getColor());
		nbt.setInteger("brightness", getBrightness());
	}

	@Callback
	public Object[] greet(Context context, Arguments args) {
		return new Object[] { "Lasciate ogne speranza, voi ch'intrate" };
	}
	
	@Callback
	public Object[] setColor(Context context, Arguments args) {
		color = args.checkInteger(0);
		worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		getDescriptionPacket();
		return new Object[] { "Ok" };
	}
	
	@Callback
	public Object[] setBrightness(Context context, Arguments args) {
		//context.pause(1);
		brightness = args.checkInteger(0);
		if (brightness > 15) {
			return new Object[] { "Error, brightness should be between 0, and 15" };
		}
		//worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
		//worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		worldObj.updateAllLightTypes(xCoord, yCoord, zCoord);
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
	
	public int getColor() {
		return this.color;
	}
	
	public int getBrightness() {
		return this.brightness;
	}
	
	@Override
    public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
        NBTTagCompound tag = pkt.data;
        readFromNBT(tag);
        this.worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        this.worldObj.updateAllLightTypes(xCoord, yCoord, zCoord);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag);
    }
}
