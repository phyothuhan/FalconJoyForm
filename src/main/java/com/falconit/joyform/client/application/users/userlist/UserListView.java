package com.falconit.joyform.client.application.users.userlist;

/*
 * #%L
 * GwtMaterial
 * %%
 * Copyright (C) 2015 - 2016 GwtMaterialDesign
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.falconit.joyform.client.application.util.Constants;
import com.falconit.joyform.client.application.util.jbpmclient.HumanTaskHelper;
import com.falconit.joyform.client.ui.CustomRenderer;
import com.falconit.joyform.client.ui.NavigatedView;
import com.falconit.joyform.client.ui.PersonRowFactory;
import com.falconit.joyform.shared.entity.Users;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.base.MaterialWidget;
import gwt.material.design.client.base.viewport.Resolution;
import gwt.material.design.client.base.viewport.ViewPort;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.Display;
import gwt.material.design.client.constants.TextAlign;
import gwt.material.design.client.data.ListDataSource;
import gwt.material.design.client.data.component.RowComponent;
import gwt.material.design.client.ui.MaterialBadge;
import gwt.material.design.client.ui.MaterialCheckBox;
import gwt.material.design.client.ui.MaterialImage;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialLoader;
import gwt.material.design.client.ui.pager.MaterialDataPager;
import gwt.material.design.client.ui.table.MaterialDataTable;
import gwt.material.design.client.ui.table.cell.TextColumn;
import gwt.material.design.client.ui.table.cell.WidgetColumn;
import gwt.material.design.jquery.client.api.JQueryElement;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class UserListView extends NavigatedView implements UserListPresenter.MyView {
    interface Binder extends UiBinder<Widget, UserListView> {
    }

    @UiField
    MaterialDataTable<Users> table;

    private MaterialDataPager<Users> pager = new MaterialDataPager<>();
    private List<Users> people = new ArrayList<>();

    private ListDataSource<Users> dataSource;

    @Inject
    UserListView(Binder uiBinder) {
        initWidget( uiBinder.createAndBindUi(this) );

        // Generate 20 categories
        int rowIndex = 1;
        table.getTableTitle().setText("User List");
        
        // We will manually add this category otherwise categories
        // can be loaded on the fly with HasDataCategory, or a custom
        // RowComponentFactory as demonstrated below
        //table.addCategory(new CustomCategoryComponent("Custom Category"));

        // We will define our own person row factory to generate the
        // category name. This can be used to generate your own
        // RowComponent's too, if custom functionality is required.
        table.setRowFactory(new PersonRowFactory());

        // It is possible to create your own custom renderer per table
        // When you use the BaseRenderer you can override certain draw
        // methods to create elements the way you would like.
        table.setRenderer(new CustomRenderer<>());

        // Now we will add our tables columns.
        // There are a number of methods that can provide custom column configurations.

                // Add an image profile on each category rows
        
                
        table.addColumn(new WidgetColumn<Users, MaterialImage>() {
            @Override
            public MaterialImage getValue(Users object) {
                MaterialImage profile = new MaterialImage();
                profile.setUrl(object.getAvatar());
                profile.setWidth("40px");
                profile.setHeight("40px");
                profile.setPadding(4);
                profile.setMarginTop(8);
                profile.setBackgroundColor(Color.GREY_LIGHTEN_2);
                profile.setCircle(true);
                return profile;
            }
        });
        table.addColumn(new TextColumn<Users>() {
            @Override
            public Comparator<? super RowComponent<Users>> sortComparator() {
                return (o1, o2) -> o1.getData().getUsername().compareToIgnoreCase(o2.getData().getUsername());
            }
            @Override
            public String getValue(Users object) {
                return object.getUsername();
            }
        }, "User name");

        table.addColumn(new TextColumn<Users>() {
            @Override
            public Comparator<? super RowComponent<Users>> sortComparator() {
                return (o1, o2) -> o1.getData().getChannel().compareToIgnoreCase(o2.getData().getChannel());
            }
            @Override
            public String getValue(Users object) {
                return object.getChannel();
            }
        }, "Channel");

                
        // Example of a widget column!
        // You can add any handler to the column cells widget.
        table.addColumn(new WidgetColumn<Users, MaterialBadge>() {
            @Override
            public TextAlign textAlign() {
                return TextAlign.CENTER;
            }
            @Override
            public MaterialBadge getValue(Users object) {
                MaterialBadge badge = new MaterialBadge();
                if( object.getLevel() == 1){
                    badge.setText("Admin");
                    badge.setBackgroundColor(Color.RED);
                }else if( object.getLevel() == 2){
                    badge.setText("Channel");
                    badge.setBackgroundColor(Color.BLUE);
                }else{
                    badge.setText("User");
                    badge.setBackgroundColor(Color.GREY);
                }
                
                badge.setLayoutPosition(Style.Position.RELATIVE);
                return badge;
            }
        }, "A/C type");
        
        table.addColumn(new TextColumn<Users>() {
//            @Override
//            public boolean numeric() {
//                return true;
//            }
//            @Override
//            public HideOn hideOn() {
//                return HideOn.HIDE_ON_MED_DOWN;
//            }
//            @Override
//            public Comparator<? super RowComponent<Users>> sortComparator() {
//                return (o1, o2) -> o1.getData().getEmail().compareToIgnoreCase(o2.getData().getEmail());
//            }
            @Override
            public String getValue(Users object) {
                return object.getEmail();
            }
        }, "Email");
        
        table.addColumn(new TextColumn<Users>() {
//            @Override
//            public boolean numeric() {
//                return true;
//            }
//            @Override
//            public HideOn hideOn() {
//                return HideOn.HIDE_ON_MED_DOWN;
//            }
//            @Override
//            public Comparator<? super RowComponent<Users>> sortComparator() {
//                return (o1, o2) -> o1.getData().getMobile().compareToIgnoreCase(o2.getData().getMobile());
//            }
            @Override
            public String getValue(Users object) {
                return object.getMobile();
            }
        }, "Mobile");
        
                
        table.addColumn(new TextColumn<Users>() {
            @Override
            public Comparator<? super RowComponent<Users>> sortComparator() {
                return (o1, o2) ->  o1.getData().getCreatedAt().compareTo(o2.getData().getCreatedAt());
            }
            @Override
            public String getValue(Users object) {
                DateTimeFormat dtfd = DateTimeFormat.getFormat("dd/MM/yyyy hh:mm a");
                return dtfd.format(object.getCreatedAt());
            }
        }, "Created");
        
                
        table.addColumn(new WidgetColumn<Users, MaterialCheckBox>() {
            @Override
            public MaterialCheckBox getValue(Users object) {
                MaterialCheckBox profile = new MaterialCheckBox();
                if( object.getStatus() == 1)
                    profile.setValue(Boolean.TRUE);
                else
                    profile.setValue(Boolean.FALSE);
                                
                profile.addValueChangeHandler(event ->{
                    if( event.getValue()){
                        object.setStatus(1);
                    }else
                         object.setStatus(0);
                    
                        //changeStatus( object );
                });
                return profile;
            }
        }, "Active");
        
                
//        table.addColumn(new TextColumn<Users>() {
//            @Override
//            public Comparator<? super RowComponent<Users>> sortComparator() {
//                return (o1, o2) -> (int) ( o1.getData().getId() - o2.getData().getId() );
//            }
//
//            @Override
//            public String getValue(Users object) {
//                return object.getId() + ")";
//            }
//        }, MyLang.LANG.email());


        // Here we are adding a row expansion handler.
        // This is invoked when a row is expanded.
        table.addRowExpandingHandler(event -> {
            JQueryElement section = event.getExpansion().getOverlay();

            // Clear the content first.
            MaterialWidget content = new MaterialWidget(
                event.getExpansion().getContent().empty().asElement());

            // Fake Async Task
            // This is demonstrating a fake asynchronous call to load
            // the data inside the expansion element.
            new Timer() {
                @Override
                public void run() {
                    MaterialLabel title = new MaterialLabel("Expansion Row Panel");
                    title.setFontSize("1.6em");
                    title.setDisplay(Display.BLOCK);
                    MaterialLabel description = new MaterialLabel("This content was made from asynchronous call " 
                            + event.getExpansion().getModel().getIsVip());

                    content.setPadding(20);
                    content.add(title);
                    content.add(description);

                    // Hide the expansion elements overlay section.
                    // The overlay is retrieved using EowExpand#getOverlay()
                    section.css("display", "none");
                }
            }.schedule(50);
        });

        // Add a row select handler, called when a user selects a row.
        table.addRowSelectHandler(event -> {
            GWT.log(event.getModel().getId() + ": " + event.isSelected());
        });

        // Add a sort column handler, called when a user sorts a column.
        table.addColumnSortHandler(event -> {
            GWT.log("Sorted: " + event.getSortContext().getSortDir() + ", columnIndex: " + event.getColumnIndex());
            table.getView().refresh();
        });

        // Add category opened handler, called when a category is opened.
        table.addCategoryOpenedHandler(event -> {
            GWT.log("Category Opened: " + event.getName());
        });

        // Add category closed handler, called when a category is closed.
        table.addCategoryClosedHandler(event -> {
            GWT.log("Category Closed: " + event.getName());
        });

        // Add a row double click handler, called when a row is double clicked.
        table.addRowDoubleClickHandler(event -> {
            // GWT.log("Row Double Clicked: " + model.getId() + ", x:" + mouseEvent.getPageX() + ", y: " + mouseEvent.getPageY());
            Window.alert("Row Double Clicked: " + event.getModel().getId());
        });

        // Configure the tables long press duration configuration.
        // The short press is when a click is held less than this duration.
        table.setLongPressDuration(400);

        // Add a row long press handler, called when a row is long pressed.
        table.addRowLongPressHandler(event -> {
            //GWT.log("Row Long Pressed: " + model.getId() + ", x:" + mouseEvent.getPageX() + ", y: " + mouseEvent.getPageY());
        });

        // Add a row short press handler, called when a row is short pressed.
        table.addRowShortPressHandler(event -> {
            //.log("Row Short Pressed: " + model.getId() + ", x:" + mouseEvent.getPageX() + ", y: " + mouseEvent.getPageY());
        });

        // Here we are adding a row expansion handler.
        // This is invoked when a row is expanded.
        /*
        table.addRowExpandingHandler(event -> {
            // Fake Async Task
            // This is demonstrating a fake asynchronous call to load
            // the data inside the expansion element.
            new Timer() {
                @Override
                public void run() {
                    // Clear the content first.
                    JQueryElement element = event.getExpansion().getContent().empty();
                    // Assign the jquery element to a GMD Widget
                    MaterialWidget content = new MaterialWidget(element);

                    // Add new content.
                    MaterialBadge badge = new MaterialBadge("This content", Color.WHITE, Color.BLUE);
                    badge.getElement().getStyle().setPosition(Position.RELATIVE);
                    badge.getElement().getStyle().setRight(0, Unit.PX);
                    badge.setFontSize(12, Unit.PX);
                    content.add(badge);

                    MaterialButton btn = new MaterialButton("was made", IconType.FULLSCREEN, ButtonType.RAISED);
                    btn.setWaves(WavesType.DEFAULT);
                    content.add(btn);

                    MaterialTextBox textBox = new MaterialTextBox();
                    textBox.setText(" from an asynchronous");
                    textBox.setGwtDisplay( Display.INLINE_TABLE);
                    textBox.setWidth("200px");
                    content.add(textBox);

                    MaterialIcon icon = new MaterialIcon(IconType.CALL);
                    icon.getElement().getStyle().setPosition(Position.RELATIVE);
                    icon.getElement().getStyle().setTop(12, Unit.PX);
                    content.add(icon);

                    // Hide the expansion elements overlay section.
                    // The overlay is retrieved using RowExpansion#getOverlay()
                    event.getExpansion().getOverlay().css("display", "none");
                }
            }.schedule(2000);
        });
        */
        
        ViewPort.when(Resolution.ALL_MOBILE).then(param1 -> {
            table.setHeight("60vh");
        }, viewPort -> {
            table.setHeight("100%");
            return false;
        });
        
        load();
    }

    private void load(){
        MaterialLoader.loading( true );
        HumanTaskHelper helper = new HumanTaskHelper();
        helper.setListener(new HumanTaskHelper.HumanTaskHelperListener() {
            @Override
            public void success(String result) {
                
                MaterialLoader.loading( false );
                
                JSONObject jsonOnlineUser = JSONParser.parseStrict( result ).isObject();
                String message = jsonOnlineUser.get("message").isString().stringValue();
                if( message.equalsIgnoreCase("OK")){
                    JSONArray users = jsonOnlineUser.get("users").isArray();
                    //Window.alert( "Size=" + users.size() );
                    for( int i=0; i < users.size(); i++){
                        JSONObject obj = users.get(i).isObject();
                        JSONObject user = obj.get("com.zwaregroup.zayattv.entity.Users").isObject();
                        Users usr = new Users();
                        usr.fromJSON( user );
                        people.add(usr);
                    }
                    //Window.alert( "after size=" + users.size() );
                    dataSource = new ListDataSource<>();
                    dataSource.add(0, people);

                    /* Uncomment to make use of listbox page selection instead of integerbox */
                    /*pager.setPageSelection(new PageListBox());*/
                    pager.setTable(table);
                    pager.setDataSource(dataSource);

                    table.setVisibleRange(1, 20);
                    table.add(pager);
                    //pager.setLimit(20);
                }else{
                    Window.alert( message );
                }
            }

            @Override
            public void fail(String message, int stage) {
              Window.alert( message );
              MaterialLoader.loading( false );
            }
        });
        
        JSONObject users = new JSONObject( );
        
        users.put("email", new JSONString( "" ));
        users.put("mobile", new JSONString( "" ));
        users.put("password", new JSONString( "" ));
        
        JSONObject user = new JSONObject();
        user.put("Users", users);
        
        JSONObject obj = new JSONObject();
        obj.put("action", new JSONString( "list" ));
        obj.put("user", user);
        
        helper.startInstances( Constants.userProcessId, obj.toString());
        //helper.query( Constants.containerId, "355");
    }
    
    
    @UiHandler("btnGotoFirstPage")
    void onGotoFirstPage(ClickEvent e) {
        pager.firstPage();
    }

}
