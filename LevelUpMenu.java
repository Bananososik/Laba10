import greenfoot.Actor;
import greenfoot.Color;
import greenfoot.GreenfootImage;
import java.util.List;

/**
 * Меню выбора улучшения при повышении уровня (ТЗ, разделы 6-7).
 * Затемнённый фон на весь экран, по центру — заголовок и до 3 карточек.
 * Снизу — кнопки «Перебор» (reroll) и «Пропустить» (skip).
 * Пока меню на экране, мир стоит на паузе (Player.openLevelUpMenuIfNeeded).
 */
public class LevelUpMenu extends Actor {
    private final Player player;
    private List<UpgradeSystem.Def> choices;
    private int rerollsLeft;
    private int appearTick = 0;

    public LevelUpMenu(Player p) {
        this.player = p;
        this.rerollsLeft = p.getRerollsPerLevel();
        this.choices = player.rollUpgradeChoices(3);
        drawOverlay();
    }

    private void drawOverlay() {
        // Размер берём по миру после добавления; до этого — типовой размер арены.
        int w = getWorld() != null ? getWorld().getWidth() : 1280;
        int h = getWorld() != null ? getWorld().getHeight() : 800;
        GreenfootImage img = new GreenfootImage(w, h);

        // затемнённый фон
        img.setColor(new Color(6, 8, 14, 205));
        img.fillRect(0, 0, w, h);

        // неоновая рамка по краю
        img.setColor(new Color(120, 200, 255, 60));
        img.drawRect(6, 6, w - 13, h - 13);
        img.drawRect(9, 9, w - 19, h - 19);

        // заголовок
        GreenfootImage title = new GreenfootImage("УРОВЕНЬ ПОВЫШЕН", 56, new Color(255, 225, 140), new Color(0, 0, 0, 0));
        GreenfootImage sub = new GreenfootImage("Выберите 1 из 3 улучшений", 26, new Color(200, 215, 255), new Color(0, 0, 0, 0));
        img.drawImage(title, (w - title.getWidth()) / 2, h / 2 - 250);
        img.drawImage(sub, (w - sub.getWidth()) / 2, h / 2 - 188);

        setImage(img);
    }

    public void addedToWorld(greenfoot.World wn) {
        drawOverlay(); // перерисовать под реальный размер мира
        spawnButtons();
    }

    private void spawnButtons() {
        int x = getX();
        int y = getY();

        if (choices == null || choices.isEmpty()) {
            // улучшений не осталось — служебный выбор
            getWorld().addObject(
                LUButton.control(this, "fallback_recalibrate", "ПЕРЕКАЛИБРОВКА", "+1 к максимальному HP"),
                x, y);
            return;
        }

        int n = Math.min(3, choices.size());
        int gap = 380;
        int startX = x - (n - 1) * gap / 2;
        for (int i = 0; i < n; i++) {
            getWorld().addObject(new LUButton(this, choices.get(i)), startX + i * gap, y);
        }

        // нижние кнопки управления
        String rerollSub = rerollsLeft > 0 ? "осталось: " + rerollsLeft : "недоступно";
        getWorld().addObject(LUButton.control(this, "__reroll__", "ПЕРЕБОР", rerollSub), x - 200, y + 200);
        getWorld().addObject(LUButton.control(this, "__skip__", "ПРОПУСТИТЬ", "+опыт, +1 HP"), x + 200, y + 200);
    }

    private void clearButtons() {
        for (LUButton btn : getWorld().getObjects(LUButton.class)) {
            getWorld().removeObject(btn);
        }
    }

    /** Клик по карточке/служебной кнопке. */
    public void onButton(String id) {
        if ("__reroll__".equals(id)) {
            if (rerollsLeft > 0) {
                rerollsLeft--;
                choices = player.rollUpgradeChoices(3);
                clearButtons();
                spawnButtons();
            }
            return;
        }
        if ("__skip__".equals(id)) {
            player.applySkipReward();
            finish();
            return;
        }
        if ("fallback_recalibrate".equals(id)) {
            player.applyFallbackUpgrade();
        } else {
            player.applyUpgradeItem(id);
        }
        finish();
    }

    private void finish() {
        MyWorld w = (MyWorld) getWorld();
        if (w == null) {
            return;
        }
        clearButtons();
        w.removeObject(this);
        // снимет паузу или откроет следующее меню, если в очереди ещё уровни
        player.onLevelUpResolved();
        w.notifyHit();
    }

    public void act() {
        // лёгкая «дышащая» подсветка заголовка
        appearTick++;
    }
}
