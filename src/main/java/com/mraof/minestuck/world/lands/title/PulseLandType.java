package com.mraof.minestuck.world.lands.title;

import com.mraof.minestuck.block.MSBlocks;
import com.mraof.minestuck.player.EnumAspect;
import com.mraof.minestuck.util.MSSoundEvents;
import com.mraof.minestuck.world.biome.LandBiomeSet;
import com.mraof.minestuck.world.biome.LandBiomeType;
import com.mraof.minestuck.world.gen.LandGenSettings;
import com.mraof.minestuck.world.gen.feature.MSFeatures;
import com.mraof.minestuck.world.gen.structure.blocks.StructureBlockRegistry;
import com.mraof.minestuck.world.lands.LandProperties;
import com.mraof.minestuck.world.lands.terrain.TerrainLandType;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.configurations.DiskConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class PulseLandType extends TitleLandType
{
	public static final String PULSE = "minestuck.pulse";
	public static final String BLOOD = "minestuck.blood";
	
	public PulseLandType()
	{
		super(EnumAspect.BLOOD);
	}
	
	@Override
	public String[] getNames()
	{
		return new String[]{PULSE, BLOOD};
	}
	
	@Override
	public void registerBlocks(StructureBlockRegistry registry)
	{
		registry.setBlock("structure_wool_2", Blocks.RED_WOOL);
		registry.setBlock("carpet", Blocks.BROWN_CARPET);
		
		registry.setBlock("ocean", MSBlocks.BLOOD);
		registry.setBlock("river", MSBlocks.BLOOD);
		registry.setBlock("slime", MSBlocks.COAGULATED_BLOOD);
	}
	
	@Override
	public void setProperties(LandProperties properties)
	{
		properties.mergeFogColor(new Vec3(0.8, 0, 0), 0.8F);
	}
	
	@Override
	public void setGenSettings(LandGenSettings settings)
	{
		settings.oceanThreshold = Math.max(settings.oceanThreshold, -0.4F);
	}
	
	@Override
	public void setBiomeGeneration(BiomeGenerationSettings.Builder builder, StructureBlockRegistry blocks, LandBiomeType type, LandBiomeSet biomeSet)
	{
		
		if(type == LandBiomeType.ROUGH)
		{
			builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, PlacementUtils.inlinePlaced(MSFeatures.DISK.get(),
					new DiskConfiguration(MSBlocks.COAGULATED_BLOOD.get().defaultBlockState(), UniformInt.of(2, 5), 2, List.of(blocks.getBlockState("surface"), blocks.getBlockState("upper"))),
					CountPlacement.of(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));
		}
		
	}
	
	@Override
	public boolean isAspectCompatible(TerrainLandType otherType)
	{
		StructureBlockRegistry registry = new StructureBlockRegistry();
		otherType.registerBlocks(registry);
		return registry.getBlockState("ocean").getMaterial() != Material.LAVA;	//Lava is likely a too important part of the terrain aspect to be replaced
	}
	
	@Override
	public SoundEvent getBackgroundMusic()
	{
		return MSSoundEvents.MUSIC_PULSE;
	}
}