package pcl.openlights;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(OpenLights.MOD_ID)
public final class OpenLights {
    public static final String MOD_ID = "openlights";

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MOD_ID);

    public static final DeferredHolder<Block, OpenLightBlock> OPEN_LIGHT = BLOCKS.register("openlight", () ->
            new OpenLightBlock(Block.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .strength(0.5F)
                    .sound(SoundType.GLASS)
                    .lightLevel(state -> state.getValue(OpenLightBlock.BRIGHTNESS))));

    public static final DeferredHolder<Item, Item> OPEN_LIGHT_ITEM = ITEMS.register("openlight", () ->
            new BlockItem(OPEN_LIGHT.get(), new Item.Properties()));

    public static final DeferredHolder<Item, Item> PRISMATIC_PASTE = ITEMS.register("prismaticpaste", () ->
            new Item(new Item.Properties()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<OpenLightBlockEntity>> OPEN_LIGHT_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("openlight", () ->
                    BlockEntityType.Builder.of(OpenLightBlockEntity::new, OPEN_LIGHT.get()).build(null));

    public OpenLights(IEventBus modBus) {
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        BLOCK_ENTITIES.register(modBus);
        modBus.addListener(this::registerCapabilities);
        modBus.addListener(this::addCreativeContents);
    }

    @SuppressWarnings("unchecked")
    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        BlockCapability<li.cil.oc.api.network.Environment, Direction> capability =
                (BlockCapability<li.cil.oc.api.network.Environment, Direction>)
                        (BlockCapability<?, ?>) li.cil.oc.common.Capabilities.EnvironmentCapability();
        event.registerBlockEntity(capability, OPEN_LIGHT_BLOCK_ENTITY.get(), (blockEntity, side) -> blockEntity);
    }

    private void addCreativeContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().location().equals(ResourceLocation.fromNamespaceAndPath("opencomputers", "main"))) {
            event.accept(OPEN_LIGHT_ITEM.get());
            event.accept(PRISMATIC_PASTE.get());
        }
    }
}
