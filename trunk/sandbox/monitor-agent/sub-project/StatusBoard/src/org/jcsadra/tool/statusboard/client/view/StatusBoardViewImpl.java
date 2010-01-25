package org.jcsadra.tool.statusboard.client.view;

import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jcsadra.tool.statusboard.client.StatusBoard;
import org.jcsadra.tool.statusboard.client.presenter.StatusBoardPresenter.StatusBoardView;

public class StatusBoardViewImpl implements StatusBoardView {
	
	interface Binder extends UiBinder<DockLayoutPanel, StatusBoardViewImpl> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	DockLayoutPanel outer ;

	@UiField
	TopPanelView topPanel;
	@UiField
	StatusListView statusList;
	@UiField
	StatusDetailView detail;
	

	

	public StatusBoardViewImpl(){
	    // Create the UI defined in Mail.ui.xml.
	    outer = binder.createAndBindUi(this);

	    // Special-case stuff to make topPanel overhang a bit.
	    Element topElem = outer.getWidgetContainerElement(topPanel);
	    topElem.getStyle().setZIndex(2);
	    topElem.getStyle().setOverflow(Overflow.HIDDEN);
	}
	
	

	@Override
	public Widget getWidget() {
		return outer;
	}
	

	@Override
	public void setStatusInfoList(Map<String, Map<String, String>> statusInfo) {
		statusList.setStatusInfoList(statusInfo);
	}

}
