/**
 * 
 */
package pcl.openlights.blocks;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pcl.openlights.OpenLights;
import pcl.openlights.tileentity.OpenLightTE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.util.IIcon;

/**
 * @author Caitlyn
 *
 */
public class LightBlock extends BlockContainer {

	public LightBlock() {
		super(Material.glass);
		
		Class<?> clz = li.cil.oc.api.CreativeTab.class;
		try {
		    Field f = clz.getField("instance");
		    setCreativeTab(li.cil.oc.api.CreativeTab.instance);
		}
		catch ( NoSuchFieldException ex) {
			setCreativeTab(li.cil.oc.api.CreativeTab.Instance);
		}
		
		setBlockName("openlight");
		setHardness(.5f);
		setBlockTextureName(OpenLights.MODID + ":openlight");
	}
	
	private Random random;
	private IIcon icon;
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		super.breakBlock(world, x, y, z, block, meta);
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
		super.onBlockPlacedBy(world, x, y, z, player, stack);
		int dir = MathHelper.floor_double((double) ((player.rotationYaw * 4F) / 360F) + 0.5D) & 3;
		world.setBlockMetadataWithNotify(x, y, z, dir, 3);
	}
	
	@Override
	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		int color = 0xFFFFFF;
		if (tileEntity instanceof OpenLightTE) {
			OpenLightTE myTE = (OpenLightTE) tileEntity;
			color = Integer.parseInt(myTE.getColor(), 16);
		}
		return color;
	}
		
	@Override
	public int damageDropped (int metadata) {
		return 0;
	}
	
	
	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z)
	{
		super.getLightValue(world, x, y, z);		
		return world.getBlockMetadata(x, y, z);
	}

	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		// TODO Auto-generated method stub
		return new OpenLightTE();
	}	
	
}
