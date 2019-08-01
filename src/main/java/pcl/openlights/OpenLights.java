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
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author Caitlyn
 *
 */

@Mod(
		modid=OpenLights.MODID, name="OpenLights",
		version=BuildInfo.versionNumber + "." + BuildInfo.buildNumber,
		dependencies = "after:opencomputers;after:albedocore;after:albedo@[0.1,);after:mirage@[2.0,)")

public class OpenLights {
	public static final String MODID = "openlights";

	@Instance(value = MODID)
	public static OpenLights instance;

	/* note about albedo, the coremod stuff didnt work for me in dev environment so you may have to test out of dev environment */
	public static boolean albedoSupport = false;
	public static boolean mirageSupport = false;

	@SidedProxy(clientSide="pcl.openlights.ClientProxy", serverSide="pcl.openlights.CommonProxy")
	public static CommonProxy proxy;
	public static Config cfg = null;

	private static boolean debug = true;
    
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		boolean mirageSupportServer;
		
		if( event.getSide() == Side.CLIENT )
		{
			if( Loader.isModLoaded( "mirage" ) && Loader.isModLoaded( "albedo" ) )
			{
				event.getModLog().error( "Albedo and Mirage are both loaded. These mods are mutually exclusive core mods and must not be loaded simultaneously. Undefined behaviour may occur, even when using Vanilla light sources. Please only use one of these core mods." );
				event.getModLog().error( "Colored lighting support initialization failed due to previous errors. Falling back to Vanilla (white) lighting. This will still result in undefined behaviour." );
			}
			else
			{
				mirageSupport = Loader.isModLoaded( "mirage" );
				albedoSupport = Loader.isModLoaded( "albedo" );
			}
		}
		else
		{
			try
			{
				Class.forName( "com.elytradev.mirage.Mirage", false, this.getClass().getClassLoader() );
				mirageSupportServer = true;
			}
			catch( ClassNotFoundException e )
			{
				mirageSupportServer = false;
			}
			
			if( Loader.isModLoaded( "albedocore" ) && mirageSupportServer )
			{
				event.getModLog().error( "Albedo and Mirage are both loaded. These mods are mutually exclusive core mods and must not be loaded simultaneously. Undefined behaviour may occur, even when using Vanilla light sources. Please only use one of these core mods." );
				event.getModLog().error( "Colored lighting support initialization failed due to previous errors. Falling back to Vanilla (white) lighting. This will still result in undefined behaviour." );
			}
		}
		
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
