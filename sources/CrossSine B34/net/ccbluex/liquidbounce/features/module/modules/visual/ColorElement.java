package net.ccbluex.liquidbounce.features.module.modules.visual;

import net.ccbluex.liquidbounce.features.value.IntegerValue;
import static net.ccbluex.liquidbounce.features.module.modules.visual.ColorMixer.regenerateColors;

public class ColorElement extends IntegerValue {

    public ColorElement(int counter, Material m, IntegerValue basis) {
        super("Color" + counter + "-" + m.getColorName(), 255, 0, 255, () -> (basis.get() >= counter));
    }

    public ColorElement(int counter, Material m) {
        super("Color" + counter + "-" + m.getColorName(), 255, 0, 255);
    }

    @Override
    protected void onChanged(final Integer oldValue, final Integer newValue) {
        regenerateColors(true);
    }

    enum Material {
        RED("Red"),
        GREEN("Green"),
        BLUE("Blue");

        private final String colName;
        Material(String name) {
            this.colName = name;
        }

        public String getColorName() {
            return this.colName;
        }
    }

}