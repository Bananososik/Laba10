import java.util.Arrays;
import java.util.List;

public final class UpgradeSystem {
    public enum Type {
        ACTIVE,
        PASSIVE,
        SPECIAL
    }

    public enum Rarity {
        COMMON("ОБЫЧНЫЙ", 60),
        UNCOMMON("НЕОБЫЧНЫЙ", 25),
        RARE("РЕДКИЙ", 10),
        EPIC("ЭПИЧЕСКИЙ", 4),
        LEGENDARY("ЛЕГЕНДАРНЫЙ", 1),
        SPECIAL("ОСОБЫЙ", 0);

        public final String title;
        public final int baseWeight;

        Rarity(String title, int baseWeight) {
            this.title = title;
            this.baseWeight = baseWeight;
        }
    }

    public static final class Def {
        public final String id;
        public final String name;
        public final String description;
        public final Type type;
        public final Rarity rarity;
        public final int maxStacks;

        public Def(String id, String name, String description, Type type, Rarity rarity, int maxStacks) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.type = type;
            this.rarity = rarity;
            this.maxStacks = maxStacks;
        }
    }

    public static final List<Def> ALL = Arrays.asList(
        // Active weapons
        new Def("rapid_burst", "Скорострельный модуль", "+скорость стрельбы", Type.ACTIVE, Rarity.COMMON, 5),
        new Def("scatter_shot", "Мультишот", "+1 снаряд", Type.ACTIVE, Rarity.UNCOMMON, 4),
        new Def("drone_orbit", "Орбитальный дрон", "добавляет авто-выстрелы", Type.ACTIVE, Rarity.RARE, 1),
        new Def("nova_pulse", "Импульсная волна", "радиальный залп по кд", Type.ACTIVE, Rarity.EPIC, 1),
        new Def("laser_array", "Лазерный массив", "мощный луч по кд", Type.ACTIVE, Rarity.LEGENDARY, 1),

        // Passive
        new Def("damage_core", "Ядро урона", "+10% к урону", Type.PASSIVE, Rarity.COMMON, 8),
        new Def("crit_chance", "Наведение на уязвимость", "+5% шанс крита", Type.PASSIVE, Rarity.UNCOMMON, 6),
        new Def("crit_damage", "Усилитель критов", "+20% крит-урон", Type.PASSIVE, Rarity.UNCOMMON, 5),
        new Def("bullet_speed", "Ионный ускоритель", "+15% скорость пуль", Type.PASSIVE, Rarity.COMMON, 6),
        new Def("range_boost", "Дальнобойная оптика", "+20% дальность", Type.PASSIVE, Rarity.UNCOMMON, 5),
        new Def("regen", "Нанорегенерация", "регенерация HP", Type.PASSIVE, Rarity.COMMON, 5),
        new Def("shield", "Щит-перегрузка", "временный щит", Type.PASSIVE, Rarity.RARE, 4),
        new Def("dodge", "Маневровые сопла", "+6% уклонение", Type.PASSIVE, Rarity.UNCOMMON, 5),
        new Def("xp_amp", "Анализатор трофеев", "+15% опыт", Type.PASSIVE, Rarity.COMMON, 6),
        new Def("luck", "Квантовая удача", "выше шанс редких", Type.PASSIVE, Rarity.RARE, 4),
        new Def("pierce", "Пробивной сердечник", "+1 пробитие", Type.PASSIVE, Rarity.UNCOMMON, 4),
        new Def("ricochet", "Рикошетный сплав", "+1 рикошет", Type.PASSIVE, Rarity.EPIC, 2),
        new Def("low_hp_berserk", "Берсерк-режим", "+50% урона при низком HP", Type.PASSIVE, Rarity.RARE, 1),
        new Def("kill_wave", "Кинетический резонатор", "каждые 10 убийств: волна", Type.PASSIVE, Rarity.EPIC, 1),
        new Def("homing", "Система самонаведения", "пули наводятся", Type.PASSIVE, Rarity.LEGENDARY, 1),

        // Special - unlocked via conditions only
        new Def("chain_bullets", "Цепные пули", "Мультишот + Рикошет", Type.SPECIAL, Rarity.SPECIAL, 1),
        new Def("vampirism", "Вампирическая матрица", "Реген + Щит", Type.SPECIAL, Rarity.SPECIAL, 1),
        new Def("chaos_drive", "Двигатель хаоса", "случайные эффекты урона", Type.SPECIAL, Rarity.SPECIAL, 1)
    );

    private UpgradeSystem() {
    }

    public static Def byId(String id) {
        for (Def def : ALL) {
            if (def.id.equals(id)) {
                return def;
            }
        }
        return null;
    }
}