import greenfoot.Greenfoot;

/**
 * Мини-босс: крупный, живучий враг с радиальными залпами.
 * Наследуется от Enemy, поэтому пули игрока, авто-прицел и начисление
 * опыта работают без изменений. Переопределяет движение и смерть.
 */
public class Boss extends Enemy {
    private int barrageCooldown = 120;
    private int contactCooldown = 0;

    public Boss() {
        super(false);
        hp = 60;
        setImage(SpriteFactory.createBossSprite(72));
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

        if (barrageCooldown > 0) {
            barrageCooldown--;
        }
        if (contactCooldown > 0) {
            contactCooldown--;
        }

        double dx = player.getX() - px;
        double dy = player.getY() - py;
        double dist = Math.hypot(dx, dy);

        // медленное упорное преследование с разрешением столкновений со стенами по осям
        if (dist > 1.0) {
            double speed = 0.7;
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

        // контактный урон
        if (contactCooldown <= 0 && getIntersectingObjects(Player.class).size() > 0) {
            player.takeDamage(2);
            contactCooldown = 30;
        }

        // радиальный залп
        if (barrageCooldown <= 0) {
            fireBarrage();
            barrageCooldown = 110 + Greenfoot.getRandomNumber(40);
        }

        if (hp <= 0) {
            die();
        }
    }

    private void fireBarrage() {
        int bullets = 12;
        double speed = 3.2;
        double spin = Greenfoot.getRandomNumber(100) / 100.0; // лёгкий случайный поворот залпа
        for (int i = 0; i < bullets; i++) {
            double a = Math.PI * 2 * i / bullets + spin;
            double vx = Math.cos(a) * speed;
            double vy = Math.sin(a) * speed;
            getWorld().addObject(new EnemyBullet(vx, vy, 160, 1), getX(), getY());
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
            // босс даёт большую награду опытом
            p.onEnemyKilled(60);
        }
        // гарантированный дроп: лечение + ещё один случайный бонус
        world.addObject(new Pickup("heal"), getX(), getY());
        world.addObject(new Pickup(Pickup.randomKind()), getX() + 24, getY());
        world.removeObject(this);
        world.notifyHit();
    }
}
