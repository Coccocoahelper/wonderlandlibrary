// 
// Decompiled by Procyon v0.5.36
// 

package net.minecraft.world.gen.structure;

import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockDoor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.BlockTorch;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.nbt.NBTTagCompound;
import javax.annotation.Nullable;
import net.minecraft.util.EnumFacing;
import java.util.Random;
import java.util.Iterator;
import com.google.common.collect.Lists;
import java.util.List;

public class StructureStrongholdPieces
{
    private static final PieceWeight[] PIECE_WEIGHTS;
    private static List<PieceWeight> structurePieceList;
    private static Class<? extends Stronghold> strongComponentType;
    static int totalWeight;
    private static final Stones STRONGHOLD_STONES;
    
    public static void registerStrongholdPieces() {
        MapGenStructureIO.registerStructureComponent(ChestCorridor.class, "SHCC");
        MapGenStructureIO.registerStructureComponent(Corridor.class, "SHFC");
        MapGenStructureIO.registerStructureComponent(Crossing.class, "SH5C");
        MapGenStructureIO.registerStructureComponent(LeftTurn.class, "SHLT");
        MapGenStructureIO.registerStructureComponent(Library.class, "SHLi");
        MapGenStructureIO.registerStructureComponent(PortalRoom.class, "SHPR");
        MapGenStructureIO.registerStructureComponent(Prison.class, "SHPH");
        MapGenStructureIO.registerStructureComponent(RightTurn.class, "SHRT");
        MapGenStructureIO.registerStructureComponent(RoomCrossing.class, "SHRC");
        MapGenStructureIO.registerStructureComponent(Stairs.class, "SHSD");
        MapGenStructureIO.registerStructureComponent(Stairs2.class, "SHStart");
        MapGenStructureIO.registerStructureComponent(Straight.class, "SHS");
        MapGenStructureIO.registerStructureComponent(StairsStraight.class, "SHSSD");
    }
    
    public static void prepareStructurePieces() {
        StructureStrongholdPieces.structurePieceList = (List<PieceWeight>)Lists.newArrayList();
        for (final PieceWeight structurestrongholdpieces$pieceweight : StructureStrongholdPieces.PIECE_WEIGHTS) {
            structurestrongholdpieces$pieceweight.instancesSpawned = 0;
            StructureStrongholdPieces.structurePieceList.add(structurestrongholdpieces$pieceweight);
        }
        StructureStrongholdPieces.strongComponentType = null;
    }
    
    private static boolean canAddStructurePieces() {
        boolean flag = false;
        StructureStrongholdPieces.totalWeight = 0;
        for (final PieceWeight structurestrongholdpieces$pieceweight : StructureStrongholdPieces.structurePieceList) {
            if (structurestrongholdpieces$pieceweight.instancesLimit > 0 && structurestrongholdpieces$pieceweight.instancesSpawned < structurestrongholdpieces$pieceweight.instancesLimit) {
                flag = true;
            }
            StructureStrongholdPieces.totalWeight += structurestrongholdpieces$pieceweight.pieceWeight;
        }
        return flag;
    }
    
    private static Stronghold findAndCreatePieceFactory(final Class<? extends Stronghold> clazz, final List<StructureComponent> p_175954_1_, final Random p_175954_2_, final int p_175954_3_, final int p_175954_4_, final int p_175954_5_, @Nullable final EnumFacing p_175954_6_, final int p_175954_7_) {
        Stronghold structurestrongholdpieces$stronghold = null;
        if (clazz == Straight.class) {
            structurestrongholdpieces$stronghold = Straight.createPiece(p_175954_1_, p_175954_2_, p_175954_3_, p_175954_4_, p_175954_5_, p_175954_6_, p_175954_7_);
        }
        else if (clazz == Prison.class) {
            structurestrongholdpieces$stronghold = Prison.createPiece(p_175954_1_, p_175954_2_, p_175954_3_, p_175954_4_, p_175954_5_, p_175954_6_, p_175954_7_);
        }
        else if (clazz == LeftTurn.class) {
            structurestrongholdpieces$stronghold = LeftTurn.createPiece(p_175954_1_, p_175954_2_, p_175954_3_, p_175954_4_, p_175954_5_, p_175954_6_, p_175954_7_);
        }
        else if (clazz == RightTurn.class) {
            structurestrongholdpieces$stronghold = LeftTurn.createPiece(p_175954_1_, p_175954_2_, p_175954_3_, p_175954_4_, p_175954_5_, p_175954_6_, p_175954_7_);
        }
        else if (clazz == RoomCrossing.class) {
            structurestrongholdpieces$stronghold = RoomCrossing.createPiece(p_175954_1_, p_175954_2_, p_175954_3_, p_175954_4_, p_175954_5_, p_175954_6_, p_175954_7_);
        }
        else if (clazz == StairsStraight.class) {
            structurestrongholdpieces$stronghold = StairsStraight.createPiece(p_175954_1_, p_175954_2_, p_175954_3_, p_175954_4_, p_175954_5_, p_175954_6_, p_175954_7_);
        }
        else if (clazz == Stairs.class) {
            structurestrongholdpieces$stronghold = Stairs.createPiece(p_175954_1_, p_175954_2_, p_175954_3_, p_175954_4_, p_175954_5_, p_175954_6_, p_175954_7_);
        }
        else if (clazz == Crossing.class) {
            structurestrongholdpieces$stronghold = Crossing.createPiece(p_175954_1_, p_175954_2_, p_175954_3_, p_175954_4_, p_175954_5_, p_175954_6_, p_175954_7_);
        }
        else if (clazz == ChestCorridor.class) {
            structurestrongholdpieces$stronghold = ChestCorridor.createPiece(p_175954_1_, p_175954_2_, p_175954_3_, p_175954_4_, p_175954_5_, p_175954_6_, p_175954_7_);
        }
        else if (clazz == Library.class) {
            structurestrongholdpieces$stronghold = Library.createPiece(p_175954_1_, p_175954_2_, p_175954_3_, p_175954_4_, p_175954_5_, p_175954_6_, p_175954_7_);
        }
        else if (clazz == PortalRoom.class) {
            structurestrongholdpieces$stronghold = PortalRoom.createPiece(p_175954_1_, p_175954_2_, p_175954_3_, p_175954_4_, p_175954_5_, p_175954_6_, p_175954_7_);
        }
        return structurestrongholdpieces$stronghold;
    }
    
    private static Stronghold generatePieceFromSmallDoor(final Stairs2 p_175955_0_, final List<StructureComponent> p_175955_1_, final Random p_175955_2_, final int p_175955_3_, final int p_175955_4_, final int p_175955_5_, final EnumFacing p_175955_6_, final int p_175955_7_) {
        if (!canAddStructurePieces()) {
            return null;
        }
        if (StructureStrongholdPieces.strongComponentType != null) {
            final Stronghold structurestrongholdpieces$stronghold = findAndCreatePieceFactory(StructureStrongholdPieces.strongComponentType, p_175955_1_, p_175955_2_, p_175955_3_, p_175955_4_, p_175955_5_, p_175955_6_, p_175955_7_);
            StructureStrongholdPieces.strongComponentType = null;
            if (structurestrongholdpieces$stronghold != null) {
                return structurestrongholdpieces$stronghold;
            }
        }
        int j = 0;
        while (j < 5) {
            ++j;
            int i = p_175955_2_.nextInt(StructureStrongholdPieces.totalWeight);
            for (final PieceWeight structurestrongholdpieces$pieceweight : StructureStrongholdPieces.structurePieceList) {
                i -= structurestrongholdpieces$pieceweight.pieceWeight;
                if (i < 0) {
                    if (!structurestrongholdpieces$pieceweight.canSpawnMoreStructuresOfType(p_175955_7_)) {
                        break;
                    }
                    if (structurestrongholdpieces$pieceweight == p_175955_0_.lastPlaced) {
                        break;
                    }
                    final Stronghold structurestrongholdpieces$stronghold2 = findAndCreatePieceFactory(structurestrongholdpieces$pieceweight.pieceClass, p_175955_1_, p_175955_2_, p_175955_3_, p_175955_4_, p_175955_5_, p_175955_6_, p_175955_7_);
                    if (structurestrongholdpieces$stronghold2 != null) {
                        final PieceWeight pieceWeight = structurestrongholdpieces$pieceweight;
                        ++pieceWeight.instancesSpawned;
                        p_175955_0_.lastPlaced = structurestrongholdpieces$pieceweight;
                        if (!structurestrongholdpieces$pieceweight.canSpawnMoreStructures()) {
                            StructureStrongholdPieces.structurePieceList.remove(structurestrongholdpieces$pieceweight);
                        }
                        return structurestrongholdpieces$stronghold2;
                    }
                    continue;
                }
            }
        }
        final StructureBoundingBox structureboundingbox = Corridor.findPieceBox(p_175955_1_, p_175955_2_, p_175955_3_, p_175955_4_, p_175955_5_, p_175955_6_);
        if (structureboundingbox != null && structureboundingbox.minY > 1) {
            return new Corridor(p_175955_7_, p_175955_2_, structureboundingbox, p_175955_6_);
        }
        return null;
    }
    
    private static StructureComponent generateAndAddPiece(final Stairs2 p_175953_0_, final List<StructureComponent> p_175953_1_, final Random p_175953_2_, final int p_175953_3_, final int p_175953_4_, final int p_175953_5_, @Nullable final EnumFacing p_175953_6_, final int p_175953_7_) {
        if (p_175953_7_ > 50) {
            return null;
        }
        if (Math.abs(p_175953_3_ - p_175953_0_.getBoundingBox().minX) <= 112 && Math.abs(p_175953_5_ - p_175953_0_.getBoundingBox().minZ) <= 112) {
            final StructureComponent structurecomponent = generatePieceFromSmallDoor(p_175953_0_, p_175953_1_, p_175953_2_, p_175953_3_, p_175953_4_, p_175953_5_, p_175953_6_, p_175953_7_ + 1);
            if (structurecomponent != null) {
                p_175953_1_.add(structurecomponent);
                p_175953_0_.pendingChildren.add(structurecomponent);
            }
            return structurecomponent;
        }
        return null;
    }
    
