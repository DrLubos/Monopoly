package hraciaPlocha.policka.kupa;

public enum TypPozemku {
    FIALOVA(50),
    BIELA(50),
    RUZOVA(100),
    ORANZOVA(100),
    CERVENA(150),
    ZLTA(150),
    ZELENA(200),
    MODRA(200),
    DOPRAVA(Integer.MAX_VALUE),
    SPOLOCNOST(Integer.MAX_VALUE);
    private final int cenaVylepsenia;

    /**
     * Konstruktor enumu pre rozne typy nehnutelnosti
     * @param cenaVylepsenia Cena ktora je potrebna na to aby sa mohola vylepsit nehnutelnost (aby sa na nehnutelnosti postavil dom)
     */
    TypPozemku(int cenaVylepsenia) {
        this.cenaVylepsenia = cenaVylepsenia;
    }

    /**
     * Metoda sluzi na vratenie ceny vylepsenia
     * @return Vracia cenu ktora je potrebna na to aby sa mohola vylepsit nehnutelnost (aby sa na nehnutelnosti postavil dom)
     */
    public int getCenaVylepsenia() {
        return this.cenaVylepsenia;
    }
}