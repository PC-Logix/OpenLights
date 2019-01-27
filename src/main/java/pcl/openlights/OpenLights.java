package pcl.openlights;

import net.minecraft.init.Blocks;
import pcl.openlights.blocks.LightBlock;
import pcl.openlights.items.PrismaticPaste;
import pcl.openlights.tileentity.OpenLightTE;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/**
 * @author Caitlyn
 *
 */

@Mod(modid=OpenLights.MODID, name="OpenLights", version=BuildInfo.versionNumber + "." + BuildInfo.buildNumber, dependencies = "after:OpenComputers")

public class OpenLights {
	public static final String MODID = "openlights";

	@Instance(value = MODID)
	public static OpenLights instance;

	@SidedProxy(clientSide="pcl.openlights.ClientProxy", serverSide="pcl.openlights.CommonProxy")
	public static CommonProxy proxy;
	public static Config cfg = null;

	private static boolean debug = true;

	public static Block openLightBlock = new LightBlock();
	public static Item  prismaticPaste = new PrismaticPaste();
    public static Item  openLightItem = new ItemBlock(openLightBlock).setRegistryName(openLightBlock.getRegistryName().toString());

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(OpenLights.class);
		cfg = new Config(new Configuration(event.getSuggestedConfigurationFile()));
		proxy.registerRenderers();
	}
	
	//This should really go in another class but meh.
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(
				prismaticPaste,
				openLightItem
				);
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
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

	@EventHandler
	public void load(FMLInitializationEvent event)
	{
		proxy.registerColorHandler();
	}

}
