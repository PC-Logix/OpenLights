package pcl.openlights;

import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import pcl.openlights.tileentity.OpenLightTE;

import java.util.HashSet;

/**
 * @author Caitlyn
 *
 */

@Mod(
		modid=OpenLights.MODID, name="OpenLights",
		version=BuildInfo.versionNumber + "." + BuildInfo.buildNumber,
		dependencies = "after:opencomputers;after:albedo@[0.1,)")

public class OpenLights {
	public static final String MODID = "openlights";

	@Instance(value = MODID)
	public static OpenLights instance;

	/* note about albedo, the coremod stuff didnt work for me in dev environment so you may have to test out of dev environment */
	public static boolean albedoSupport = false;

	public static HashSet<OpenLightTE> lights = new HashSet<>();

	@SidedProxy(clientSide="pcl.openlights.ClientProxy", serverSide="pcl.openlights.CommonProxy")
	public static CommonProxy proxy;
	public static Config cfg = null;

	private static boolean debug = true;
    
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		albedoSupport = Loader.isModLoaded("albedo");
		MinecraftForge.EVENT_BUS.register(ContentRegistry.class);
		MinecraftForge.EVENT_BUS.register(OpenLights.class);
		cfg = new Config(new Configuration(event.getSuggestedConfigurationFile()));
		proxy.registerRenderers();
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event)
	{
		proxy.registerColorHandler();
	}
	
    @SubscribeEvent
    public static void onRegisterModels(ModelRegistryEvent event) {
        proxy.registerModels();
    }

}
