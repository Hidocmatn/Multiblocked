package com.lowdragmc.multiblocked.api.recipe;

import javax.annotation.Nullable;

public class Content {
    public transient Object content;
    public float chance;
    @Nullable
    public String slotName;
    @Nullable
    public String uiName;

    public Content(Object content, float chance, @Nullable String slotName, @Nullable String uiName) {
        this.content = content;
        this.chance = chance;
        this.slotName = slotName;
        this.uiName = uiName;
    }

    public Object getContent() {
        return content;
    }

}
