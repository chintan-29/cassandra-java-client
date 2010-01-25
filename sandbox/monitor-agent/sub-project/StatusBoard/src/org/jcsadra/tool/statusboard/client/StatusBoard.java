package org.jcsadra.tool.statusboard.client;

import com.google.gwt.core.client.EntryPoint;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import com.webex.jcasandra.board.client.presenter.StatusBoardPresenter;
import com.webex.jcasandra.board.client.servcie.MBeanInfoService;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class StatusBoard implements EntryPoint {



	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
	    // Get rid of scrollbars, and clear out the window's built-in margin,
	    // because we want to take advantage of the entire client area.
	    Window.enableScrolling(false);
	    Window.setMargin("1px");
	    


		StatusBoardPresenter presenter = new StatusBoardPresenter();

		
	    // Add the outer panel to the RootLayoutPanel, so that it will be
	    // displayed.
	    RootLayoutPanel root = RootLayoutPanel.get();
	    presenter.go(root) ;
	}
}
