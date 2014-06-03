package pcl.openlights;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class MultiItemBlock extends ItemBlock {

	public MultiItemBlock(Block par1) {
		super(par1);
		setHasSubtypes(true);
		setUnlocalizedName("openLight");
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