package pcl.openlights;

import net.minecraft.block.Block;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import pcl.openlights.CommonProxy;

public class ClientProxy extends CommonProxy {
	
	public void registerRenderers()
	{
		registerBlockItem(OpenLights.openLightBlock, 0, "openLightBlock");
		registerItem(OpenLights.prismaticPaste, "prismaticPaste");
	}
	
	public static void registerBlockItem(final Block block, int meta, final String blockName) {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), meta, new ModelResourceLocation(OpenLights.MODID + ":" + blockName, "inventory"));
		System.out.println("Registering " + blockName + " Item Renderer");
    }
	
	public static void registerItem(final Item item, final String itemName)  {
		ModelLoader.setCustomModelResourceLocation(item,  0, new ModelResourceLocation(OpenLights.MODID + ":" + itemName, "inventory"));
		System.out.println("Registering " + itemName + " Item Renderer");
    }	
}