package com.jsxp.dwintemps;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Arrays;

import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

@SpringBootApplication
public class DwinTempsApplication implements CommandLineRunner {
    final private CmdOptionDescriptions mainArgs = new CmdOptionDescriptions();
    private JCommander jCommander = new JCommander(mainArgs);

    public static void main(String[] args) {
        SpringApplication.run(DwinTempsApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        jCommander.setProgramName("dwintemp");
        try {
            jCommander.parse(args);
        } catch (ParameterException exception) {
            System.out.println(exception.getMessage());
            showUsage(jCommander);
        }

        if (mainArgs.isHelp()) {
            showUsage(jCommander);
        }

        File bin = new File(mainArgs.getFile().toString());
        byte[] binBytes = new byte[]{};
        try {
            binBytes = Files.readAllBytes(bin.toPath());
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
            System.exit(1);
        }

        byte[] hotendPattern  = new byte[] { (byte)0xfe, (byte)0xff, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x04 };
        byte[] heatbedPattern = new byte[] { (byte)0xfe, (byte)0xff, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x6e };

        byte[] newHotendTemp  = Arrays.copyOf(hotendPattern, hotendPattern.length);
        byte[] newHeatbedTemp = Arrays.copyOf(heatbedPattern, heatbedPattern.length);

        newHotendTemp[newHotendTemp.length-2] = (byte)((mainArgs.getHotendTemp()>>8)&0x000000FF);
        newHotendTemp[newHotendTemp.length-1] = (byte)(mainArgs.getHotendTemp()&0x000000FF);

        newHeatbedTemp[newHeatbedTemp.length-2] = (byte)((mainArgs.getHeatbedTemp()>>8)&0x0000FF00);
        newHeatbedTemp[newHeatbedTemp.length-1] = (byte)(mainArgs.getHeatbedTemp()&0x000000FF);

        boolean replaceHotendTemp  = !Arrays.equals(hotendPattern, newHotendTemp);
        boolean replaceHeatBedTemp = !Arrays.equals(heatbedPattern, newHeatbedTemp);
        boolean done = false;

        ByteBuffer replaceBuffer = ByteBuffer.wrap(binBytes);
        int replacemets=0;

        while (!done) {
            int position;
            done = true;

            if (replaceHotendTemp) {
                position = indexOf(replaceBuffer.array(), hotendPattern);
                if (position != -1) {
                    replacemets++;
                    replaceBuffer.position(position);
                    replaceBuffer.put(newHotendTemp);
                    done = false;
                }
            }

            if (replaceHeatBedTemp) {
                position = indexOf(replaceBuffer.array(), heatbedPattern);
                if (position != -1) {
                    replacemets++;
                    replaceBuffer.position(position);
                    replaceBuffer.put(newHeatbedTemp);
                    done = false;
                }
            }
        }

        if (replacemets > 0) {
            System.out.printf("Number of substitutions made inside '%s': '%d'\n", mainArgs.getFile(), replacemets);
            try {
                Files.write(bin.toPath(), replaceBuffer.array(), TRUNCATE_EXISTING, WRITE);
            } catch (IOException exception) {
                System.out.println(exception.getMessage());
                System.exit(2);
            }
        } else {
            System.out.printf("Nothing has been changed inside '%s'\n", mainArgs.getFile());
        }

    }

    private void showUsage(JCommander jCommander) {
        jCommander.usage();
        System.exit(0);
    }

    /**
     * Returns the start position of the first occurrence of the specified {@code target} within
     * {@code array}, or {@code -1} if there is no such occurrence.
     *
     * <p>More formally, returns the lowest index {@code i} such that {@code Arrays.copyOfRange(array,
     * i, i + target.length)} contains exactly the same elements as {@code target}.
     *
     * @param array the array to search for the sequence {@code target}
     * @param target the array to search for as a sub-sequence of {@code array}
     */
    private static int indexOf(byte[] array, byte[] target) {
        if (target.length == 0) {
            return 0;
        }

        outer:
        for (int i = 0; i < array.length - target.length + 1; i++) {
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }

}