    static {
        PIECE_WEIGHTS = new PieceWeight[] { new PieceWeight(Straight.class, 40, 0), new PieceWeight(Prison.class, 5, 5), new PieceWeight(LeftTurn.class, 20, 0), new PieceWeight(RightTurn.class, 20, 0), new PieceWeight(RoomCrossing.class, 10, 6), new PieceWeight(StairsStraight.class, 5, 5), new PieceWeight(Stairs.class, 5, 5), new PieceWeight(Crossing.class, 5, 4), new PieceWeight(ChestCorridor.class, 5, 4), new PieceWeight(Library.class, 10, 2) {
                @Override
                public boolean canSpawnMoreStructuresOfType(final int p_75189_1_) {
                    return super.canSpawnMoreStructuresOfType(p_75189_1_) && p_75189_1_ > 4;
                }
            }, new PieceWeight(PortalRoom.class, 20, 1) {
                @Override
                public boolean canSpawnMoreStructuresOfType(final int p_75189_1_) {
                    return super.canSpawnMoreStructuresOfType(p_75189_1_) && p_75189_1_ > 5;
                }
            } };
        STRONGHOLD_STONES = new Stones();
    }
    
    public static class ChestCorridor extends Stronghold
    {
        private boolean hasMadeChest;
        
        public ChestCorridor() {
        }
        
        public ChestCorridor(final int p_i45582_1_, final Random p_i45582_2_, final StructureBoundingBox p_i45582_3_, final EnumFacing p_i45582_4_) {
            super(p_i45582_1_);
            this.setCoordBaseMode(p_i45582_4_);
            this.entryDoor = this.getRandomDoor(p_i45582_2_);
            this.boundingBox = p_i45582_3_;
        }
        
        @Override
        protected void writeStructureToNBT(final NBTTagCompound tagCompound) {
            super.writeStructureToNBT(tagCompound);
            tagCompound.setBoolean("Chest", this.hasMadeChest);
        }
        
        @Override
        protected void readStructureFromNBT(final NBTTagCompound tagCompound, final TemplateManager p_143011_2_) {
            super.readStructureFromNBT(tagCompound, p_143011_2_);
            this.hasMadeChest = tagCompound.getBoolean("Chest");
        }
        
        @Override
        public void buildComponent(final StructureComponent componentIn, final List<StructureComponent> listIn, final Random rand) {
            this.getNextComponentNormal((Stairs2)componentIn, listIn, rand, 1, 1);
        }
        
        public static ChestCorridor createPiece(final List<StructureComponent> p_175868_0_, final Random p_175868_1_, final int p_175868_2_, final int p_175868_3_, final int p_175868_4_, final EnumFacing p_175868_5_, final int p_175868_6_) {
            final StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(p_175868_2_, p_175868_3_, p_175868_4_, -1, -1, 0, 5, 5, 7, p_175868_5_);
            return (Stronghold.canStrongholdGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(p_175868_0_, structureboundingbox) == null) ? new ChestCorridor(p_175868_6_, p_175868_1_, structureboundingbox, p_175868_5_) : null;
        }
        
