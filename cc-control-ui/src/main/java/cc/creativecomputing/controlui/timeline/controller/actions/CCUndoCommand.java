package cc.creativecomputing.controlui.timeline.controller.actions;

public interface CCUndoCommand {

	void apply();
	
	void undo();
}