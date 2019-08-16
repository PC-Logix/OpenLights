package pcl.openlights.util;

import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.client.CustomModLoadingErrorDisplayException;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly( Side.CLIENT )
public class ConcurrentlyLoadedColoredLightsModException extends CustomModLoadingErrorDisplayException
{
	public void initGui( GuiErrorScreen errorScreen, FontRenderer fontRenderer )
	{
		// Do nothing.
	}

	public void drawScreen( GuiErrorScreen errorScreen, FontRenderer fontRenderer, int mouseRelX, int mouseRelY, float tickTime )
	{
		int offset = 75;

		errorScreen.drawDefaultBackground();
		errorScreen.drawCenteredString( fontRenderer, "Albedo and Mirage are both loaded.", ( errorScreen.width / 2 ), offset, 0xFFFFFF );

		offset += 25;

		errorScreen.drawCenteredString( fontRenderer, "These core mods are mutually exclusive and must not be loaded simultaneously.", ( errorScreen.width / 2 ), offset, 0xEEEEEE );

		offset += 15;

		errorScreen.drawCenteredString( fontRenderer, "This error exists to prevent undefined behaviour which would otherwise occur", ( errorScreen.width / 2 ), offset, 0xEEEEEE );

		offset += 15;

		errorScreen.drawCenteredString( fontRenderer, "(e.g. broken texture and lighting renderer).", ( errorScreen.width / 2 ), offset, 0xEEEEEE );

		offset += 25;

		errorScreen.drawCenteredString( fontRenderer, "Please use only one of these core mods.", ( errorScreen.width / 2 ), offset, 0xFFFFFF );

		offset += 25;
	}
}
