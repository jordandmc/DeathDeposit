package org.servegame.jordandmc.deathDeposit;

import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.InGameHUD;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.gui.Color;

public class cBox extends GenericPopup
{
	private InGameHUD screen;
	private DeathDeposit plugin;
	
	public cBox(InGameHUD screen){
		this.screen = screen;
		plugin = DeathDeposit.getInstance();
		
		attachWidget(plugin, label("Are you sure you want to exit your DeathChest now?", -20, -30));
		attachWidget(plugin, button("O.K", -55, 20));
		attachWidget(plugin, button("Cancel", 55, 20));
	}

	private GenericButton button(String text, int offsetX, int offsetY) {
		GenericButton button = new GenericButton(text);

		button.setVisible(true);
		button.setWidth(50);
		button.setHeight(20);

		center(button);
		button.setX(button.getX() + offsetX);
		button.setY(button.getY() + offsetY);

		return button;
	}

	private GenericLabel label(String text, int offsetX, int offsetY) {
		GenericLabel lbl = new GenericLabel(text);

		center(lbl);
		lbl.setX(lbl.getX() - lbl.getX() / 2);
		lbl.setX(lbl.getX() + offsetX);
		lbl.setY(lbl.getY() + offsetY);

		lbl.setTextColor(new Color(214, 0, 0));

		return lbl;
	}

	private void center(Widget widget) {
		widget.setX((screen.getWidth() - widget.getWidth()) / 2);
		widget.setY((screen.getHeight() - widget.getHeight()) / 2);
	}
}