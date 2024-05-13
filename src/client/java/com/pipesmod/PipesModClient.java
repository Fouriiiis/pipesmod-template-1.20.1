package com.pipesmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;




@SuppressWarnings("deprecation")
public class PipesModClient implements ClientModInitializer {
	
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		BlockEntityRendererRegistry.register(PipesMod.PIPE_BLOCK_ENTITY, PipeBlockEntityRenderer::new);
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(), PipesMod.PIPE_BLOCK);
		
	}
}