package com.github.thomasfox.sailboatcalculator.gui;

import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import com.github.thomasfox.sailboatcalculator.boat.Boat;
import com.github.thomasfox.sailboatcalculator.boat.impl.Flying29er;
import com.github.thomasfox.sailboatcalculator.boat.impl.Skiff29er;

public class Menubar extends JMenuBar
{
  /** SerialVersionUID. */
  private static final long serialVersionUID = 1L;

  private final JMenu presetsMenu = new JMenu("Presets");

  JMenuItem boatTypeMenuItem;

  JMenuItem boatTypeSubmenu = new JMenu();

  private final Consumer<Boat> boatTypeSelected;

  ButtonGroup typeSelectButtonGroup = new ButtonGroup();

  private final Map<JRadioButtonMenuItem, Boat> boats = new HashMap<>();

  /**
   * Constructor for the menubar of the application.
   */
  public Menubar(Consumer<Boat> boatTypeSelected)
  {
    this.boatTypeSelected = boatTypeSelected;
    for (Boat boat : new Boat[] {new Flying29er(), new Skiff29er()})
    {
      JRadioButtonMenuItem rbMenuItem = new JRadioButtonMenuItem(boat.toString());
      typeSelectButtonGroup.add(rbMenuItem);
      presetsMenu.add(rbMenuItem);
      boats.put(rbMenuItem, boat);
      rbMenuItem.addActionListener(this::typeSelected);
    }
    typeSelectButtonGroup.getElements().nextElement().setSelected(true);

    add(presetsMenu);
  }

  public Boat getSelectedBoat()
  {
    Enumeration<AbstractButton> elements = typeSelectButtonGroup.getElements();
    while (elements.hasMoreElements())
    {
      AbstractButton button = elements.nextElement();
      if (button.isSelected())
      {
        return boats.get(button);
      }
    }
    return null;
  }

  public void typeSelected(ActionEvent e)
  {
    boatTypeSelected.accept(getSelectedBoat());
  }
}
