/*
 * Copyright 2007 Google Inc.
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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

/**
 * A composite for displaying the details of an email message.
 */
public class StatusDetailView extends ResizeComposite {

  interface Binder extends UiBinder<Widget, StatusDetailView> { }
  private static final Binder binder = GWT.create(Binder.class);

  @UiField Element key;
  @UiField Element value;


  public StatusDetailView() {
    initWidget(binder.createAndBindUi(this));
  }

  public void setItem(String keyname , String valuestr) {
    key.setInnerText(keyname);
    value.setInnerText(valuestr) ;
  }
}