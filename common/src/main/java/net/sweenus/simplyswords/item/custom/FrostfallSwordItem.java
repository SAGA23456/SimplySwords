package net.sweenus.simplyswords.item.custom;


import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.sweenus.simplyswords.config.SimplySwordsConfig;
import net.sweenus.simplyswords.registry.EffectRegistry;
import net.sweenus.simplyswords.registry.SoundRegistry;
import net.sweenus.simplyswords.util.HelperMethods;

import java.util.List;

public class FrostfallSwordItem extends SwordItem {
    public FrostfallSwordItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    private final int abilityCooldown = (int) SimplySwordsConfig.getFloatValue("soultether_duration");
    int radius = (int) (SimplySwordsConfig.getFloatValue("soultether_radius"));
    int proc_chance = 50;
    int shatter_timer_max = 80; //(int) (SimplySwordsConfig.getFloatValue("soultether_radius"));
    int shatter_timer;
    int player_shatter_timer;
    double lastX;
    double lastY;
    double lastZ;



    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.world.isClient()) {
            ServerWorld world = (ServerWorld) attacker.world;
            boolean impactsounds_enabled = (SimplySwordsConfig.getBooleanValue("enable_weapon_impact_sounds"));

            if (impactsounds_enabled) {
                int choose_sound = (int) (Math.random() * 30);
                float choose_pitch = (float) Math.random() * 2;
                if (choose_sound <= 10)
                    world.playSoundFromEntity(null, target, SoundRegistry.MAGIC_SWORD_ATTACK_WITH_BLOOD_01.get(), SoundCategory.PLAYERS, 0.5f, 1.1f + choose_pitch);
                if (choose_sound <= 20 && choose_sound > 10)
                    world.playSoundFromEntity(null, target, SoundRegistry.MAGIC_SWORD_ATTACK_WITH_BLOOD_02.get(), SoundCategory.PLAYERS, 0.5f, 1.1f + choose_pitch);
                if (choose_sound <= 30 && choose_sound > 20)
                    world.playSoundFromEntity(null, target, SoundRegistry.MAGIC_SWORD_ATTACK_WITH_BLOOD_03.get(), SoundCategory.PLAYERS, 0.5f, 1.1f + choose_pitch);
                if (choose_sound <= 40 && choose_sound > 30)
                    world.playSoundFromEntity(null, target, SoundRegistry.MAGIC_SWORD_ATTACK_WITH_BLOOD_04.get(), SoundCategory.PLAYERS, 0.5f, 1.1f + choose_pitch);
            }


            //AOE freeze
            if (attacker.getRandom().nextInt(100) <= proc_chance) {
                Box box = new Box(target.getX() + radius, target.getY() + radius, target.getZ() + radius, target.getX() - radius, target.getY() - radius, target.getZ() - radius);
                for (Entity entities : world.getOtherEntities(attacker, box, EntityPredicates.VALID_LIVING_ENTITY)) {

                    if (entities != null) {
                        if (entities instanceof LivingEntity le) {
                            le.addStatusEffect(new StatusEffectInstance(EffectRegistry.FREEZE.get(), 90, 0), attacker);
                            le.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 80, 4), attacker);
                            world.playSoundFromEntity(null, le, SoundRegistry.ELEMENTAL_BOW_ICE_SHOOT_IMPACT_01.get(), SoundCategory.PLAYERS, 0.1f, 3f);
                            BlockPos pos = new BlockPos(le.getX(), le.getY(), le.getZ());
                            BlockPos pos2 = new BlockPos(le.getX(), le.getY() + 1, le.getZ());
                            BlockState state = Blocks.ICE.getDefaultState();
                            world.setBlockState(pos, state);
                            world.setBlockState(pos2, state);
                        }
                    }
                }
                shatter_timer = shatter_timer_max;
            }


        }

            return super.postHit(stack, target, attacker);
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {


        if (!user.world.isClient()) {

            lastX = user.getX();
            lastY = user.getY();
            lastZ = user.getZ();

            user.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 80, 4), user);
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 80, 4), user);
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 80, 4), user);
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 80, 2), user);

            world.playSoundFromEntity(null, user, SoundRegistry.ELEMENTAL_BOW_ICE_SHOOT_IMPACT_03.get(), SoundCategory.PLAYERS, 0.6f, 2f);

            double xpos = user.getX() -2;
            double ypos = user.getY();
            double zpos = user.getZ() -2;
            BlockPos playerPos = new BlockPos(lastX, lastY, lastZ);
            user.teleport(lastX, lastY, lastZ);

            for (int i = 3; i > 0; i--) {
                for (int j = 3; j > 0; j--) {
                    BlockPos poscheck = new BlockPos(xpos + i, ypos, zpos + j);
                    BlockPos poscheck2 = new BlockPos(xpos + i, ypos + 1, zpos + j);
                    BlockPos poscheck3 = new BlockPos(xpos + i, ypos + 2, zpos + j);
                    BlockPos poscheck4 = new BlockPos(xpos + i, ypos - 1, zpos + j);

                    BlockState currentState = world.getBlockState(poscheck);
                    BlockState currentState2 = world.getBlockState(poscheck2);
                    BlockState currentState3 = world.getBlockState(poscheck3);
                    BlockState currentState4 = world.getBlockState(poscheck4);
                    BlockState state = Blocks.ICE.getDefaultState();
                    if (i + j != 4) {
                        if (currentState == Blocks.AIR.getDefaultState() || currentState == Blocks.GRASS.getDefaultState() || currentState == Blocks.FERN.getDefaultState())
                            world.setBlockState(poscheck, state);
                        if (currentState2 == Blocks.AIR.getDefaultState() || currentState == Blocks.GRASS.getDefaultState() || currentState == Blocks.FERN.getDefaultState())
                            world.setBlockState(poscheck2, state);
                    }
                    if (currentState3 == Blocks.AIR.getDefaultState() || currentState == Blocks.GRASS.getDefaultState() || currentState == Blocks.FERN.getDefaultState())
                        world.setBlockState(poscheck3, state);
                    if (currentState4 == Blocks.AIR.getDefaultState() || currentState == Blocks.GRASS.getDefaultState() || currentState == Blocks.FERN.getDefaultState())
                        world.setBlockState(poscheck4, state);
                }
            }

            user.teleport(lastX, lastY, lastZ);
            player_shatter_timer = shatter_timer_max;
            user.getItemCooldownManager().set(this, abilityCooldown);


            /*
            BlockPos pos = new BlockPos(user.getX(), user.getY(), user.getZ());
            BlockPos pos2 = new BlockPos(user.getX(), user.getY() + 1, user.getZ());
            BlockState state = Blocks.ICE.getDefaultState();
            BlockState currentState = world.getBlockState(pos);
            if (currentState == Blocks.AIR.getDefaultState()) {
                world.setBlockState(pos, state);
                world.setBlockState(pos2, state);
                player_shatter_timer = shatter_timer_max;

                user.getItemCooldownManager().set(this, abilityCooldown);
            } */
        }
        return super.use(world, user, hand);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && (entity instanceof PlayerEntity player)) {
                if (shatter_timer > 0)
                    shatter_timer --;
            if (player_shatter_timer > 0) {
                player_shatter_timer--;
            }
            if (shatter_timer == 1) {
                Box box = new Box(player.getX() + radius + 10, player.getY() + radius  + 10, player.getZ() + radius  + 10, player.getX() - radius  - 10, player.getY() - radius  - 10, player.getZ() - radius  - 10);
                for (Entity entities : world.getOtherEntities(player, box, EntityPredicates.VALID_LIVING_ENTITY)) {

                        //Ice shatter
                        if (entities != null) {
                            if (entities instanceof LivingEntity le) {
                                double xpos = le.getX() -3;
                                double ypos = le.getY();
                                double zpos = le.getZ() -3;


                                for (int i = 6; i > 0; i--) {
                                    for (int j = 6; j > 0; j--) {
                                        BlockPos poscheck = new BlockPos(xpos+i, ypos, zpos+j);
                                        BlockPos poscheck2 = new BlockPos(xpos+i, ypos + 1, zpos+j);

                                        BlockState currentState = world.getBlockState(poscheck);
                                        if (currentState == Blocks.ICE.getDefaultState()) {
                                            BlockState state = Blocks.AIR.getDefaultState();
                                            world.setBlockState(poscheck, state);
                                            world.setBlockState(poscheck2, state);
                                            world.playSoundFromEntity(null, le, SoundRegistry.ELEMENTAL_BOW_ICE_SHOOT_IMPACT_02.get(), SoundCategory.PLAYERS, 0.3f, 3f);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            //Ice shatter - player edition
            if (player_shatter_timer == 75) //failsafe
                player.teleport(lastX, lastY, lastZ);

            if (player_shatter_timer == 1) {
                world.playSoundFromEntity(null, player, SoundRegistry.ELEMENTAL_BOW_ICE_SHOOT_IMPACT_02.get(), SoundCategory.PLAYERS, 0.6f, 3f);
                double xpos = lastX -4;
                double ypos = lastY;
                double zpos = lastZ -4;


                for (int i = 8; i > 0; i--) {
                    for (int j = 8; j > 0; j--) {
                        BlockPos poscheck = new BlockPos(xpos+i, ypos, zpos+j);
                        BlockPos poscheck2 = new BlockPos(xpos+i, ypos + 1, zpos+j);
                        BlockPos poscheck3 = new BlockPos(xpos+i, ypos + 2, zpos+j);
                        BlockPos poscheck4 = new BlockPos(xpos+i, ypos - 1, zpos+j);

                        BlockState currentState = world.getBlockState(poscheck);
                        if (currentState == Blocks.ICE.getDefaultState()) {
                            BlockState state = Blocks.AIR.getDefaultState();
                            world.setBlockState(poscheck, state);
                            world.setBlockState(poscheck2, state);
                            world.setBlockState(poscheck3, state);
                            world.setBlockState(poscheck4, state);
                        }
                    }
                }
            }
        }
    }

    @Override
    public Text getName(ItemStack stack) {
        return Text.translatable(this.getTranslationKey(stack)).formatted(Formatting.GOLD, Formatting.BOLD, Formatting.UNDERLINE);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {

        //1.19

        tooltip.add(Text.literal(""));
        tooltip.add(Text.translatable("item.simplyswords.soulpyresworditem.tooltip1").formatted(Formatting.GOLD, Formatting.BOLD));
        tooltip.add(Text.literal(""));
        tooltip.add(Text.translatable("item.simplyswords.onrightclick").formatted(Formatting.BOLD, Formatting.GREEN));
        tooltip.add(Text.translatable("item.simplyswords.soulpyresworditem.tooltip2"));
        tooltip.add(Text.translatable("item.simplyswords.soulpyresworditem.tooltip3"));
        tooltip.add(Text.translatable("item.simplyswords.soulpyresworditem.tooltip4"));
        tooltip.add(Text.translatable("item.simplyswords.soulpyresworditem.tooltip5"));
        tooltip.add(Text.translatable("item.simplyswords.soulpyresworditem.tooltip6"));
        tooltip.add(Text.literal(""));
        tooltip.add(Text.translatable("item.simplyswords.soulpyresworditem.tooltip7", abilityCooldown / 20));
        tooltip.add(Text.translatable("item.simplyswords.soulpyresworditem.tooltip8"));
        tooltip.add(Text.translatable("item.simplyswords.soulpyresworditem.tooltip9"));
        tooltip.add(Text.literal(""));

        /*
        //1.18.2
        tooltip.add(new LiteralText(""));
        tooltip.add(new TranslatableText("item.simplyswords.stealsworditem.tooltip1").formatted(Formatting.GOLD, Formatting.BOLD));
        tooltip.add(new TranslatableText("item.simplyswords.stealsworditem.tooltip2"));
        tooltip.add(new TranslatableText("item.simplyswords.stealsworditem.tooltip3"));
        tooltip.add(new TranslatableText("item.simplyswords.stealsworditem.tooltip4"));
        tooltip.add(new LiteralText(""));
        tooltip.add(new TranslatableText("item.simplyswords.onrightclick").formatted(Formatting.BOLD, Formatting.GREEN));
        tooltip.add(new TranslatableText("item.simplyswords.stealsworditem.tooltip5"));
        tooltip.add(new TranslatableText("item.simplyswords.stealsworditem.tooltip6"));
        tooltip.add(new TranslatableText("item.simplyswords.stealsworditem.tooltip7"));
        tooltip.add(new LiteralText(""));
        tooltip.add(new TranslatableText("item.simplyswords.stealsworditem.tooltip8"));
        tooltip.add(new TranslatableText("item.simplyswords.stealsworditem.tooltip9"));
        tooltip.add(new TranslatableText("item.simplyswords.stealsworditem.tooltip10"));

 */
    }

}
