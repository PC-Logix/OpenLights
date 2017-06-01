package pcl.openlights;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pcl.openlights.CommonProxy;
import pcl.openlights.tileentity.OpenLightTE;

public class ClientProxy extends CommonProxy {

	@Override
	public void registerRenderers()
	{
		registerBlockItem(OpenLights.openLightBlock, 0);
		registerItem(OpenLights.prismaticPaste);
	}

	@Override
	public void registerColorHandler() {
		Minecraft mc = Minecraft.getMinecraft();
		mc.getBlockColors().registerBlockColorHandler(new BlockColorHandler(), OpenLights.openLightBlock);
	}

	public static void registerBlockItem(final Block block, int meta) {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), meta, new ModelResourceLocation(block.getRegistryName(), "inventory"));
		System.out.println("Registering " + block.getRegistryName() + " Item Renderer");
	}

	public static void registerItem(final Item item)  {
		System.out.println(item.getRegistryName());
		ModelLoader.setCustomModelResourceLocation(item,  0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
		System.out.println("Registering " + item.getRegistryName() + " Item Renderer");
	}	

	@SideOnly(Side.CLIENT)
	private static class BlockColorHandler implements IBlockColor {
		@Override
		public int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
			OpenLightTE te = (OpenLightTE) worldIn.getTileEntity(pos);
			int color = Integer.parseInt(te.getColor(), 16);
			return color;
		}

	}


}