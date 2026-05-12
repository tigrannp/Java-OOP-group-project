package Core;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static Core.Team.PLAYER;

public class UnitDatabase {

    public static ArrayList<Unit> loadUnits(String path) {
        ArrayList<Unit> templates = new ArrayList<Unit>();

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

                }
                else {
                    u = new Unit(name, symbol, hp, power, move, attack, PLAYER);
                }
                templates.add(u);
            }
            file.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not load units.txt");
        }
        return templates;
    }
}