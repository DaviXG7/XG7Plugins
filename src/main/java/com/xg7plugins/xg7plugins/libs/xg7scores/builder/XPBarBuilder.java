package com.xg7plugins.xg7plugins.libs.xg7scores.builder;

public class XPBarBuilder extends ScoreBuilder<XPBarBuilder> {

    private int level;
    private float progress;

    public XPBarBuilder(String id) {
        super(id);
    }

    public XPBarBuilder level(int level) {
        this.level = level;
        return this;
    }

    public XPBarBuilder progress(float progress) {
        this.progress = progress;
        return this;
    }

    @Override
    public XPBar build(Plugin plugin) {
        if (id == null || delayToUpdate == 0) throw new IllegalArgumentException("You must specify the id and the delay to update the score");

        return new XPBar(level, progress, id, condition, delayToUpdate, plugin);
    }
}
