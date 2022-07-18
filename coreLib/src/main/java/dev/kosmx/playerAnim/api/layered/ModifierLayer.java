package dev.kosmx.playerAnim.api.layered;

import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.core.util.Vec3f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ModifierLayer<T extends IAnimation> implements IAnimation {

    private final List<IModifier> modifiers = new ArrayList<>();
    @Nullable
    final T animation;


    public ModifierLayer(@Nullable T animation, IModifier... modifiers) {
        this.animation = animation;
        Collections.addAll(this.modifiers, modifiers);
    }

    @Override
    public void tick() {
        for (int i = 0; i < modifiers.size(); i++) {
            if (modifiers.get(i).canRemove()) {
                removeModifier(i--);
            }
        }
        if (modifiers.size() > 0) {
            modifiers.get(0).tick();
        } else if (animation != null) animation.tick();
    }

    public void addModifier(IModifier modifier, int idx) {
        modifiers.add(idx, modifier);
        this.linkModifiers();
    }

    public void removeModifier(int idx) {
        modifiers.remove(idx);
        this.linkModifiers();
    }

    public int size() {
        return modifiers.size();
    }

    public @Nullable T getAnimation() {
        return animation;
    }

    protected void linkModifiers() {
        Iterator<IModifier> modifierIterator = modifiers.iterator();
        if (modifierIterator.hasNext()) {
            IModifier tmp = modifierIterator.next();
            while (modifierIterator.hasNext()) {
                IModifier tmp2 = modifierIterator.next();
                tmp.setAnim(tmp2);
                tmp = tmp2;
            }
        }
    }


    @Override
    public boolean isActive() {
        if (modifiers.size() > 0) {
            return modifiers.get(0).isActive();
        } else if (animation != null) return animation.isActive();
        return false;
    }

    @Override
    public Vec3f get3DTransform(String modelName, TransformType type, float tickDelta, Vec3f value0) {
        if (modifiers.size() > 0) {
            return modifiers.get(0).get3DTransform(modelName, type, tickDelta, value0);
        } else if (animation != null) return animation.get3DTransform(modelName, type, tickDelta, value0);
        return value0;
    }

    @Override
    public void setupAnim(float tickDelta) {
        if (modifiers.size() > 0) {
            modifiers.get(0).setupAnim(tickDelta);
        } else animation.setupAnim(tickDelta);
    }
}