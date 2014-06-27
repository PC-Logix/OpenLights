package pcl.openlights;

import java.net.URL;
import java.util.logging.Logger;
import java.lang.reflect.Field;

import li.cil.oc.api.Items;
import pcl.openlights.blocks.LightBlock;
import pcl.openlights.items.PrismaticPaste;
import pcl.openlights.tileentity.OpenLightTE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

/**
 * @author Caitlyn
 *
 */

@Mod(modid=OpenLights.MODID, name="OpenLights", version=BuildInfo.versionNumber + "." + BuildInfo.buildNumber, dependencies = "after:OpenComputers")
@NetworkMod(clientSideRequired=true)

public class OpenLights {
	public static final String MODID = "openlights";
	
	@Instance(value = MODID)
    public static OpenLights instance;
	
    @SidedProxy(clientSide="pcl.openlights.ClientProxy", serverSide="pcl.openlights.CommonProxy")
    public static CommonProxy proxy;
    public static Config cfg = null;
    
    private static boolean debug = true;
    public static Logger logger;
    
    public static Block openLightBlock;
	    

	
	final String[] multiBlockNames = { 
			"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"
	};
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	cfg = new Config(new Configuration(event.getSuggestedConfigurationFile()));
    	final Block openLightBlock = new LightBlock(cfg.printerBlockID, Material.iron);
    	final Item prismaticPaste = new PrismaticPaste(cfg.prismaticPasteID);
        if((event.getSourceFile().getName().endsWith(".jar") || debug) && event.getSide().isClient() && cfg.enableMUD){
            try {
                Class.forName("pcl.openprinter.mud.ModUpdateDetector").getDeclaredMethod("registerMod", ModContainer.class, URL.class, URL.class).invoke(null,
                        FMLCommonHandler.instance().findContainerFor(this),
                        new URL("http://PC-Logix.com/OpenLights/get_latest_build.php"),
                        new URL("http://PC-Logix.com/OpenLights/changelog.txt")
                );
            } catch (Throwable e) {
                //e.printStackTrace();
            }
        }
        logger = event.getModLog();
        
    	//openLightBlock = new LightBlock(cfg.printerBlockID, Material.iron);
    	//GameRegistry.registerBlock(openLightBlock, "openlights.openlight");
        

    	
		GameRegistry.registerBlock(openLightBlock, MultiItemBlock.class, "openlights.openlight");
		
		for (int ix = 0; ix < 16; ix++) {
			ItemStack multiBlockStack = new ItemStack(openLightBlock, 1, ix);
			LanguageRegistry.addName(multiBlockStack, multiBlockNames[multiBlockStack.getItemDamage()]);
		}
		
    	GameRegistry.registerTileEntity(OpenLightTE.class, "OpenLightTE");
    	
    	Class<?> clz = li.cil.oc.api.CreativeTab.class;
		try {
		    Field f = clz.getField("instance");
		    openLightBlock.setCreativeTab(li.cil.oc.api.CreativeTab.instance);
	        ItemStack redDye	= new ItemStack(Item.dyePowder, 1, 1);
	        ItemStack greenDye	= new ItemStack(Item.dyePowder, 1, 2);
	        ItemStack blueDye	= new ItemStack(Item.dyePowder, 1, 4);
	        ItemStack glowDust = new ItemStack(Item.glowstone);
	        ItemStack pcb	= li.cil.oc.api.Items.get("printedCircuitBoard").createItemStack(1);
	        ItemStack glassPane = new ItemStack(Block.thinGlass);

	        GameRegistry.addShapelessRecipe( new ItemStack(prismaticPaste, 4), redDye, greenDye, blueDye, glowDust);

	        GameRegistry.addRecipe( new ItemStack(openLightBlock, 1),
	                " G ",
	                "GPG",
	                " C ",
	                'G', glassPane, 'P', prismaticPaste, 'C', pcb);
		}
		catch ( NoSuchFieldException ex) {
			openLightBlock.setCreativeTab(li.cil.oc.api.CreativeTab.Instance);
	        ItemStack redDye	= new ItemStack(Item.dyePowder, 1, 1);
	        ItemStack greenDye	= new ItemStack(Item.dyePowder, 1, 2);
	        ItemStack blueDye	= new ItemStack(Item.dyePowder, 1, 4);
	        ItemStack glowDust = new ItemStack(Item.glowstone);
	        ItemStack pcb	= Items.PrintedCircuitBoard;
	        ItemStack glassPane = new ItemStack(Block.thinGlass);

	        GameRegistry.addShapelessRecipe( new ItemStack(prismaticPaste, 4), redDye, greenDye, blueDye, glowDust);

	        GameRegistry.addRecipe( new ItemStack(openLightBlock, 1),
	                " G ",
	                "GPG",
	                " C ",
	                'G', glassPane, 'P', prismaticPaste, 'C', pcb);
		}
    	
    	openLightBlock.setUnlocalizedName("openlight");
        
    }
}
