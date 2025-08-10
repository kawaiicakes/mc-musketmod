package ewewukek.musketmod.capabilities;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class WeightCapability {
    public static final UUID WEIGHT_MODIFIER_UUID = UUID.fromString("9f3f8cb9-a7c7-4968-8f87-655d84d548c3");
    public static final String WEIGHT_MODIFIER_NAME = "musketmod.player_weight.movement_modifier";

    protected float weight = 0.0F;

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public static class Provider implements ICapabilityProvider {
        public static Capability<WeightCapability> WEIGHT_CAP = CapabilityManager.get(new CapabilityToken<>() {});
        protected final LazyOptional<WeightCapability> lazyOptional = LazyOptional.of(WeightCapability::new);

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if (cap.equals(WEIGHT_CAP)) {
                return lazyOptional.cast();
            }

            return LazyOptional.empty();
        }
    }
}
