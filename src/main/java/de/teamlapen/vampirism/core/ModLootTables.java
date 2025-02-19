package de.teamlapen.vampirism.core;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.loot.LootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.loot.TableLootEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;

/**
 * Handles loading mod loot tables as well as injecting pools into vanilla tables
 * Inspired (or almost copied) from @williewillus LootHandler for vazkii's Botania
 * https://github.com/williewillus/Botania/blob/07f68b37da9ad3a246b95c042cd6c10bd91698d1/src/main/java/vazkii/botania/common/core/loot/LootHandler.java
 */
public class ModLootTables {
    private final static Logger LOGGER = LogManager.getLogger();
    private static final Set<ResourceLocation> LOOT_TABLES = Sets.newHashSet();
    //chests
    public static final ResourceLocation chest_hunter_trainer = register("chests/village/hunter_trainer");
    public static final ResourceLocation chest_vampire_dungeon = register("chests/dungeon/vampire_dungeon");
    private static final Map<String, ResourceLocation> INJECTION_TABLES = Maps.newHashMap();
    //inject
    public static final ResourceLocation abandoned_mineshaft = registerInject("abandoned_mineshaft");
    public static final ResourceLocation jungle_temple = registerInject("jungle_temple");
    public static final ResourceLocation stronghold_corridor = registerInject("stronghold_corridor");
    public static final ResourceLocation desert_pyramid = registerInject("desert_pyramid");
    public static final ResourceLocation stronghold_library = registerInject("stronghold_library");
    private static int injected = 0;

    static ResourceLocation registerInject(String resourceName) {
        ResourceLocation registryName = register("inject/" + resourceName);
        INJECTION_TABLES.put(resourceName, registryName);
        return registryName;
    }

    static ResourceLocation register(String resourceName) {
        return register(new ResourceLocation(REFERENCE.MODID, resourceName));
    }

    static ResourceLocation register(@Nonnull ResourceLocation resourceLocation) {
        LOOT_TABLES.add(resourceLocation);
        return resourceLocation;
    }

    public static Set<ResourceLocation> getLootTables() {
        return ImmutableSet.copyOf(LOOT_TABLES);
    }


    @SubscribeEvent
    public static void onLootLoad(LootTableLoadEvent event) {
        String prefix = "minecraft:chests/";
        String name = event.getName().toString();
        if (name.startsWith(prefix)) {
            String file = name.substring(name.indexOf(prefix) + prefix.length());
            if (INJECTION_TABLES.containsKey(file)) {
                try {
                    event.getTable().addPool(getInjectPool(file));
                    injected++;
                } catch (NullPointerException e) {
                    LOGGER.warn("Loottable {} is broken by some other mod. Cannot add Vampirism loot to it.", name);
                }
            }
        }
    }

    private static LootPool getInjectPool(String entryName) {
        LootEntry.Builder<?> entryBuilder = TableLootEntry.lootTableReference(INJECTION_TABLES.get(entryName)).setWeight(1);
        return LootPool.lootPool().name("vampirism_inject_pool").bonusRolls(0, 1).setRolls(new RandomValueRange(1)).add(entryBuilder).build();
    }

    /**
     * @return 0 if alright, or the count of not injected loottables
     */
    public static int checkAndResetInsertedAll() {
        int i = injected;
        injected = 0;
        return Math.max(0, INJECTION_TABLES.size()-i); //Sponge loads the loot tables for all worlds at start. Which makes this test not work anyway.
    }
}