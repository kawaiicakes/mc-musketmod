package ewewukek.musketmod;

import java.util.function.Supplier;

import ewewukek.musketmod.client.particle.GunfireSmokeParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.PacketListener;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.network.NetworkEvent;

import static ewewukek.musketmod.MusketMod.getParticleFromId;

public class ClientSetup {
    public ClientSetup(IEventBus bus) {
        ModLoadingContext.get().registerExtensionPoint(
            ConfigScreenHandler.ConfigScreenFactory.class,
            () -> new ConfigScreenHandler.ConfigScreenFactory(
                (client, parent) -> ClothConfigScreen.build(parent)));

        bus.addListener(this::setup);
        bus.addListener(this::registerRenderers);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, this::renderHand);
    }

    public void setup(final FMLClientSetupEvent event) {
        ClientUtilities.registerItemProperties();
    }

    public void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(BulletEntity.ENTITY_TYPE, BulletRenderer::new);
    }

    public void renderHand(final RenderHandEvent event) {
        ItemStack stack = event.getItemStack();
        if (!stack.isEmpty() && stack.getItem() instanceof GunItem) {
            Minecraft mc = Minecraft.getInstance();
            ClientUtilities.renderGunInHand(
                    mc.getEntityRenderDispatcher().getItemInHandRenderer(), mc.player,
                    event.getHand(), event.getPartialTick(), event.getInterpolatedPitch(),
                    event.getSwingProgress(), event.getEquipProgress(), stack,
                    event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight());
            event.setCanceled(true);
        }
    }

    public static void handleSmokeEffectPacket(MusketMod.SmokeEffectPacket packet, Supplier<NetworkEvent.Context> ctx) {
        PacketListener listener = ctx.get().getNetworkManager().getPacketListener();
        if (listener instanceof ClientPacketListener) {
            GunItem.fireParticles(
                    ((ClientPacketListener)listener).getLevel(), packet.origin, packet.direction, packet.largeSmoke
            );
        }
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = MusketMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Listener {
        @SubscribeEvent
        public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(getParticleFromId("gunfire_smoke_large"), GunfireSmokeParticle.TwoHandedGunProvider::new);
            event.registerSpriteSet(getParticleFromId("gunfire_smoke_small"), GunfireSmokeParticle.OneHandedGunProvider::new);
        }
    }
}
