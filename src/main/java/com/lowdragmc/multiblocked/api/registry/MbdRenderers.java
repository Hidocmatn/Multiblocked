package com.lowdragmc.multiblocked.api.registry;


import com.google.common.collect.Maps;
import com.lowdragmc.multiblocked.Multiblocked;
import com.lowdragmc.multiblocked.client.renderer.IMultiblockedRenderer;
import com.lowdragmc.multiblocked.client.renderer.impl.GTRenderer;
import com.lowdragmc.multiblocked.client.renderer.impl.GeoComponentRenderer;
import com.lowdragmc.multiblocked.client.renderer.impl.MBDBlockStateRenderer;
import com.lowdragmc.multiblocked.client.renderer.impl.MBDIModelRenderer;
import com.lowdragmc.multiblocked.client.renderer.impl.ParticleRenderer;

import java.util.Map;

public class MbdRenderers {
    public static final Map<String, IMultiblockedRenderer> RENDERER_REGISTRY = Maps.newHashMap();

    public static void registerRenderer(IMultiblockedRenderer renderer) {
        RENDERER_REGISTRY.put(renderer.getType().toLowerCase(), renderer);
    }

    public static IMultiblockedRenderer getRenderer(String type) {
        return RENDERER_REGISTRY.get(type.toLowerCase());
    }

    public static void registerRenderers() {
        registerRenderer(MBDIModelRenderer.INSTANCE);
        registerRenderer(MBDBlockStateRenderer.INSTANCE);
        registerRenderer(ParticleRenderer.INSTANCE);
        registerRenderer(GTRenderer.INSTANCE);
        if (Multiblocked.isGeoLoaded()) {
            registerRenderer(GeoComponentRenderer.INSTANCE);
        }
    }
}
