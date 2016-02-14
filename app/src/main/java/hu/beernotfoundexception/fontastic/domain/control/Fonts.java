package hu.beernotfoundexception.fontastic.domain.control;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Fonts {
    public static List<String> acceptedFonts = Arrays.asList(
            "Akiza Sans",
            "Anonymus Pro",
            "Autonym",
            "Averia Sans",
            "Averia Serif",
            "Comic Relief",
            "Courier Code",
            "Coval",
            "Crimson",
            "Cursive Sans",
            "Cursive Serif",
            "Dancing Script",
            "Deja Vu Sans",
            "Deja Vu Serif",
            "Fanwood",
            "Fibel Nord",
            "Free Universal",
            "GFS Artemisia",
            "Katamotz Ikasi",
            "Khmer OS Classic",
            "Liberation Sans",
            "Liberation Serif",
            "Libre Bodoni",
            "Petit Formal Script",
            "Quattrocento",
            "Segoe UI Symbol"
    );

    public static String getRandomAcceptedFont() {
        return Fonts.acceptedFonts.get(new Random().nextInt(Fonts.acceptedFonts.size()));
    }
}
