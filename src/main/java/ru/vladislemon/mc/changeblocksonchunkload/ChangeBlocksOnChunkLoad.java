package ru.vladislemon.mc.changeblocksonchunkload;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.PalettedContainer;

import java.util.HashMap;
import java.util.Map;

public class ChangeBlocksOnChunkLoad implements ModInitializer {
    private final Map<Block, Block> swapMap = new HashMap<>();

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            swapMap.put(Registry.BLOCK.get(new Identifier("indrev:tin_ore")), Blocks.STONE);
            swapMap.put(Registry.BLOCK.get(new Identifier("indrev:deepslate_tin_ore")), Blocks.DEEPSLATE);
            swapMap.put(Registry.BLOCK.get(new Identifier("indrev:lead_ore")), Blocks.STONE);
            swapMap.put(Registry.BLOCK.get(new Identifier("indrev:deepslate_lead_ore")), Blocks.DEEPSLATE);
            swapMap.put(Registry.BLOCK.get(new Identifier("indrev:silver_ore")), Blocks.STONE);
            swapMap.put(Registry.BLOCK.get(new Identifier("indrev:deepslate_silver_ore")), Blocks.DEEPSLATE);
        });
        ServerChunkEvents.CHUNK_LOAD.register((world, chunk) -> {
            ChunkSection[] sectionArray = chunk.getSectionArray();
            for (ChunkSection chunkSection : sectionArray) {
                if (chunkSection.isEmpty()) {
                    continue;
                }
                PalettedContainer<BlockState> blockStateContainer = chunkSection.getBlockStateContainer();
                blockStateContainer.lock();
                try {
                    for (int y = 0; y < 16; y++) {
                        for (int z = 0; z < 16; z++) {
                            for (int x = 0; x < 16; x++) {
                                BlockState blockState = blockStateContainer.get(x, y, z);
                                Block replace = swapMap.get(blockState.getBlock());
                                if (replace != null) {
                                    blockStateContainer.swapUnsafe(x, y, z, replace.getDefaultState());
                                }
                            }
                        }
                    }
                } finally {
                    blockStateContainer.unlock();
                }
            }
        });
    }
}
