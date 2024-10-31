package com.minecraftabnormals.mindful_eating.client;

import com.illusivesoulworks.diet.api.DietApi;
import com.illusivesoulworks.diet.api.type.IDietGroup;
import com.minecraftabnormals.mindful_eating.compat.AppleskinCompat;
import com.minecraftabnormals.mindful_eating.compat.FarmersDelightCompat;
import com.minecraftabnormals.mindful_eating.core.MindfulEating;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teamabnormals.blueprint.common.world.storage.tracking.IDataManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.GuiOverlayManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import squeek.appleskin.client.HUDOverlayHandler;

import java.util.Random;
import java.util.Set;

import static com.minecraftabnormals.mindful_eating.core.MindfulEating.bus;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = MindfulEating.MODID, value = Dist.CLIENT)
public class HungerOverlay {

    public static final ResourceLocation GUI_HUNGER_ICONS_LOCATION = new ResourceLocation(MindfulEating.MODID, "textures/gui/hunger_icons.png");
    public static final ResourceLocation GUI_NOURISHMENT_ICONS_LOCATION = new ResourceLocation(MindfulEating.MODID, "textures/gui/nourished_icons.png");
    public static final ResourceLocation GUI_SATURATION_ICONS_LOCATION = new ResourceLocation(MindfulEating.MODID, "textures/gui/saturation_icons.png");
    public static final ResourceLocation GUI_EMPTY_ICONS_LOCATION = new ResourceLocation(MindfulEating.MODID, "textures/gui/empty_icons.png");
    static ResourceLocation FOOD_LEVEL_ELEMENT = new ResourceLocation("minecraft", "food_level");
    private static final Minecraft minecraft = Minecraft.getInstance();

    private static final Random random = new Random();
    public static int foodIconOffset;
    public static void init(){

        MinecraftForge.EVENT_BUS.addListener(HungerOverlay::hungerIconOverride);
        bus.addListener(HungerOverlay::registerOverlay);
    }
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void hungerIconOverride(RenderGuiOverlayEvent event) {
        if (event.getOverlay() == GuiOverlayManager.findOverlay(FOOD_LEVEL_ELEMENT) && ModList.get().isLoaded("farmersdelight")) {
            FarmersDelightCompat.resetNourishedHungerOverlay();
        }
    }
    @SubscribeEvent
    public static void registerOverlay(RegisterGuiOverlaysEvent event){
        event.registerAbove(FOOD_LEVEL_ELEMENT, "mindful_eating_hunger", ((gui, graphics, partialTicks, width, height) -> {
            boolean isMounted = minecraft.player != null && minecraft.player.getVehicle() instanceof LivingEntity;
            if (!isMounted && !minecraft.options.hideGui && gui.shouldDrawSurvivalElements()) {
                renderHungerIcons(gui, graphics);
            }
        }));
    }
    public static void renderHungerIcons(ForgeGui gui, GuiGraphics poseStack) {
        Player player = minecraft.player;
        IDataManager playerManager = ((IDataManager) player);

        ResourceLocation lastAte = playerManager.getValue(MindfulEating.LAST_FOOD);
        ItemStack stack = new ItemStack(ForgeRegistries.ITEMS.getValue(lastAte));
        Set<IDietGroup> groups = DietApi.getInstance().getGroups(player, stack);

        if (groups.isEmpty()){
            return;
        }


        FoodData foodData = player.getFoodData();
        foodIconOffset = gui.rightHeight;

        int top = minecraft.getWindow().getGuiScaledHeight() - foodIconOffset + 10;
        foodIconOffset += 10;

        int left = minecraft.getWindow().getGuiScaledWidth() / 2 + 91;

        drawHungerIcons(player, foodData, top, left, poseStack, playerManager, groups.toArray(new IDietGroup[0]));
    }

    public static void drawHungerIcons(Player player, FoodData stats, int top, int left, GuiGraphics poseStack, IDataManager playerManager, IDietGroup[] groups) {
        ResourceLocation texture = GUI_HUNGER_ICONS_LOCATION;
        int level = stats.getFoodLevel();
        int ticks = minecraft.gui.getGuiTicks();
        float modifiedSaturation = Math.min(stats.getSaturationLevel(), 20);
        for (int i = 0; i < 10; i++) {
            int idx = i * 2 + 1;
            int x = left - i * 8 - 9;
            int y = top;
            int icon = 0;

            FoodGroups foodGroup = FoodGroups.byDietGroup(groups[i % groups.length]);
            int group = foodGroup != null ? foodGroup.getTextureOffset() : 0;
            byte background = 0;

            //has hunger offset
            if (player.hasEffect(MobEffects.HUNGER)) {
                icon += 36;
                background = 13;
            }
            //has farmers delight effect
            if (ModList.get().isLoaded("farmersdelight")
                    && player.hasEffect(ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("farmersdelight", "nourishment")))
                    && FarmersDelightCompat.NOURISHED_HUNGER_OVERLAY) {
                FarmersDelightCompat.setNourishedHungerOverlay(false);
                texture = GUI_NOURISHMENT_ICONS_LOCATION;
                icon -= player.hasEffect(MobEffects.HUNGER) ? 45 : 27;
                background = 0;
            }

            if (player.getFoodData().getSaturationLevel() <= 0.0F && ticks % (level * 3 + 1) == 0) {
                y = top + (random.nextInt(3) - 1);
            }

            //Draw some stuff
            poseStack.blit(texture, x, y, background * 9, group, 9, 9, 126, 45);
            if (idx < level) {
                poseStack.blit(texture, x, y, icon + 36, group, 9, 9, 126, 45);
            } else if (idx == level) {
                poseStack.blit(texture, x, y, icon + 45, group, 9, 9, 126, 45);
            }

            texture = GUI_SATURATION_ICONS_LOCATION;
            //apple skin loaded draw saturationn
            if (ModList.get().isLoaded("appleskin") && AppleskinCompat.SHOW_SATURATION_OVERLAY) {
                float effectiveSaturationOfBar = (modifiedSaturation / 2.0F) - i;

                int v = group;
                int u;

                if (effectiveSaturationOfBar >= 1)
                    u = 4 * 9;
                else if (effectiveSaturationOfBar > .75)
                    u = 3 * 9;
                else if (effectiveSaturationOfBar > .5)
                    u = 2 * 9;
                else if (effectiveSaturationOfBar > .25)
                    u = 9;
                else
                    u = 0;

                poseStack.blit(texture, x, y, u, v, 9, 9, 126, 45);
            }

            if (idx <= level) {
                int tick = ticks % 20;
                if (playerManager.getValue(MindfulEating.SHEEN_COOLDOWN) > 0 && ((tick < idx + level / 4 && tick > idx - level / 4)
                        || (tick == 49 && i == 0))) {
                    texture = GUI_NOURISHMENT_ICONS_LOCATION;
                    int uOffset = idx == level ? 18 : 9;
                    poseStack.blit(texture, x, y, uOffset, group, 9, 9, 126, 45);
                }
            }
            texture = GUI_HUNGER_ICONS_LOCATION;
        }
    }
}
