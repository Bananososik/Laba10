import greenfoot.Actor;
import greenfoot.Color;
import greenfoot.Greenfoot;
import greenfoot.GreenfootImage;
import greenfoot.MouseInfo;

/**
 * Кнопка в меню повышения уровня.
 * Два режима:
 *  - карточка улучшения (иконка, название, описание, цвет и свечение по редкости);
 *  - служебная кнопка (Перебор / Пропустить / Перекалибровка).
 * Подсветка при наведении, клик → применяет выбор через LevelUpMenu.
 */
public class LUButton extends Actor {
    private final String id;
    private final LevelUpMenu menu;

    // данные для отрисовки карточки
    private final boolean isCard;
    private final String title;
    private final String description;
    private final String subtitle;
    private final String rarityKey;
    private boolean lastHover = false;
    private boolean built = false;

    private LUButton(LevelUpMenu menu, String id, boolean isCard,
                     String title, String description, String subtitle, String rarityKey) {
        this.menu = menu;
        this.id = id;
        this.isCard = isCard;
        this.title = title;
        this.description = description;
        this.subtitle = subtitle;
        this.rarityKey = rarityKey;
        draw(false);
    }

    /** Карточка улучшения. */
    public LUButton(LevelUpMenu menu, UpgradeSystem.Def def) {
        this(menu, def.id, true, def.name, def.description,
             def.rarity.title + " • " + typeTitle(def.type), def.rarity.name());
    }

    /** Служебная кнопка (Перебор / Пропустить / Перекалибровка). */
    public static LUButton control(LevelUpMenu menu, String id, String title, String subtitle) {
        return new LUButton(menu, id, false, title, subtitle, "", "COMMON");
    }

    public void act() {
        MouseInfo mouse = Greenfoot.getMouseInfo();
        boolean hover = mouse != null && mouse.getActor() == this;
        if (hover != lastHover || !built) {
            draw(hover);
            lastHover = hover;
            built = true;
        }
        if (Greenfoot.mouseClicked(this)) {
            menu.onButton(id);
        }
    }

    private void draw(boolean hover) {
        if (isCard) {
            drawCard(hover);
        } else {
            drawControl(hover);
        }
    }

    private void drawCard(boolean hover) {
        int width = 340;
        int height = 360;
        Color rc = SpriteFactory.rarityColor(rarityKey);

        GreenfootImage img = new GreenfootImage(width, height);

        // фон карточки
        Color bg = hover ? new Color(34, 38, 54, 250) : new Color(22, 24, 36, 240);
        img.setColor(bg);
        img.fillRect(0, 0, width, height);

        // верхняя цветная полоса по редкости
        img.setColor(rc);
        img.fillRect(0, 0, width, 8);

        // свечение по редкости (ярче при наведении)
        GreenfootImage glow = SpriteFactory.createRarityGlow(width, height, rarityKey);
        if (hover) {
            img.drawImage(glow, 0, 0);
            img.drawImage(glow, 0, 0);
        } else {
            img.drawImage(glow, 0, 0);
        }

        // рамка
        img.setColor(hover ? rc : new Color(rc.getRed(), rc.getGreen(), rc.getBlue(), 150));
        img.drawRect(0, 0, width - 1, height - 1);
        img.drawRect(1, 1, width - 3, height - 3);

        // иконка в кружке
        int iconSize = 96;
        GreenfootImage icon = SpriteFactory.createUpgradeIcon(id, iconSize);
        img.drawImage(icon, (width - iconSize) / 2, 36);

        // тексты
        GreenfootImage titleText = wrapText(title, 30, new Color(255, 255, 255), width - 24);
        img.drawImage(titleText, (width - titleText.getWidth()) / 2, 150);

        GreenfootImage subText = new GreenfootImage(subtitle, 18, rc, new Color(0, 0, 0, 0));
        img.drawImage(subText, (width - subText.getWidth()) / 2, 200);

        GreenfootImage descText = wrapText(description, 20, new Color(205, 215, 240), width - 36);
        img.drawImage(descText, (width - descText.getWidth()) / 2, 248);

        setImage(img);
    }

    private void drawControl(boolean hover) {
        int width = 300;
        int height = 92;
        Color border = hover ? new Color(255, 220, 120) : new Color(160, 170, 190);
        Color fill = hover ? new Color(46, 52, 74, 245) : new Color(26, 28, 40, 235);

        GreenfootImage img = new GreenfootImage(width, height);
        img.setColor(fill);
        img.fillRect(0, 0, width, height);
        img.setColor(border);
        img.drawRect(0, 0, width - 1, height - 1);
        img.drawRect(1, 1, width - 3, height - 3);

        GreenfootImage titleText = new GreenfootImage(title, 28, new Color(255, 255, 255), new Color(0, 0, 0, 0));
        GreenfootImage subText = new GreenfootImage(subtitle, 18, new Color(200, 210, 235), new Color(0, 0, 0, 0));
        img.drawImage(titleText, (width - titleText.getWidth()) / 2, 16);
        img.drawImage(subText, (width - subText.getWidth()) / 2, 54);

        setImage(img);
    }

    /** Простой перенос строки по ширине (Greenfoot не умеет переносить сам). */
    private GreenfootImage wrapText(String text, int fontSize, Color color, int maxWidth) {
        String[] words = text.split(" ");
        java.util.List<String> lines = new java.util.ArrayList<>();
        StringBuilder cur = new StringBuilder();
        for (String word : words) {
            String trial = cur.length() == 0 ? word : cur + " " + word;
            GreenfootImage probe = new GreenfootImage(trial, fontSize, color, new Color(0, 0, 0, 0));
            if (probe.getWidth() > maxWidth && cur.length() > 0) {
                lines.add(cur.toString());
                cur = new StringBuilder(word);
            } else {
                cur = new StringBuilder(trial);
            }
        }
        if (cur.length() > 0) {
            lines.add(cur.toString());
        }

        int lineH = fontSize + 4;
        int w = Math.min(maxWidth, 1);
        GreenfootImage[] rendered = new GreenfootImage[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            rendered[i] = new GreenfootImage(lines.get(i), fontSize, color, new Color(0, 0, 0, 0));
            w = Math.max(w, rendered[i].getWidth());
        }
        GreenfootImage out = new GreenfootImage(Math.max(1, w), Math.max(1, lineH * lines.size()));
        for (int i = 0; i < rendered.length; i++) {
            out.drawImage(rendered[i], (w - rendered[i].getWidth()) / 2, i * lineH);
        }
        return out;
    }

    private static String typeTitle(UpgradeSystem.Type t) {
        switch (t) {
            case ACTIVE:  return "АКТИВНОЕ";
            case PASSIVE: return "ПАССИВНОЕ";
            case SPECIAL: return "ОСОБОЕ";
            default:      return "НЕИЗВЕСТНО";
        }
    }
}
