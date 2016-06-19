
package net.epoxide.surge.features;

import net.darkhax.bookshelf.lib.util.RenderUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FeatureHideUnseenEntities extends Feature {

    private boolean enabled = true;

    @Override
    public void onClientPreInit () {
        if (enabled)
            MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void setupConfig (Configuration config) {
        //TODO
    }

    @SubscribeEvent
    public void onRenderLiving (RenderLivingEvent.Pre event) {

        this.hideEntities(event);
    }

    @SubscribeEvent
    public void onRenderLiving (RenderLivingEvent.Specials.Pre event) {

        this.hideEntities(event);
    }

    private void hideEntities (RenderLivingEvent event) {

        final EntityLivingBase entity = event.getEntity();
        if (entity instanceof EntityPlayer)
            return;

        final Minecraft mc = Minecraft.getMinecraft();
        final Frustum camera = RenderUtils.getCamera(mc.getRenderViewEntity(), mc.getRenderPartialTicks());
        if (!camera.isBoundingBoxInFrustum(entity.getRenderBoundingBox()))
            event.setCanceled(true);
    }
}
