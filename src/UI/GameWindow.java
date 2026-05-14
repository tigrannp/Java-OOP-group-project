package UI;

import Core.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GameWindow extends JFrame implements MouseListener {

    private static final int TILE = 50;
    private static final int OFFSET_X = 50;
    private static final int OFFSET_Y = 50;
    private static final int ROWS = 7;
    private static final int COLS = 12;
    private static final int MAX_PLACE_COL = 5;

    private ArrayList<int[]> highlightedTiles = new ArrayList<>();

    private GameEngine engine;
    private UnitPlacementSession session;
    private String selectedUnitName = null;
    private boolean inPlacement = true;

    public GameWindow() {
        super("Grid Strategy - Defenders");
        setSize(800, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addMouseListener(this);

        ArrayList<Unit> blueprints = UnitDatabase.loadUnits(GameEngine.databasePath);
        session = new UnitPlacementSession(blueprints);

        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        if (inPlacement) paintPlacement(g);
        else             paintBattle(g);
    }

    private void paintPlacement(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, 800, 650);

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                int x = OFFSET_X + c * TILE;
                int y = OFFSET_Y + r * TILE;
                if (c <= MAX_PLACE_COL) {
                    g.setColor(new Color(180, 210, 240));
                } else {
                    g.setColor(Color.GRAY);
                }
                g.fillRect(x, y, TILE, TILE);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, TILE, TILE);
            }
        }

        for (Unit u : session.getPlacedUnits()) {
            int x = OFFSET_X + u.getCol() * TILE;
            int y = OFFSET_Y + u.getRow() * TILE;
            g.setColor(Color.BLUE);
            g.fillOval(x + 5, y + 5, 40, 40);
            g.setColor(Color.WHITE);
            g.drawString(u.getSymbol(), x + 18, y + 30);
        }

        int bx = OFFSET_X + COLS * TILE + 10;
        g.setColor(Color.WHITE);
        g.drawString("Select unit:", bx, OFFSET_Y);
        ArrayList<Unit> blueprints = session.getUnitBlueprints();
        for (int i = 0; i < blueprints.size(); i++) {
            String name = blueprints.get(i).getName();
            int by = OFFSET_Y + 20 + i * 40;
            g.setColor(name.equals(selectedUnitName) ? Color.YELLOW : Color.LIGHT_GRAY);
            g.fillRect(bx, by, 100, 30);
            g.setColor(Color.BLACK);
            g.drawRect(bx, by, 100, 30);
            g.drawString(name, bx + 8, by + 20);
        }

        int doneY = OFFSET_Y + 20 + blueprints.size() * 40 + 20;
        g.setColor(session.isReady() ? Color.GREEN : Color.GRAY);
        g.fillRect(bx, doneY, 100, 30);
        g.setColor(Color.BLACK);
        g.drawRect(bx, doneY, 100, 30);
        g.drawString("Start Battle", bx + 5, doneY + 20);

        g.setColor(Color.WHITE);
        g.drawString(session.getSummary().split("\n")[0], OFFSET_X, OFFSET_Y + ROWS * TILE + 30);
        String hint = selectedUnitName != null ? "Placing: " + selectedUnitName + "  |  Click tile to place, click unit to remove" : "Select a unit type on the right";
        g.drawString(hint, OFFSET_X, OFFSET_Y + ROWS * TILE + 50);
    }

    private void paintBattle(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, 800, 650);

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                int x = OFFSET_X + c * TILE;
                int y = OFFSET_Y + r * TILE;
                g.setColor((r + c) % 2 == 0 ? Color.LIGHT_GRAY : Color.GRAY);
                g.fillRect(x, y, TILE, TILE);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, TILE, TILE);
            }
        }

        for (int[] tile : highlightedTiles) {
            int x = OFFSET_X + tile[1] * TILE;
            int y = OFFSET_Y + tile[0] * TILE;
            g.setColor(new Color(255, 255, 0, 80));
            g.fillRect(x, y, TILE, TILE);
        }

        Unit selected = engine.getSelectedUnit();
        if (selected != null && engine.getCurrentTurn() == Team.PLAYER) {
            g.setColor(Color.YELLOW);
            g.drawRect(OFFSET_X + selected.getCol() * TILE, OFFSET_Y + selected.getRow() * TILE, TILE, TILE);
        }

        for (Unit u : engine.getUnits()) {
            int x = OFFSET_X + u.getCol() * TILE + 5;
            int y = OFFSET_Y + u.getRow() * TILE + 5;
            if (u.getHasMoved() && u.getHasActed()) g.setColor(Color.BLACK);
            else if (u.getTeam() == Team.PLAYER)    g.setColor(Color.BLUE);
            else                                     g.setColor(Color.RED);
            g.fillOval(x, y, 40, 40);
            g.setColor(Color.WHITE);
            g.drawString(u.getSymbol(), x + 15, y + 25);
            g.setColor(Color.GREEN);
            g.drawString(u.getHp() + "", x + 10, y + 45);
        }

        g.setColor(Color.WHITE);
        g.drawString("Status: " + engine.getStatusMessage(), 50, 440);

        if (selected != null) {
            String role = (selected instanceof SupportUnit) ? "Support" : "Combat";
            g.drawString("Selected: " + selected.getName() + " | HP: " + selected.getHp() + "/" + selected.getMaxHp(), 50, 460);
            g.drawString("Role: " + role + " | Power: " + selected.getPower() + " | Move: " + selected.getMoveRange() + " | Range: " + selected.getAttackRange(), 50, 478);
            g.drawString((selected.getHasMoved() ? "[Moved]" : "[Can Move]") + " " + (selected.getHasActed() ? "[Acted]" : "[Can Act]"), 50, 496);
        }

        g.setColor(Color.RED);
        g.fillRect(600, 460, 120, 50);
        g.setColor(Color.WHITE);
        g.drawString("END TURN", 630, 490);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (inPlacement) handlePlacementClick(e.getX(), e.getY());
        else             handleBattleClick(e.getX(), e.getY());
        repaint();
    }

    private void handlePlacementClick(int mx, int my) {
        ArrayList<Unit> blueprints = session.getUnitBlueprints();

        // unit type buttons
        int bx = OFFSET_X + COLS * TILE + 10;
        for (int i = 0; i < blueprints.size(); i++) {
            int by = OFFSET_Y + 20 + i * 40;
            if (mx >= bx && mx <= bx + 100 && my >= by && my <= by + 30) {
                selectedUnitName = blueprints.get(i).getName();
                return;
            }
        }

        // done button
        int doneY = OFFSET_Y + 20 + blueprints.size() * 40 + 20;
        if (mx >= bx && mx <= bx + 100 && my >= doneY && my <= doneY + 30) {
            if (session.isReady()) {
                engine = new GameEngine(session.getPlacedUnits());
                inPlacement = false;
            }
            return;
        }

        if (mx >= OFFSET_X && my >= OFFSET_Y) {
            int c = (mx - OFFSET_X) / TILE;
            int r = (my - OFFSET_Y) / TILE;
            if (r < ROWS && c <= MAX_PLACE_COL) {
                for (Unit u : session.getPlacedUnits()) {
                    if (u.getRow() == r && u.getCol() == c) {
                        session.removeUnit(r, c);
                        return;
                    }
                }
                // empty tile → place
                if (selectedUnitName != null)
                    session.placeUnit(selectedUnitName, r, c);
            }
        }
    }

    private void handleBattleClick(int mx, int my) {
        if (engine.getCurrentTurn() != Team.PLAYER) return;

        if (mx >= 600 && mx <= 720 && my >= 460 && my <= 510) {
            engine.passTurn();
            if (engine.getCurrentTurn() == null)
                JOptionPane.showMessageDialog(this, engine.getStatusMessage());
            return;
        }

        if (mx >= OFFSET_X && mx < OFFSET_X + COLS * TILE && my >= OFFSET_Y && my < OFFSET_Y + ROWS * TILE) {
            int c = (mx - OFFSET_X) / TILE;
            int r = (my - OFFSET_Y) / TILE;
            engine.handleClick(r, c);
            Unit selected = engine.getSelectedUnit();
            if (selected != null) {
                highlightedTiles = engine.getReachableTiles(selected);
            } else {
                highlightedTiles.clear();
            }
            if (engine.getCurrentTurn() == null)
                JOptionPane.showMessageDialog(this, engine.getStatusMessage());
        }
    }

    public void mouseClicked(MouseEvent e)  {}
    public void mouseEntered(MouseEvent e)  {}
    public void mouseExited(MouseEvent e)   {}
    public void mouseReleased(MouseEvent e) {}
}