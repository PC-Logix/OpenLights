package pcl.openlights.tileentity;

import elucent.albedo.lighting.ILightProvider;
import elucent.albedo.lighting.Light;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AlbedoOpenLightTE extends OpenLightTE implements ILightProvider {

    private Light light;

    public AlbedoOpenLightTE(){
        light = Light.builder().pos(getPos()).color(0xFF0000, false).radius(15).build();
    }

    @Override
    public void readFromNBT(NBTTagCompound tag){
        super.readFromNBT(tag);
        
        if( world.isRemote )
            updateLight();
    }

    private void updateLight(){
        light = Light.builder().pos(getPos()).color(getLampColor(), false).radius(getBrightnessVal()).build();
    }

    @SideOnly(Side.CLIENT)
    public Light provideLight(){
        return light;
    }

}
