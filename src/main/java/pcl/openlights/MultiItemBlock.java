package pcl.openlights;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class MultiItemBlock extends ItemBlock {

	public MultiItemBlock(int id) {
		super(id);
		setHasSubtypes(true);
		setUnlocalizedName("multiBlock");
	}
	
	@Override
	public int getMetadata (int damageValue) {
		return damageValue;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return getUnlocalizedName();
	}

}