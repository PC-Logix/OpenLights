package pcl.openlights;

import net.minecraft.block.Block;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import pcl.openlights.CommonProxy;

public class ClientProxy extends CommonProxy {
	
	@Override
	public void registerRenderers()
	{
		registerBlockItem(OpenLights.openLightBlock, 0);
		registerItem(OpenLights.prismaticPaste);
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
}