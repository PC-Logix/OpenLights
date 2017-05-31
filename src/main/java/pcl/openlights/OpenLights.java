package pcl.openlights;

import java.net.URL;
import java.util.logging.Logger;

import net.minecraft.init.Blocks;
import pcl.openlights.blocks.LightBlock;
import pcl.openlights.items.PrismaticPaste;
import pcl.openlights.tileentity.OpenLightTE;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.LanguageRegistry;

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

	public static Item  prismaticPaste;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		cfg = new Config(new Configuration(event.getSuggestedConfigurationFile()));
		GameRegistry.registerBlock(openLightBlock,"openlight");
		GameRegistry.registerTileEntity(OpenLightTE.class, "OpenLightTE");

		prismaticPaste = new PrismaticPaste();
		GameRegistry.registerItem(prismaticPaste, "prismaticPaste");
	}

	@EventHandler
	public void load(FMLInitializationEvent event)
	{
		ItemStack redDye	    = new ItemStack(Items.dye, 1, 1);
		ItemStack greenDye	    = new ItemStack(Items.dye, 1, 2);
		ItemStack blueDye	    = new ItemStack(Items.dye, 1, 4);
		ItemStack glowDust      = new ItemStack(Items.glowstone_dust);
		ItemStack pcb		    = li.cil.oc.api.Items.get("printedCircuitBoard").createItemStack(1);
		ItemStack glassPane     = new ItemStack(Blocks.glass_pane);

		GameRegistry.addShapelessRecipe( new ItemStack(prismaticPaste, 4), redDye, greenDye, blueDye, glowDust);

		GameRegistry.addRecipe( new ItemStack(openLightBlock, 1),
				" G ",
				"GPG",
				" C ",
				'G', glassPane, 'P', prismaticPaste, 'C', pcb);

	}
}
