package pcl.openlights.blocks;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pcl.openlights.tileentity.AlbedoOpenLightTE;

public class AlbedoLightBlock extends LightBlock {
    @Override
    public TileEntity createNewTileEntity(World arg0, int arg1) {
        return new AlbedoOpenLightTE();
    }

}
