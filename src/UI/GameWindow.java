package UI;

import Core.GameEngine;
import Core.Team;
import Core.Unit;
import Core.SupportUnit;

import javax.swing.JFrame;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class GameWindow extends JFrame implements MouseListener {
    private GameEngine engine;

    public GameWindow() {
        super("Grid Strategy - Defenders");
        this.setSize(800, 650);

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });

        this.addMouseListener(this);
    }

    public void paint(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, 800, 650);

        int offsetX = 50;
        int offsetY = 50;
        int tileSize = 50;

        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 12; c++) {
                int x = offsetX + (c * tileSize);
                int y = offsetY + (r * tileSize);
                if ((r + c) % 2 == 0) {
                    g.setColor(Color.LIGHT_GRAY);
                } else {
                    g.setColor(Color.GRAY);
                }
                g.fillRect(x, y, tileSize, tileSize);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, tileSize, tileSize);
            }
        }

        Unit selectedUnit = engine.getSelectedUnit();
        Team currentTurn = engine.getCurrentTurn();

        if (selectedUnit != null && currentTurn == Team.PLAYER) {
            g.setColor(Color.YELLOW);
            g.drawRect(offsetX + (selectedUnit.getCol() * tileSize), offsetY + (selectedUnit.getRow() * tileSize), tileSize, tileSize);
        }

        ArrayList<Unit> units = engine.getUnits();
        for (int i = 0; i < units.size(); i++) {
            Unit u = units.get(i);
            int x = offsetX + (u.getCol() * tileSize) + 5;
            int y = offsetY + (u.getRow() * tileSize) + 5;

            if (u.getTeam() == Team.PLAYER) {
                g.setColor(Color.BLUE);
            } else {
                g.setColor(Color.RED);
            }

            if (u.getHasMoved() && u.getHasActed()) {
                g.setColor(Color.BLACK);
            }

            g.fillOval(x, y, 40, 40);
            g.setColor(Color.WHITE);
            g.drawString(u.getSymbol(), x + 15, y + 25);
            g.setColor(Color.GREEN);
            g.drawString(u.getHp() + "", x + 10, y + 45);
        }

        g.setColor(Color.WHITE);
        g.drawString("Status Log: " + engine.getStatusMessage(), 50, 440);

        if (selectedUnit != null) {
            String role = (selectedUnit instanceof SupportUnit) ? "Support" : "Combat";
            g.drawString("Selected: " + selectedUnit.getName() + " | HP: " + selectedUnit.getHp() + "/" + selectedUnit.getMaxHp(), 50, 470);
            g.drawString("Role: " + role + " | Power: " + selectedUnit.getPower(), 50, 490);
            g.drawString("Move Range: " + selectedUnit.getMoveRange() + " | Attack Range: " + selectedUnit.getAttackRange(), 50, 510);
            String actedStr = selectedUnit.getHasActed() ? "[Acted]" : "[Can Act]";
            String movedStr = selectedUnit.getHasMoved() ? "[Moved]" : "[Can Move]";
            g.drawString("Status: " + movedStr + " " + actedStr, 50, 530);
        }

        g.setColor(Color.RED);
        g.fillRect(600, 460, 120, 50);
        g.setColor(Color.WHITE);
        g.drawString("END TURN", 630, 490);
    }

    public void mouseClicked(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
    public void mouseReleased(MouseEvent e) { }

    public void mousePressed(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        if (mouseX >= 600 && mouseX <= 720 && mouseY >= 460 && mouseY <= 510) {
            if (engine.getCurrentTurn() == Team.PLAYER) {
                engine.passTurn();
            }
            repaint();
            return;
        }

        if (engine.getCurrentTurn() != Team.PLAYER) return;

        if (mouseX >= 50 && mouseX < 650 && mouseY >= 50 && mouseY < 400) {
            int c = (mouseX - 50) / 50;
            int r = (mouseY - 50) / 50;
            engine.handleClick(r, c);
            repaint();
        }
    }
}