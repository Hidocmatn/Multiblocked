package com.lowdragmc.multiblocked.api.tile.part;

import com.lowdragmc.multiblocked.Multiblocked;
import com.lowdragmc.multiblocked.api.definition.PartDefinition;
import com.lowdragmc.multiblocked.api.kubejs.events.PartAddedEvent;
import com.lowdragmc.multiblocked.api.kubejs.events.PartRemovedEvent;
import com.lowdragmc.multiblocked.api.kubejs.events.UpdateRendererEvent;
import com.lowdragmc.multiblocked.api.pattern.MultiblockState;
import com.lowdragmc.multiblocked.api.tile.ComponentTileEntity;
import com.lowdragmc.multiblocked.api.tile.ControllerTileEntity;
import com.lowdragmc.multiblocked.api.tile.IControllerComponent;
import com.lowdragmc.multiblocked.client.renderer.IMultiblockedRenderer;
import com.lowdragmc.multiblocked.persistence.MultiblockWorldSavedData;
import dev.latvian.kubejs.script.ScriptType;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A TileEntity that defies the part of multi.
 *
 * part of the multiblock.
 */
public abstract class PartTileEntity<T extends PartDefinition> extends ComponentTileEntity<T> implements IPartComponent {

    public Set<BlockPos> controllerPos = new HashSet<>();

    public PartTileEntity(T definition) {
        super(definition);
    }

    @Override
    public boolean isFormed() {
        for (BlockPos blockPos : controllerPos) {
            TileEntity controller = level.getBlockEntity(blockPos);
            if (controller instanceof IControllerComponent && ((IControllerComponent) controller).isFormed()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public IMultiblockedRenderer updateCurrentRenderer() {
        if (definition.workingRenderer != null) {
            for (IControllerComponent controller : getControllers()) {
                if (controller.isFormed() && controller.getStatus().equals("working")) {
                    IMultiblockedRenderer renderer = definition.workingRenderer;
                    if (Multiblocked.isKubeJSLoaded() && level != null) {
                        UpdateRendererEvent event = new UpdateRendererEvent(this, renderer);
                        event.post(ScriptType.of(level), UpdateRendererEvent.ID, getSubID());
                        renderer = event.getRenderer();
                    }
                    return renderer;
                }
            }
        }
        return super.updateCurrentRenderer();
    }

    public boolean canShared() {
        return definition.canShared;
    }

    @Override
    public boolean hasController(BlockPos controllerPos) {
        return this.controllerPos.contains(controllerPos);
    }

    public List<IControllerComponent> getControllers() {
        List<IControllerComponent> result = new ArrayList<>();
        for (BlockPos blockPos : controllerPos) {
            TileEntity controller = level.getBlockEntity(blockPos);
            if (controller instanceof IControllerComponent && ((IControllerComponent) controller).isFormed()) {
                result.add((IControllerComponent) controller);
            }
        }
        return result;
    }

    public void addedToController(@Nonnull IControllerComponent controller){
        if (controllerPos.add(controller.self().getBlockPos())) {
            writeCustomData(-1, this::writeControllersToBuffer);
            if (Multiblocked.isKubeJSLoaded() && controller instanceof ControllerTileEntity && level != null) {
                new PartAddedEvent((ControllerTileEntity) controller).post(ScriptType.of(level), PartAddedEvent.ID, getSubID());
            }
            setStatus("idle");
        }
    }

    public void removedFromController(@Nonnull IControllerComponent controller){
        if (controllerPos.remove(controller.self().getBlockPos())) {
            writeCustomData(-1, this::writeControllersToBuffer);
            if (Multiblocked.isKubeJSLoaded() && controller instanceof ControllerTileEntity && level != null) {
                new PartRemovedEvent((ControllerTileEntity) controller).post(ScriptType.of(level), PartRemovedEvent.ID, getSubID());
            }
            if (getControllers().isEmpty()) {
                setStatus("unformed");
            }
        }
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        writeControllersToBuffer(buf);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        readControllersFromBuffer(buf);
    }

    @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        if (dataId == -1) {
            readControllersFromBuffer(buf);
        } else {
            super.receiveCustomData(dataId, buf);
        }
    }

    @Override
    public void load(@Nonnull BlockState state, @Nonnull CompoundNBT compound) {
        super.load(state, compound);
        for (MultiblockState multiblockState : MultiblockWorldSavedData.getOrCreate(level).getControllerInChunk(new ChunkPos(getBlockPos()))) {
            if(multiblockState.isPosInCache(getBlockPos())) {
                controllerPos.add(multiblockState.controllerPos);
            }
        }
    }

    private void writeControllersToBuffer(PacketBuffer buffer) {
        buffer.writeVarInt(controllerPos.size());
        for (BlockPos pos : controllerPos) {
            buffer.writeBlockPos(pos);
        }
    }

    private void readControllersFromBuffer(PacketBuffer buffer) {
        int size = buffer.readVarInt();
        controllerPos.clear();
        for (int i = size; i > 0; i--) {
            controllerPos.add(buffer.readBlockPos());
        }
    }

    public static class PartSimpleTileEntity extends PartTileEntity<PartDefinition> {

        public PartSimpleTileEntity(PartDefinition definition) {
            super(definition);
        }
    }

}
