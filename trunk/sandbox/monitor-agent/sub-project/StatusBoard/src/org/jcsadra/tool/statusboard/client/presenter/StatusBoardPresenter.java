package org.jcsadra.tool.statusboard.client.presenter;

import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import org.jcsadra.tool.statusboard.client.service.MBeanInfoService;
import org.jcsadra.tool.statusboard.client.service.MBeanInfoServiceAsync;
import org.jcsadra.tool.statusboard.client.view.StatusBoardViewImpl;
import org.jcsadra.tool.statusboard.client.view.StatusDetailView;
import org.jcsadra.tool.statusboard.client.view.StatusListView;


/**
 * orgnize view, process action.
 * 
 * @author sanli
 *
 */
public class StatusBoardPresenter implements Presenter {
	
	/**
	 * the status board
	 * @author sanli
	 *
	 */
	public interface StatusBoardView{
		// set the status info into view, this will triger the
		// status list panel been filled.
		public void setStatusInfoList(Map<String, Map<String, String>> statusInfo);
		
		// get the root widget of this view, this should be a 
		// simple and single widget.
		public Widget getWidget();
	}
	
	
	StatusBoardView view ; 
	MBeanInfoServiceAsync infoservice ;
	
	public StatusBoardPresenter(){
	    infoservice = GWT.create(MBeanInfoService.class);
		view = new StatusBoardViewImpl(); 
	}
	

	@Override
	public void bind() {
		// bind the event to action function
	}

	@Override
	public void go(HasWidgets container) {

		container.add(view.getWidget());
		
		infoservice.getAllMBeans(new AsyncCallback<Map<String, Map<String, String>>>(){

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				Window.alert("Error:"+caught.getMessage());
			}

			@Override
			public void onSuccess(Map<String, Map<String, String>> result) {
				view.setStatusInfoList(result);
			}
			
		});

	}
	
}
