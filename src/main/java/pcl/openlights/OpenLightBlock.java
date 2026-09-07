package pcl.openlights;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.Nullable;

public final class OpenLightBlock extends BaseEntityBlock {
    public static final MapCodec<OpenLightBlock> CODEC = simpleCodec(OpenLightBlock::new);
    public static final IntegerProperty BRIGHTNESS = IntegerProperty.create("brightness", 0, 15);

    public OpenLightBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(BRIGHTNESS, 0));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(BRIGHTNESS);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new OpenLightBlockEntity(pos, state);
    }
}
