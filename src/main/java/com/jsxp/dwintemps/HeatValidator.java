package com.jsxp.dwintemps;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

public class HeatValidator implements IParameterValidator {
    @Override
    public void validate(String name, String value) throws ParameterException {
        Integer maxTemp = Integer.parseInt(value);

        if ((name.equals("--hotend") || name.equals("-e")) && (maxTemp < 1 || maxTemp > 320)) {
            throw new ParameterException(String.format("Hotend parameter cannot be less than 1 or greater than 320"));
        }

        if ((name.equals("--heatbed") || name.equals("-b")) && (maxTemp < 1 || maxTemp > 150)) {
            throw new ParameterException(String.format("Heatbed parameter cannot be less than 1 or greater than 150"));
        }
    }
}
