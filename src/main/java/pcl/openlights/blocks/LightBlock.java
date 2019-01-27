/**
 * 
 */
package pcl.openlights.blocks;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pcl.openlights.tileentity.OpenLightTE;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * @author Caitlyn
 *
 */
public class LightBlock extends Block implements ITileEntityProvider {

	public LightBlock() {
		super(Material.GLASS);
		setUnlocalizedName("openlight");
		setRegistryName("openlight");
		this.setHardness(.5F);
		setCreativeTab(li.cil.oc.api.CreativeTab.instance);
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}

	public static final PropertyInteger BRIGHTNESS = PropertyInteger.create("brightness", 0, 15);

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		return state;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {BRIGHTNESS});
	}


	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(BRIGHTNESS, meta);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(BRIGHTNESS);
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof OpenLightTE) {
			return state.withProperty(BRIGHTNESS, ((OpenLightTE) tile).getLampColor() == 0 ? 0 : 15);
		} else {
			return state;
		}
	}

	@Override
	public int damageDropped (IBlockState state) {
		return 0;
	}

	@SuppressWarnings("deprecation")
	@Override
	public int getLightOpacity(IBlockState state) {
		return super.getLightOpacity(state);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.CUTOUT_MIPPED || super.canRenderInLayer(state, layer);
	}

	@SuppressWarnings("deprecation")
	@Override
	public int getLightValue(IBlockState state)
	{
		super.getLightValue(state);		
		return this.lightValue = state.getValue(BRIGHTNESS);
	}

	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		// TODO Auto-generated method stub
		return new OpenLightTE();
	}

}
