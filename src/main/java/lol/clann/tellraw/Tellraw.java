package lol.clann.tellraw;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lol.clann.Utils.StringUtil;
import lol.clann.api.CollectionsApi;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Tellraw implements Cloneable {

    private static Pattern COLOR_FORMAT = Pattern.compile("§([0-9a-fA-Fk-oK-ORr])");

    private String mText = "";
    private final ChatStyle mStyle = new ChatStyle();
    private ArrayList<Tellraw> mExtras = new ArrayList<>();

    public Tellraw() {
        this.mText = "";
    }

    /**
     * 创建一个实例
     * <p>
     * 不将最后的样式设置为主样式</p>
     */
    public Tellraw(String pText) {
        this(pText, false);
    }

    /**
     * 创建一个实例
     *
     * @param pText 字符串
     * @param pSetLastStyleAsMainStyle 是否将字符串最后的样式设置为主样式
     * <p>
     * 不将最后的样式设置为主样式</p>
     */
    public Tellraw(String pText, boolean pSetLastStyleAsMainStyle) {
        this.setText(pText, pSetLastStyleAsMainStyle);
    }

    /**
     * 创建一个实例
     *
     * @param pText 字符串
     * @param pTextColor 设置主样式颜色
     * <p>
     * 不将最后的样式设置为主样式</p>
     */
    public Tellraw(String pText, Color pTextColor) {
        this(pText, pTextColor, false);
    }

    /**
     * 创建一个实例
     *
     * @param pText 字符串
     * @param pTextColor 设置主样式颜色
     * @param pSetLastStyleAsMainStyle 是否将字符串最后的样式设置为主样式
     * <p>
     * 将最后的样式设置为主样式</p>
     */
    public Tellraw(String pText, Color pTextColor, boolean pSetLastStyleAsMainStyle) {
        this.setText(pText);
        this.mStyle.setColor(pTextColor);
    }

    /**
     * 设置此实例的文本内容,将会重置主样式
     * <p>
     * 不将最后的样式设置为主样式</p>
     */
    public void setText(String pText) {
        this.setText(pText, false);
    }

    /**
     * 设置此实例的文本内容,将会重置主样式
     *
     * @param pText 内容
     * @param pSetLastStyleAsMainStyle 是否将字符串最后的样式设置为主样式
     */
    public Tellraw setText(String pText, boolean pSetLastStyleAsMainStyle) {
        this.mText = "";
        this.mExtras.clear();
        this.mStyle.clearFormat();
        pText = ChatColor.translateAlternateColorCodes('&', pText);

        if (StringUtil.isEmpty(pText) || Tellraw.COLOR_FORMAT.matcher(pText).replaceAll("").equals(pText)) {
            this.mText = pText;
            return this;
        }

        ArrayList<Tellraw> tChildExtras = new ArrayList<>();
        ChatStyle tmpStyle = new ChatStyle();
        if (pSetLastStyleAsMainStyle) {
            tmpStyle.setDefaultStyle();
        }
        Matcher matcher = COLOR_FORMAT.matcher(pText);
        int pos = 0;
        Color tColor;
        Format tFormat;
        while (matcher.find()) {
            char cf = matcher.group(1).charAt(0);
            if (matcher.start() != pos) {
                Tellraw te = new Tellraw();
                te.mText = pText.substring(pos, matcher.start());
                te.mStyle.copyFrom(tmpStyle);
                tChildExtras.add(te);
            }
            if ((tColor = Color.getColor(cf)) != null) {
                tmpStyle.setColor(tColor);
                tmpStyle.turnOffAllFormat();
            } else if ((tFormat = Format.getStyle(cf)) != null) {
                if (tFormat == Format.reset) {
                    tmpStyle.setColor(Color.white);
                    tmpStyle.turnOffAllFormat();
                } else {
                    tmpStyle.addFormat(tFormat);
                }
            }
            pos = matcher.end();
        }

        String leftText = pText.substring(pos, pText.length());
        if (!leftText.isEmpty()) {
            Tellraw te = new Tellraw();
            te.mText = leftText;
            te.mStyle.copyFrom(tmpStyle);
            tChildExtras.add(te);
        }

        if (pSetLastStyleAsMainStyle) {
            this.mStyle.copyColorAndFormat(tmpStyle);
        } else {
            if (tChildExtras.isEmpty()) {
                return this;
            }
            Tellraw tRaw = tChildExtras.remove(0);
            this.mStyle.copyColorAndFormat(tRaw.mStyle);
            this.mText = tRaw.mText;
        }
        for (Tellraw sRaw : tChildExtras) {
            this.addExtra(sRaw);
        }
        return this;
    }

    /**
     * 设置此实例的文件,不更改主样式,并将字体直接设置到test中
     * <p>
     * 设置的内容仍然会进行以 '§' 字符开头的颜色字符串的过滤</p>
     */
    public void setRawText(String pRawText) {
        this.mText = COLOR_FORMAT.matcher(pRawText).replaceAll("");
    }

    /**
     * 添加一段字符串到现有的Tellraw中
     *
     * @param pText 字符串
     * @return 自身
     */
    public Tellraw addText(String pText) {
        if (StringUtil.isNotEmpty(pText)) {
            Tellraw tExtra = new Tellraw(pText);
            this.addExtra(tExtra);
        }
        return this;
    }

    /**
     * 直接添加字符串到最后一个Tellraw中
     * <p>
     * 函数会对字符串进行颜色字符的过滤
     * </p>
     *
     * @param pText 文件
     * @return 本身
     */
    public Tellraw addRawText(String pText) {
        if (StringUtil.isNotEmpty(pText)) {
            this.getLastExtra().mText += pText;
        }
        return this;
    }

    /**
     * 将该Json的颜色和样式设置成最后一个子Json的颜色和样式
     *
     * @return 自身
     */
    public Tellraw setLastJsonExtraAsMainStyle() {
        Tellraw tRaw = this.getLastExtra();
        if (tRaw != this) {
            this.mStyle.copyColorAndFormat(tRaw.mStyle);
        }
        return this;
    }

    /**
     * 返回最末端的Json
     */
    public Tellraw getLastExtra() {
        Tellraw tRaw = this;
        while (!tRaw.mExtras.isEmpty()) {
            tRaw = tRaw.mExtras.get(tRaw.mExtras.size() - 1);
        }
        return tRaw;
    }

    /**
     * 获取当前实例的样式
     */
    public ChatStyle getChatStyle() {
        return this.mStyle;
    }

    /**
     * 设置当前实例的样式
     * <p>
     * 此操作只复制参数中的样式到本实例的样式中</p>
     */
    public Tellraw setChatStyle(ChatStyle pStyle) {
        if (this.mStyle != pStyle) {
            this.mStyle.copyFrom(pStyle);
        }
        return this;
    }

    /**
     * 获取此实例的Json串
     */
    public JSONObject getJson() {
        JSONObject tJson = new JSONObject();
        tJson.put("text", this.mText);
        for (Map.Entry<Format, Boolean> sEntry : this.mStyle.getFormats().entrySet()) {
            if (sEntry.getKey().getJsonLabel() == null || sEntry.getValue() == null) {
                continue;
            }
            // Format sStyle
            tJson.put(sEntry.getKey().getJsonLabel(), sEntry.getValue());
        }
        Color tColor = this.mStyle.getColor();
        if (tColor != null) {
            tJson.put("color", tColor.name());
        }
        if (this.mStyle.getClickEvent() != null) {
            JSONObject tSubJson = new JSONObject();
            ClickEvent pEvent = this.mStyle.getClickEvent();
            tSubJson.put("action", pEvent.mAction.name());
            tSubJson.put("value", pEvent.mValue);
            tJson.put("clickEvent", tSubJson);
        }
        if (this.mStyle.getHoverEvent() != null) {
            JSONObject tSubJson = new JSONObject();
            HoverEvent pEvent = this.mStyle.getHoverEvent();
            tSubJson.put("action", pEvent.mAction.name());
            tSubJson.put("value", pEvent.mValue);
            tJson.put("hoverEvent", tSubJson);
        }
        if (!this.mExtras.isEmpty()) {
            JSONArray tJsonArray = new JSONArray();
            for (Tellraw sExtra : this.mExtras) {
                tJsonArray.add(sExtra.getJson());
            }
            tJson.put("extra", tJsonArray);
        }
        return tJson;
    }

    @Override
    public String toString() {
        return this.getJson().toJSONString();
    }

    @Override
    public Tellraw clone() {
        Tellraw tClone = new Tellraw();
        tClone.mText = this.mText;
        tClone.mStyle.copyFrom(this.mStyle);
        for (Tellraw sRaw : this.mExtras) {
            tClone.addExtra(sRaw.clone());
        }
        return tClone;
    }

    /**
     * 获取只由颜色和格式字符,文本组成的字符内容
     */
    public String toSimpleString() {
        StringBuilder tStr = new StringBuilder();
        this.toSimpleString(tStr, new ChatStyle(Color.white), "");
        return tStr.toString();
    }

    private void toSimpleString(StringBuilder pSBuilder, ChatStyle pLastStyle, String pLastText) {
        Color tLastColor = pLastStyle.mColor;
        Color tColor = this.mStyle.getColor();
        pLastStyle.mColor = tColor == null ? Color.white : tColor;

        boolean appendReset = false;
        HashSet<Format> tAppendFormats = new HashSet<>();
        HashSet<Format> tFormats = new HashSet<>(pLastStyle.mFormats.keySet());
        pLastStyle.mFormats.clear();
        boolean tNowEnable, tLastEnable;
        for (Format sFormat : Format.values()) {
            if (sFormat == Format.reset) {
                continue;
            }
            tLastEnable = tFormats.remove(sFormat);
            Boolean t = this.mStyle.getFormat(sFormat);
            tNowEnable = t == null ? false : t.booleanValue();
            if (tLastEnable && !tNowEnable) {
                appendReset = true;
            }

            if (tNowEnable) {
                pLastStyle.mFormats.put(sFormat, Boolean.TRUE);
                if (!tLastEnable || pLastText.isEmpty()) {
                    tAppendFormats.add(sFormat);
                }
            }
        }

        if (!this.mText.isEmpty()) {
            tColor = pLastStyle.mColor;
            if (pSBuilder.length() == 0) {
                if (tColor == Color.white) {
                    tColor = null;
                }
                if (appendReset) {
                    appendReset = false;
                }
            }

            if (appendReset) {
                if (tColor == Color.white) {
                    tColor = null;
                }
                if (tColor != null) {
                    appendReset = false;
                    tLastColor = null;
                } else {
                    pSBuilder.append(Format.reset);
                }

            }
            if (tColor != null && (appendReset || (tLastColor != tColor || pLastText.isEmpty()))) {
                pSBuilder.append(tColor.toString());
            }

            for (Format sForamt : tAppendFormats) {
                pSBuilder.append(sForamt.toString());
            }
            pSBuilder.append(this.mText);
        }

        pLastText = this.mText;
        for (Tellraw sRaw : this.mExtras) {
            sRaw.toSimpleString(pSBuilder, pLastStyle, pLastText);
            pLastText = sRaw.mText;
        }
    }

    /**
     * 添加一个子Json,并返回自身
     */
    public Tellraw addExtra(Tellraw pRaw) {
        pRaw.mStyle.setParentStyle(this.mStyle);
        this.mExtras.add(pRaw);
        return this;
    }

    /**
     * 将Json消息发送给玩家<br />
     * 如果CommandSender不是玩家,将只发送非Json的消息串
     */
    public void sendToPlayer(CommandSender pSender) {
        if (pSender == null) {
            pSender = Bukkit.getConsoleSender();
        }
        if (pSender instanceof Player) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + pSender.getName() + " " + this.toString());
        } else {
            pSender.sendMessage(this.toSimpleString());
        }
        // System.out.println(this.toString());
    }

    /**
     * 将Json消息发送给全体在线玩家
     *
     * @param pExcludePlayers 排除的玩家
     */
    public void boardcast(CommandSender... pExcludePlayers) {// boardcast
        ArrayList<CommandSender> tExcludePlayers;
        if (pExcludePlayers != null && pExcludePlayers.length > 0) {
            tExcludePlayers = CollectionsApi.asList(pExcludePlayers);
        } else {
            tExcludePlayers = new ArrayList<>(0);
        }
        String message = this.toString();
        for (Player sPlayer : Bukkit.getOnlinePlayers()) {
            if (!tExcludePlayers.contains(sPlayer)) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + sPlayer.getName() + " " + message);
            }
        }
    }

    /**
     * 替换变量为指定的Tellraw<br>
     * 注意此操作将使被替换的字符串所在的Tellraw的ClickEvent和HoverEvent失效
     * <p>
     * 1. 操作会替换所有变量<br>
     * 2. 使用{@link Tellraw#addText(String)}添加的两个字符串拼接而成的变量不会被替换
     * </p>
     *
     * @param pPlaceHolder 占位符
     * @param pExtra 替换成的JsonExtra
     */
    public Tellraw replace(String pPlaceHolder, Tellraw pExtra) {
        if (pPlaceHolder == null || pPlaceHolder.isEmpty() || pExtra == null) {
            return this;
        }

        for (Tellraw sExtra : this.mExtras) {
            sExtra.replace(pPlaceHolder, pExtra);
        }
        ArrayList<Tellraw> tSplit = new ArrayList<>();
        int length = pPlaceHolder.length(), tMatchIndex = -length, tLastMatch = 0;
        while ((tMatchIndex = this.mText.indexOf(pPlaceHolder, tMatchIndex + length)) != -1) {
            if (tLastMatch < tMatchIndex) {
                Tellraw tRaw = new Tellraw();
                tRaw.mText = this.mText.substring(tLastMatch, tMatchIndex);
                tSplit.add(tRaw);
            }
            tLastMatch = tMatchIndex;
            tSplit.add(pExtra);
        }
        if (tSplit.isEmpty()) {
            return this;
        }
        tLastMatch += length;
        if (tLastMatch < this.mText.length() - 1) {
            Tellraw tRaw = new Tellraw();
            tRaw.mText = this.mText.substring(tLastMatch);
            tSplit.add(tRaw);
        }

        this.mStyle.setClickEvent(null);
        this.mStyle.setHoverEvent(null);
        this.mText = "";
        for (Tellraw sExtra : tSplit) {
            if (sExtra != pExtra) {
                this.addExtra(sExtra);
            } else {
                this.addExtra(sExtra.clone());
            }
        }
        return this;
    }

}
