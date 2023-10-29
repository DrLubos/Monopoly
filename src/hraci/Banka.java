package hraci;

public abstract class Banka {
    private static int velkostPoplatkov;

    /**
     * Prida sa do velksoti poplatkov dalsi poplatok
     * @param vyskaPoplatku Velkost poplatku, ktory sa ma pridat
     */
    public static void pridajPoplatok(int vyskaPoplatku) {
        velkostPoplatkov += vyskaPoplatku;
    }

    /**
     * Pouziva sa na policku parkovanie a hracovy ktory tu zastal, tak sa mu odovzdavaju poplatky, ktore su v banke nazbierane
     * @return Suma na vyplatenie hracovy
     */
    public static int vyplatVelkostPoplatkov() {
        if (velkostPoplatkov <= 0) {
            velkostPoplatkov = 0;
            return 0;
        }
        int navrat = velkostPoplatkov;
        velkostPoplatkov = 0;
        return navrat;
    }
}
