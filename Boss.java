import greenfoot.Greenfoot;
import greenfoot.GreenfootImage;
import greenfoot.Color;

/**
 * Босс. Наследуется от Enemy (пули игрока, авто-прицел и опыт работают без правок).
 * Тип задаёт паттерн атаки и спрайт:
 *  - GUNNER  : радиальные залпы по кругу
 *  - SPINNER : вращающаяся спираль пуль
 *  - WARLORD : призывает миньонов и таранит игрока
 * HP и награда масштабируются от уровня игрока.
 */
public class Boss extends Enemy {
    public enum Kind { GUNNER, SPINNER, WARLORD }

    private final Kind bossKind;
    private final int level;
    private int bossHp;
    private int bossMaxHp;
    private int barrageCooldown = 90;
    private int contactCooldown = 0;
    private int summonCooldown = 200;
    private double spinAngle = 0.0;
    private final int size;

    public Boss(Kind kind, int level) {
        super(Enemy.Kind.TANK, level, true); // используем boss-конструктор Enemy
        this.bossKind = kind;
        this.level = Math.max(1, level);

        int baseHp;
        switch (kind) {
            case GUNNER:  baseHp = 70;  size = 72; break;
            case SPINNER: baseHp = 85;  size = 76; break;
            case WARLORD: baseHp = 110; size = 84; break;
            default:      baseHp = 70;  size = 72;
        }
        bossMaxHp = (int) Math.round(baseHp * (1.0 + 0.18 * (this.level - 1)));
        bossHp = bossMaxHp;
        // синхронизируем с полем hp из Enemy, чтобы пули наносили урон
        this.hp = bossHp;
        setImage(SpriteFactory.createBossSprite(size, bossKindKey()));
    }

    private String bossKindKey() {
        switch (bossKind) {
            case SPINNER: return "spinner";
            case WARLORD: return "warlord";
            default: return "gunner";
        }
    }

    @Override
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

        if (barrageCooldown > 0) barrageCooldown--;
        if (contactCooldown > 0) contactCooldown--;
        if (summonCooldown > 0) summonCooldown--;

        double dx = player.getX() - px;
        double dy = player.getY() - py;
        double dist = Math.hypot(dx, dy);

        double speed = (bossKind == Kind.WARLORD) ? 1.1 : 0.7;
        moveWithWalls(dx, dy, dist, speed);

        if (contactCooldown <= 0 && getIntersectingObjects(Player.class).size() > 0) {
            player.takeDamage(bossKind == Kind.WARLORD ? 3 : 2);
            contactCooldown = 30;
        }

        switch (bossKind) {
            case GUNNER:  actGunner(); break;
            case SPINNER: actSpinner(); break;
            case WARLORD: actWarlord(dx, dy, dist); break;
        }

        if (hp <= 0) {
            die();
        }
    }

    private void moveWithWalls(double dx, double dy, double dist, double speed) {
        if (dist <= 1.0) {
            return;
        }
        double stepX = (dx / dist) * speed;
        double stepY = (dy / dist) * speed;

        double oldPx = px;
        px += stepX;
        setLocation((int) Math.round(px), (int) Math.round(py));
        if (isTouching(Wall.class)) {
            px = oldPx;
            setLocation((int) Math.round(px), (int) Math.round(py));
        }
        double oldPy = py;
        py += stepY;
        setLocation((int) Math.round(px), (int) Math.round(py));
        if (isTouching(Wall.class)) {
            py = oldPy;
            setLocation((int) Math.round(px), (int) Math.round(py));
        }
    }

    private void actGunner() {
        if (barrageCooldown <= 0) {
            int bullets = 14;
            double speed = 3.2;
            double spin = Greenfoot.getRandomNumber(100) / 100.0;
            for (int i = 0; i < bullets; i++) {
                double a = Math.PI * 2 * i / bullets + spin;
                getWorld().addObject(new EnemyBullet(Math.cos(a) * speed, Math.sin(a) * speed, 170, 1), getX(), getY());
            }
            barrageCooldown = 95 + Greenfoot.getRandomNumber(40);
        }
    }

    private void actSpinner() {
        // непрерывная вращающаяся спираль: по 2 пули каждые ~6 кадров
        if (barrageCooldown <= 0) {
            double speed = 3.4;
            for (int arm = 0; arm < 2; arm++) {
                double a = spinAngle + Math.PI * arm;
                getWorld().addObject(new EnemyBullet(Math.cos(a) * speed, Math.sin(a) * speed, 150, 1), getX(), getY());
            }
            spinAngle += Math.toRadians(22);
            barrageCooldown = 6;
        }
    }

    private void actWarlord(double dx, double dy, double dist) {
        // призыв миньонов
        if (summonCooldown <= 0) {
            int count = 2 + Greenfoot.getRandomNumber(2);
            for (int i = 0; i < count; i++) {
                int ox = getX() + (Greenfoot.getRandomNumber(120) - 60);
                int oy = getY() + (Greenfoot.getRandomNumber(120) - 60);
                if (getWorld().getObjectsAt(ox, oy, Wall.class).isEmpty()) {
                    getWorld().addObject(new Enemy(Enemy.Kind.FAST, level), ox, oy);
                }
            }
            summonCooldown = 260 + Greenfoot.getRandomNumber(120);
        }
        // редкий прицельный выстрел
        if (barrageCooldown <= 0 && dist < 520) {
            double speed = 4.2;
            double d = Math.max(1.0, dist);
            getWorld().addObject(new EnemyBullet((dx / d) * speed, (dy / d) * speed, 160, 2), getX(), getY());
            barrageCooldown = 70 + Greenfoot.getRandomNumber(30);
        }
    }

    @Override
    public void takeDamage(int amount) {
        hp -= amount;
        // вспышка попадания
        if (hp > 0) {
            GreenfootImage img = SpriteFactory.createBossSprite(size, bossKindKey());
            img.setColor(new Color(255, 255, 255, 70));
            img.fillOval(0, 0, size, size);
            setImage(img);
        }
    }

    @Override
    protected void die() {
        MyWorld world = (MyWorld) getWorld();
        if (world == null) {
            return;
        }
        Player p = world.getPlayer();
        if (p != null) {
            p.onEnemyKilled(50 + level * 8);
        }
        // щедрый дроп
        world.addObject(new Pickup("heal"), getX(), getY());
        world.addObject(new Pickup(Pickup.randomKind()), getX() + 26, getY());
        world.addObject(new Pickup(Pickup.randomKind()), getX() - 26, getY());
        world.removeObject(this);
        world.notifyHit();
    }
}
