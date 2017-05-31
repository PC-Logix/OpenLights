/**
 * 
 */
package pcl.openlights.blocks;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.LanguageRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pcl.openlights.OpenLights;
import pcl.openlights.tileentity.OpenLightTE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * @author Caitlyn
 *
 */
public class LightBlock extends Block implements ITileEntityProvider {

	public LightBlock() {
		super(Material.glass);
		setCreativeTab(li.cil.oc.api.CreativeTab.instance);
		setUnlocalizedName("openlight");
		setHardness(.5f);
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
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] {BRIGHTNESS});
	}
	
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(BRIGHTNESS, meta);
	}
	
    @SideOnly(Side.CLIENT)
    public EnumWorldBlockLayer getBlockLayer()
    {
        return EnumWorldBlockLayer.CUTOUT;
    }

    @SideOnly(Side.CLIENT)
    public int getBlockColor()
    {
        return ColorizerGrass.getGrassColor(0.5D, 1.0D);
    }

    @SideOnly(Side.CLIENT)
    public int getRenderColor(IBlockState state)
    {
        return this.getBlockColor();
    }
    
	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(BRIGHTNESS);
	}
	
	@Override
	public int colorMultiplier(IBlockAccess world, BlockPos pos, int renderPass) {
		TileEntity tileEntity = world.getTileEntity(pos);
		int color = 0xFFFFFF;
		if (tileEntity instanceof OpenLightTE) {
			OpenLightTE myTE = (OpenLightTE) tileEntity;
			color = Integer.parseInt(myTE.getColor(), 16);
		}
		return color;
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

	@Override
	public int getLightOpacity() {
		return super.getLightOpacity();
	}

	@Override
	public boolean canRenderInLayer(EnumWorldBlockLayer layer) {
		return layer == EnumWorldBlockLayer.CUTOUT_MIPPED || super.canRenderInLayer(layer);
	}
	
	@Override
	public int getLightValue(IBlockAccess world, BlockPos pos)
	{
		super.getLightValue(world, pos);		
		return this.lightValue = world.getBlockState(pos).getValue(BRIGHTNESS);
	}

	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		// TODO Auto-generated method stub
		return new OpenLightTE();
	}	
}
