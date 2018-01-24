package lol.clann.object.nbt.utils;

import static lol.clann.Utils.ReflectionUtils.getRefClass;
import lol.clann.object.Refection.RefClass;
import lol.clann.object.Refection.RefField;
import lol.clann.object.Refection.RefMethod;
import org.bukkit.entity.Player;

public final class PacketUtils {

    public static final PacketUtils packetUtils = new PacketUtils();

    private PacketUtils() {
    }

    public RefClass classCraftPlayer = getRefClass("{cb}.entity.CraftPlayer, {CraftPlayer}");
    public RefClass classEntityPlayer = getRefClass("{nms}.EntityPlayer, {nm}.entity.player.EntityPlayer, {EntityPlayer}");
    public RefClass classEntityPlayerMP = getRefClass("{nm}.entity.player.EntityPlayerMP, {EntityPlayerMP}, null");
    public RefClass classNetworkElement = getRefClass(
            "{nms}.NetServerHandler,"
            + "{nms}.PlayerConnection,"
            + "{nm}.network.NetServerHandler,"
            + "{nm}.network.NetHandlerPlayServer,"
            + "{NetworkElement}"
    );

    private RefField fieldNetworkElement = classEntityPlayerMP != null
            ? classEntityPlayerMP.findField(classNetworkElement)
            : classEntityPlayer.findField(classNetworkElement);

    private RefMethod sendPacket = classNetworkElement.findMethodByParams("{nms}.Packet, {nm}.network.Packet, {nm}.network.packet.Packet, {Packet}");
    private RefMethod getHandle = classCraftPlayer.findMethodByReturnType(classEntityPlayer);

    public void sendPacket(Player player, Object packet) {
        Object entityPlayer = getHandle.of(player).call();
        Object network = fieldNetworkElement.of(entityPlayer).get();
        sendPacket.of(network).call(packet);
    }
}
