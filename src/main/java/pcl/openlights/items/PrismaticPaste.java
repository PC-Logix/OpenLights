package pcl.openlights.items;

import java.lang.reflect.Field;

import pcl.openlights.tileentity.OpenLightTE;
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

	public PrismaticPaste(int id) {
		super(id);
		maxStackSize = 64;
		setUnlocalizedName("prismaticPaste");
		setTextureName("openlights:prismaticpaste");

		Class<?> clz = li.cil.oc.api.CreativeTab.class;
		try {
			Field f = clz.getField("instance");
			setCreativeTab(li.cil.oc.api.CreativeTab.instance);
		}
		catch ( NoSuchFieldException ex) {
			setCreativeTab(li.cil.oc.api.CreativeTab.Instance);
		}
	}
}