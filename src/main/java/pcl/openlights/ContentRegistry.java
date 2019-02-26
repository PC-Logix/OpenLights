package pcl.openlights;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import pcl.openlights.blocks.AlbedoLightBlock;
import pcl.openlights.blocks.LightBlock;
import pcl.openlights.items.PrismaticPaste;
import pcl.openlights.tileentity.OpenLightTE;

import static pcl.openlights.OpenLights.albedoSupport;

@Mod.EventBusSubscriber
public class ContentRegistry {
	public static Block openLightBlock;
	public static Item  prismaticPaste;
    public static Item  openLightItem;
    
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		prismaticPaste = new PrismaticPaste();
		openLightItem = new ItemBlock(openLightBlock).setRegistryName(openLightBlock.getRegistryName().toString());
		event.getRegistry().registerAll(
				prismaticPaste,
				openLightItem
				);
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		if(albedoSupport)
			openLightBlock = new AlbedoLightBlock();
		else
			openLightBlock = new LightBlock();

		event.getRegistry().registerAll(openLightBlock);

		if(albedoSupport)
			registerTileEntity(pcl.openlights.tileentity.AlbedoOpenLightTE.class, "OpenLightTE");
		else
			registerTileEntity(OpenLightTE.class, "OpenLightTE");
	}

	private static void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String key) {
		// For better readability
		GameRegistry.registerTileEntity(tileEntityClass, new ResourceLocation(OpenLights.MODID, key));
	}
}
