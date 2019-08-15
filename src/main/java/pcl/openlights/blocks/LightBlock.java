package pcl.openlights.blocks;

import pcl.openlights.tileentity.OpenLightTE;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * @author Caitlyn
 */
public class LightBlock extends Block implements ITileEntityProvider
{
	public static final String NAME = "openlight";

	public LightBlock()
	{
		super( Material.GLASS );
		setRegistryName( NAME );
		setUnlocalizedName( NAME );
		setHardness( .5F );
		setLightLevel( 1.0F );
		setCreativeTab( li.cil.oc.api.CreativeTab.instance );
	}

	public static final PropertyInteger BRIGHTNESS = PropertyInteger.create( "brightness", 0, 15 );

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer( this, new IProperty[]{ BRIGHTNESS } );
	}

	@Override
	@Deprecated
	public IBlockState getStateFromMeta( int meta )
	{
		return this.getDefaultState().withProperty( BRIGHTNESS, meta );
	}

	@Override
	public int getMetaFromState( IBlockState state )
	{
		return state.getValue( BRIGHTNESS );
	}

	@Override
	public IBlockState getExtendedState( IBlockState state, IBlockAccess world, BlockPos pos )
	{
		return state.withProperty( BRIGHTNESS, state.getValue( BRIGHTNESS ) );
	}

	@Override
	@Deprecated
	public int getLightValue( IBlockState state )
	{
		lightValue = state.getValue( BRIGHTNESS );
		return lightValue;
	}

	@Override
	public int getLightValue( IBlockState state, IBlockAccess world, BlockPos pos )
	{
		return getLightValue( state );
	}

	@Override
	public TileEntity createNewTileEntity( World arg0, int arg1 )
	{
		return new OpenLightTE();
	}
}
