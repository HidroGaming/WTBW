package com.wtbw;

import com.wtbw.block.IronFurnaceBlock;
import com.wtbw.block.ModBlocks;
import com.wtbw.block.redstone.RedstoneEmitterBlock;
import com.wtbw.block.redstone.RedstoneTimerBlock;
import com.wtbw.config.WTBWConfig;
import com.wtbw.gui.container.IronFurnaceContainer;
import com.wtbw.item.tools.HammerItem;
import com.wtbw.tile.furnace.BaseFurnaceTileEntity;
import com.wtbw.tile.furnace.FurnaceTier;
import com.wtbw.tile.redstone.RedstoneTimerTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/*
  @author: Naxanria
*/
public class Registrator
{
  private static List<BlockItem> blockItems = new ArrayList<>();
  private static IForgeRegistry<Block> blockRegistry;
  private static IForgeRegistry<Item> itemRegistry;
  private static IForgeRegistry<TileEntityType<?>> tileRegistry;
  
  private static void registerAllBlocks()
  {
    // register blocks here
    register(new Block(getBlockProperties(Material.ROCK).hardnessAndResistance(5, 6)), "charcoal_block", false);
    
    register(new IronFurnaceBlock(getBlockProperties(Material.IRON).hardnessAndResistance(7)), "iron_furnace");
    register(new RedstoneTimerBlock(getBlockProperties(Material.IRON).hardnessAndResistance(4)), "redstone_timer");
    register(new RedstoneEmitterBlock(getBlockProperties(Material.IRON).hardnessAndResistance(4)), "redstone_emitter");
  }
  
  private static void registerAllItems()
  {
    // register items here
    register(new Item(getItemProperties()){
      @Override
      public int getBurnTime(ItemStack itemStack)
      {
        return 200;
      }
    }, "tiny_coal");
  
    register(new Item(getItemProperties()){
      @Override
      public int getBurnTime(ItemStack itemStack)
      {
        return 200;
      }
    }, "tiny_charcoal");
    
    register(new BlockItem(ModBlocks.CHARCOAL_BLOCK, getItemProperties()){
      @Override
      public int getBurnTime(ItemStack itemStack)
      {
        return 14400;
      }
    }, "charcoal_block");
    
    // durability multiplier
    int dMul = WTBWConfig.common.toolsDurabilityMultiplier.get();
    register(new HammerItem(ItemTier.STONE, 6, -3.6f, getItemProperties().maxDamage(Items.STONE_PICKAXE.getMaxDamage(null) * dMul)), "stone_hammer");
    register(new HammerItem(ItemTier.IRON, 8, -3.6f, getItemProperties().maxDamage(Items.IRON_PICKAXE.getMaxDamage(null) * dMul)), "iron_hammer");
    register(new HammerItem(ItemTier.GOLD, 6, -3.2f, getItemProperties().maxDamage(Items.GOLDEN_PICKAXE.getMaxDamage(null) * dMul)), "gold_hammer");
    register(new HammerItem(ItemTier.DIAMOND, 11, -3.6f, getItemProperties().maxDamage(Items.DIAMOND_PICKAXE.getMaxDamage(null) * dMul)), "diamond_hammer");
  }
  
  private static void registerAllTiles()
  {
    register(() -> new BaseFurnaceTileEntity(FurnaceTier.IRON, IRecipeType.SMELTING), ModBlocks.IRON_FURNACE, "iron_furnace");
    register(RedstoneTimerTileEntity::new, ModBlocks.REDSTONE_TIMER, "redstone_timer");
  }
  
  private static Item.Properties getItemProperties()
  {
    return new Item.Properties().group(WTBW.GROUP);
  }
  
  private static Block.Properties getBlockProperties(Material material)
  {
    return Block.Properties.create(material);
  }
  
  
  public static void registerBlocks(RegistryEvent.Register<Block> event)
  {
    blockRegistry = event.getRegistry();
    
    registerAllBlocks();
  }
  
  public static void registerItems(RegistryEvent.Register<Item> event)
  {
    itemRegistry = event.getRegistry();
    for (BlockItem blockItem : blockItems)
    {
      itemRegistry.register(blockItem);
    }
  
    blockItems.clear();
    
    registerAllItems();
  }
  
  public static void registerTileEntity(RegistryEvent.Register<TileEntityType<?>> event)
  {
    tileRegistry = event.getRegistry();
    registerAllTiles();
  }
  
  public static void registerContainer(RegistryEvent.Register<ContainerType<?>> event)
  {
    IForgeRegistry<ContainerType<?>> registry = event.getRegistry();
    
    registry.register(IForgeContainerType.create((windowId, inv, data) ->
      {
        BlockPos pos = data.readBlockPos();
        return new IronFurnaceContainer(windowId, Minecraft.getInstance().world, pos, inv);
      }
    ).setRegistryName(WTBW.MODID, "iron_furnace"));
  }
  
  private static <T extends Block> T register(T block, String registryName)
  {
    return register(block, registryName, true, null);
  }
  
  private static <T extends Block> T register(T block, String registryName, boolean createBlockItem)
  {
    return register(block, registryName, createBlockItem, null);
  }
  
  private static <T extends Block> T register(T block, String registryName, boolean createBlockItem, Item.Properties blockItemProperties)
  {
    block.setRegistryName(WTBW.MODID, registryName);
    if (createBlockItem)
    {
      if (blockItemProperties == null)
      {
        blockItemProperties = getItemProperties();
      }
      blockItems.add((BlockItem) new BlockItem(block, blockItemProperties).setRegistryName(WTBW.MODID, registryName));
    }
    
    blockRegistry.register(block);
    
    return block;
  }
  
  private static <T extends Item> T register(T item, String name)
  {
    itemRegistry.register(item.setRegistryName(WTBW.MODID, name));
    return item;
  }
  
  private static TileEntityType<?> register(Supplier<TileEntity> factory, Block block, String registryName)
  {
    TileEntityType<TileEntity> type = TileEntityType.Builder.create(factory, block).build(null);
    type.setRegistryName(WTBW.MODID, registryName);
    tileRegistry.register(type);
    return type;
  }
  
}
