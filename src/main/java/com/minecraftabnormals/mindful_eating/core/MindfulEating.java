package com.minecraftabnormals.mindful_eating.core;

import com.minecraftabnormals.mindful_eating.client.HungerOverlay;
import com.minecraftabnormals.mindful_eating.compat.AppleskinCompat;
import com.teamabnormals.blueprint.common.world.storage.tracking.DataProcessors;
import com.teamabnormals.blueprint.common.world.storage.tracking.TrackedData;
import com.teamabnormals.blueprint.common.world.storage.tracking.TrackedDataManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import squeek.appleskin.client.DebugInfoHandler;
import squeek.appleskin.client.HUDOverlayHandler;
import squeek.appleskin.client.TooltipOverlayHandler;

import java.util.HashMap;

@Mod(MindfulEating.MODID)
@Mod.EventBusSubscriber(modid = MindfulEating.MODID)
public class MindfulEating
{
    public static final String MODID = "mindful_eating";

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static HashMap<String, Integer> ORIGINAL_ITEMS = new HashMap<>();

    public static HashMap<String, FoodProperties> ORIGINAL_FOODS = new HashMap<>();

    public static final TrackedData<ResourceLocation> LAST_FOOD = TrackedData.Builder.create(DataProcessors.RESOURCE_LOCATION, () -> new ResourceLocation("cooked_beef")).enableSaving().build();

    public static final TrackedData<Integer> SHEEN_COOLDOWN = TrackedData.Builder.create(DataProcessors.INT, () -> 0).enableSaving().build();

    public static final TrackedData<Boolean> HURT_OR_HEAL = TrackedData.Builder.create(DataProcessors.BOOLEAN, () -> false).enableSaving().build();

    public static IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    public MindfulEating() {
        //IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();


        MinecraftForge.EVENT_BUS.register(this);
        //if(Minecraft.getInstance().level.isClientSide)
        bus.addListener(ClientSetup::init);

        if (ModList.get().isLoaded("appleskin")) {
            MinecraftForge.EVENT_BUS.register(AppleskinCompat.class);
        }

        TrackedDataManager.INSTANCE.registerData(new ResourceLocation(MindfulEating.MODID, "last_food"), LAST_FOOD);
        TrackedDataManager.INSTANCE.registerData(new ResourceLocation(MindfulEating.MODID, "correct_food"), SHEEN_COOLDOWN);
        TrackedDataManager.INSTANCE.registerData(new ResourceLocation(MindfulEating.MODID, "hurt_or_heal"), HURT_OR_HEAL);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MEConfig.COMMON_SPEC);
    }
    public static class ClientSetup {
        public static void init(FMLClientSetupEvent event) {
            HungerOverlay.init();
        }
    }
}
