package lol.clann.object.command;

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
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 指令框架,依赖注解
 */
public abstract class CustomCommand implements CommandExecutor {

    Map<String, SubCommand> subCommands = new HashMap();
    final String cmd;

    public CustomCommand(JavaPlugin plugin, String cmd) {
        this.cmd = cmd;
        register(getClass());
        plugin.getCommand(cmd).setExecutor(this);
    }

    /*
     * 指令样例1:/cmd testCommand args
     * @SubCommandAnnotation(needOp = true, permission = "", mustPlayer =
     * false,args = "(player)", des = "查看玩家afk状态")
     * public void testCommand1(CommandSender sender, String[] args) {
     * code
     * }
     */
 /*
     * 指令样例2:/cmd testCommand2 args
     *
     * @SubCommandAnnotation(needOp = true, permission = "", mustPlayer = true,
     * args = "(player)", des = "查看玩家afk状态")
     * public void testCommand2(Player player, String[] args) {
     * code
     * }
     */
    /**
     * 注册类
     *
     * @param clazz
     */
    public void register(Class clazz) {
        SubCommandAnnotation sub;
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(SubCommandAnnotation.class)) {
                method.setAccessible(true);
                sub = method.getAnnotation(SubCommandAnnotation.class);
                if (sub.des().length != sub.args().length) {
                    //指令描述描述不成对
                    throw new SubCommandAnnotationException("指令与描述不成对");
                }
                subCommands.put(method.getName().toLowerCase(), new SubCommand(sub, method));
            }
        }
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
        for (int i = 0; i < sub.annotation.args().length; i++) {
            sendMessage(sender, sub, sub.annotation.args()[i], "    &6-&a " + sub.annotation.des()[i]);
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
        Tellraw c = new Tellraw(("&6/" + cmd + " &b" + sub.method.getName() + "&3 " + format).trim());
        c.getChatStyle().setClickEvent(ClickEvent.Action.suggest_command, ("/" + cmd + " " + sub.method.getName() + " " + format).trim());
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
                    if (!checkParams(sub, temp)) {
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
    private boolean checkParams(SubCommand sub, String[] args) {
        String[] ss = sub.annotation.args();
        for (int i = 0; i < ss.length; i++) {
            if (checkParams(ss[i], args)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkParams(String param, String[] args) {
        String parms[] = param.split(" ");
        int n = 0;
        for (int i = 0; i < parms.length; i++) {
            if (parms[i].isEmpty() || (parms[i].startsWith("[") && parms[i].endsWith("]"))) {
                continue;
            }
            if (!parms[i].startsWith("(") && !parms[i].equals(args[i])) {
                return false;
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
            if (sub.annotation.mustPlayer()) {
                sub.method.invoke(this, (Player) sender, args);
            } else {
                sub.method.invoke(this, sender, args);
            }
        } catch (Throwable e) {
            e.printStackTrace();//增加控制台打印
            Throwable ce;
            String info = "指令执行过程中抛出";
            ce = e;
            while (ce != null && !(ce instanceof CEException)) {
                ce = ce.getCause();
            }
            if (ce != null) {
                info += "异常:" + ce.getMessage();
            } else {
                info += e.getClass().getName() + "异常:" + e.getMessage();
            }
            sender.sendMessage(info);
        }
    }

}
