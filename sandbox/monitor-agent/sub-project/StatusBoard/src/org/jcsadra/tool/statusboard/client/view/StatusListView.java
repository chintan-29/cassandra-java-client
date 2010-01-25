/*
 * Copyright 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jcsadra.tool.statusboard.client.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DecoratedStackPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A composite that contains the shortcut stack panel on the left side. The
 * mailbox tree and shortcut lists don't actually do anything, but serve to show
 * how you can construct an interface using
 * {@link com.google.gwt.user.client.ui.StackPanel},
 * {@link com.google.gwt.user.client.ui.Tree}, and other custom widgets.
 */
public class StatusListView extends ResizeComposite {

	interface Binder extends UiBinder<DockLayoutPanel, StatusListView> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	/**
	 * Constructs a new shortcuts widget using the specified images.
	 * 
	 * @param images
	 *            a bundle that provides the images for this widget
	 */
	public StatusListView() {
		initWidget(binder.createAndBindUi(this));

		onInitialize();

	}

	@UiField
	DecoratedStackPanel statusPanel;
	ImageResource statusImg;

	/**
	 * Initialize this example.
	 */
	public void onInitialize() {
		statusImg = Resources.INSTANCE.status();
		statusPanel.showStack(0);
		// Return the stack panel
		statusPanel.ensureDebugId("cwStackPanel");

	}

	/**
	 * Create the list of filters for the Filters item.
	 * 
	 * @return the list of filters
	 */
	private VerticalPanel createFiltersItem(Map<String, String> map) {

		VerticalPanel filtersPanel = new VerticalPanel();
		filtersPanel.setSpacing(4);
		
		for (Map.Entry<String , String > e : map.entrySet()) {
			Button function = new Button(e.getKey());
			filtersPanel.add(function );
			// add event listener
			
		}

		return filtersPanel;
	}

	/**
	 * Get a string representation of the header that includes an image and some
	 * text.
	 * 
	 * @param text
	 *            the header text
	 * @param image
	 *            the {@link ImageResource} to add next to the header
	 * @return the header as a string
	 */
	private String getHeaderString(String text, ImageResource image) {
		// Add the image and text to a horizontal panel
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setSpacing(0);
		hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hPanel.add(new Image(image));
		HTML headerText = new HTML(text);
		headerText.setStyleName("cw-StackPanelHeader");
		hPanel.add(headerText);

		// Return the HTML string for the panel
		return hPanel.getElement().getString();
	}
	

	public void setStatusInfoList(Map<String, Map<String, String>> statusInfo) {
		if (statusInfo.size() > 0) {
			for (Map.Entry<String, Map<String, String>> e : statusInfo.entrySet()) {
				
				// Add a list of filters
				statusPanel.add(createFiltersItem(e.getValue()), getHeaderString(
						e.getKey(), statusImg), true);
			}
		} else {
			// Add a list of filters
			statusPanel.add(createFiltersItem(new HashMap<String , String>()),
					getHeaderString("No Status", statusImg), true);
		}
	}

}
