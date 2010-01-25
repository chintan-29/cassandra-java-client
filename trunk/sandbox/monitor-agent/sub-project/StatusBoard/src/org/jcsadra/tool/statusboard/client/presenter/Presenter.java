package org.jcsadra.tool.statusboard.client.presenter;

import com.google.gwt.user.client.ui.HasWidgets;

public interface Presenter {

	/**
	 * Bind View to Presenter EventHandler
	 */
	public void bind()  ;

	/**
	 * show this presenter to given container, will fetch data
	 * and fill it to view in this function.
	 * @param container
	 */
	public void go(HasWidgets container);
	
	
	
}
