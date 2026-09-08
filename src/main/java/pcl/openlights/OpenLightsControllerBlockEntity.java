package pcl.openlights;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.BlockEntityEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public final class OpenLightsControllerBlockEntity extends BlockEntityEnvironment {
    public static final int MAX_LIGHTS = 4096;

    private static final String CONTROLLER_ID_TAG = "controller_id";
    private static final String NEXT_LIGHT_ID_TAG = "next_light_id";
    private static final String LIGHTS_TAG = "lights";
    private static final String LIGHT_ID_TAG = "id";
    private static final String LIGHT_POSITION_TAG = "position";

    private final Map<Integer, BlockPos> lights = new TreeMap<>();
    private UUID controllerId = UUID.randomUUID();
    private int nextLightId = 1;

    public OpenLightsControllerBlockEntity(BlockPos pos, BlockState state) {
        super(OpenLights.OPEN_LIGHTS_CONTROLLER_BLOCK_ENTITY.get(), pos, state);
        node = Network.newNode(this, Visibility.Network)
                .withComponent("openlights_controller", Visibility.Network)
                .create();
    }

    UUID controllerId() {
        return controllerId;
    }

    boolean matches(UUID id) {
        return controllerId.equals(id);
    }

    int bindLight(OpenLightBlockEntity light, String playerName) {
        if (level == null || level.isClientSide || light.getLevel() != level) {
            return -1;
        }

        Integer existingId = idForPosition(light.getBlockPos());
        if (existingId != null) {
            if (!light.isBoundTo(controllerId, worldPosition, existingId)) {
                light.detachFromController();
                light.bindToController(controllerId, worldPosition, existingId);
            }
            emitBound(existingId, light.getBlockPos(), playerName);
            return existingId;
        }

        if (lights.size() >= MAX_LIGHTS) {
            return -1;
        }

        light.detachFromController();
        int id = allocateId();
        lights.put(id, light.getBlockPos().immutable());
        light.bindToController(controllerId, worldPosition, id);
        setChanged();
        emitBound(id, light.getBlockPos(), playerName);
        return id;
    }

    void unregisterLight(int id, BlockPos expectedPosition, boolean emitEvent) {
        BlockPos registeredPosition = lights.get(id);
        if (registeredPosition == null || !registeredPosition.equals(expectedPosition)) {
            return;
        }
        lights.remove(id);
        setChanged();
        if (emitEvent) {
            emitUnbound(id, registeredPosition);
        }
    }

    void releaseLoadedLights() {
        if (level == null || level.isClientSide) {
            return;
        }
        for (Map.Entry<Integer, BlockPos> entry : new ArrayList<>(lights.entrySet())) {
            OpenLightBlockEntity light = loadedLight(entry.getKey(), entry.getValue());
            if (light != null) {
                light.clearControllerBinding();
            }
        }
        lights.clear();
        setChanged();
    }

    @Callback(doc = "function():string -- Return this controller's persistent identifier.")
    public Object[] getControllerId(Context context, Arguments args) {
        return new Object[]{controllerId.toString()};
    }

    @Callback(doc = "function():number -- Return the number of registered OpenLights.")
    public Object[] count(Context context, Arguments args) {
        return new Object[]{lights.size()};
    }

    @Callback(doc = "function():table -- List registered OpenLights by numeric ID.")
    public Object[] list(Context context, Arguments args) {
        Map<Integer, Object> result = new TreeMap<>();
        for (Map.Entry<Integer, BlockPos> entry : lights.entrySet()) {
            result.put(entry.getKey(), describe(entry.getKey(), entry.getValue()));
        }
        return new Object[]{result};
    }

    @Callback(doc = "function(id:number):table -- Return position, loaded state, color and brightness for an OpenLight.")
    public Object[] getLight(Context context, Arguments args) {
        int id = args.checkInteger(0);
        BlockPos pos = lights.get(id);
        return pos == null
                ? new Object[]{null, "unknown light ID"}
                : new Object[]{describe(id, pos)};
    }

    @Callback(doc = "function(id:number,color:number):boolean,string -- Set one OpenLight's RGB color.")
    public Object[] setColor(Context context, Arguments args) throws Exception {
        int id = args.checkInteger(0);
        int color = checkedColor(args.checkInteger(1));
        OpenLightBlockEntity light = loadedLight(id);
        if (light == null) {
            return unavailable(id);
        }
        light.setColorValue(color);
        return new Object[]{true, light.getColorString()};
    }

    @Callback(doc = "function(id:number,brightness:number):boolean,number -- Set one OpenLight's brightness from 0 to 15.")
    public Object[] setBrightness(Context context, Arguments args) throws Exception {
        int id = args.checkInteger(0);
        int brightness = checkedBrightness(args.checkInteger(1));
        OpenLightBlockEntity light = loadedLight(id);
        if (light == null) {
            return unavailable(id);
        }
        light.setBrightnessValue(brightness);
        return new Object[]{true, light.brightness()};
    }

    @Callback(doc = "function(id:number,color:number,brightness:number):boolean,string,number -- Set one OpenLight's color and brightness.")
    public Object[] setLight(Context context, Arguments args) throws Exception {
        int id = args.checkInteger(0);
        int color = checkedColor(args.checkInteger(1));
        int brightness = checkedBrightness(args.checkInteger(2));
        OpenLightBlockEntity light = loadedLight(id);
        if (light == null) {
            return unavailable(id);
        }
        light.setValues(color, brightness);
        return new Object[]{true, light.getColorString(), light.brightness()};
    }

    @Callback(doc = "function(color:number):number,number -- Set all loaded OpenLight colors; returns changed and unavailable counts.")
    public Object[] setAllColor(Context context, Arguments args) throws Exception {
        return updateAll(checkedColor(args.checkInteger(0)), null);
    }

    @Callback(doc = "function(brightness:number):number,number -- Set all loaded OpenLight brightness values; returns changed and unavailable counts.")
    public Object[] setAllBrightness(Context context, Arguments args) throws Exception {
        return updateAll(null, checkedBrightness(args.checkInteger(0)));
    }

    @Callback(doc = "function(color:number,brightness:number):number,number -- Set all loaded OpenLights; returns changed and unavailable counts.")
    public Object[] setAll(Context context, Arguments args) throws Exception {
        return updateAll(checkedColor(args.checkInteger(0)), checkedBrightness(args.checkInteger(1)));
    }

    @Callback(doc = "function(id:number):boolean -- Remove an OpenLight registration.")
    public Object[] removeLight(Context context, Arguments args) {
        int id = args.checkInteger(0);
        BlockPos pos = lights.get(id);
        if (pos == null) {
            return new Object[]{false, "unknown light ID"};
        }

        OpenLightBlockEntity light = loadedLight(id, pos);
        if (light != null) {
            light.clearControllerBinding();
        }
        unregisterLight(id, pos, true);
        return new Object[]{true};
    }

    private Object[] updateAll(Integer color, Integer brightness) {
        int changed = 0;
        int unavailable = 0;
        for (Map.Entry<Integer, BlockPos> entry : lights.entrySet()) {
            OpenLightBlockEntity light = loadedLight(entry.getKey(), entry.getValue());
            if (light == null) {
                unavailable++;
                continue;
            }
            if (color != null && brightness != null) {
                light.setValues(color, brightness);
            } else if (color != null) {
                light.setColorValue(color);
            } else if (brightness != null) {
                light.setBrightnessValue(brightness);
            }
            changed++;
        }
        return new Object[]{changed, unavailable};
    }

    private Map<String, Object> describe(int id, BlockPos pos) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", id);
        result.put("x", pos.getX());
        result.put("y", pos.getY());
        result.put("z", pos.getZ());

        OpenLightBlockEntity light = loadedLight(id, pos);
        result.put("loaded", light != null);
        if (light != null) {
            result.put("color", light.color());
            result.put("colorHex", light.getColorString());
            result.put("brightness", light.brightness());
        }
        return result;
    }

    private Object[] unavailable(int id) {
        return lights.containsKey(id)
                ? new Object[]{false, "light is not loaded"}
                : new Object[]{false, "unknown light ID"};
    }

    private OpenLightBlockEntity loadedLight(int id) {
        BlockPos pos = lights.get(id);
        return pos == null ? null : loadedLight(id, pos);
    }

    private OpenLightBlockEntity loadedLight(int id, BlockPos pos) {
        if (level == null || !level.hasChunkAt(pos)
                || !(level.getBlockEntity(pos) instanceof OpenLightBlockEntity light)
                || !light.isBoundTo(controllerId, worldPosition, id)) {
            return null;
        }
        return light;
    }

    private Integer idForPosition(BlockPos pos) {
        for (Map.Entry<Integer, BlockPos> entry : lights.entrySet()) {
            if (entry.getValue().equals(pos)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private int allocateId() {
        while (lights.containsKey(nextLightId)) {
            nextLightId++;
            if (nextLightId <= 0) {
                nextLightId = 1;
            }
        }
        int id = nextLightId++;
        if (nextLightId <= 0) {
            nextLightId = 1;
        }
        return id;
    }

    private void emitBound(int id, BlockPos pos, String playerName) {
        node.sendToReachable("computer.signal", "openlight_bound",
                id, pos.getX(), pos.getY(), pos.getZ(), playerName);
    }

    private void emitUnbound(int id, BlockPos pos) {
        node.sendToReachable("computer.signal", "openlight_unbound",
                id, pos.getX(), pos.getY(), pos.getZ());
    }

    private static int checkedColor(int color) throws Exception {
        if (color < 0x000000 || color > 0xFFFFFF) {
            throw new Exception("Valid RGB range is 0x000000 to 0xFFFFFF");
        }
        return color;
    }

    private static int checkedBrightness(int brightness) throws Exception {
        if (brightness < 0 || brightness > 15) {
            throw new Exception("Valid brightness range is 0 to 15");
        }
        return brightness;
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putUUID(CONTROLLER_ID_TAG, controllerId);
        tag.putInt(NEXT_LIGHT_ID_TAG, nextLightId);

        ListTag registrations = new ListTag();
        for (Map.Entry<Integer, BlockPos> light : lights.entrySet()) {
            CompoundTag registration = new CompoundTag();
            registration.putInt(LIGHT_ID_TAG, light.getKey());
            registration.putLong(LIGHT_POSITION_TAG, light.getValue().asLong());
            registrations.add(registration);
        }
        tag.put(LIGHTS_TAG, registrations);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        controllerId = tag.hasUUID(CONTROLLER_ID_TAG) ? tag.getUUID(CONTROLLER_ID_TAG) : UUID.randomUUID();
        nextLightId = Math.max(1, tag.getInt(NEXT_LIGHT_ID_TAG));
        lights.clear();

        ListTag registrations = tag.getList(LIGHTS_TAG, Tag.TAG_COMPOUND);
        for (int index = 0; index < registrations.size() && lights.size() < MAX_LIGHTS; index++) {
            CompoundTag registration = registrations.getCompound(index);
            int id = registration.getInt(LIGHT_ID_TAG);
            if (id > 0 && !lights.containsKey(id)) {
                lights.put(id, BlockPos.of(registration.getLong(LIGHT_POSITION_TAG)));
                nextLightId = Math.max(nextLightId, id + 1);
            }
        }
    }
}
