package com.jsxp.dwintemps;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.PathConverter;

import java.nio.file.Path;

@Parameters(separators = "=")
public class CmdOptionDescriptions {
    @Parameter(names = {"-h", "--help"},
            help = true,
            description = "Display help usage")
    private boolean help;

    @Parameter(names = {"-f", "--file"},
            required = true,
            description = "File path to the 13.bin",
            converter = PathConverter.class,
            validateWith = FileValidator.class)
    private Path file;

    @Parameter(names = {"-e", "--hotend"},
            description = "Hotend maximum temperature",
            validateWith = HeatValidator.class)
    private Integer hotendMax = 260;

    @Parameter(names = {"-b", "--heatbed"},
            description = "Heatbed maximum temperature",
            validateWith = HeatValidator.class)
    private Integer heatbedMax = 110;

    public boolean isHelp() {
        return help;
    }
    public Path getFile() { return file; }
    public Integer getHotendTemp() { return hotendMax; }
    public Integer getHeatbedTemp() { return heatbedMax; }
}
