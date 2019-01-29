/**
 * 
 */
package pcl.openlights.items;

import net.minecraft.item.Item;

/**
 * @author Caitlyn, Techokami
 *
 */
public class PrismaticPaste extends Item {
	public final static String NAME = "prismaticpaste";

	public PrismaticPaste() {
		super();
		setMaxStackSize(64);
		setRegistryName(NAME);
		setUnlocalizedName(NAME);
		setCreativeTab(li.cil.oc.api.CreativeTab.instance);
	}
}