/*
 * Decompiled with CFR 0.152.
 */
package de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.packets;

import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_8;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.Protocol1_7_6_10TO1_8;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.entityreplacements.ArmorStandReplacement;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.entityreplacements.EndermiteReplacement;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.entityreplacements.GuardianReplacement;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.items.ReplacementRegistry1_7_6_10to1_8;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.metadata.MetadataRewriter;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.storage.EntityTracker;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.storage.GameProfileStorage;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.types.Types1_7_6_10;
import de.gerrygames.viarewind.replacement.Replacement;
import de.gerrygames.viarewind.utils.PacketUtil;
import java.util.List;
import java.util.UUID;

public class SpawnPackets {
    public static void register(Protocol1_7_6_10TO1_8 protocol) {
        protocol.registerClientbound(ClientboundPackets1_8.SPAWN_PLAYER, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.handler(packetWrapper -> {
                    UUID uuid = packetWrapper.read(Type.UUID);
                    packetWrapper.write(Type.STRING, uuid.toString());
                    GameProfileStorage gameProfileStorage = packetWrapper.user().get(GameProfileStorage.class);
                    GameProfileStorage.GameProfile gameProfile = gameProfileStorage.get(uuid);
                    if (gameProfile == null) {
                        packetWrapper.write(Type.STRING, "");
                        packetWrapper.write(Type.VAR_INT, 0);
                    } else {
                        packetWrapper.write(Type.STRING, gameProfile.name.length() > 16 ? gameProfile.name.substring(0, 16) : gameProfile.name);
                        packetWrapper.write(Type.VAR_INT, gameProfile.properties.size());
                        for (GameProfileStorage.Property property : gameProfile.properties) {
                            packetWrapper.write(Type.STRING, property.name);
                            packetWrapper.write(Type.STRING, property.value);
                            packetWrapper.write(Type.STRING, property.signature == null ? "" : property.signature);
                        }
                    }
                    if (gameProfile != null && gameProfile.gamemode == 3) {
                        int entityId = packetWrapper.get(Type.VAR_INT, 0);
                        PacketWrapper equipmentPacket = PacketWrapper.create(4, null, packetWrapper.user());
                        equipmentPacket.write(Type.INT, entityId);
                        equipmentPacket.write(Type.SHORT, (short)4);
                        equipmentPacket.write(Types1_7_6_10.COMPRESSED_NBT_ITEM, gameProfile.getSkull());
                        PacketUtil.sendPacket(equipmentPacket, Protocol1_7_6_10TO1_8.class);
                        for (short i = 0; i < 4; i = (short)(i + 1)) {
                            equipmentPacket = PacketWrapper.create(4, null, packetWrapper.user());
                            equipmentPacket.write(Type.INT, entityId);
                            equipmentPacket.write(Type.SHORT, i);
                            equipmentPacket.write(Types1_7_6_10.COMPRESSED_NBT_ITEM, null);
                            PacketUtil.sendPacket(equipmentPacket, Protocol1_7_6_10TO1_8.class);
                        }
                    }
                    EntityTracker tracker = packetWrapper.user().get(EntityTracker.class);
                    tracker.addPlayer(packetWrapper.get(Type.VAR_INT, 0), uuid);
                });
                this.map(Type.INT);
                this.map(Type.INT);
                this.map(Type.INT);
                this.map(Type.BYTE);
                this.map(Type.BYTE);
                this.map(Type.SHORT);
                this.map(Types1_8.METADATA_LIST, Types1_7_6_10.METADATA_LIST);
                this.handler(packetWrapper -> {
                    List<Metadata> metadata = packetWrapper.get(Types1_7_6_10.METADATA_LIST, 0);
                    MetadataRewriter.transform(Entity1_10Types.EntityType.PLAYER, metadata);
                });
                this.handler(packetWrapper -> {
                    int entityId = packetWrapper.get(Type.VAR_INT, 0);
                    EntityTracker tracker = packetWrapper.user().get(EntityTracker.class);
                    tracker.getClientEntityTypes().put(entityId, Entity1_10Types.EntityType.PLAYER);
                    tracker.sendMetadataBuffer(entityId);
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.SPAWN_ENTITY, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.BYTE);
                this.map(Type.INT);
                this.map(Type.INT);
                this.map(Type.INT);
                this.map(Type.BYTE);
                this.map(Type.BYTE);
                this.map(Type.INT);
                this.handler(packetWrapper -> {
                    EntityTracker tracker;
                    int entityId = packetWrapper.get(Type.VAR_INT, 0);
                    byte typeId = packetWrapper.get(Type.BYTE, 0);
                    int x = packetWrapper.get(Type.INT, 0);
                    int y = packetWrapper.get(Type.INT, 1);
                    int z = packetWrapper.get(Type.INT, 2);
                    byte pitch = packetWrapper.get(Type.BYTE, 1);
                    byte yaw = packetWrapper.get(Type.BYTE, 2);
                    if (typeId == 71) {
                        switch (yaw) {
                            case -128: {
                                z += 32;
                                yaw = 0;
                                break;
                            }
                            case -64: {
                                x -= 32;
                                yaw = -64;
                                break;
                            }
                            case 0: {
                                z -= 32;
                                yaw = -128;
                                break;
                            }
                            case 64: {
                                x += 32;
                                yaw = 64;
                            }
                        }
                    } else if (typeId == 78) {
                        packetWrapper.cancel();
                        tracker = packetWrapper.user().get(EntityTracker.class);
                        ArmorStandReplacement armorStand = new ArmorStandReplacement(entityId, packetWrapper.user());
                        armorStand.setLocation((double)x / 32.0, (double)y / 32.0, (double)z / 32.0);
                        armorStand.setYawPitch((float)yaw * 360.0f / 256.0f, (float)pitch * 360.0f / 256.0f);
                        armorStand.setHeadYaw((float)yaw * 360.0f / 256.0f);
                        tracker.addEntityReplacement(armorStand);
                    } else if (typeId == 10) {
                        y += 12;
                    }
                    packetWrapper.set(Type.BYTE, 0, typeId);
                    packetWrapper.set(Type.INT, 0, x);
                    packetWrapper.set(Type.INT, 1, y);
                    packetWrapper.set(Type.INT, 2, z);
                    packetWrapper.set(Type.BYTE, 1, pitch);
                    packetWrapper.set(Type.BYTE, 2, yaw);
                    tracker = packetWrapper.user().get(EntityTracker.class);
                    Entity1_10Types.EntityType type = Entity1_10Types.getTypeFromId(typeId, true);
                    tracker.getClientEntityTypes().put(entityId, type);
                    tracker.sendMetadataBuffer(entityId);
                    int data = packetWrapper.get(Type.INT, 3);
                    if (type != null && type.isOrHasParent(Entity1_10Types.EntityType.FALLING_BLOCK)) {
                        int blockId = data & 0xFFF;
                        int blockData = data >> 12 & 0xF;
                        Replacement replace = ReplacementRegistry1_7_6_10to1_8.getReplacement(blockId, blockData);
                        if (replace != null) {
                            blockId = replace.getId();
                            blockData = replace.replaceData(blockData);
                        }
                        data = blockId | blockData << 16;
                        packetWrapper.set(Type.INT, 3, data);
                    }
                    if (data > 0) {
                        packetWrapper.passthrough(Type.SHORT);
                        packetWrapper.passthrough(Type.SHORT);
                        packetWrapper.passthrough(Type.SHORT);
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.SPAWN_MOB, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.INT);
                this.map(Type.INT);
                this.map(Type.INT);
                this.map(Type.BYTE);
                this.map(Type.BYTE);
                this.map(Type.BYTE);
                this.map(Type.SHORT);
                this.map(Type.SHORT);
                this.map(Type.SHORT);
                this.map(Types1_8.METADATA_LIST, Types1_7_6_10.METADATA_LIST);
                this.handler(packetWrapper -> {
                    int entityId = packetWrapper.get(Type.VAR_INT, 0);
                    short typeId = packetWrapper.get(Type.UNSIGNED_BYTE, 0);
                    int x = packetWrapper.get(Type.INT, 0);
                    int y = packetWrapper.get(Type.INT, 1);
                    int z = packetWrapper.get(Type.INT, 2);
                    byte pitch = packetWrapper.get(Type.BYTE, 1);
                    byte yaw = packetWrapper.get(Type.BYTE, 0);
                    byte headYaw = packetWrapper.get(Type.BYTE, 2);
                    if (typeId == 30) {
                        packetWrapper.cancel();
                        EntityTracker tracker = packetWrapper.user().get(EntityTracker.class);
                        ArmorStandReplacement armorStand = new ArmorStandReplacement(entityId, packetWrapper.user());
                        armorStand.setLocation((double)x / 32.0, (double)y / 32.0, (double)z / 32.0);
                        armorStand.setYawPitch((float)yaw * 360.0f / 256.0f, (float)pitch * 360.0f / 256.0f);
                        armorStand.setHeadYaw((float)headYaw * 360.0f / 256.0f);
                        tracker.addEntityReplacement(armorStand);
                    } else if (typeId == 68) {
                        packetWrapper.cancel();
                        EntityTracker tracker = packetWrapper.user().get(EntityTracker.class);
                        GuardianReplacement guardian = new GuardianReplacement(entityId, packetWrapper.user());
                        guardian.setLocation((double)x / 32.0, (double)y / 32.0, (double)z / 32.0);
                        guardian.setYawPitch((float)yaw * 360.0f / 256.0f, (float)pitch * 360.0f / 256.0f);
                        guardian.setHeadYaw((float)headYaw * 360.0f / 256.0f);
                        tracker.addEntityReplacement(guardian);
                    } else if (typeId == 67) {
                        packetWrapper.cancel();
                        EntityTracker tracker = packetWrapper.user().get(EntityTracker.class);
                        EndermiteReplacement endermite = new EndermiteReplacement(entityId, packetWrapper.user());
                        endermite.setLocation((double)x / 32.0, (double)y / 32.0, (double)z / 32.0);
                        endermite.setYawPitch((float)yaw * 360.0f / 256.0f, (float)pitch * 360.0f / 256.0f);
                        endermite.setHeadYaw((float)headYaw * 360.0f / 256.0f);
                        tracker.addEntityReplacement(endermite);
                    } else if (typeId == 101 || typeId == 255 || typeId == -1) {
                        packetWrapper.cancel();
                    }
                });
                this.handler(packetWrapper -> {
                    int entityId = packetWrapper.get(Type.VAR_INT, 0);
                    short typeId = packetWrapper.get(Type.UNSIGNED_BYTE, 0);
                    EntityTracker tracker = packetWrapper.user().get(EntityTracker.class);
                    tracker.getClientEntityTypes().put(entityId, Entity1_10Types.getTypeFromId(typeId, false));
                    tracker.sendMetadataBuffer(entityId);
                });
                this.handler(wrapper -> {
                    List<Metadata> metadataList = wrapper.get(Types1_7_6_10.METADATA_LIST, 0);
                    int entityId = wrapper.get(Type.VAR_INT, 0);
                    EntityTracker tracker = wrapper.user().get(EntityTracker.class);
                    if (tracker.getEntityReplacement(entityId) != null) {
                        tracker.getEntityReplacement(entityId).updateMetadata(metadataList);
                    } else if (tracker.getClientEntityTypes().containsKey(entityId)) {
                        MetadataRewriter.transform(tracker.getClientEntityTypes().get(entityId), metadataList);
                    } else {
                        wrapper.cancel();
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.SPAWN_PAINTING, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.STRING);
                this.handler(packetWrapper -> {
                    Position position = packetWrapper.read(Type.POSITION);
                    packetWrapper.write(Type.INT, position.getX());
                    packetWrapper.write(Type.INT, position.getY());
                    packetWrapper.write(Type.INT, position.getZ());
                });
                this.map((Type)Type.UNSIGNED_BYTE, Type.INT);
                this.handler(packetWrapper -> {
                    int entityId = packetWrapper.get(Type.VAR_INT, 0);
                    EntityTracker tracker = packetWrapper.user().get(EntityTracker.class);
                    tracker.getClientEntityTypes().put(entityId, Entity1_10Types.EntityType.PAINTING);
                    tracker.sendMetadataBuffer(entityId);
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.SPAWN_EXPERIENCE_ORB, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.INT);
                this.map(Type.INT);
                this.map(Type.INT);
                this.map(Type.SHORT);
                this.handler(packetWrapper -> {
                    int entityId = packetWrapper.get(Type.VAR_INT, 0);
                    EntityTracker tracker = packetWrapper.user().get(EntityTracker.class);
                    tracker.getClientEntityTypes().put(entityId, Entity1_10Types.EntityType.EXPERIENCE_ORB);
                    tracker.sendMetadataBuffer(entityId);
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.SPAWN_GLOBAL_ENTITY, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.BYTE);
                this.map(Type.INT);
                this.map(Type.INT);
                this.map(Type.INT);
                this.handler(packetWrapper -> {
                    int entityId = packetWrapper.get(Type.VAR_INT, 0);
                    EntityTracker tracker = packetWrapper.user().get(EntityTracker.class);
                    tracker.getClientEntityTypes().put(entityId, Entity1_10Types.EntityType.LIGHTNING);
                    tracker.sendMetadataBuffer(entityId);
                });
            }
        });
    }
}

