package com.mraof.minestuck.world.storage.loot.conditions;

import java.lang.reflect.Field;
import java.util.Random;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.mraof.minestuck.world.MinestuckDimensionHandler;
import com.mraof.minestuck.world.lands.LandAspectRegistry.AspectCombination;
import com.mraof.minestuck.world.lands.terrain.TerrainLandAspect;
import com.mraof.minestuck.world.lands.title.TitleLandAspect;

import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public class LandAspectLootCondition implements LootCondition
{
	
	private final String[] landAspectNames;
	private final boolean inverted, includeSubtypes;
	
	public LandAspectLootCondition(String[] landAspect, boolean inverted, boolean subtypes)
	{
		this.landAspectNames = landAspect;
		this.inverted = inverted;
		this.includeSubtypes = subtypes;
	}
	
	@Override
	public boolean testCondition(Random rand, LootContext context)
	{
		WorldServer world = null;
		try	//TODO Wait for forge to add a proper getter
		{
			Field[] fields = context.getClass().getDeclaredFields();
			for(Field field : fields)
				if(field.getType().equals(WorldServer.class))
				{
					field.setAccessible(true);
					world = (WorldServer) field.get(context);
				}
		} catch(Exception e)
		{}
		
		if(world != null && MinestuckDimensionHandler.isLandDimension(world.provider.getDimension()))
		{
			AspectCombination aspects = MinestuckDimensionHandler.getAspects(world.provider.getDimension());
			
			TerrainLandAspect terrain = includeSubtypes ? aspects.aspectTerrain.getPrimaryVariant() : aspects.aspectTerrain;
			TitleLandAspect title = includeSubtypes ? aspects.aspectTitle.getPrimaryVariant() : aspects.aspectTitle;
			
			for(String name : landAspectNames)
				if(terrain.getPrimaryName().equals(name) || title.getPrimaryName().equals(name))
					return !inverted;
		}
		
		return inverted;
	}
	
	public static class Serializer extends LootCondition.Serializer<LandAspectLootCondition>
	{
		public Serializer()
		{
			super(new ResourceLocation("minestuck", "land_aspect"), LandAspectLootCondition.class);
		}
		
		@Override
		public void serialize(JsonObject json, LandAspectLootCondition value, JsonSerializationContext context)
		{
			if(value.landAspectNames.length == 1)
				json.addProperty("land_aspect", value.landAspectNames[0]);
			else
			{
				JsonArray list = new JsonArray();
				for(String aspect : value.landAspectNames)
					list.add(new JsonPrimitive(aspect));
				
				json.add("land_aspect", list);
			}
			
			json.addProperty("inverse", value.inverted);
			json.addProperty("subtypes", value.includeSubtypes);
		}
		@Override
		public LandAspectLootCondition deserialize(JsonObject json, JsonDeserializationContext context)
		{
			String[] landAspects;
			if(json.has("land_aspect") && json.get("land_aspect").isJsonArray())
			{
				JsonArray list = json.getAsJsonArray("land_aspect");
				landAspects = new String[list.size()];
				for(int i = 0; i < list.size(); i++)
					landAspects[i] = JsonUtils.getString(list.get(i), "land_aspect");
				
			} else landAspects = new String[] {JsonUtils.getString(json, "land_aspect")};
			boolean inverted = JsonUtils.getBoolean(json, "inverse", false);
			boolean subtypes = JsonUtils.getBoolean(json, "subtypes", true);
			return new LandAspectLootCondition(landAspects, inverted, subtypes);
		}
	}
}