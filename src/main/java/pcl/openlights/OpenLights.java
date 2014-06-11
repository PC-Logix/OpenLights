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
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

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
        	
	final String[] multiBlockNames = { 
			"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"
	};
	
	final Block openLightBlock = new LightBlock();
	
	public static Item  prismaticPaste;
	
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	cfg = new Config(new Configuration(event.getSuggestedConfigurationFile()));
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
    	
		//GameRegistry.registerBlock(openLightBlock, "openlights.openlight");
        
    	GameRegistry.registerBlock(openLightBlock, MultiItemBlock.class, "openlights.openlight");

    	for (int ix = 0; ix < 16; ix++) {
    		ItemStack multiBlockStack = new ItemStack(openLightBlock, 1, ix);
    	}
        
    	GameRegistry.registerTileEntity(OpenLightTE.class, "OpenLightTE");
    	//openLightBlock.setCreativeTab(li.cil.oc.api.CreativeTab.instance);
		
		prismaticPaste = new PrismaticPaste();
		GameRegistry.registerItem(prismaticPaste, "openlights.prismaticPaste");
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
