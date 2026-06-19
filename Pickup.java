import greenfoot.Actor;
import greenfoot.Greenfoot;
import greenfoot.GreenfootImage;

/**
 * Подбираемый бонус, выпадающий из врагов и босса.
 * Игрок собирает его, касаясь — эффект применяется мгновенно.
 * Виды: "heal" (лечение), "shield" (временный щит), "xp" (опыт).
 */
public class Pickup extends Actor {
    private final String kind;
    private int life = 600; // ~10 секунд до исчезновения
    private int bob = 0;

    public Pickup(String kind) {
        this.kind = kind;
        setImage(SpriteFactory.createPickupSprite(22, kind));
    }

    public static String randomKind() {
        int r = Greenfoot.getRandomNumber(100);
        if (r < 50) {
            return "heal";
        } else if (r < 80) {
            return "xp";
        }
        return "shield";
    }

    public void act() {
        MyWorld w = (MyWorld) getWorld();
        if (w == null || w.isPaused()) {
            return;
        }

        // лёгкое мерцание, чтобы бонус было видно на полу
        bob++;
        if (bob % 30 == 0) {
            setImage(SpriteFactory.createPickupSprite(bob % 60 == 0 ? 24 : 20, kind));
        }

        Player player = (Player) getOneIntersectingObject(Player.class);
        if (player != null) {
            applyTo(player);
            w.removeObject(this);
            return;
        }

        life--;
        if (life <= 0) {
            w.removeObject(this);
        }
    }

    private void applyTo(Player player) {
        switch (kind) {
            case "heal":
                player.heal(3);
                break;
            case "shield":
                player.grantShield(300); // ~5 секунд неуязвимости
                break;
            case "xp":
                player.addXp(15);
                break;
            default:
                break;
        }
    }
}
