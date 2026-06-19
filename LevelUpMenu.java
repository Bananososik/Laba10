import greenfoot.Actor;
import greenfoot.Color;
import greenfoot.GreenfootImage;
import java.util.List;

public class LevelUpMenu extends Actor {
    private final Player player;
    private List<UpgradeSystem.Def> choices;

    public LevelUpMenu(Player p) {
        this.player = p;
        GreenfootImage placeholder = SpriteFactory.createButtonPanel(
            "УРОВЕНЬ ПОВЫШЕН",
            "выберите 1 из 3 улучшений",
            1080,
            320,
            new Color(18, 18, 28, 235),
            new Color(255, 220, 120)
        );
        setImage(placeholder);
        choices = player.rollUpgradeChoices(3);
    }

    public void addedToWorld(greenfoot.World wn) {
        if (choices == null || choices.isEmpty()) {
            wn.addObject(new LUButton("Перекалибровка", "+1 к максимальному HP", "СЛУЖЕБНЫЙ ВЫБОР", this, "fallback_recalibrate"), getX(), getY() + 18);
            return;
        }

        int x = getX();
        int y = getY();

        int[] xs = {x - 360, x, x + 360};
        for (int i = 0; i < choices.size() && i < 3; i++) {
            UpgradeSystem.Def def = choices.get(i);
            String subtitle = def.rarity.title + " • " + typeTitle(def.type);
            wn.addObject(new LUButton(def.name, def.description, subtitle, this, def.id), xs[i], y + 92);
        }
    }

    public void applyChoice(String id) {
        MyWorld w = (MyWorld) getWorld();
        if (w == null) {
            return;
        }

        if ("fallback_recalibrate".equals(id)) {
            player.applyFallbackUpgrade();
        } else {
            player.applyUpgradeItem(id);
        }
        // remove buttons and menu
        java.util.List<LUButton> buttons = w.getObjects(LUButton.class);
        for (LUButton btn : buttons) {
            w.removeObject(btn);
        }
        w.removeObject(this);
        w.setPaused(false);
        w.notifyHit();
    }

    private String typeTitle(UpgradeSystem.Type t) {
        switch (t) {
            case ACTIVE:
                return "АКТИВНОЕ";
            case PASSIVE:
                return "ПАССИВНОЕ";
            case SPECIAL:
                return "ОСОБОЕ";
            default:
                return "НЕИЗВЕСТНО";
        }
    }
}
