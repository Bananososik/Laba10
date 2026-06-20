import greenfoot.Actor;
import greenfoot.Color;
import greenfoot.Greenfoot;
import greenfoot.GreenfootImage;

/**
 * Враг. Тип (Kind) задаёт поведение, характеристики и спрайт.
 * Характеристики масштабируются от уровня игрока (баланс).
 *  - CHASER  : быстрый ближний преследователь, мало HP
 *  - RANGER  : держит дистанцию, стреляет
 *  - FAST    : очень быстрый, хрупкий «рой»
 *  - TANK    : медленный, много HP, больно бьёт в ближнем
 *  - BOMBER  : подлетает и взрывается, выпуская веер пуль
 */
public class Enemy extends Actor {
    public enum Kind { CHASER, RANGER, FAST, TANK, BOMBER }

    protected int hp;
    protected int maxHp;
    protected int attackCooldown = 0;
    protected double px, py;
    protected final Kind kind;
    protected final boolean ranged;

    // характеристики, вычисленные из типа и уровня
    protected double moveSpeed;
    protected int contactDamage;
    protected int xpReward;
    protected int dropChance;

    /** Совместимость со старым кодом: булев ranged → CHASER/RANGER. */
    public Enemy(boolean ranged) {
        this(ranged ? Kind.RANGER : Kind.CHASER, 1);
    }

    public Enemy() {
        this(Kind.CHASER, 1);
    }

    public Enemy(Kind kind, int level) {
        this.kind = kind;
        this.ranged = (kind == Kind.RANGER);
        configure(level);
        setImage(SpriteFactory.createEnemySprite(spriteSize(), kindKey()));
    }

    /** Конструктор для подклассов (Boss), которые сами задают спрайт/характеристики. */
    protected Enemy(Kind kind, int level, boolean bossPlaceholder) {
        this.kind = kind;
        this.ranged = false;
        this.hp = 1;
        this.maxHp = 1;
    }

    private void configure(int level) {
        double lvl = Math.max(1, level);
        // плавный масштаб: +12% HP и +6% урона за уровень
        double hpScale = 1.0 + 0.12 * (lvl - 1);
        switch (kind) {
            case CHASER:
                maxHp = (int) Math.round(3 * hpScale);
                moveSpeed = 0.95;
                contactDamage = 1;
                xpReward = 5;
                dropChance = 12;
                break;
            case RANGER:
                maxHp = (int) Math.round(2 * hpScale);
                moveSpeed = 0.6;
                contactDamage = 1;
                xpReward = 7;
                dropChance = 16;
                break;
            case FAST:
                maxHp = (int) Math.round(2 * hpScale);
                moveSpeed = 1.7;
                contactDamage = 1;
                xpReward = 6;
                dropChance = 12;
                break;
            case TANK:
                maxHp = (int) Math.round(10 * hpScale);
                moveSpeed = 0.5;
                contactDamage = 2;
                xpReward = 14;
                dropChance = 28;
                break;
            case BOMBER:
                maxHp = (int) Math.round(4 * hpScale);
                moveSpeed = 1.1;
                contactDamage = 2;
                xpReward = 10;
                dropChance = 22;
                break;
            default:
                maxHp = 3;
                moveSpeed = 0.9;
                contactDamage = 1;
                xpReward = 5;
                dropChance = 12;
        }
        hp = maxHp;
    }

    /** Какие типы могут появляться на данном уровне (разнообразие растёт). */
    public static Kind rollKind(int level) {
        int r = Greenfoot.getRandomNumber(100);
        if (level < 3) {
            return r < 70 ? Kind.CHASER : Kind.RANGER;
        }
        if (level < 6) {
            if (r < 45) return Kind.CHASER;
            if (r < 70) return Kind.RANGER;
            if (r < 90) return Kind.FAST;
            return Kind.TANK;
        }
        // 6+: весь набор
        if (r < 32) return Kind.CHASER;
        if (r < 54) return Kind.RANGER;
        if (r < 74) return Kind.FAST;
        if (r < 88) return Kind.TANK;
        return Kind.BOMBER;
    }

    protected int spriteSize() {
        switch (kind) {
            case TANK: return 46;
            case FAST: return 28;
            case BOMBER: return 36;
            default: return 34;
        }
    }

