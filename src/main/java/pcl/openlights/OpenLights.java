package pcl.openlights;

import java.net.URL;
import java.util.logging.Logger;

import pcl.openlights.blocks.LightBlock;
import pcl.openlights.tileentity.OpenLightTE;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
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
    public static boolean render3D = true;
    
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
        if((event.getSourceFile().getName().endsWith(".jar") || debug) && event.getSide().isClient() && cfg.enableMUD){
            try {
                Class.forName("pcl.openprinter.mud.ModUpdateDetector").getDeclaredMethod("registerMod", ModContainer.class, URL.class, URL.class).invoke(null,
                        FMLCommonHandler.instance().findContainerFor(this),
                        new URL("http://PC-Logix.com/OpenPrinter/get_latest_build.php"),
                        new URL("http://PC-Logix.com/OpenPrinter/changelog.txt")
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
    	openLightBlock.setCreativeTab(li.cil.oc.api.CreativeTab.instance);
    	openLightBlock.setUnlocalizedName("openlight");
        
    }
}
