package com.xg7plugins.xg7plugins.libs.xg7holograms;

import com.xg7plugins.xg7plugins.libs.xg7holograms.holograms.Hologram;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ClickEvent {

    private final ClickType clickType;
    private final Hologram hologram;


    enum ClickType {
        LEFT_CLICK,
        RIGHT_CLICK,
        SHIFT_LEFT_CLICK,
        SHIFT_RIGHT_CLICK
    }

}
