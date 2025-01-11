package com.xg7plugins.data.playerdata;

import com.xg7plugins.data.database.entity.Column;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.data.database.entity.Pkey;
import com.xg7plugins.data.database.entity.Table;
import lombok.Data;

import java.util.UUID;

@Table(name = "player_data")
@Data
public class PlayerData implements Entity {

    @Pkey
    @Column(name = "player_id")
    private UUID playerUUID;
    @Column(name = "first_join")
    private Long firstJoin;
    @Column(name = "lang_id")
    private String langId;

    private PlayerData() {}

    public PlayerData(UUID playerUUID, String langId, Long firstJoin) {
        this.playerUUID = playerUUID;
        this.firstJoin = firstJoin;
        this.langId = langId;
    }
    public PlayerData(UUID playerUUID, String langId) {
        this.playerUUID = playerUUID;
        this.langId = langId;
    }


}
