package com.xg7plugins.xg7plugins.libs.xg7scores.builder;

import com.xg7plugins.xg7plugins.boot.Plugin;
import com.xg7plugins.xg7plugins.libs.xg7scores.Score;
import com.xg7plugins.xg7plugins.libs.xg7scores.ScoreCondition;

public abstract class ScoreBuilder<B extends ScoreBuilder<B>> {

    protected String id;
    protected long delayToUpdate;
    protected ScoreCondition condition;

    public ScoreBuilder(String id) {
        this.id = id;
        this.condition = player -> true;
    }

    public B delay(long delay) {
        delayToUpdate = delay;
        return (B) this;
    }
    public B condition(ScoreCondition condition) {
        this.condition = condition;
        return (B) this;
    }

    public abstract <T extends Score> T build(Plugin plugin);







}
