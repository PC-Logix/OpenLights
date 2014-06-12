/**
 * 
 */
package pcl.openlights.items;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * @author Caitlyn, Techokami
 *
 */
public class PrismaticPaste extends Item{

	public PrismaticPaste() {
		super();
		maxStackSize = 64;
		setUnlocalizedName("prismaticPaste");
		setTextureName("openlights:prismaticpaste");
		setCreativeTab(li.cil.oc.api.CreativeTab.instance);
	}

	public static void init() {
		PrismaticPaste item = new PrismaticPaste();
	}
}