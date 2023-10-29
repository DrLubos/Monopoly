package hraciaPlocha.policka;

import hraci.Hrac;
import zaklad.Spravca;

public class Start implements Policko {

    @Override
    public String getNazov() {
        return "START";
    }

    @Override
    public void akcia(Hrac hrac, Spravca spravca) {
    }
}