    protected String kindKey() {
        switch (kind) {
            case RANGER: return "ranger";
            case FAST: return "fast";
            case TANK: return "tank";
            case BOMBER: return "bomber";
            default: return "chaser";
        }
    }

    public void act() {
        if (getWorld() == null) {
            return;
        }
        MyWorld w = (MyWorld) getWorld();
        if (w.isPaused()) {
            return;
        }

        Player player = w.getPlayer();
        if (player == null) {
            return;
        }

        if (px == 0 && py == 0) {
            px = getX();
            py = getY();
        }

        if (attackCooldown > 0) {
            attackCooldown--;
        }

        double dx = player.getX() - px;
        double dy = player.getY() - py;
        double dist = Math.hypot(dx, dy);

        if (kind == Kind.RANGER) {
            actRanger(dx, dy, dist);
        } else if (kind == Kind.BOMBER) {
            actBomber(player, dx, dy, dist);
        } else {
            actChaser(dx, dy, dist);
            if (getIntersectingObjects(Player.class).size() > 0) {
                player.takeDamage(contactDamage);
            }
        }

        if (hp <= 0) {
            die();
        }
    }

    private void actChaser(double dx, double dy, double dist) {
        if (dist > 0.5) {
            px += (dx / dist) * moveSpeed;
            py += (dy / dist) * moveSpeed;
            setLocation((int) Math.round(px), (int) Math.round(py));
        }
    }

    private void actRanger(double dx, double dy, double dist) {
        if (dist > 280) {
            px += (dx / Math.max(1.0, dist)) * moveSpeed;
            py += (dy / Math.max(1.0, dist)) * moveSpeed;
        } else if (dist < 180) {
            px -= (dx / Math.max(1.0, dist)) * moveSpeed;
            py -= (dy / Math.max(1.0, dist)) * moveSpeed;
        }
        setLocation((int) Math.round(px), (int) Math.round(py));

        if (attackCooldown <= 0 && dist < 440) {
            double projSpeed = 4.0;
            double vx = (dx / Math.max(1.0, dist)) * projSpeed;
            double vy = (dy / Math.max(1.0, dist)) * projSpeed;
            getWorld().addObject(new EnemyBullet(vx, vy, 110, 1), getX(), getY());
            attackCooldown = 75 + Greenfoot.getRandomNumber(30);
        }
    }

    private void actBomber(Player player, double dx, double dy, double dist) {
        // быстро сближается; вблизи — детонирует веером пуль
        if (dist > 0.5) {
            px += (dx / dist) * moveSpeed;
            py += (dy / dist) * moveSpeed;
            setLocation((int) Math.round(px), (int) Math.round(py));
        }
        if (dist < 36 || getIntersectingObjects(Player.class).size() > 0) {
            detonate(player);
        }
    }

    private void detonate(Player player) {
        player.takeDamage(contactDamage);
        int bullets = 8;
        double speed = 3.0;
        for (int i = 0; i < bullets; i++) {
            double a = Math.PI * 2 * i / bullets;
            getWorld().addObject(new EnemyBullet(Math.cos(a) * speed, Math.sin(a) * speed, 90, 1), getX(), getY());
        }
        hp = 0; // взрыв уничтожает бомбера
    }

    /** Награда за убийство, выпадение бонуса и удаление актёра. Босс переопределяет. */
    protected void die() {
        MyWorld world = (MyWorld) getWorld();
        if (world == null) {
            return;
        }
        Player p = world.getPlayer();
        if (p != null) {
            p.onEnemyKilled(xpReward);
        }
        maybeDropPickup(world, dropChance);
        world.removeObject(this);
        world.notifyHit();
    }

    /** С шансом chancePercent роняет случайный бонус на месте смерти. */
    protected void maybeDropPickup(MyWorld world, int chancePercent) {
        if (Greenfoot.getRandomNumber(100) < chancePercent) {
            world.addObject(new Pickup(Pickup.randomKind()), getX(), getY());
        }
    }

    public void takeDamage(int amount) {
        hp -= amount;
        // короткая вспышка попадания
        if (hp > 0) {
            GreenfootImage img = SpriteFactory.createEnemySprite(spriteSize(), kindKey());
            img.setColor(new Color(255, 255, 255, 90));
            img.fillOval(0, 0, img.getWidth(), img.getHeight());
            setImage(img);
        }
    }
}
