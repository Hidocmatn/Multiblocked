package com.lowdragmc.multiblocked.api.gui.blueprint_table;

import com.lowdragmc.lowdraglib.gui.editor.ui.Editor;
import com.lowdragmc.lowdraglib.gui.texture.ItemStackTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.multiblocked.Multiblocked;
import com.lowdragmc.multiblocked.api.tile.BlueprintTableTileEntity;
import net.minecraft.world.item.Items;

import java.awt.Desktop;
import java.net.URI;

public class BlueprintTableWidget extends WidgetGroup {
    public final BlueprintTableTileEntity table;

    public BlueprintTableWidget(BlueprintTableTileEntity table) {
        super(0, 0, 384, 256);
        this.table = table;
        this.addWidget(0, new ImageWidget(0, 0, getSize().width, getSize().height, new ResourceTexture("multiblocked:textures/gui/blueprint_table.png")));
//        this.addWidget(new ButtonWidget(40, 40, 40, 40, new ItemStackTexture(MbdItems.BUILDER), this::templateBuilder).setHoverBorderTexture(1, -1).setHoverTooltips("multiblocked.gui.blueprint_table.builder_template"));
        this.addWidget(new ButtonWidget(40, 40, 40, 40, new ItemStackTexture(
                Items.PAPER), this::recipeMapBuilder).setHoverBorderTexture(1, -1).setHoverTooltips("multiblocked.gui.blueprint_table.recipe_map"));
        this.addWidget(new ButtonWidget(90, 40, 40, 40, new ItemStackTexture(BlueprintTableTileEntity.tableDefinition.getStackForm()), this::controllerBuilder).setHoverBorderTexture(1, -1).setHoverTooltips("multiblocked.gui.blueprint_table.controller_block"));
        this.addWidget(new ButtonWidget(140, 40, 40, 40, new ItemStackTexture(BlueprintTableTileEntity.partDefinition.getStackForm()), this::partBuilder).setHoverBorderTexture(1, -1).setHoverTooltips("multiblocked.gui.blueprint_table.part_block"));
        this.addWidget(new ButtonWidget(40, 90, 40, 40, new ItemStackTexture(Items.END_CRYSTAL), this::rendererBuilder).setHoverBorderTexture(1, -1).setHoverTooltips("multiblocked.gui.blueprint_table.renderer_assistant"));
        this.addWidget(new ButtonWidget(90, 90, 40, 40, new ResourceTexture("multiblocked:textures/gui/wiki.png"), this::openWikiPage).setHoverBorderTexture(1, -1).setHoverTooltips("multiblocked.gui.blueprint_table.wiki"));
        this.addWidget(new ButtonWidget(140, 90, 40, 40, new ResourceTexture(), this::openUIEditor).setHoverBorderTexture(1, -1).setHoverTooltips("multiblocked.gui.blueprint_table.ui_editor"));
    }

    private void openUIEditor(ClickData clickData) {
        widgets.forEach(this::waitToRemoved);
        if (clickData.isRemote) {
            getGui().setFullScreen();
            Editor editor = new Editor(Multiblocked.location);
            getGui().mainGroup.addWidget(editor);
            editor.onScreenSizeUpdate(getGui().getScreenWidth(), getGui().getScreenHeight());
        }
    }

    private void openWikiPage(ClickData clickData) {
        if (clickData.isRemote) {
            try {
                Desktop desktop = Desktop.getDesktop();
                if (Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(new URI("https://github.com/Low-Drag-MC/Multiblocked/wiki"));
                }
            } catch (Throwable e) {
                Multiblocked.LOGGER.warn("unable to open the web page: {}", e.getMessage());
            }
        }
    }

    private void rendererBuilder(ClickData clickData) {
        widgets.forEach(this::waitToRemoved);
        if (clickData.isRemote) {
            this.addWidget(0, new RendererBuilderWidget());
        }
    }

    private void partBuilder(ClickData clickData) {
        widgets.forEach(this::waitToRemoved);
        if (clickData.isRemote) {
            this.addWidget(0, new PartBuilderWidget());
        }
    }

    private void recipeMapBuilder(ClickData clickData) {
        widgets.forEach(this::waitToRemoved);
        WidgetGroup group = new WidgetGroup(0, 0, getSize().width, getSize().height).setClientSideWidget();
        this.addWidget(0, group);
        if (clickData.isRemote) {
            group.addWidget(0, new ImageWidget(0, 0, getSize().width, getSize().height, new ResourceTexture("multiblocked:textures/gui/blueprint_page.png")));
            group.addWidget(new RecipeMapBuilderWidget(this, 200, 31, 150, 188));
        }
    }

    private void controllerBuilder(ClickData clickData) {
        widgets.forEach(this::waitToRemoved);
        this.addWidget(0, new ControllerBuilderWidget(table));
    }

    private void templateBuilder(ClickData clickData) {
        widgets.forEach(this::waitToRemoved);
        this.addWidget(0, new TemplateBuilderWidget(table));
    }

}