        @Override
        public boolean addComponentParts(final World worldIn, final Random randomIn, final StructureBoundingBox structureBoundingBoxIn) {
            if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
                return false;
            }
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 4, 6, true, randomIn, StructureStrongholdPieces.STRONGHOLD_STONES);
            this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.entryDoor, 1, 1, 0);
            this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, Door.OPENING, 1, 1, 6);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 2, 3, 1, 4, Blocks.STONEBRICK.getDefaultState(), Blocks.STONEBRICK.getDefaultState(), false);
            this.setBlockState(worldIn, Blocks.STONE_SLAB.getStateFromMeta(BlockStoneSlab.EnumType.SMOOTHBRICK.getMetadata()), 3, 1, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STONE_SLAB.getStateFromMeta(BlockStoneSlab.EnumType.SMOOTHBRICK.getMetadata()), 3, 1, 5, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STONE_SLAB.getStateFromMeta(BlockStoneSlab.EnumType.SMOOTHBRICK.getMetadata()), 3, 2, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STONE_SLAB.getStateFromMeta(BlockStoneSlab.EnumType.SMOOTHBRICK.getMetadata()), 3, 2, 4, structureBoundingBoxIn);
            for (int i = 2; i <= 4; ++i) {
                this.setBlockState(worldIn, Blocks.STONE_SLAB.getStateFromMeta(BlockStoneSlab.EnumType.SMOOTHBRICK.getMetadata()), 2, 1, i, structureBoundingBoxIn);
            }
            if (!this.hasMadeChest && structureBoundingBoxIn.isVecInside(new BlockPos(this.getXWithOffset(3, 3), this.getYWithOffset(2), this.getZWithOffset(3, 3)))) {
                this.hasMadeChest = true;
                this.generateChest(worldIn, structureBoundingBoxIn, randomIn, 3, 2, 3, LootTableList.CHESTS_STRONGHOLD_CORRIDOR);
            }
            return true;
        }
    }
    
    public static class Corridor extends Stronghold
    {
        private int steps;
        
        public Corridor() {
        }
        
        public Corridor(final int p_i45581_1_, final Random p_i45581_2_, final StructureBoundingBox p_i45581_3_, final EnumFacing p_i45581_4_) {
            super(p_i45581_1_);
            this.setCoordBaseMode(p_i45581_4_);
            this.boundingBox = p_i45581_3_;
            this.steps = ((p_i45581_4_ != EnumFacing.NORTH && p_i45581_4_ != EnumFacing.SOUTH) ? p_i45581_3_.getXSize() : p_i45581_3_.getZSize());
        }
        
        @Override
        protected void writeStructureToNBT(final NBTTagCompound tagCompound) {
            super.writeStructureToNBT(tagCompound);
            tagCompound.setInteger("Steps", this.steps);
        }
        
        @Override
        protected void readStructureFromNBT(final NBTTagCompound tagCompound, final TemplateManager p_143011_2_) {
            super.readStructureFromNBT(tagCompound, p_143011_2_);
            this.steps = tagCompound.getInteger("Steps");
        }
        
        public static StructureBoundingBox findPieceBox(final List<StructureComponent> p_175869_0_, final Random p_175869_1_, final int p_175869_2_, final int p_175869_3_, final int p_175869_4_, final EnumFacing p_175869_5_) {
            final int i = 3;
            StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(p_175869_2_, p_175869_3_, p_175869_4_, -1, -1, 0, 5, 5, 4, p_175869_5_);
            final StructureComponent structurecomponent = StructureComponent.findIntersecting(p_175869_0_, structureboundingbox);
            if (structurecomponent == null) {
                return null;
            }
            if (structurecomponent.getBoundingBox().minY == structureboundingbox.minY) {
                for (int j = 3; j >= 1; --j) {
                    structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(p_175869_2_, p_175869_3_, p_175869_4_, -1, -1, 0, 5, 5, j - 1, p_175869_5_);
                    if (!structurecomponent.getBoundingBox().intersectsWith(structureboundingbox)) {
                        return StructureBoundingBox.getComponentToAddBoundingBox(p_175869_2_, p_175869_3_, p_175869_4_, -1, -1, 0, 5, 5, j, p_175869_5_);
                    }
                }
            }
            return null;
        }
        
        @Override
        public boolean addComponentParts(final World worldIn, final Random randomIn, final StructureBoundingBox structureBoundingBoxIn) {
            if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
                return false;
            }
            for (int i = 0; i < this.steps; ++i) {
                this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), 0, 0, i, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), 1, 0, i, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), 2, 0, i, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), 3, 0, i, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), 4, 0, i, structureBoundingBoxIn);
                for (int j = 1; j <= 3; ++j) {
                    this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), 0, j, i, structureBoundingBoxIn);
                    this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 1, j, i, structureBoundingBoxIn);
                    this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 2, j, i, structureBoundingBoxIn);
                    this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 3, j, i, structureBoundingBoxIn);
                    this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), 4, j, i, structureBoundingBoxIn);
                }
                this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), 0, 4, i, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), 1, 4, i, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), 2, 4, i, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), 3, 4, i, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), 4, 4, i, structureBoundingBoxIn);
            }
            return true;
        }
    }
    
    public static class Crossing extends Stronghold
    {
        private boolean leftLow;
        private boolean leftHigh;
        private boolean rightLow;
        private boolean rightHigh;
        
        public Crossing() {
        }
        
        public Crossing(final int p_i45580_1_, final Random p_i45580_2_, final StructureBoundingBox p_i45580_3_, final EnumFacing p_i45580_4_) {
            super(p_i45580_1_);
            this.setCoordBaseMode(p_i45580_4_);
            this.entryDoor = this.getRandomDoor(p_i45580_2_);
            this.boundingBox = p_i45580_3_;
            this.leftLow = p_i45580_2_.nextBoolean();
            this.leftHigh = p_i45580_2_.nextBoolean();
            this.rightLow = p_i45580_2_.nextBoolean();
            this.rightHigh = (p_i45580_2_.nextInt(3) > 0);
        }
        
        @Override
        protected void writeStructureToNBT(final NBTTagCompound tagCompound) {
            super.writeStructureToNBT(tagCompound);
            tagCompound.setBoolean("leftLow", this.leftLow);
            tagCompound.setBoolean("leftHigh", this.leftHigh);
            tagCompound.setBoolean("rightLow", this.rightLow);
            tagCompound.setBoolean("rightHigh", this.rightHigh);
        }
        
        @Override
        protected void readStructureFromNBT(final NBTTagCompound tagCompound, final TemplateManager p_143011_2_) {
            super.readStructureFromNBT(tagCompound, p_143011_2_);
            this.leftLow = tagCompound.getBoolean("leftLow");
            this.leftHigh = tagCompound.getBoolean("leftHigh");
            this.rightLow = tagCompound.getBoolean("rightLow");
            this.rightHigh = tagCompound.getBoolean("rightHigh");
        }
        
        @Override
        public void buildComponent(final StructureComponent componentIn, final List<StructureComponent> listIn, final Random rand) {
            int i = 3;
            int j = 5;
            final EnumFacing enumfacing = this.getCoordBaseMode();
            if (enumfacing == EnumFacing.WEST || enumfacing == EnumFacing.NORTH) {
                i = 8 - i;
                j = 8 - j;
            }
            this.getNextComponentNormal((Stairs2)componentIn, listIn, rand, 5, 1);
            if (this.leftLow) {
                this.getNextComponentX((Stairs2)componentIn, listIn, rand, i, 1);
            }
            if (this.leftHigh) {
                this.getNextComponentX((Stairs2)componentIn, listIn, rand, j, 7);
            }
            if (this.rightLow) {
                this.getNextComponentZ((Stairs2)componentIn, listIn, rand, i, 1);
            }
            if (this.rightHigh) {
                this.getNextComponentZ((Stairs2)componentIn, listIn, rand, j, 7);
            }
        }
        
        public static Crossing createPiece(final List<StructureComponent> p_175866_0_, final Random p_175866_1_, final int p_175866_2_, final int p_175866_3_, final int p_175866_4_, final EnumFacing p_175866_5_, final int p_175866_6_) {
            final StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(p_175866_2_, p_175866_3_, p_175866_4_, -4, -3, 0, 10, 9, 11, p_175866_5_);
            return (Stronghold.canStrongholdGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(p_175866_0_, structureboundingbox) == null) ? new Crossing(p_175866_6_, p_175866_1_, structureboundingbox, p_175866_5_) : null;
        }
        
        @Override
        public boolean addComponentParts(final World worldIn, final Random randomIn, final StructureBoundingBox structureBoundingBoxIn) {
            if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
                return false;
            }
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 9, 8, 10, true, randomIn, StructureStrongholdPieces.STRONGHOLD_STONES);
            this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.entryDoor, 4, 3, 0);
            if (this.leftLow) {
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 3, 1, 0, 5, 3, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            }
            if (this.rightLow) {
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 3, 1, 9, 5, 3, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            }
            if (this.leftHigh) {
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5, 7, 0, 7, 9, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            }
            if (this.rightHigh) {
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 5, 7, 9, 7, 9, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            }
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 10, 7, 3, 10, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, 2, 1, 8, 2, 6, false, randomIn, StructureStrongholdPieces.STRONGHOLD_STONES);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 1, 5, 4, 4, 9, false, randomIn, StructureStrongholdPieces.STRONGHOLD_STONES);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 8, 1, 5, 8, 4, 9, false, randomIn, StructureStrongholdPieces.STRONGHOLD_STONES);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, 4, 7, 3, 4, 9, false, randomIn, StructureStrongholdPieces.STRONGHOLD_STONES);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, 3, 5, 3, 3, 6, false, randomIn, StructureStrongholdPieces.STRONGHOLD_STONES);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 4, 3, 3, 4, Blocks.STONE_SLAB.getDefaultState(), Blocks.STONE_SLAB.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 6, 3, 4, 6, Blocks.STONE_SLAB.getDefaultState(), Blocks.STONE_SLAB.getDefaultState(), false);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 5, 1, 7, 7, 1, 8, false, randomIn, StructureStrongholdPieces.STRONGHOLD_STONES);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 9, 7, 1, 9, Blocks.STONE_SLAB.getDefaultState(), Blocks.STONE_SLAB.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 2, 7, 7, 2, 7, Blocks.STONE_SLAB.getDefaultState(), Blocks.STONE_SLAB.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 5, 7, 4, 5, 9, Blocks.STONE_SLAB.getDefaultState(), Blocks.STONE_SLAB.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 5, 7, 8, 5, 9, Blocks.STONE_SLAB.getDefaultState(), Blocks.STONE_SLAB.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 5, 7, 7, 5, 9, Blocks.DOUBLE_STONE_SLAB.getDefaultState(), Blocks.DOUBLE_STONE_SLAB.getDefaultState(), false);
            this.setBlockState(worldIn, Blocks.TORCH.getDefaultState().withProperty((IProperty<Comparable>)BlockTorch.FACING, EnumFacing.SOUTH), 6, 5, 6, structureBoundingBoxIn);
            return true;
        }
    }
    
    public static class LeftTurn extends Stronghold
    {
        public LeftTurn() {
        }
        
        public LeftTurn(final int p_i45579_1_, final Random p_i45579_2_, final StructureBoundingBox p_i45579_3_, final EnumFacing p_i45579_4_) {
            super(p_i45579_1_);
            this.setCoordBaseMode(p_i45579_4_);
            this.entryDoor = this.getRandomDoor(p_i45579_2_);
            this.boundingBox = p_i45579_3_;
        }
        
        @Override
        public void buildComponent(final StructureComponent componentIn, final List<StructureComponent> listIn, final Random rand) {
            final EnumFacing enumfacing = this.getCoordBaseMode();
            if (enumfacing != EnumFacing.NORTH && enumfacing != EnumFacing.EAST) {
                this.getNextComponentZ((Stairs2)componentIn, listIn, rand, 1, 1);
            }
            else {
                this.getNextComponentX((Stairs2)componentIn, listIn, rand, 1, 1);
            }
        }
        
        public static LeftTurn createPiece(final List<StructureComponent> p_175867_0_, final Random p_175867_1_, final int p_175867_2_, final int p_175867_3_, final int p_175867_4_, final EnumFacing p_175867_5_, final int p_175867_6_) {
            final StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(p_175867_2_, p_175867_3_, p_175867_4_, -1, -1, 0, 5, 5, 5, p_175867_5_);
            return (Stronghold.canStrongholdGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(p_175867_0_, structureboundingbox) == null) ? new LeftTurn(p_175867_6_, p_175867_1_, structureboundingbox, p_175867_5_) : null;
        }
        
        @Override
        public boolean addComponentParts(final World worldIn, final Random randomIn, final StructureBoundingBox structureBoundingBoxIn) {
            if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
                return false;
            }
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 4, 4, true, randomIn, StructureStrongholdPieces.STRONGHOLD_STONES);
            this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.entryDoor, 1, 1, 0);
            final EnumFacing enumfacing = this.getCoordBaseMode();
            if (enumfacing != EnumFacing.NORTH && enumfacing != EnumFacing.EAST) {
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 1, 1, 4, 3, 3, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            }
            else {
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 1, 0, 3, 3, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            }
            return true;
        }
    }
    
    public static class Library extends Stronghold
    {
        private boolean isLargeRoom;
        
        public Library() {
        }
        
        public Library(final int p_i45578_1_, final Random p_i45578_2_, final StructureBoundingBox p_i45578_3_, final EnumFacing p_i45578_4_) {
            super(p_i45578_1_);
            this.setCoordBaseMode(p_i45578_4_);
            this.entryDoor = this.getRandomDoor(p_i45578_2_);
            this.boundingBox = p_i45578_3_;
            this.isLargeRoom = (p_i45578_3_.getYSize() > 6);
        }
        
        @Override
        protected void writeStructureToNBT(final NBTTagCompound tagCompound) {
            super.writeStructureToNBT(tagCompound);
            tagCompound.setBoolean("Tall", this.isLargeRoom);
        }
        
        @Override
        protected void readStructureFromNBT(final NBTTagCompound tagCompound, final TemplateManager p_143011_2_) {
            super.readStructureFromNBT(tagCompound, p_143011_2_);
            this.isLargeRoom = tagCompound.getBoolean("Tall");
        }
        
        public static Library createPiece(final List<StructureComponent> p_175864_0_, final Random p_175864_1_, final int p_175864_2_, final int p_175864_3_, final int p_175864_4_, final EnumFacing p_175864_5_, final int p_175864_6_) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(p_175864_2_, p_175864_3_, p_175864_4_, -4, -1, 0, 14, 11, 15, p_175864_5_);
            if (!Stronghold.canStrongholdGoDeeper(structureboundingbox) || StructureComponent.findIntersecting(p_175864_0_, structureboundingbox) != null) {
                structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(p_175864_2_, p_175864_3_, p_175864_4_, -4, -1, 0, 14, 6, 15, p_175864_5_);
                if (!Stronghold.canStrongholdGoDeeper(structureboundingbox) || StructureComponent.findIntersecting(p_175864_0_, structureboundingbox) != null) {
                    return null;
                }
            }
            return new Library(p_175864_6_, p_175864_1_, structureboundingbox, p_175864_5_);
        }
        
        @Override
        public boolean addComponentParts(final World worldIn, final Random randomIn, final StructureBoundingBox structureBoundingBoxIn) {
            if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
                return false;
            }
            int i = 11;
            if (!this.isLargeRoom) {
                i = 6;
            }
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 13, i - 1, 14, true, randomIn, StructureStrongholdPieces.STRONGHOLD_STONES);
            this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.entryDoor, 4, 1, 0);
            this.generateMaybeBox(worldIn, structureBoundingBoxIn, randomIn, 0.07f, 2, 1, 1, 11, 4, 13, Blocks.WEB.getDefaultState(), Blocks.WEB.getDefaultState(), false, 0);
            final int j = 1;
            final int k = 12;
            for (int l = 1; l <= 13; ++l) {
                if ((l - 1) % 4 == 0) {
                    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, l, 1, 4, l, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
                    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 12, 1, l, 12, 4, l, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
                    this.setBlockState(worldIn, Blocks.TORCH.getDefaultState().withProperty((IProperty<Comparable>)BlockTorch.FACING, EnumFacing.EAST), 2, 3, l, structureBoundingBoxIn);
                    this.setBlockState(worldIn, Blocks.TORCH.getDefaultState().withProperty((IProperty<Comparable>)BlockTorch.FACING, EnumFacing.WEST), 11, 3, l, structureBoundingBoxIn);
                    if (this.isLargeRoom) {
                        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 6, l, 1, 9, l, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
                        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 12, 6, l, 12, 9, l, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
                    }
                }
                else {
                    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, l, 1, 4, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
                    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 12, 1, l, 12, 4, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
                    if (this.isLargeRoom) {
                        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 6, l, 1, 9, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
                        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 12, 6, l, 12, 9, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
                    }
                }
            }
            for (int k2 = 3; k2 < 12; k2 += 2) {
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, k2, 4, 3, k2, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, k2, 7, 3, k2, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 1, k2, 10, 3, k2, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
            }
            if (this.isLargeRoom) {
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 5, 1, 3, 5, 13, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 5, 1, 12, 5, 13, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 5, 1, 9, 5, 2, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 5, 12, 9, 5, 13, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
                this.setBlockState(worldIn, Blocks.PLANKS.getDefaultState(), 9, 5, 11, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.PLANKS.getDefaultState(), 8, 5, 11, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.PLANKS.getDefaultState(), 9, 5, 10, structureBoundingBoxIn);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 6, 2, 3, 6, 12, Blocks.OAK_FENCE.getDefaultState(), Blocks.OAK_FENCE.getDefaultState(), false);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 6, 2, 10, 6, 10, Blocks.OAK_FENCE.getDefaultState(), Blocks.OAK_FENCE.getDefaultState(), false);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 6, 2, 9, 6, 2, Blocks.OAK_FENCE.getDefaultState(), Blocks.OAK_FENCE.getDefaultState(), false);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 6, 12, 8, 6, 12, Blocks.OAK_FENCE.getDefaultState(), Blocks.OAK_FENCE.getDefaultState(), false);
                this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 9, 6, 11, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 8, 6, 11, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 9, 6, 10, structureBoundingBoxIn);
                final IBlockState iblockstate1 = Blocks.LADDER.getDefaultState().withProperty((IProperty<Comparable>)BlockLadder.FACING, EnumFacing.SOUTH);
                this.setBlockState(worldIn, iblockstate1, 10, 1, 13, structureBoundingBoxIn);
                this.setBlockState(worldIn, iblockstate1, 10, 2, 13, structureBoundingBoxIn);
                this.setBlockState(worldIn, iblockstate1, 10, 3, 13, structureBoundingBoxIn);
                this.setBlockState(worldIn, iblockstate1, 10, 4, 13, structureBoundingBoxIn);
                this.setBlockState(worldIn, iblockstate1, 10, 5, 13, structureBoundingBoxIn);
                this.setBlockState(worldIn, iblockstate1, 10, 6, 13, structureBoundingBoxIn);
                this.setBlockState(worldIn, iblockstate1, 10, 7, 13, structureBoundingBoxIn);
                final int i2 = 7;
                final int j2 = 7;
                this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 6, 9, 7, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 7, 9, 7, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 6, 8, 7, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 7, 8, 7, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 6, 7, 7, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 7, 7, 7, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 5, 7, 7, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 8, 7, 7, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 6, 7, 6, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 6, 7, 8, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 7, 7, 6, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 7, 7, 8, structureBoundingBoxIn);
                final IBlockState iblockstate2 = Blocks.TORCH.getDefaultState().withProperty((IProperty<Comparable>)BlockTorch.FACING, EnumFacing.UP);
                this.setBlockState(worldIn, iblockstate2, 5, 8, 7, structureBoundingBoxIn);
                this.setBlockState(worldIn, iblockstate2, 8, 8, 7, structureBoundingBoxIn);
                this.setBlockState(worldIn, iblockstate2, 6, 8, 6, structureBoundingBoxIn);
                this.setBlockState(worldIn, iblockstate2, 6, 8, 8, structureBoundingBoxIn);
                this.setBlockState(worldIn, iblockstate2, 7, 8, 6, structureBoundingBoxIn);
                this.setBlockState(worldIn, iblockstate2, 7, 8, 8, structureBoundingBoxIn);
            }
            this.generateChest(worldIn, structureBoundingBoxIn, randomIn, 3, 3, 5, LootTableList.CHESTS_STRONGHOLD_LIBRARY);
            if (this.isLargeRoom) {
                this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 12, 9, 1, structureBoundingBoxIn);
                this.generateChest(worldIn, structureBoundingBoxIn, randomIn, 12, 8, 1, LootTableList.CHESTS_STRONGHOLD_LIBRARY);
            }
            return true;
        }
    }
    
    static class PieceWeight
    {
        public Class<? extends Stronghold> pieceClass;
        public final int pieceWeight;
        public int instancesSpawned;
        public int instancesLimit;
        
        public PieceWeight(final Class<? extends Stronghold> p_i2076_1_, final int p_i2076_2_, final int p_i2076_3_) {
            this.pieceClass = p_i2076_1_;
            this.pieceWeight = p_i2076_2_;
            this.instancesLimit = p_i2076_3_;
        }
        
        public boolean canSpawnMoreStructuresOfType(final int p_75189_1_) {
            return this.instancesLimit == 0 || this.instancesSpawned < this.instancesLimit;
        }
        
        public boolean canSpawnMoreStructures() {
            return this.instancesLimit == 0 || this.instancesSpawned < this.instancesLimit;
        }
    }
    
    public static class PortalRoom extends Stronghold
    {
        private boolean hasSpawner;
        
        public PortalRoom() {
        }
        
        public PortalRoom(final int p_i45577_1_, final Random p_i45577_2_, final StructureBoundingBox p_i45577_3_, final EnumFacing p_i45577_4_) {
            super(p_i45577_1_);
            this.setCoordBaseMode(p_i45577_4_);
            this.boundingBox = p_i45577_3_;
        }
        
        @Override
        protected void writeStructureToNBT(final NBTTagCompound tagCompound) {
            super.writeStructureToNBT(tagCompound);
            tagCompound.setBoolean("Mob", this.hasSpawner);
        }
        
        @Override
        protected void readStructureFromNBT(final NBTTagCompound tagCompound, final TemplateManager p_143011_2_) {
            super.readStructureFromNBT(tagCompound, p_143011_2_);
            this.hasSpawner = tagCompound.getBoolean("Mob");
        }
        
        @Override
        public void buildComponent(final StructureComponent componentIn, final List<StructureComponent> listIn, final Random rand) {
            if (componentIn != null) {
                ((Stairs2)componentIn).strongholdPortalRoom = this;
            }
        }
        
        public static PortalRoom createPiece(final List<StructureComponent> p_175865_0_, final Random p_175865_1_, final int p_175865_2_, final int p_175865_3_, final int p_175865_4_, final EnumFacing p_175865_5_, final int p_175865_6_) {
            final StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(p_175865_2_, p_175865_3_, p_175865_4_, -4, -1, 0, 11, 8, 16, p_175865_5_);
            return (Stronghold.canStrongholdGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(p_175865_0_, structureboundingbox) == null) ? new PortalRoom(p_175865_6_, p_175865_1_, structureboundingbox, p_175865_5_) : null;
        }
        
        @Override
        public boolean addComponentParts(final World worldIn, final Random randomIn, final StructureBoundingBox structureBoundingBoxIn) {
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 10, 7, 15, false, randomIn, StructureStrongholdPieces.STRONGHOLD_STONES);
            this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, Door.GRATES, 4, 1, 0);
            int i = 6;
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, i, 1, 1, i, 14, false, randomIn, StructureStrongholdPieces.STRONGHOLD_STONES);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 9, i, 1, 9, i, 14, false, randomIn, StructureStrongholdPieces.STRONGHOLD_STONES);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, i, 1, 8, i, 2, false, randomIn, StructureStrongholdPieces.STRONGHOLD_STONES);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, i, 14, 8, i, 14, false, randomIn, StructureStrongholdPieces.STRONGHOLD_STONES);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, 1, 1, 2, 1, 4, false, randomIn, StructureStrongholdPieces.STRONGHOLD_STONES);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 8, 1, 1, 9, 1, 4, false, randomIn, StructureStrongholdPieces.STRONGHOLD_STONES);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 1, 1, 1, 3, Blocks.FLOWING_LAVA.getDefaultState(), Blocks.FLOWING_LAVA.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 1, 1, 9, 1, 3, Blocks.FLOWING_LAVA.getDefaultState(), Blocks.FLOWING_LAVA.getDefaultState(), false);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 3, 1, 8, 7, 1, 12, false, randomIn, StructureStrongholdPieces.STRONGHOLD_STONES);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 1, 9, 6, 1, 11, Blocks.FLOWING_LAVA.getDefaultState(), Blocks.FLOWING_LAVA.getDefaultState(), false);
            for (int j = 3; j < 14; j += 2) {
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 3, j, 0, 4, j, Blocks.IRON_BARS.getDefaultState(), Blocks.IRON_BARS.getDefaultState(), false);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 3, j, 10, 4, j, Blocks.IRON_BARS.getDefaultState(), Blocks.IRON_BARS.getDefaultState(), false);
            }
            for (int i2 = 2; i2 < 9; i2 += 2) {
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, i2, 3, 15, i2, 4, 15, Blocks.IRON_BARS.getDefaultState(), Blocks.IRON_BARS.getDefaultState(), false);
            }
            final IBlockState iblockstate3 = Blocks.STONE_BRICK_STAIRS.getDefaultState().withProperty((IProperty<Comparable>)BlockStairs.FACING, EnumFacing.NORTH);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 1, 5, 6, 1, 7, false, randomIn, StructureStrongholdPieces.STRONGHOLD_STONES);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 2, 6, 6, 2, 7, false, randomIn, StructureStrongholdPieces.STRONGHOLD_STONES);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 3, 7, 6, 3, 7, false, randomIn, StructureStrongholdPieces.STRONGHOLD_STONES);
            for (int k = 4; k <= 6; ++k) {
                this.setBlockState(worldIn, iblockstate3, k, 1, 4, structureBoundingBoxIn);
                this.setBlockState(worldIn, iblockstate3, k, 2, 5, structureBoundingBoxIn);
                this.setBlockState(worldIn, iblockstate3, k, 3, 6, structureBoundingBoxIn);
            }
            final IBlockState iblockstate4 = Blocks.END_PORTAL_FRAME.getDefaultState().withProperty((IProperty<Comparable>)BlockEndPortalFrame.FACING, EnumFacing.NORTH);
            final IBlockState iblockstate5 = Blocks.END_PORTAL_FRAME.getDefaultState().withProperty((IProperty<Comparable>)BlockEndPortalFrame.FACING, EnumFacing.SOUTH);
            final IBlockState iblockstate6 = Blocks.END_PORTAL_FRAME.getDefaultState().withProperty((IProperty<Comparable>)BlockEndPortalFrame.FACING, EnumFacing.EAST);
            final IBlockState iblockstate7 = Blocks.END_PORTAL_FRAME.getDefaultState().withProperty((IProperty<Comparable>)BlockEndPortalFrame.FACING, EnumFacing.WEST);
            boolean flag = true;
            final boolean[] aboolean = new boolean[12];
            for (int l = 0; l < aboolean.length; ++l) {
                aboolean[l] = (randomIn.nextFloat() > 0.9f);
                flag &= aboolean[l];
            }
            this.setBlockState(worldIn, iblockstate4.withProperty((IProperty<Comparable>)BlockEndPortalFrame.EYE, aboolean[0]), 4, 3, 8, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate4.withProperty((IProperty<Comparable>)BlockEndPortalFrame.EYE, aboolean[1]), 5, 3, 8, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate4.withProperty((IProperty<Comparable>)BlockEndPortalFrame.EYE, aboolean[2]), 6, 3, 8, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate5.withProperty((IProperty<Comparable>)BlockEndPortalFrame.EYE, aboolean[3]), 4, 3, 12, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate5.withProperty((IProperty<Comparable>)BlockEndPortalFrame.EYE, aboolean[4]), 5, 3, 12, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate5.withProperty((IProperty<Comparable>)BlockEndPortalFrame.EYE, aboolean[5]), 6, 3, 12, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate6.withProperty((IProperty<Comparable>)BlockEndPortalFrame.EYE, aboolean[6]), 3, 3, 9, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate6.withProperty((IProperty<Comparable>)BlockEndPortalFrame.EYE, aboolean[7]), 3, 3, 10, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate6.withProperty((IProperty<Comparable>)BlockEndPortalFrame.EYE, aboolean[8]), 3, 3, 11, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate7.withProperty((IProperty<Comparable>)BlockEndPortalFrame.EYE, aboolean[9]), 7, 3, 9, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate7.withProperty((IProperty<Comparable>)BlockEndPortalFrame.EYE, aboolean[10]), 7, 3, 10, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate7.withProperty((IProperty<Comparable>)BlockEndPortalFrame.EYE, aboolean[11]), 7, 3, 11, structureBoundingBoxIn);
            if (flag) {
                final IBlockState iblockstate8 = Blocks.END_PORTAL.getDefaultState();
                this.setBlockState(worldIn, iblockstate8, 4, 3, 9, structureBoundingBoxIn);
                this.setBlockState(worldIn, iblockstate8, 5, 3, 9, structureBoundingBoxIn);
                this.setBlockState(worldIn, iblockstate8, 6, 3, 9, structureBoundingBoxIn);
                this.setBlockState(worldIn, iblockstate8, 4, 3, 10, structureBoundingBoxIn);
                this.setBlockState(worldIn, iblockstate8, 5, 3, 10, structureBoundingBoxIn);
                this.setBlockState(worldIn, iblockstate8, 6, 3, 10, structureBoundingBoxIn);
                this.setBlockState(worldIn, iblockstate8, 4, 3, 11, structureBoundingBoxIn);
                this.setBlockState(worldIn, iblockstate8, 5, 3, 11, structureBoundingBoxIn);
                this.setBlockState(worldIn, iblockstate8, 6, 3, 11, structureBoundingBoxIn);
            }
            if (!this.hasSpawner) {
                i = this.getYWithOffset(3);
                final BlockPos blockpos = new BlockPos(this.getXWithOffset(5, 6), i, this.getZWithOffset(5, 6));
                if (structureBoundingBoxIn.isVecInside(blockpos)) {
                    this.hasSpawner = true;
                    worldIn.setBlockState(blockpos, Blocks.MOB_SPAWNER.getDefaultState(), 2);
                    final TileEntity tileentity = worldIn.getTileEntity(blockpos);
                    if (tileentity instanceof TileEntityMobSpawner) {
                        ((TileEntityMobSpawner)tileentity).getSpawnerBaseLogic().setEntityId(EntityList.getKey(EntitySilverfish.class));
                    }
                }
            }
            return true;
        }
    }
    
    public static class Prison extends Stronghold
    {
        public Prison() {
        }
        
        public Prison(final int p_i45576_1_, final Random p_i45576_2_, final StructureBoundingBox p_i45576_3_, final EnumFacing p_i45576_4_) {
            super(p_i45576_1_);
            this.setCoordBaseMode(p_i45576_4_);
            this.entryDoor = this.getRandomDoor(p_i45576_2_);
            this.boundingBox = p_i45576_3_;
        }
        
        @Override
        public void buildComponent(final StructureComponent componentIn, final List<StructureComponent> listIn, final Random rand) {
            this.getNextComponentNormal((Stairs2)componentIn, listIn, rand, 1, 1);
        }
        
        public static Prison createPiece(final List<StructureComponent> p_175860_0_, final Random p_175860_1_, final int p_175860_2_, final int p_175860_3_, final int p_175860_4_, final EnumFacing p_175860_5_, final int p_175860_6_) {
            final StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(p_175860_2_, p_175860_3_, p_175860_4_, -1, -1, 0, 9, 5, 11, p_175860_5_);
            return (Stronghold.canStrongholdGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(p_175860_0_, structureboundingbox) == null) ? new Prison(p_175860_6_, p_175860_1_, structureboundingbox, p_175860_5_) : null;
        }
        
        @Override
        public boolean addComponentParts(final World worldIn, final Random randomIn, final StructureBoundingBox structureBoundingBoxIn) {
            if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
                return false;
            }
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 8, 4, 10, true, randomIn, StructureStrongholdPieces.STRONGHOLD_STONES);
            this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.entryDoor, 1, 1, 0);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 10, 3, 3, 10, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 1, 1, 4, 3, 1, false, randomIn, StructureStrongholdPieces.STRONGHOLD_STONES);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 1, 3, 4, 3, 3, false, randomIn, StructureStrongholdPieces.STRONGHOLD_STONES);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 1, 7, 4, 3, 7, false, randomIn, StructureStrongholdPieces.STRONGHOLD_STONES);
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 1, 9, 4, 3, 9, false, randomIn, StructureStrongholdPieces.STRONGHOLD_STONES);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 1, 4, 4, 3, 6, Blocks.IRON_BARS.getDefaultState(), Blocks.IRON_BARS.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 5, 7, 3, 5, Blocks.IRON_BARS.getDefaultState(), Blocks.IRON_BARS.getDefaultState(), false);
            this.setBlockState(worldIn, Blocks.IRON_BARS.getDefaultState(), 4, 3, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.IRON_BARS.getDefaultState(), 4, 3, 8, structureBoundingBoxIn);
            final IBlockState iblockstate = Blocks.IRON_DOOR.getDefaultState().withProperty((IProperty<Comparable>)BlockDoor.FACING, EnumFacing.WEST);
            final IBlockState iblockstate2 = Blocks.IRON_DOOR.getDefaultState().withProperty((IProperty<Comparable>)BlockDoor.FACING, EnumFacing.WEST).withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER);
            this.setBlockState(worldIn, iblockstate, 4, 1, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate2, 4, 2, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate, 4, 1, 8, structureBoundingBoxIn);
            this.setBlockState(worldIn, iblockstate2, 4, 2, 8, structureBoundingBoxIn);
            return true;
        }
    }
    
    public static class RightTurn extends LeftTurn
    {
        @Override
        public void buildComponent(final StructureComponent componentIn, final List<StructureComponent> listIn, final Random rand) {
            final EnumFacing enumfacing = this.getCoordBaseMode();
            if (enumfacing != EnumFacing.NORTH && enumfacing != EnumFacing.EAST) {
                this.getNextComponentX((Stairs2)componentIn, listIn, rand, 1, 1);
            }
            else {
                this.getNextComponentZ((Stairs2)componentIn, listIn, rand, 1, 1);
            }
        }
        
        @Override
        public boolean addComponentParts(final World worldIn, final Random randomIn, final StructureBoundingBox structureBoundingBoxIn) {
            if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
                return false;
            }
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 4, 4, true, randomIn, StructureStrongholdPieces.STRONGHOLD_STONES);
            this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.entryDoor, 1, 1, 0);
            final EnumFacing enumfacing = this.getCoordBaseMode();
            if (enumfacing != EnumFacing.NORTH && enumfacing != EnumFacing.EAST) {
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 1, 0, 3, 3, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            }
            else {
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 1, 1, 4, 3, 3, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            }
            return true;
        }
    }
    
    public static class RoomCrossing extends Stronghold
    {
        protected int roomType;
        
        public RoomCrossing() {
        }
        
        public RoomCrossing(final int p_i45575_1_, final Random p_i45575_2_, final StructureBoundingBox p_i45575_3_, final EnumFacing p_i45575_4_) {
            super(p_i45575_1_);
            this.setCoordBaseMode(p_i45575_4_);
            this.entryDoor = this.getRandomDoor(p_i45575_2_);
            this.boundingBox = p_i45575_3_;
            this.roomType = p_i45575_2_.nextInt(5);
        }
        
        @Override
        protected void writeStructureToNBT(final NBTTagCompound tagCompound) {
            super.writeStructureToNBT(tagCompound);
            tagCompound.setInteger("Type", this.roomType);
        }
        
        @Override
        protected void readStructureFromNBT(final NBTTagCompound tagCompound, final TemplateManager p_143011_2_) {
            super.readStructureFromNBT(tagCompound, p_143011_2_);
            this.roomType = tagCompound.getInteger("Type");
        }
        
        @Override
        public void buildComponent(final StructureComponent componentIn, final List<StructureComponent> listIn, final Random rand) {
            this.getNextComponentNormal((Stairs2)componentIn, listIn, rand, 4, 1);
            this.getNextComponentX((Stairs2)componentIn, listIn, rand, 1, 4);
            this.getNextComponentZ((Stairs2)componentIn, listIn, rand, 1, 4);
        }
        
        public static RoomCrossing createPiece(final List<StructureComponent> p_175859_0_, final Random p_175859_1_, final int p_175859_2_, final int p_175859_3_, final int p_175859_4_, final EnumFacing p_175859_5_, final int p_175859_6_) {
            final StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(p_175859_2_, p_175859_3_, p_175859_4_, -4, -1, 0, 11, 7, 11, p_175859_5_);
            return (Stronghold.canStrongholdGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(p_175859_0_, structureboundingbox) == null) ? new RoomCrossing(p_175859_6_, p_175859_1_, structureboundingbox, p_175859_5_) : null;
        }
        
        @Override
        public boolean addComponentParts(final World worldIn, final Random randomIn, final StructureBoundingBox structureBoundingBoxIn) {
            if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
                return false;
            }
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 10, 6, 10, true, randomIn, StructureStrongholdPieces.STRONGHOLD_STONES);
            this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.entryDoor, 4, 1, 0);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 1, 10, 6, 3, 10, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 4, 0, 3, 6, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 1, 4, 10, 3, 6, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            switch (this.roomType) {
                case 0: {
                    this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), 5, 1, 5, structureBoundingBoxIn);
                    this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), 5, 2, 5, structureBoundingBoxIn);
                    this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), 5, 3, 5, structureBoundingBoxIn);
                    this.setBlockState(worldIn, Blocks.TORCH.getDefaultState().withProperty((IProperty<Comparable>)BlockTorch.FACING, EnumFacing.WEST), 4, 3, 5, structureBoundingBoxIn);
                    this.setBlockState(worldIn, Blocks.TORCH.getDefaultState().withProperty((IProperty<Comparable>)BlockTorch.FACING, EnumFacing.EAST), 6, 3, 5, structureBoundingBoxIn);
                    this.setBlockState(worldIn, Blocks.TORCH.getDefaultState().withProperty((IProperty<Comparable>)BlockTorch.FACING, EnumFacing.SOUTH), 5, 3, 4, structureBoundingBoxIn);
                    this.setBlockState(worldIn, Blocks.TORCH.getDefaultState().withProperty((IProperty<Comparable>)BlockTorch.FACING, EnumFacing.NORTH), 5, 3, 6, structureBoundingBoxIn);
                    this.setBlockState(worldIn, Blocks.STONE_SLAB.getDefaultState(), 4, 1, 4, structureBoundingBoxIn);
                    this.setBlockState(worldIn, Blocks.STONE_SLAB.getDefaultState(), 4, 1, 5, structureBoundingBoxIn);
                    this.setBlockState(worldIn, Blocks.STONE_SLAB.getDefaultState(), 4, 1, 6, structureBoundingBoxIn);
                    this.setBlockState(worldIn, Blocks.STONE_SLAB.getDefaultState(), 6, 1, 4, structureBoundingBoxIn);
                    this.setBlockState(worldIn, Blocks.STONE_SLAB.getDefaultState(), 6, 1, 5, structureBoundingBoxIn);
                    this.setBlockState(worldIn, Blocks.STONE_SLAB.getDefaultState(), 6, 1, 6, structureBoundingBoxIn);
                    this.setBlockState(worldIn, Blocks.STONE_SLAB.getDefaultState(), 5, 1, 4, structureBoundingBoxIn);
                    this.setBlockState(worldIn, Blocks.STONE_SLAB.getDefaultState(), 5, 1, 6, structureBoundingBoxIn);
                    break;
                }
                case 1: {
                    for (int i1 = 0; i1 < 5; ++i1) {
                        this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), 3, 1, 3 + i1, structureBoundingBoxIn);
                        this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), 7, 1, 3 + i1, structureBoundingBoxIn);
                        this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), 3 + i1, 1, 3, structureBoundingBoxIn);
                        this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), 3 + i1, 1, 7, structureBoundingBoxIn);
                    }
                    this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), 5, 1, 5, structureBoundingBoxIn);
                    this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), 5, 2, 5, structureBoundingBoxIn);
                    this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), 5, 3, 5, structureBoundingBoxIn);
                    this.setBlockState(worldIn, Blocks.FLOWING_WATER.getDefaultState(), 5, 4, 5, structureBoundingBoxIn);
                    break;
                }
                case 2: {
                    for (int j = 1; j <= 9; ++j) {
                        this.setBlockState(worldIn, Blocks.COBBLESTONE.getDefaultState(), 1, 3, j, structureBoundingBoxIn);
                        this.setBlockState(worldIn, Blocks.COBBLESTONE.getDefaultState(), 9, 3, j, structureBoundingBoxIn);
                    }
                    for (int k = 1; k <= 9; ++k) {
                        this.setBlockState(worldIn, Blocks.COBBLESTONE.getDefaultState(), k, 3, 1, structureBoundingBoxIn);
                        this.setBlockState(worldIn, Blocks.COBBLESTONE.getDefaultState(), k, 3, 9, structureBoundingBoxIn);
                    }
                    this.setBlockState(worldIn, Blocks.COBBLESTONE.getDefaultState(), 5, 1, 4, structureBoundingBoxIn);
                    this.setBlockState(worldIn, Blocks.COBBLESTONE.getDefaultState(), 5, 1, 6, structureBoundingBoxIn);
                    this.setBlockState(worldIn, Blocks.COBBLESTONE.getDefaultState(), 5, 3, 4, structureBoundingBoxIn);
                    this.setBlockState(worldIn, Blocks.COBBLESTONE.getDefaultState(), 5, 3, 6, structureBoundingBoxIn);
                    this.setBlockState(worldIn, Blocks.COBBLESTONE.getDefaultState(), 4, 1, 5, structureBoundingBoxIn);
                    this.setBlockState(worldIn, Blocks.COBBLESTONE.getDefaultState(), 6, 1, 5, structureBoundingBoxIn);
                    this.setBlockState(worldIn, Blocks.COBBLESTONE.getDefaultState(), 4, 3, 5, structureBoundingBoxIn);
                    this.setBlockState(worldIn, Blocks.COBBLESTONE.getDefaultState(), 6, 3, 5, structureBoundingBoxIn);
                    for (int l = 1; l <= 3; ++l) {
                        this.setBlockState(worldIn, Blocks.COBBLESTONE.getDefaultState(), 4, l, 4, structureBoundingBoxIn);
                        this.setBlockState(worldIn, Blocks.COBBLESTONE.getDefaultState(), 6, l, 4, structureBoundingBoxIn);
                        this.setBlockState(worldIn, Blocks.COBBLESTONE.getDefaultState(), 4, l, 6, structureBoundingBoxIn);
                        this.setBlockState(worldIn, Blocks.COBBLESTONE.getDefaultState(), 6, l, 6, structureBoundingBoxIn);
                    }
                    this.setBlockState(worldIn, Blocks.TORCH.getDefaultState(), 5, 3, 5, structureBoundingBoxIn);
                    for (int m = 2; m <= 8; ++m) {
                        this.setBlockState(worldIn, Blocks.PLANKS.getDefaultState(), 2, 3, m, structureBoundingBoxIn);
                        this.setBlockState(worldIn, Blocks.PLANKS.getDefaultState(), 3, 3, m, structureBoundingBoxIn);
                        if (m <= 3 || m >= 7) {
                            this.setBlockState(worldIn, Blocks.PLANKS.getDefaultState(), 4, 3, m, structureBoundingBoxIn);
                            this.setBlockState(worldIn, Blocks.PLANKS.getDefaultState(), 5, 3, m, structureBoundingBoxIn);
                            this.setBlockState(worldIn, Blocks.PLANKS.getDefaultState(), 6, 3, m, structureBoundingBoxIn);
                        }
                        this.setBlockState(worldIn, Blocks.PLANKS.getDefaultState(), 7, 3, m, structureBoundingBoxIn);
                        this.setBlockState(worldIn, Blocks.PLANKS.getDefaultState(), 8, 3, m, structureBoundingBoxIn);
                    }
                    final IBlockState iblockstate = Blocks.LADDER.getDefaultState().withProperty((IProperty<Comparable>)BlockLadder.FACING, EnumFacing.WEST);
                    this.setBlockState(worldIn, iblockstate, 9, 1, 3, structureBoundingBoxIn);
                    this.setBlockState(worldIn, iblockstate, 9, 2, 3, structureBoundingBoxIn);
                    this.setBlockState(worldIn, iblockstate, 9, 3, 3, structureBoundingBoxIn);
                    this.generateChest(worldIn, structureBoundingBoxIn, randomIn, 3, 4, 8, LootTableList.CHESTS_STRONGHOLD_CROSSING);
                    break;
                }
            }
            return true;
        }
    }
    
    public static class Stairs extends Stronghold
    {
        private boolean source;
        
        public Stairs() {
        }
        
        public Stairs(final int p_i2081_1_, final Random p_i2081_2_, final int p_i2081_3_, final int p_i2081_4_) {
            super(p_i2081_1_);
            this.source = true;
            this.setCoordBaseMode(EnumFacing.Plane.HORIZONTAL.random(p_i2081_2_));
            this.entryDoor = Door.OPENING;
            if (this.getCoordBaseMode().getAxis() == EnumFacing.Axis.Z) {
                this.boundingBox = new StructureBoundingBox(p_i2081_3_, 64, p_i2081_4_, p_i2081_3_ + 5 - 1, 74, p_i2081_4_ + 5 - 1);
            }
            else {
                this.boundingBox = new StructureBoundingBox(p_i2081_3_, 64, p_i2081_4_, p_i2081_3_ + 5 - 1, 74, p_i2081_4_ + 5 - 1);
            }
        }
        
        public Stairs(final int p_i45574_1_, final Random p_i45574_2_, final StructureBoundingBox p_i45574_3_, final EnumFacing p_i45574_4_) {
            super(p_i45574_1_);
            this.source = false;
            this.setCoordBaseMode(p_i45574_4_);
            this.entryDoor = this.getRandomDoor(p_i45574_2_);
            this.boundingBox = p_i45574_3_;
        }
        
        @Override
        protected void writeStructureToNBT(final NBTTagCompound tagCompound) {
            super.writeStructureToNBT(tagCompound);
            tagCompound.setBoolean("Source", this.source);
        }
        
        @Override
        protected void readStructureFromNBT(final NBTTagCompound tagCompound, final TemplateManager p_143011_2_) {
            super.readStructureFromNBT(tagCompound, p_143011_2_);
            this.source = tagCompound.getBoolean("Source");
        }
        
        @Override
        public void buildComponent(final StructureComponent componentIn, final List<StructureComponent> listIn, final Random rand) {
            if (this.source) {
                StructureStrongholdPieces.strongComponentType = Crossing.class;
            }
            this.getNextComponentNormal((Stairs2)componentIn, listIn, rand, 1, 1);
        }
        
        public static Stairs createPiece(final List<StructureComponent> p_175863_0_, final Random p_175863_1_, final int p_175863_2_, final int p_175863_3_, final int p_175863_4_, final EnumFacing p_175863_5_, final int p_175863_6_) {
            final StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(p_175863_2_, p_175863_3_, p_175863_4_, -1, -7, 0, 5, 11, 5, p_175863_5_);
            return (Stronghold.canStrongholdGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(p_175863_0_, structureboundingbox) == null) ? new Stairs(p_175863_6_, p_175863_1_, structureboundingbox, p_175863_5_) : null;
        }
        
        @Override
        public boolean addComponentParts(final World worldIn, final Random randomIn, final StructureBoundingBox structureBoundingBoxIn) {
            if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
                return false;
            }
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 10, 4, true, randomIn, StructureStrongholdPieces.STRONGHOLD_STONES);
            this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.entryDoor, 1, 7, 0);
            this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, Door.OPENING, 1, 1, 4);
            this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), 2, 6, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), 1, 5, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STONE_SLAB.getStateFromMeta(BlockStoneSlab.EnumType.STONE.getMetadata()), 1, 6, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), 1, 5, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), 1, 4, 3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STONE_SLAB.getStateFromMeta(BlockStoneSlab.EnumType.STONE.getMetadata()), 1, 5, 3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), 2, 4, 3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), 3, 3, 3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STONE_SLAB.getStateFromMeta(BlockStoneSlab.EnumType.STONE.getMetadata()), 3, 4, 3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), 3, 3, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), 3, 2, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STONE_SLAB.getStateFromMeta(BlockStoneSlab.EnumType.STONE.getMetadata()), 3, 3, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), 2, 2, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), 1, 1, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STONE_SLAB.getStateFromMeta(BlockStoneSlab.EnumType.STONE.getMetadata()), 1, 2, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), 1, 1, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.STONE_SLAB.getStateFromMeta(BlockStoneSlab.EnumType.STONE.getMetadata()), 1, 1, 3, structureBoundingBoxIn);
            return true;
        }
    }
    
    public static class Stairs2 extends Stairs
    {
        public PieceWeight lastPlaced;
        public PortalRoom strongholdPortalRoom;
        public List<StructureComponent> pendingChildren;
        
        public Stairs2() {
            this.pendingChildren = (List<StructureComponent>)Lists.newArrayList();
        }
        
        public Stairs2(final int p_i2083_1_, final Random p_i2083_2_, final int p_i2083_3_, final int p_i2083_4_) {
            super(0, p_i2083_2_, p_i2083_3_, p_i2083_4_);
            this.pendingChildren = (List<StructureComponent>)Lists.newArrayList();
        }
    }
    
    public static class StairsStraight extends Stronghold
    {
        public StairsStraight() {
        }
        
        public StairsStraight(final int p_i45572_1_, final Random p_i45572_2_, final StructureBoundingBox p_i45572_3_, final EnumFacing p_i45572_4_) {
            super(p_i45572_1_);
            this.setCoordBaseMode(p_i45572_4_);
            this.entryDoor = this.getRandomDoor(p_i45572_2_);
            this.boundingBox = p_i45572_3_;
        }
        
        @Override
        public void buildComponent(final StructureComponent componentIn, final List<StructureComponent> listIn, final Random rand) {
            this.getNextComponentNormal((Stairs2)componentIn, listIn, rand, 1, 1);
        }
        
        public static StairsStraight createPiece(final List<StructureComponent> p_175861_0_, final Random p_175861_1_, final int p_175861_2_, final int p_175861_3_, final int p_175861_4_, final EnumFacing p_175861_5_, final int p_175861_6_) {
            final StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(p_175861_2_, p_175861_3_, p_175861_4_, -1, -7, 0, 5, 11, 8, p_175861_5_);
            return (Stronghold.canStrongholdGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(p_175861_0_, structureboundingbox) == null) ? new StairsStraight(p_175861_6_, p_175861_1_, structureboundingbox, p_175861_5_) : null;
        }
        
        @Override
        public boolean addComponentParts(final World worldIn, final Random randomIn, final StructureBoundingBox structureBoundingBoxIn) {
            if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
                return false;
            }
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 10, 7, true, randomIn, StructureStrongholdPieces.STRONGHOLD_STONES);
            this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.entryDoor, 1, 7, 0);
            this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, Door.OPENING, 1, 1, 7);
            final IBlockState iblockstate = Blocks.STONE_STAIRS.getDefaultState().withProperty((IProperty<Comparable>)BlockStairs.FACING, EnumFacing.SOUTH);
            for (int i = 0; i < 6; ++i) {
                this.setBlockState(worldIn, iblockstate, 1, 6 - i, 1 + i, structureBoundingBoxIn);
                this.setBlockState(worldIn, iblockstate, 2, 6 - i, 1 + i, structureBoundingBoxIn);
                this.setBlockState(worldIn, iblockstate, 3, 6 - i, 1 + i, structureBoundingBoxIn);
                if (i < 5) {
                    this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), 1, 5 - i, 1 + i, structureBoundingBoxIn);
                    this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), 2, 5 - i, 1 + i, structureBoundingBoxIn);
                    this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), 3, 5 - i, 1 + i, structureBoundingBoxIn);
                }
            }
            return true;
        }
    }
    
    static class Stones extends StructureComponent.BlockSelector
    {
        private Stones() {
        }
        
        @Override
        public void selectBlocks(final Random rand, final int x, final int y, final int z, final boolean wall) {
            if (wall) {
                final float f = rand.nextFloat();
                if (f < 0.2f) {
                    this.blockstate = Blocks.STONEBRICK.getStateFromMeta(BlockStoneBrick.CRACKED_META);
                }
                else if (f < 0.5f) {
                    this.blockstate = Blocks.STONEBRICK.getStateFromMeta(BlockStoneBrick.MOSSY_META);
                }
                else if (f < 0.55f) {
                    this.blockstate = Blocks.MONSTER_EGG.getStateFromMeta(BlockSilverfish.EnumType.STONEBRICK.getMetadata());
                }
                else {
                    this.blockstate = Blocks.STONEBRICK.getDefaultState();
                }
            }
            else {
                this.blockstate = Blocks.AIR.getDefaultState();
            }
        }
    }
    
    public static class Straight extends Stronghold
    {
        private boolean expandsX;
        private boolean expandsZ;
        
        public Straight() {
        }
        
        public Straight(final int p_i45573_1_, final Random p_i45573_2_, final StructureBoundingBox p_i45573_3_, final EnumFacing p_i45573_4_) {
            super(p_i45573_1_);
            this.setCoordBaseMode(p_i45573_4_);
            this.entryDoor = this.getRandomDoor(p_i45573_2_);
            this.boundingBox = p_i45573_3_;
            this.expandsX = (p_i45573_2_.nextInt(2) == 0);
            this.expandsZ = (p_i45573_2_.nextInt(2) == 0);
        }
        
        @Override
        protected void writeStructureToNBT(final NBTTagCompound tagCompound) {
            super.writeStructureToNBT(tagCompound);
            tagCompound.setBoolean("Left", this.expandsX);
            tagCompound.setBoolean("Right", this.expandsZ);
        }
        
        @Override
        protected void readStructureFromNBT(final NBTTagCompound tagCompound, final TemplateManager p_143011_2_) {
            super.readStructureFromNBT(tagCompound, p_143011_2_);
            this.expandsX = tagCompound.getBoolean("Left");
            this.expandsZ = tagCompound.getBoolean("Right");
        }
        
        @Override
        public void buildComponent(final StructureComponent componentIn, final List<StructureComponent> listIn, final Random rand) {
            this.getNextComponentNormal((Stairs2)componentIn, listIn, rand, 1, 1);
            if (this.expandsX) {
                this.getNextComponentX((Stairs2)componentIn, listIn, rand, 1, 2);
            }
            if (this.expandsZ) {
                this.getNextComponentZ((Stairs2)componentIn, listIn, rand, 1, 2);
            }
        }
        
        public static Straight createPiece(final List<StructureComponent> p_175862_0_, final Random p_175862_1_, final int p_175862_2_, final int p_175862_3_, final int p_175862_4_, final EnumFacing p_175862_5_, final int p_175862_6_) {
            final StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(p_175862_2_, p_175862_3_, p_175862_4_, -1, -1, 0, 5, 5, 7, p_175862_5_);
            return (Stronghold.canStrongholdGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(p_175862_0_, structureboundingbox) == null) ? new Straight(p_175862_6_, p_175862_1_, structureboundingbox, p_175862_5_) : null;
        }
        
        @Override
        public boolean addComponentParts(final World worldIn, final Random randomIn, final StructureBoundingBox structureBoundingBoxIn) {
            if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
                return false;
            }
            this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 4, 6, true, randomIn, StructureStrongholdPieces.STRONGHOLD_STONES);
            this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.entryDoor, 1, 1, 0);
            this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, Door.OPENING, 1, 1, 6);
            final IBlockState iblockstate = Blocks.TORCH.getDefaultState().withProperty((IProperty<Comparable>)BlockTorch.FACING, EnumFacing.EAST);
            final IBlockState iblockstate2 = Blocks.TORCH.getDefaultState().withProperty((IProperty<Comparable>)BlockTorch.FACING, EnumFacing.WEST);
            this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1f, 1, 2, 1, iblockstate);
            this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1f, 3, 2, 1, iblockstate2);
            this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1f, 1, 2, 5, iblockstate);
            this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1f, 3, 2, 5, iblockstate2);
            if (this.expandsX) {
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 2, 0, 3, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            }
            if (this.expandsZ) {
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 1, 2, 4, 3, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            }
            return true;
        }
    }
    
    abstract static class Stronghold extends StructureComponent
    {
        protected Door entryDoor;
        
        public Stronghold() {
            this.entryDoor = Door.OPENING;
        }
        
        protected Stronghold(final int p_i2087_1_) {
            super(p_i2087_1_);
            this.entryDoor = Door.OPENING;
        }
        
        @Override
        protected void writeStructureToNBT(final NBTTagCompound tagCompound) {
            tagCompound.setString("EntryDoor", this.entryDoor.name());
        }
        
        @Override
        protected void readStructureFromNBT(final NBTTagCompound tagCompound, final TemplateManager p_143011_2_) {
            this.entryDoor = Door.valueOf(tagCompound.getString("EntryDoor"));
        }
        
        protected void placeDoor(final World worldIn, final Random p_74990_2_, final StructureBoundingBox p_74990_3_, final Door p_74990_4_, final int p_74990_5_, final int p_74990_6_, final int p_74990_7_) {
            switch (p_74990_4_) {
                case OPENING: {
                    this.fillWithBlocks(worldIn, p_74990_3_, p_74990_5_, p_74990_6_, p_74990_7_, p_74990_5_ + 3 - 1, p_74990_6_ + 3 - 1, p_74990_7_, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
                    break;
                }
                case WOOD_DOOR: {
                    this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), p_74990_5_, p_74990_6_, p_74990_7_, p_74990_3_);
                    this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), p_74990_5_, p_74990_6_ + 1, p_74990_7_, p_74990_3_);
                    this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), p_74990_5_, p_74990_6_ + 2, p_74990_7_, p_74990_3_);
                    this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), p_74990_5_ + 1, p_74990_6_ + 2, p_74990_7_, p_74990_3_);
                    this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), p_74990_5_ + 2, p_74990_6_ + 2, p_74990_7_, p_74990_3_);
                    this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), p_74990_5_ + 2, p_74990_6_ + 1, p_74990_7_, p_74990_3_);
                    this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), p_74990_5_ + 2, p_74990_6_, p_74990_7_, p_74990_3_);
                    this.setBlockState(worldIn, Blocks.OAK_DOOR.getDefaultState(), p_74990_5_ + 1, p_74990_6_, p_74990_7_, p_74990_3_);
                    this.setBlockState(worldIn, Blocks.OAK_DOOR.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER), p_74990_5_ + 1, p_74990_6_ + 1, p_74990_7_, p_74990_3_);
                    break;
                }
                case GRATES: {
                    this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), p_74990_5_ + 1, p_74990_6_, p_74990_7_, p_74990_3_);
                    this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), p_74990_5_ + 1, p_74990_6_ + 1, p_74990_7_, p_74990_3_);
                    this.setBlockState(worldIn, Blocks.IRON_BARS.getDefaultState(), p_74990_5_, p_74990_6_, p_74990_7_, p_74990_3_);
                    this.setBlockState(worldIn, Blocks.IRON_BARS.getDefaultState(), p_74990_5_, p_74990_6_ + 1, p_74990_7_, p_74990_3_);
                    this.setBlockState(worldIn, Blocks.IRON_BARS.getDefaultState(), p_74990_5_, p_74990_6_ + 2, p_74990_7_, p_74990_3_);
                    this.setBlockState(worldIn, Blocks.IRON_BARS.getDefaultState(), p_74990_5_ + 1, p_74990_6_ + 2, p_74990_7_, p_74990_3_);
                    this.setBlockState(worldIn, Blocks.IRON_BARS.getDefaultState(), p_74990_5_ + 2, p_74990_6_ + 2, p_74990_7_, p_74990_3_);
                    this.setBlockState(worldIn, Blocks.IRON_BARS.getDefaultState(), p_74990_5_ + 2, p_74990_6_ + 1, p_74990_7_, p_74990_3_);
                    this.setBlockState(worldIn, Blocks.IRON_BARS.getDefaultState(), p_74990_5_ + 2, p_74990_6_, p_74990_7_, p_74990_3_);
                    break;
                }
                case IRON_DOOR: {
                    this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), p_74990_5_, p_74990_6_, p_74990_7_, p_74990_3_);
                    this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), p_74990_5_, p_74990_6_ + 1, p_74990_7_, p_74990_3_);
                    this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), p_74990_5_, p_74990_6_ + 2, p_74990_7_, p_74990_3_);
                    this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), p_74990_5_ + 1, p_74990_6_ + 2, p_74990_7_, p_74990_3_);
                    this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), p_74990_5_ + 2, p_74990_6_ + 2, p_74990_7_, p_74990_3_);
                    this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), p_74990_5_ + 2, p_74990_6_ + 1, p_74990_7_, p_74990_3_);
                    this.setBlockState(worldIn, Blocks.STONEBRICK.getDefaultState(), p_74990_5_ + 2, p_74990_6_, p_74990_7_, p_74990_3_);
                    this.setBlockState(worldIn, Blocks.IRON_DOOR.getDefaultState(), p_74990_5_ + 1, p_74990_6_, p_74990_7_, p_74990_3_);
                    this.setBlockState(worldIn, Blocks.IRON_DOOR.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER), p_74990_5_ + 1, p_74990_6_ + 1, p_74990_7_, p_74990_3_);
                    this.setBlockState(worldIn, Blocks.STONE_BUTTON.getDefaultState().withProperty((IProperty<Comparable>)BlockButton.FACING, EnumFacing.NORTH), p_74990_5_ + 2, p_74990_6_ + 1, p_74990_7_ + 1, p_74990_3_);
                    this.setBlockState(worldIn, Blocks.STONE_BUTTON.getDefaultState().withProperty((IProperty<Comparable>)BlockButton.FACING, EnumFacing.SOUTH), p_74990_5_ + 2, p_74990_6_ + 1, p_74990_7_ - 1, p_74990_3_);
                    break;
                }
            }
        }
        
        protected Door getRandomDoor(final Random p_74988_1_) {
            final int i = p_74988_1_.nextInt(5);
            switch (i) {
                default: {
                    return Door.OPENING;
                }
                case 2: {
                    return Door.WOOD_DOOR;
                }
                case 3: {
                    return Door.GRATES;
                }
                case 4: {
                    return Door.IRON_DOOR;
                }
            }
        }
        
        @Nullable
        protected StructureComponent getNextComponentNormal(final Stairs2 p_74986_1_, final List<StructureComponent> p_74986_2_, final Random p_74986_3_, final int p_74986_4_, final int p_74986_5_) {
            final EnumFacing enumfacing = this.getCoordBaseMode();
            if (enumfacing != null) {
                switch (enumfacing) {
                    case NORTH: {
                        return generateAndAddPiece(p_74986_1_, p_74986_2_, p_74986_3_, this.boundingBox.minX + p_74986_4_, this.boundingBox.minY + p_74986_5_, this.boundingBox.minZ - 1, enumfacing, this.getComponentType());
                    }
                    case SOUTH: {
                        return generateAndAddPiece(p_74986_1_, p_74986_2_, p_74986_3_, this.boundingBox.minX + p_74986_4_, this.boundingBox.minY + p_74986_5_, this.boundingBox.maxZ + 1, enumfacing, this.getComponentType());
                    }
                    case WEST: {
                        return generateAndAddPiece(p_74986_1_, p_74986_2_, p_74986_3_, this.boundingBox.minX - 1, this.boundingBox.minY + p_74986_5_, this.boundingBox.minZ + p_74986_4_, enumfacing, this.getComponentType());
                    }
                    case EAST: {
                        return generateAndAddPiece(p_74986_1_, p_74986_2_, p_74986_3_, this.boundingBox.maxX + 1, this.boundingBox.minY + p_74986_5_, this.boundingBox.minZ + p_74986_4_, enumfacing, this.getComponentType());
                    }
                }
            }
            return null;
        }
        
        @Nullable
        protected StructureComponent getNextComponentX(final Stairs2 p_74989_1_, final List<StructureComponent> p_74989_2_, final Random p_74989_3_, final int p_74989_4_, final int p_74989_5_) {
            final EnumFacing enumfacing = this.getCoordBaseMode();
            if (enumfacing != null) {
                switch (enumfacing) {
                    case NORTH: {
                        return generateAndAddPiece(p_74989_1_, p_74989_2_, p_74989_3_, this.boundingBox.minX - 1, this.boundingBox.minY + p_74989_4_, this.boundingBox.minZ + p_74989_5_, EnumFacing.WEST, this.getComponentType());
                    }
                    case SOUTH: {
                        return generateAndAddPiece(p_74989_1_, p_74989_2_, p_74989_3_, this.boundingBox.minX - 1, this.boundingBox.minY + p_74989_4_, this.boundingBox.minZ + p_74989_5_, EnumFacing.WEST, this.getComponentType());
                    }
                    case WEST: {
                        return generateAndAddPiece(p_74989_1_, p_74989_2_, p_74989_3_, this.boundingBox.minX + p_74989_5_, this.boundingBox.minY + p_74989_4_, this.boundingBox.minZ - 1, EnumFacing.NORTH, this.getComponentType());
                    }
                    case EAST: {
                        return generateAndAddPiece(p_74989_1_, p_74989_2_, p_74989_3_, this.boundingBox.minX + p_74989_5_, this.boundingBox.minY + p_74989_4_, this.boundingBox.minZ - 1, EnumFacing.NORTH, this.getComponentType());
                    }
                }
            }
            return null;
        }
        
        @Nullable
        protected StructureComponent getNextComponentZ(final Stairs2 p_74987_1_, final List<StructureComponent> p_74987_2_, final Random p_74987_3_, final int p_74987_4_, final int p_74987_5_) {
            final EnumFacing enumfacing = this.getCoordBaseMode();
            if (enumfacing != null) {
                switch (enumfacing) {
                    case NORTH: {
                        return generateAndAddPiece(p_74987_1_, p_74987_2_, p_74987_3_, this.boundingBox.maxX + 1, this.boundingBox.minY + p_74987_4_, this.boundingBox.minZ + p_74987_5_, EnumFacing.EAST, this.getComponentType());
                    }
                    case SOUTH: {
                        return generateAndAddPiece(p_74987_1_, p_74987_2_, p_74987_3_, this.boundingBox.maxX + 1, this.boundingBox.minY + p_74987_4_, this.boundingBox.minZ + p_74987_5_, EnumFacing.EAST, this.getComponentType());
                    }
                    case WEST: {
                        return generateAndAddPiece(p_74987_1_, p_74987_2_, p_74987_3_, this.boundingBox.minX + p_74987_5_, this.boundingBox.minY + p_74987_4_, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, this.getComponentType());
                    }
                    case EAST: {
                        return generateAndAddPiece(p_74987_1_, p_74987_2_, p_74987_3_, this.boundingBox.minX + p_74987_5_, this.boundingBox.minY + p_74987_4_, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, this.getComponentType());
                    }
                }
            }
            return null;
        }
        
        protected static boolean canStrongholdGoDeeper(final StructureBoundingBox p_74991_0_) {
            return p_74991_0_ != null && p_74991_0_.minY > 10;
        }
        
        public enum Door
        {
            OPENING, 
            WOOD_DOOR, 
            GRATES, 
            IRON_DOOR;
        }
    }
}
