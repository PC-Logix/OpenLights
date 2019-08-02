package pcl.openlights.tileentity;

import javax.annotation.Nullable;

import elucent.albedo.lighting.ILightProvider;
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
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pcl.openlights.blocks.LightBlock;

/**
 * @author Caitlyn
 *
 */

@Optional.InterfaceList({
	@Optional.Interface( iface = "elucent.albedo.lighting.ILightProvider", modid = "albedo" ),
	@Optional.Interface( iface = "com.elytradev.mirage.lighting.IColoredLight", modid = "mirage" )
})

// NOTE: IColoredLight is to be removed after 1.12.2. See comment in IColoredLight for more details.
@SuppressWarnings("deprecation")
public class OpenLightTE extends TileEntity implements SimpleComponent, ILightProvider, com.elytradev.mirage.lighting.IColoredLight {

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

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setInteger("color", Integer.parseInt(getColor(), 16));
		nbt.setInteger("brightness", getBrightness());
		return nbt;
	}

	@Callback
	public Object[] greet(Context context, Arguments args) {
		return new Object[] { "Lasciate ogne speranza, voi ch'entrate" };
	}

	// TODO QMX: If QMX considers making an official fork, use ColorValue class from QMXMCStdLib to support ordinal and RGB color values.
	// NOTE QMX: Delaying any decision on official forks because it is trivial to convert a hex string to a usable numeric value using tonumber(hexstring, 16).
	@Callback( doc = "function(color:number):string; Set the light color as an RGB value. Returns the new color as an RGB hex string. Use `tonumber(value, 16)' to convert return value to a usable numeric value.", direct = true, limit = 32 )
	public Object[] setColor( Context context, Arguments args ) throws Exception
	{
		if( args.count() != 1 )
			throw new Exception( "Invalid number of arguments, expected 1" );
		
		int buf = args.checkInteger( 0 );
		
		if ( ( buf > 0xFFFFFF ) || ( buf < 0x000000 ) )
			throw new Exception( "Valid RGB range is 0x000000 to 0xFFFFFF" );
		
		color = buf;
		
		getUpdateTag();
		world.notifyBlockUpdate( pos, world.getBlockState( pos ), world.getBlockState( pos ), 2 );
		world.markBlockRangeForRenderUpdate( pos, pos );
		markDirty();
		
		return new Object[] { getColor() };
	}

	@Callback( doc = "function(brightness:number):number; Set the brightness of the light. Returns the new brightness.", direct = true, limit = 32 )
	public Object[] setBrightness( Context context, Arguments args ) throws Exception
	{
		if( args.count() != 1 )
			throw new Exception( "Invalid number of arguments, expected 1" );
	
		int buf = args.checkInteger( 0 );
		
		if ( ( buf > 15 ) || ( buf < 0 ) )
			throw new Exception( "Valid brightness range is 0 to 15" );
		
		brightness = buf;
		
		IBlockState state = world.getBlockState( pos );
		world.setBlockState( pos, state.withProperty( LightBlock.BRIGHTNESS, brightness ) );
		
		getUpdateTag();
		world.notifyBlockUpdate( pos, world.getBlockState( pos ), world.getBlockState( pos ), 2 );
		world.markBlockRangeForRenderUpdate( pos, pos );
		markDirty();
		
		return new Object[] { getBrightness() };
	}

	@Callback( doc = "function():string; Get the light color as an RGB hex string. Use `tonumber(value, 16)' to convert return value to a usable numeric value.", direct = true, limit = 32 )
	public Object[] getColor(Context context, Arguments args) {
		return new Object[] { getColor() };
	}

	@Callback( doc = "function():number; Get the brightness of the light.", direct = true, limit = 32 )
	public Object[] getBrightness(Context context, Arguments args) {
		return new Object[] { getBrightness() };
	}

	public String getColor() {
		return String.format("%06X", (0xFFFFFF & this.color));
	}

	public int getBrightness() {
		return world.getBlockState(pos).getValue( LightBlock.BRIGHTNESS );
	}

	public int getLampColor() {
		return color;
	}
	
	@Override
	@SideOnly( Side.CLIENT )
	@Optional.Method( modid = "albedo" )
    public elucent.albedo.lighting.Light provideLight()
    {
        return elucent.albedo.lighting.Light.builder()
			.pos( getPos() )
			.color( color, false )
			.radius( brightness )
			.build();
    }
    
	@Nullable
	@Override
	@SideOnly( Side.CLIENT )
	@Optional.Method( modid = "mirage" )
	public com.elytradev.mirage.lighting.Light getColoredLight() {
		return com.elytradev.mirage.lighting.Light.builder()
			.pos( getPos() )
			.color( color, false )
			.radius( brightness )
			.build();
	}

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
