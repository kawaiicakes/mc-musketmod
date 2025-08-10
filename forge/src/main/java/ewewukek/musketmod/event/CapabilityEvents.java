package ewewukek.musketmod.event;

import ewewukek.musketmod.MusketMod;
import ewewukek.musketmod.capabilities.WeightCapability;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MusketMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CapabilityEvents {
    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof ServerPlayer player)) return;
        if (player.getCapability(WeightCapability.Provider.WEIGHT_CAP).isPresent()) return;

        event.addCapability(new ResourceLocation(
                MusketMod.MODID, "weight"),
                new WeightCapability.Provider()
        );
    }
}
