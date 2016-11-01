package utils;

import javax.swing.DefaultListSelectionModel;

/**
 * Implementa un tipo particolare di modo di selezione delle JList.
 * Ogni elemento della lista mantiene il proprio stato (selezionato/deselezionato)
 * fino al successivo click del mouse su quell'elemento.
 * @author Andrea
 * @version 1.0
 * @since 1.0
 * @see DefaultListSelectionModel
 */
public class ToggleListSelectionModel extends DefaultListSelectionModel {
	private static final long serialVersionUID = 2554215043950511372L;
	
	public ToggleListSelectionModel(){
		super();
	}
	
	@Override
	public void setSelectionInterval(int start,int end) {
		for(int i=start;i<=end;i++)
			if (isSelectedIndex(i))
				removeSelectionInterval(i,i);
			else
				addSelectionInterval(i,i);
	}
}
