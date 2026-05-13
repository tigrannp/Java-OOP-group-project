package Core;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static Core.Team.PLAYER;

/**
 * Loads unit templates from a CSV database file.
 */
public class UnitDatabase {
    /**
     * Reads unit definitions from a CSV file and returns them as a list of unit templates.
     * Each line defines one unit. Lines with 6 fields create a regular {@link Unit};
     * lines with 7 fields (including healing power) create a {@link SupportUnit}.
     * Blank lines are skipped.
     *
     * @param path the path to the CSV database file
     * @return a list of unit templates loaded from the file, or an empty list if the file is not found
     */
    public static ArrayList<Unit> loadUnits(String path) {
        ArrayList<Unit> unitBlueprints = new ArrayList<Unit>();

        try {
            Scanner file = new Scanner(new FileInputStream(path));

            while (file.hasNextLine()) {
                String line = file.nextLine();

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",");

                String name = parts[0];
                String symbol = parts[1];

                int hp = Integer.parseInt(parts[2]);
                int power = Integer.parseInt(parts[3]);
                int move = Integer.parseInt(parts[4]);
                int attack = Integer.parseInt(parts[5]);

                Unit u;

                if (parts.length == 7) {
                    int healingPower = Integer.parseInt(parts[6]);

                    u = new SupportUnit(name, symbol, hp, power, move, attack, PLAYER, healingPower);

                } else {
                    u = new Unit(name, symbol, hp, power, move, attack, PLAYER);
                }
                unitBlueprints.add(u);
            }
            file.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not load units.txt");
        }
        return unitBlueprints;
    }
}
