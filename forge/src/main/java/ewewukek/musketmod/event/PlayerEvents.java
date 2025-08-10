package ewewukek.musketmod.event;

import ewewukek.musketmod.GunItem;
import ewewukek.musketmod.MusketMod;
import ewewukek.musketmod.capabilities.WeightCapability;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

@Mod.EventBusSubscriber(modid = MusketMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerEvents {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!(event.player instanceof ServerPlayer player)) return;

        float totalWeight = 0.0F;

        for (ItemStack stack : player.getInventory().items) {
            if (!(stack.getItem() instanceof GunItem gun)) continue;

            totalWeight += gun.weight();
        }

        if (player.getOffhandItem().getItem() instanceof GunItem gun) {
            totalWeight += gun.weight();
        }

        final AtomicBoolean updateModifier = new AtomicBoolean(false);

        // must be final or effectively final
        final float finalTotalWeight = totalWeight;
        player.getCapability(WeightCapability.Provider.WEIGHT_CAP).ifPresent(
                weightCapability -> {
                    if (weightCapability.getWeight() == finalTotalWeight) return;

                    weightCapability.setWeight(finalTotalWeight);
                    updateModifier.set(true);
                }
        );

        if (!updateModifier.get()) return;

        Objects.requireNonNull(player.getAttribute(Attributes.MOVEMENT_SPEED)).removeModifier(
                WeightCapability.WEIGHT_MODIFIER_UUID
        );

        Objects.requireNonNull(player.getAttribute(Attributes.MOVEMENT_SPEED)).addPermanentModifier(
                new AttributeModifier(
                        WeightCapability.WEIGHT_MODIFIER_UUID,
                        WeightCapability.WEIGHT_MODIFIER_NAME,
                        -(finalTotalWeight * finalTotalWeight / 100.0F),
                        AttributeModifier.Operation.MULTIPLY_TOTAL
                )
        );
    }
}
