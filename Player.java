import greenfoot.Actor;
import greenfoot.Color;
import greenfoot.Greenfoot;
import greenfoot.GreenfootImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Player extends Actor {
    private static final int MAX_ACTIVE_SLOTS = 6;
    private static final int MAX_PASSIVE_SLOTS = 6;

    private int hp = 10;
    private int maxHp = 10;
    private int damageCooldown = 0;
    private int facingX = 1;
    private int facingY = 0;

    private int fireCooldown = 0;
    private int baseFireRate = 18;
    private int shieldTicks = 0;

    private int xp = 0;
    private int level = 1;
    private int xpToNext = 10;

    private double px, py;
    private int animTick = 0;
    private int lastShotTick = 0;
    private int killCount = 0;
    private int laserCooldown = 0;
    private int novaCooldown = 0;
    private double regenAccumulator = 0.0;
    private int pendingLevelUps = 0;
    private int rerollsPerLevel = 1;

    // Core stats
    private double damageMult = 1.0;
    private double critChance = 0.05;
    private double critMult = 1.5;
    private double bulletSpeedMult = 1.0;
    private double rangeMult = 1.0;
    private double regenPerSecond = 0.0;
    private double xpMult = 1.0;
    private double dodgeChance = 0.0;
    private int luck = 0;
    private int bonusProjectiles = 0;
    private int pierceCount = 0;
    private int ricochetCount = 0;

    // Build toggles
    private boolean hasDrone = false;
    private boolean hasNova = false;
    private boolean hasLaser = false;
    private boolean hasKillWave = false;
    private boolean hasHoming = false;
    private boolean hasBerserk = false;
    private boolean hasChainBullets = false;
    private boolean hasVampirism = false;
    private boolean hasChaosDrive = false;

    private final Map<String, Integer> stacks = new HashMap<>();
    private final Set<String> activeOwned = new HashSet<>();
    private final Set<String> passiveOwned = new HashSet<>();
    private final Set<String> specialOwned = new HashSet<>();

    private static final int SHIP_SIZE = 48;
    private double aimAngleDeg = 0.0;   // куда повёрнут корабль (носом)
    private boolean thrusting = false;  // есть ли движение (для пламени)

    public Player() {
        setImage(SpriteFactory.createPlayerShip(SHIP_SIZE, 0, false, false, false));
    }

    public void act() {
        if (getWorld() == null) {
            return;
        }

        if (px == 0 && py == 0) {
            px = getX();
            py = getY();
        }

        if (!((MyWorld) getWorld()).isPaused()) {
            handleMovement();
            handleAutoFire();
            handlePassiveEffects();
        }

        animate();
        setLocation((int) Math.round(px), (int) Math.round(py));

        if (damageCooldown > 0) {
            damageCooldown--;
        }
        if (shieldTicks > 0) {
            shieldTicks--;
        }
        if (laserCooldown > 0) {
            laserCooldown--;
        }
        if (novaCooldown > 0) {
            novaCooldown--;
        }

        if (hp <= 0) {
            ((MyWorld) getWorld()).restartLevel();
        }
    }

    private void handlePassiveEffects() {
        if (regenPerSecond > 0.0) {
            regenAccumulator += regenPerSecond / 60.0;
            while (regenAccumulator >= 1.0) {
                if (hp < maxHp) {
                    hp++;
                    ((MyWorld) getWorld()).notifyHit();
                }
                regenAccumulator -= 1.0;
            }
        }
    }

    private void handleMovement() {
        int dx = 0;
        int dy = 0;

        if (Greenfoot.isKeyDown("a") || Greenfoot.isKeyDown("left")) {
            dx--;
        }
        if (Greenfoot.isKeyDown("d") || Greenfoot.isKeyDown("right")) {
            dx++;
        }
        if (Greenfoot.isKeyDown("w") || Greenfoot.isKeyDown("up")) {
            dy--;
        }
        if (Greenfoot.isKeyDown("s") || Greenfoot.isKeyDown("down")) {
            dy++;
        }

        if (dx != 0 || dy != 0) {
            facingX = dx;
            facingY = dy;
        }
        thrusting = (dx != 0 || dy != 0);

        double speed = 2.0;
        double newPx = px + dx * speed;
        double newPy = py;

        int testX = (int) Math.round(newPx);
        int testY = (int) Math.round(newPy);
        setLocation(testX, testY);
        if (isTouching(Wall.class)) {
            setLocation((int) Math.round(px), (int) Math.round(py));
        } else {
            px = newPx;
        }

        newPy = py + dy * speed;
        testX = (int) Math.round(px);
        testY = (int) Math.round(newPy);
        setLocation(testX, testY);
        if (isTouching(Wall.class)) {
            setLocation((int) Math.round(px), (int) Math.round(py));
        } else {
            py = newPy;
        }
    }

    private void handleAutoFire() {
        if (fireCooldown > 0) {
            fireCooldown--;
            return;
        }

        List<Enemy> enemies = getWorld().getObjects(Enemy.class);
        if (enemies.isEmpty()) {
            fireCooldown = getCurrentFireRate();
            return;
        }

        Enemy nearest = findNearestEnemy(enemies);
        if (nearest == null) {
            fireCooldown = getCurrentFireRate();
            return;
        }

        int sx = getX();
        int sy = getY();
        double dx = nearest.getX() - sx;
        double dy = nearest.getY() - sy;
        double dist = Math.hypot(dx, dy);
        if (dist == 0) {
            dist = 1;
        }

        double speed = 6.0 * bulletSpeedMult;
        double baseAngle = Math.atan2(dy, dx);
        aimAngleDeg = Math.toDegrees(baseAngle); // корабль смотрит на цель при стрельбе

        int projectileCount = 1 + bonusProjectiles;
        if (hasChainBullets) {
            projectileCount += 1;
        }

        for (int i = 0; i < projectileCount; i++) {
            double angleOffset = (i - (projectileCount - 1) / 2.0) * Math.toRadians(8);
            double angle = baseAngle + angleOffset;
            int damage = rollDamage();
            int life = (int) Math.round(80 * rangeMult);
            Bullet b = new Bullet(
                Math.cos(angle) * speed,
                Math.sin(angle) * speed,
                life,
                damage,
                pierceCount,
                ricochetCount,
                hasHoming || hasChainBullets
            );
            if (hasChainBullets) {
                b.withKind("chain");
            }
            getWorld().addObject(b, sx, sy);
        }

        if (hasDrone) {
            double droneAngle = baseAngle + Math.PI / 2.0;
            int life = (int) Math.round(60 * rangeMult);
            getWorld().addObject(
                new Bullet(Math.cos(droneAngle) * speed * 0.8, Math.sin(droneAngle) * speed * 0.8, life, rollDamage(), pierceCount, ricochetCount, hasHoming).withKind("drone"),
                sx,
                sy
            );
        }

        if (hasLaser && laserCooldown <= 0) {
            int life = (int) Math.round(130 * rangeMult);
            getWorld().addObject(
                new Bullet(Math.cos(baseAngle) * speed * 1.4, Math.sin(baseAngle) * speed * 1.4, life, (int) Math.round(rollDamage() * 2.4), pierceCount + 2, ricochetCount, hasHoming).withKind("laser"),
                sx,
                sy
            );
            laserCooldown = 70;
        }

        if (hasNova && novaCooldown <= 0) {
            for (int i = 0; i < 8; i++) {
                double a = Math.PI * 2 * i / 8.0;
                int life = (int) Math.round(55 * rangeMult);
                getWorld().addObject(
                    new Bullet(Math.cos(a) * speed * 0.9, Math.sin(a) * speed * 0.9, life, rollDamage(), pierceCount, ricochetCount, hasHoming).withKind("nova"),
                    sx,
                    sy
                );
            }
            novaCooldown = 110;
        }

        lastShotTick = animTick;
        fireCooldown = getCurrentFireRate();
    }

    private Enemy findNearestEnemy(List<Enemy> enemies) {
        Enemy nearest = null;
        double best = Double.MAX_VALUE;
        int sx = getX();
        int sy = getY();
        for (Enemy e : enemies) {
            double d = Math.hypot(e.getX() - sx, e.getY() - sy);
            if (d < best) {
                best = d;
                nearest = e;
            }
        }
        return nearest;
    }

    private int getCurrentFireRate() {
        return Math.max(4, baseFireRate);
    }

    private int rollDamage() {
        double damage = 1.0 * damageMult;
        if (hasBerserk && hp <= Math.max(1, maxHp / 2)) {
            damage *= 1.5;
        }
        if (Greenfoot.getRandomNumber(1000) < (int) Math.round(critChance * 1000.0)) {
            damage *= critMult;
        }
        if (hasChaosDrive) {
            double variance = 0.75 + Greenfoot.getRandomNumber(90) / 100.0;
            damage *= variance;
        }
        return Math.max(1, (int) Math.round(damage));
    }

    private void animate() {
        animTick++;
        boolean shooting = (animTick - lastShotTick) < 6;
        int frame = (animTick / 5) % 4;

        // если стоим на месте и не стреляли — корабль смотрит по направлению последнего движения
        if (!thrusting && !shooting) {
            aimAngleDeg = Math.toDegrees(Math.atan2(facingY, facingX));
        }

        setImage(SpriteFactory.createPlayerShip(SHIP_SIZE, frame, thrusting, shooting, hasShield()));
        setRotation((int) Math.round(aimAngleDeg));
    }

    public List<UpgradeSystem.Def> rollUpgradeChoices(int count) {
        ArrayList<UpgradeSystem.Def> result = new ArrayList<>();
        ArrayList<UpgradeSystem.Def> pool = new ArrayList<>();
        for (UpgradeSystem.Def def : UpgradeSystem.ALL) {
            if (canOffer(def)) {
                pool.add(def);
            }
        }

        for (int i = 0; i < count && !pool.isEmpty(); i++) {
            UpgradeSystem.Def picked = weightedPick(pool);
            if (picked == null) {
                break;
            }
            result.add(picked);
            pool.remove(picked);
        }

        return result;
    }

    private UpgradeSystem.Def weightedPick(List<UpgradeSystem.Def> pool) {
        ArrayList<Integer> weights = new ArrayList<>();
        int sum = 0;
        for (UpgradeSystem.Def def : pool) {
            int w = getWeight(def);
            weights.add(w);
            sum += Math.max(0, w);
        }

        if (sum <= 0) {
            return null;
        }

        int roll = Greenfoot.getRandomNumber(sum);
        int acc = 0;
        for (int i = 0; i < pool.size(); i++) {
            acc += Math.max(0, weights.get(i));
            if (roll < acc) {
                return pool.get(i);
            }
        }
        return pool.get(pool.size() - 1);
    }

    private int getWeight(UpgradeSystem.Def def) {
        if (def.rarity == UpgradeSystem.Rarity.SPECIAL) {
            return isSpecialUnlocked(def.id) ? 12 : 0;
        }

        double w = def.rarity.baseWeight;
        switch (def.rarity) {
            case COMMON:
                w *= Math.max(0.20, 1.0 - 0.08 * luck);
                break;
            case UNCOMMON:
                w *= (1.0 + 0.05 * luck);
                break;
            case RARE:
                w *= (1.0 + 0.12 * luck);
                break;
            case EPIC:
                w *= (1.0 + 0.18 * luck);
                break;
            case LEGENDARY:
                w *= (1.0 + 0.25 * luck);
                break;
            default:
                break;
        }
        return Math.max(1, (int) Math.round(w));
    }

    private boolean canOffer(UpgradeSystem.Def def) {
        int currentStacks = stackOf(def.id);
        if (currentStacks >= def.maxStacks) {
            return false;
        }

        if (def.type == UpgradeSystem.Type.ACTIVE && currentStacks == 0 && activeOwned.size() >= MAX_ACTIVE_SLOTS) {
            return false;
        }
        if (def.type == UpgradeSystem.Type.PASSIVE && currentStacks == 0 && passiveOwned.size() >= MAX_PASSIVE_SLOTS) {
            return false;
        }
        if (def.type == UpgradeSystem.Type.SPECIAL && !isSpecialUnlocked(def.id)) {
            return false;
        }
        return true;
    }

    private boolean isSpecialUnlocked(String id) {
        if ("chain_bullets".equals(id)) {
            return hasUpgrade("scatter_shot") && hasUpgrade("ricochet");
        }
        if ("vampirism".equals(id)) {
            return hasUpgrade("regen") && hasUpgrade("shield");
        }
        if ("chaos_drive".equals(id)) {
            return hasUpgrade("luck") && stackOf("luck") >= 2;
        }
        return false;
    }

    private int stackOf(String id) {
        Integer n = stacks.get(id);
        return n == null ? 0 : n;
    }

    private boolean hasUpgrade(String id) {
        return stackOf(id) > 0;
    }

    public void applyUpgradeItem(String id) {
        UpgradeSystem.Def def = UpgradeSystem.byId(id);
        if (def == null || !canOffer(def)) {
            return;
        }

        stacks.put(id, stackOf(id) + 1);
        if (def.type == UpgradeSystem.Type.ACTIVE) {
            activeOwned.add(id);
        } else if (def.type == UpgradeSystem.Type.PASSIVE) {
            passiveOwned.add(id);
        } else {
            specialOwned.add(id);
        }

        switch (id) {
            case "rapid_burst":
                baseFireRate = Math.max(4, baseFireRate - 2);
                break;
            case "scatter_shot":
                bonusProjectiles += 1;
                break;
            case "drone_orbit":
                hasDrone = true;
                break;
            case "nova_pulse":
                hasNova = true;
                break;
            case "laser_array":
                hasLaser = true;
                break;

            case "damage_core":
                damageMult += 0.10;
                break;
            case "crit_chance":
                critChance += 0.05;
                break;
            case "crit_damage":
                critMult += 0.20;
                break;
            case "bullet_speed":
                bulletSpeedMult += 0.15;
                break;
            case "range_boost":
                rangeMult += 0.20;
                break;
            case "regen":
                regenPerSecond += 0.6;
                break;
            case "shield":
                shieldTicks += 240;
                break;
            case "dodge":
                dodgeChance += 0.06;
                break;
            case "xp_amp":
                xpMult += 0.15;
                break;
            case "luck":
                luck += 1;
                break;
            case "pierce":
                pierceCount += 1;
                break;
            case "ricochet":
                ricochetCount += 1;
                break;
            case "low_hp_berserk":
                hasBerserk = true;
                break;
            case "kill_wave":
                hasKillWave = true;
                break;
            case "homing":
                hasHoming = true;
                break;

            case "chain_bullets":
                hasChainBullets = true;
                break;
            case "vampirism":
                hasVampirism = true;
                break;
            case "chaos_drive":
                hasChaosDrive = true;
                break;
            default:
                break;
        }

        ((MyWorld) getWorld()).notifyHit();
    }

    public void onEnemyKilled(int baseXp) {
        killCount++;
        int gained = Math.max(1, (int) Math.round(baseXp * xpMult));
        addXp(gained);

        if (hasVampirism && hp < maxHp) {
            hp = Math.min(maxHp, hp + 1);
        }

        if (hasKillWave && killCount % 10 == 0 && getWorld() != null) {
            List<Enemy> enemies = getWorld().getObjects(Enemy.class);
            int sx = getX();
            int sy = getY();
            for (Enemy e : enemies) {
                double d = Math.hypot(e.getX() - sx, e.getY() - sy);
                if (d < 220) {
                    e.takeDamage(3);
                }
            }
        }

        ((MyWorld) getWorld()).notifyHit();
    }

    public void addXp(int amount) {
        xp += amount;
        boolean leveled = false;
        if (xp >= xpToNext) {
            do {
                xp -= xpToNext;
                level++;
                xpToNext = 10 + level * 5;
                pendingLevelUps++;
                leveled = true;
            } while (xp >= xpToNext);
        }
        if (leveled) {
            openLevelUpMenuIfNeeded();
        }
        ((MyWorld) getWorld()).notifyHit();
    }

    /** Открывает меню выбора улучшения и ставит игру на паузу, если меню ещё не открыто. */
    private void openLevelUpMenuIfNeeded() {
        MyWorld w = (MyWorld) getWorld();
        if (w == null || pendingLevelUps <= 0) {
            return;
        }
        if (!w.getObjects(LevelUpMenu.class).isEmpty()) {
            return; // меню уже на экране — оставшиеся уровни обработаются по очереди
        }
        w.setPaused(true);
        w.addObject(new LevelUpMenu(this), w.getWidth() / 2, w.getHeight() / 2);
    }

    /** Вызывается меню после выбора/пропуска: уменьшает очередь и открывает следующее меню при необходимости. */
    public void onLevelUpResolved() {
        if (pendingLevelUps > 0) {
            pendingLevelUps--;
        }
        if (pendingLevelUps > 0) {
            openLevelUpMenuIfNeeded();
        } else {
            MyWorld w = (MyWorld) getWorld();
            if (w != null) {
                w.setPaused(false);
            }
        }
    }

    public int getPendingLevelUps() {
        return pendingLevelUps;
    }

    public int getRerollsPerLevel() {
        return rerollsPerLevel;
    }

    public void takeDamage(int amount) {
        if (Greenfoot.getRandomNumber(1000) < (int) Math.round(dodgeChance * 1000.0)) {
            return;
        }
        if (shieldTicks > 0) {
            return;
        }
        if (damageCooldown > 0) {
            return;
        }

        hp -= amount;
        damageCooldown = 25;
        ((MyWorld) getWorld()).notifyHit();
    }

    public int getXp() {
        return xp;
    }

    public int getLevel() {
        return level;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getXpToNext() {
        return xpToNext;
    }

    public boolean hasShield() {
        return shieldTicks > 0;
    }

    public int getShieldTicks() {
        return shieldTicks;
    }

    /** Опорное значение для шкалы щита в HUD (макс. длительность одного щита). */
    public int getShieldMaxTicks() {
        return 300;
    }

    public void heal(int amount) {
        if (amount <= 0) {
            return;
        }
        hp = Math.min(maxHp, hp + amount);
        if (getWorld() != null) {
            ((MyWorld) getWorld()).notifyHit();
        }
    }

    public void grantShield(int ticks) {
        shieldTicks = Math.max(shieldTicks, ticks);
        if (getWorld() != null) {
            ((MyWorld) getWorld()).notifyHit();
        }
    }

    public int getActiveSlotCount() {
        return activeOwned.size();
    }

    public int getPassiveSlotCount() {
        return passiveOwned.size();
    }

    public int getMaxActiveSlots() {
        return MAX_ACTIVE_SLOTS;
    }

    public int getMaxPassiveSlots() {
        return MAX_PASSIVE_SLOTS;
    }

    public void applyFallbackUpgrade() {
        maxHp += 1;
        hp += 1;
        ((MyWorld) getWorld()).notifyHit();
    }

    /** Награда за пропуск выбора (ТЗ, раздел 6): немного опыта и лечение. */
    public void applySkipReward() {
        xp += Math.max(1, xpToNext / 5);
        hp = Math.min(maxHp, hp + 1);
        if (getWorld() != null) {
            ((MyWorld) getWorld()).notifyHit();
        }
    }
}