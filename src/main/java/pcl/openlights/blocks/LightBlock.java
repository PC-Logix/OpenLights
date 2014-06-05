/**
 * 
 */
package pcl.openlights.blocks;

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
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * @author Caitlyn
 *
 */
public class LightBlock extends BlockContainer {

	public LightBlock(int par1, Material par2Material) {
		super(par1, par2Material);
		// TODO Auto-generated constructor stub
	}
	
	private Random random;
	private Icon icon;
	
	@Override
	public void breakBlock(World world, int x, int y, int z, int i, int j) {
		world.removeBlockTileEntity(x, y, z);
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
		super.onBlockPlacedBy(world, x, y, z, player, stack);
		int dir = MathHelper.floor_double((double) ((player.rotationYaw * 4F) / 360F) + 0.5D) & 3;
		world.setBlockMetadataWithNotify(x, y, z, dir, 3);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister registry) {
		icon = registry.registerIcon(OpenLights.MODID + ":OpenLight");
	}

	@Override
	public TileEntity createNewTileEntity(World par1World) {
		return new OpenLightTE();
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public Icon getIcon(int par1, int par2) {
		return icon;
	}
	
	@Override
	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
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
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		int brightness = 0;
		if (tileEntity instanceof OpenLightTE) {
			OpenLightTE myTE = (OpenLightTE) tileEntity;
			brightness = myTE.getBrightness();
			if (brightness > 15) {
				brightness = 15;
			}
		}
		return brightness;
	}	
	
}
