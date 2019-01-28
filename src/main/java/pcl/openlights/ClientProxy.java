package pcl.openlights;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pcl.openlights.CommonProxy;
import pcl.openlights.blocks.LightBlock;
import pcl.openlights.tileentity.OpenLightTE;

public class ClientProxy extends CommonProxy {
	@Override
	public void registerColorHandler() {
		Minecraft mc = Minecraft.getMinecraft();
		mc.getBlockColors().registerBlockColorHandler(new BlockColorHandler(), ContentRegistry.openLightBlock);
	}

	@SideOnly(Side.CLIENT)
	private static class BlockColorHandler implements IBlockColor {
		@Override
		public int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
			if (pos != null && state != null && state.getBlock() instanceof LightBlock) {
				OpenLightTE te = (OpenLightTE) worldIn.getTileEntity(pos);
				int color = Integer.parseInt(te.getColor(), 16);
				return color;
			}
			else
				return 0;
		}

	}
	public void registerModels() {
		ModelLoader.setCustomModelResourceLocation(ContentRegistry.prismaticPaste,  0, new ModelResourceLocation(ContentRegistry.prismaticPaste.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(ContentRegistry.openLightItem,  0, new ModelResourceLocation(ContentRegistry.openLightItem.getRegistryName(), "inventory"));

	}

}