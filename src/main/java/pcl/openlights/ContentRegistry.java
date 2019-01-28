package pcl.openlights;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import pcl.openlights.blocks.LightBlock;
import pcl.openlights.items.PrismaticPaste;
import pcl.openlights.tileentity.OpenLightTE;

public class ContentRegistry {
	
    public ContentRegistry() {
    }
    
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
		openLightBlock = new LightBlock();
		event.getRegistry().registerAll(
				openLightBlock
				);
		registerTileEntity(OpenLightTE.class, "OpenLightTE");
	}

	@SubscribeEvent
	public void registerRecipes(RegistryEvent.Register<IRecipe> event) {
		ItemStack redDye	    = new ItemStack(Items.DYE, 1, 1);
		ItemStack greenDye	    = new ItemStack(Items.DYE, 1, 2);
		ItemStack blueDye	    = new ItemStack(Items.DYE, 1, 4);
		ItemStack glowDust      = new ItemStack(Items.GLOWSTONE_DUST);
		ItemStack pcb		    = li.cil.oc.api.Items.get("printedcircuitboard").createItemStack(1);
		ItemStack glassPane     = new ItemStack(Blocks.GLASS_PANE);

		//event.getRegistry().register(new ShapelessOreRecipe(new ResourceLocation(OpenLights.MODID), new ItemStack(prismaticPaste, 4), new Object[]{
		//		redDye, greenDye, blueDye, glowDust
		//}).setRegistryName(new ResourceLocation(OpenLights.MODID)));

		//event.getRegistry().register(new ShapedOreRecipe(openLightBlock.getRegistryName(), new ItemStack(openLightBlock, 1),
		//		" G ",
		//		"GPG",
		//		" C ",
		//		'G', glassPane, 'P', prismaticPaste, 'C', pcb).setRegistryName(OpenLights.MODID,openLightBlock.getUnlocalizedName()));		
	}
	
	private static void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String key) {
		// For better readability
		GameRegistry.registerTileEntity(tileEntityClass, new ResourceLocation(OpenLights.MODID, key));
	}
}
