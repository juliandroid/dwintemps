package com.jsxp.dwintemps;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.Files.exists;

public class FileValidator implements IParameterValidator {
    @Override
    public void validate(String name, String value) throws ParameterException {
        Path file = Paths.get(value);
        if (!exists(file, LinkOption.NOFOLLOW_LINKS)) {
            throw new ParameterException(String.format("Path '%s' does not exits: ", value));
        }

        if (!Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS)) {
            throw new ParameterException(String.format("Path '%s' is not a file: ", value));
        }
    }
}
