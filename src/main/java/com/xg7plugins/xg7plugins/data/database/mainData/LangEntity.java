package com.xg7plugins.xg7plugins.data.database.mainData;

import com.xg7plugins.xg7plugins.data.database.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class LangEntity implements Entity {

    @Entity.PKey
    private String langId;
    private UUID playerUUID;

    public LangEntity() {}
}
