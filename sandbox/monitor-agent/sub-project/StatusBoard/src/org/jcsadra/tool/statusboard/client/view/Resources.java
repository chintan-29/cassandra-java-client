package org.jcsadra.tool.statusboard.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Resources extends ClientBundle {
	Resources INSTANCE = GWT.create(Resources.class);

	@Source("dashborad.png")
	ImageResource dashborad();

	@Source("status_1.png")
	ImageResource status();

}
