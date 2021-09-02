package de.dai_labor.conversation_engine_gui.interfaces;

import org.json.JSONObject;

public interface IStorableGuiData {
	public JSONObject getGUIData();

	public void setGUIData(JSONObject data);

	public void resetData();

	public boolean hasChanged();

	public void setUnchanged();
}
