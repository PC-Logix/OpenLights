package pcl.openlights;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/**
 * @author Caitlyn
 *
 */
public class Config
{
    	
	private boolean defaultEnableMUD = true;
	public final boolean enableMUD;

    public Config(Configuration config)
    {
        config.load();
        enableMUD = config.get("options", "enableMUD", true, "Enable the Update Checker? Disabling this will remove all traces of the MUD.").getBoolean(true);
        if( config.hasChanged() )
        {
            config.save();
        }
    }
}
