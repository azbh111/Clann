package lol.clann.object.command;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import lol.clann.api.PlayerApi;
import lol.clann.tellraw.ClickEvent;
import lol.clann.tellraw.Tellraw;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 样例
 * 方法名为指令的第一个参数,/op example
 *
 * @SubCommandAnnotation(
 * mustPlayer = true, //是否必须玩家才能执行,可省,默认false
 * needOp = false, //是否必须有OP权限(控制台也有)才能执行,可省,默认true
 * permission = "essentials.fly", //执行此指令所需权限,可省
 * args = "(player) [count]", //无参时 args可省略,圆括号表示参数,方括号表示可省参数
 * des = "导出当前世界[指定世界]所有TileEntity信息") //指令描述信息,不可省
 * 当有可省参数,导致指令描述困难时,des可以是字符串数组,但数量必须偶数,以指令+描述的形式成对存在,如:
 * des = {"玩家","指令描述1","玩家 数量","指令描述2"}
 * public void example(CommandSender sender, String[] args) {
 *  //args包含输入指令的第二个参数到结尾的所有参数
 * }
 *
 *
 */
public abstract class CustomCommand implements CommandExecutor {

    Map<String, SubCommand> subCommands = new HashMap();
    final String cmd;

    public CustomCommand(JavaPlugin plugin, String cmd) {
        this.cmd = cmd;
        SubCommandAnnotation sub;
        for (Method method : getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(SubCommandAnnotation.class)) {
                method.setAccessible(true);
                sub = method.getAnnotation(SubCommandAnnotation.class);
                if (sub.des().length > 1 && sub.des().length % 2 != 0) {
                    //des描述不成对
                    throw new SubCommandAnnotationException("当存在多个des描述时,应成对存在:" + sub.des().length);
                }
                subCommands.put(method.getName().toLowerCase(), new SubCommand(sub, method));
            }
        }
        plugin.getCommand(cmd).setExecutor(this);
    }

    public String getCmd() {
        return cmd;
    }

    /**
     * 子命令
     *
     * @param sender
     * @param sub
     */
    public final void help(CommandSender sender, SubCommand sub) {
        if (sub.annotation.des().length == 1) {
            sendMessage(sender, sub, " &6-&a " + sub.annotation.des()[0]);
        } else {
            //描述成对存在,分别发送
            for (int i = 0; i + 1 < sub.annotation.des().length; i += 2) {
                sendMessage(sender, sub, sub.annotation.des()[i], " &6-&a " + sub.annotation.des()[i + 1]);
            }
        }
    }

    /**
     * 直接传入指令格式,用于指令有可省参数而描述困难时
     *
     * @param sender
     * @param message
     * @param sub
     * @param format
     */
    private void sendMessage(CommandSender sender, SubCommand sub, String format, String message) {
        Tellraw c = new Tellraw(("&6/" + cmd + " &b" + sub.method.getName() + " &3" + format).trim());
        c.getChatStyle().setClickEvent(ClickEvent.Action.suggest_command, ("/" + cmd + " " + sub.method.getName() + " " + format).trim());
        c.addText(message).sendToPlayer(sender);
    }

    /**
     * 直接使用SubCommand生成指令格式
     *
     * @param sender
     * @param message
     * @param sub
     */
    private void sendMessage(CommandSender sender, SubCommand sub, String message) {
        Tellraw c = new Tellraw(("&6/" + cmd + " &b" + sub.method.getName() + " &3" + sub.annotation.args()).trim());
        c.getChatStyle().setClickEvent(ClickEvent.Action.suggest_command, ("/" + cmd + " " + sub.method.getName() + " " + sub.annotation.args()).trim());
        c.addText(message).sendToPlayer(sender);
    }

    /**
     * 所有子命令
     *
     * @param sender
     */
    public final void help(CommandSender sender) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6===============" + "&b帮助" + "&6==============="));
        subCommands.values().stream().forEach((sub) -> {
            if (hasPermission(sub, sender, false)) {
                help(sender, sub);
            }
        });
    }

    /**
     * 帮助指令
     *
     * @param sender
     */
    public final void help0(CommandSender sender) {
        Tellraw c = new Tellraw("&6/" + cmd + " &bhelp");
        c.getChatStyle().setClickEvent(ClickEvent.Action.run_command, "/" + cmd + " help");
        c.addText(" &6-&a 帮助").sendToPlayer(sender);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            help0(sender);
            return true;
        } else if (args[0].equalsIgnoreCase("help")) {
            help(sender);
            return true;
        } else {
            String[] temp = new String[args.length - 1];
            for (int i = 1; i < args.length; i++) {
                temp[i - 1] = args[i];
            }
            SubCommand sub = subCommands.get(args[0].toLowerCase());
            if (sub != null) {
                try {
                    if (!checkParms(sub, temp)) {
                        sender.sendMessage("格式错误");
                        help(sender, sub);
                        return true;
                    }
                    handle(sub, sender, temp);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    help(sender, sub);
                }
            } else {
                otherSubCommands(sender, args);
            }
            return true;
        }
    }

    /**
     * 处理其他子命令 参数含有子命令..
     *
     * @param sender
     * @param args 子命令 子命令参数...
     */
    public void otherSubCommands(CommandSender sender, String args[]) {
        help(sender);
    }

    /**
     * 权限判断
     *
     * @param sub
     * @param sender
     * @param notice
     *
     * @return
     */
    private boolean hasPermission(SubCommand sub, CommandSender sender, boolean notice) {
        if (sub.annotation.mustPlayer() && !PlayerApi.isPlayer(sender)) {
            if (notice) {
                sender.sendMessage("控制台无法执行此指令");
            }
            return false;
        }
        if (sub.annotation.needOp() && !sender.isOp()) {
            if (notice) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4权限不足"));
            }
            return false;
        }
        if (!sub.annotation.permission().isEmpty() && !sender.hasPermission(sub.annotation.permission())) {
            if (notice) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4权限不足:&2" + sub.annotation.permission()));
            }
            return false;
        }
        return true;
    }

    /**
     * 格式检查
     *
     * @param sub
     * @param args
     *
     * @return
     */
    private boolean checkParms(SubCommand sub, String[] args) {
        if (sub.annotation.args().isEmpty()) {
            return true;
        }
        String parms[] = sub.annotation.args().split(" ");
        int n = 0;
        for (int i = 0; i < parms.length; i++) {
            if (parms[i].isEmpty() || (parms[i].startsWith("[") && parms[i].endsWith("]"))) {
                continue;
            }
            n++;
        }
        return args.length >= n;
    }

    /**
     * 反射执行
     *
     * @param sub
     * @param sender
     * @param args
     *
     * @throws Exception
     */
    private void handle(SubCommand sub, CommandSender sender, String[] args) {
        if (!hasPermission(sub, sender, true)) {
            return;
        }
        try {
            sub.method.invoke(this, sender, args);
        } catch (Throwable e) {
            e.printStackTrace();
            sender.sendMessage("指令执行过程中抛出异常" + e.getClass().getName() + "  " + e.getMessage());
        }
    }

}
