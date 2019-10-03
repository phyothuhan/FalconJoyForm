package com.falconit.joyform.client.application.tasks.list;



import com.falconit.joyform.client.application.tasks.display.TaskDisplayView;
import com.falconit.joyform.client.application.util.Constants;
import com.falconit.joyform.client.application.util.CookieHelper;
import com.falconit.joyform.client.application.util.jbpmclient.APIHelper;
import com.falconit.joyform.client.place.NameTokens;
import com.falconit.joyform.client.ui.NavigatedView;
import com.falconit.joyform.shared.jsonconvert.ObjectConverter;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.addins.client.combobox.MaterialComboBox;
import gwt.material.design.client.base.viewport.Resolution;
import gwt.material.design.client.base.viewport.ViewPort;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.TextAlign;
import gwt.material.design.client.data.ListDataSource;
import gwt.material.design.client.data.component.RowComponent;
import gwt.material.design.client.ui.MaterialBadge;
import gwt.material.design.client.ui.MaterialLoader;
import gwt.material.design.client.ui.pager.MaterialDataPager;
import gwt.material.design.client.ui.table.MaterialDataTable;
import gwt.material.design.client.ui.table.cell.TextColumn;
import gwt.material.design.client.ui.table.cell.WidgetColumn;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TasksListView extends NavigatedView implements TasksListPresenter.MyView {
    interface Binder extends UiBinder<Widget, TasksListView> {
    }

    @UiField
    MaterialDataTable<java.util.Map<String, Object[]>> table;
    @UiField
    MaterialComboBox cbostatus;
    
    private MaterialDataPager<java.util.Map<String, Object[]>> pager = new MaterialDataPager<>();
    private ListDataSource<java.util.Map<String, Object[]>> dataSource;
    
    private List<java.util.Map<String, Object[]>> lstTasks = new ArrayList<>();
    


    @Inject
    TasksListView(Binder uiBinder) {
        initWidget( uiBinder.createAndBindUi(this) );
        
        if( CookieHelper.getMyCookie( Constants.COOKIE_USER_ID ) == null ){
            History.newItem( NameTokens.login );
        }
        
        table.getTableTitle().setText( "Inbox" );
        /*
        table.addColumn(new WidgetColumn<java.util.Map<String, Object[]>, MaterialImage>() {
            @Override
            public MaterialImage getValue(java.util.Map<String, Object[]> object) {
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
        */
        table.addColumn(new TextColumn<java.util.Map<String, Object[]>>() {
            @Override
            public Comparator<? super RowComponent<java.util.Map<String, Object[]>>> sortComparator() {
                return (o1, o2) -> o1.getData().get("task-name")[1].toString().compareToIgnoreCase(o2.getData().get("task-name")[1].toString());
            }
            @Override
            public String getValue(java.util.Map<String, Object[]> object) {
                return object.get("task-name")[1].toString();
            }
        }, "Task name");

        table.addColumn(new TextColumn<java.util.Map<String, Object[]>>() {
            @Override
            public Comparator<? super RowComponent<java.util.Map<String, Object[]>>> sortComparator() {
                return (o1, o2) -> o1.getData().get("task-subject")[1].toString().compareToIgnoreCase(o2.getData().get("task-subject")[1].toString());
            }
            @Override
            public String getValue(java.util.Map<String, Object[]> object) {
                return object.get("task-subject")[1].toString();
            }
        }, "Subject");
        
        table.addColumn(new TextColumn<java.util.Map<String, Object[]>>() {
            @Override
            public Comparator<? super RowComponent<java.util.Map<String, Object[]>>> sortComparator() {
                return (o1, o2) -> (int) ( (long)o1.getData().get("task-created-on")[1] - (long) o2.getData().get("task-created-on")[1] );
            }
            @Override
            public String getValue(java.util.Map<String, Object[]> object) {
                DateTimeFormat dtfd = DateTimeFormat.getFormat("dd/MM/yyyy hh:mm a");
                return dtfd.format(new java.util.Date( (long) object.get("task-created-on")[1] ));
            }
        }, "Created On");
        
        table.addColumn(new TextColumn<java.util.Map<String, Object[]>>() {
//            @Override
//            public boolean numeric() {
//                return true;
//            }
//            @Override
//            public HideOn hideOn() {
//                return HideOn.HIDE_ON_MED_DOWN;
//            }
            @Override
            public Comparator<? super RowComponent<java.util.Map<String, Object[]>>> sortComparator() {
                return (o1, o2) -> (int) ( (long)o1.getData().get("task-priority")[1] - (long) o2.getData().get("task-priority")[1] );
            }
            @Override
            public String getValue(java.util.Map<String, Object[]> object) {
                return object.get("task-priority")[1].toString();
            }
        }, "Priority");
             
        table.addColumn(new TextColumn<java.util.Map<String, Object[]>>() {
            @Override
            public Comparator<? super RowComponent<java.util.Map<String, Object[]>>> sortComparator() {
                return (o1, o2) -> o1.getData().get("task-proc-def-id")[1].toString().compareToIgnoreCase(o2.getData().get("task-proc-def-id")[1].toString());
            }
            @Override
            public String getValue(java.util.Map<String, Object[]> object) {
                return object.get("task-proc-def-id")[1].toString();
            }
        }, "Apps");
        
        table.addColumn(new WidgetColumn<java.util.Map<String, Object[]>, MaterialBadge>() {
            @Override
            public TextAlign textAlign() {
                return TextAlign.CENTER;
            }
            @Override
            public MaterialBadge getValue(java.util.Map<String, Object[]> object) {
                MaterialBadge badge = new MaterialBadge();
                String status = object.get("task-status")[1].toString();
                badge.setText( status );
                
                if( status.equalsIgnoreCase(APIHelper.STATUS_CREATED) 
                        || status.equalsIgnoreCase(APIHelper.STATUS_READY)
                        || status.equalsIgnoreCase(APIHelper.STATUS_INPROGRESS)
                        || status.equalsIgnoreCase(APIHelper.STATUS_RESERVED)){
                    badge.setBackgroundColor(Color.GREEN);
                }else if( status.equalsIgnoreCase(APIHelper.STATUS_COMPLETED) ){
                    badge.setBackgroundColor(Color.BLUE);
                }else if( status.equalsIgnoreCase(APIHelper.STATUS_ERROR) || status.equalsIgnoreCase(APIHelper.STATUS_FAILED) ){
                    badge.setBackgroundColor(Color.RED);
                }else if( status.equalsIgnoreCase(APIHelper.STATUS_EXITED) || status.equalsIgnoreCase(APIHelper.STATUS_SUSPENDED) ){
                    badge.setBackgroundColor(Color.RED);
                }else if( status.equalsIgnoreCase(APIHelper.STATUS_OBSOLETE) ){
                    badge.setBackgroundColor(Color.BROWN);
                }else{
                    badge.setBackgroundColor(Color.GREY);
                }
                
                badge.setLayoutPosition(Style.Position.RELATIVE);
                return badge;
            }
        }, "Status");
           
        /*
        table.addColumn(new WidgetColumn<java.util.Map<String, Object[]>, MaterialCheckBox>() {
            @Override
            public MaterialCheckBox getValue(java.util.Map<String, Object[]> object) {
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
        }, MyLang.LANG.active());
        */


        // Here we are adding a row expansion handler.
        // This is invoked when a row is expanded.
        /*
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
                    MaterialLabel description = new MaterialLabel("This content was made from asynchronous call "  );

                    content.setPadding(20);
                    content.add(title);
                    content.add(description);

                    // Hide the expansion elements overlay section.
                    // The overlay is retrieved using EowExpand#getOverlay()
                    section.css("display", "none");
                }
            }.schedule(50);
        });
*/
        // Add a row select handler, called when a user selects a row.
        table.addRowSelectHandler(event -> {
            taskSelected( event.getModel() );
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
        
        ViewPort.when(Resolution.ALL_MOBILE).then(param1 -> {
            table.setHeight("60vh");
        }, viewPort -> {
            table.setHeight("100%");
            return false;
        });
        
        java.util.List<String> selectedItems = new java.util.ArrayList<>();
        String filter = com.google.gwt.user.client.Window.Location.getParameter( "filter" );
        if( filter != null ){
            selectedItems.add( APIHelper.STATUS_COMPLETED );
        }else{
            selectedItems.add( APIHelper.STATUS_READY );
            selectedItems.add( APIHelper.STATUS_RESERVED );
            selectedItems.add( APIHelper.STATUS_INPROGRESS );
        }
        cbostatus.setValues(selectedItems, true);
        
        cbostatus.addValueChangeHandler( handler ->{
            load( );
        });
        
        load( );
    }

    private void load(){
        lstTasks.clear();
        MaterialLoader.loading( true );
        APIHelper helper = new APIHelper();
        helper.setListener(new APIHelper.APIHelperListener() {
            @Override
            public void success(String result) {
                
                MaterialLoader.loading( false );
                
                JSONObject jsonOnlineUser = JSONParser.parseStrict( result ).isObject();
                JSONArray tasks = jsonOnlineUser.get("task-summary").isArray();
                if( tasks == null || tasks.size() == 0 ){
                    
                }else{
                    for( int i=0; i < tasks.size(); i++){
                        JSONObject task = tasks.get(i).isObject();
                        
                        try {
                            java.util.Map<String, Object[]> taskMap = new ObjectConverter().fromJSON( task, false, false );
                            String cid = taskMap.get("task-container-id")[1].toString();
                            if( !Constants.containerFilter.contains( cid )){
                                String userName = CookieHelper.getMyCookie( Constants.COOKIE_USER_NAME );
                                String userId =CookieHelper.getMyCookie( Constants.COOKIE_USER_ID );
                                
                                
                                String actualOwner = taskMap.get("task-actual-owner")[1] != null ? taskMap.get("task-actual-owner")[1].toString() : "";
                                String createdBy = taskMap.get("task-created-by")[1] !=null ? taskMap.get("task-created-by")[1].toString() : "";
                                if( ( !actualOwner.isEmpty() && actualOwner.equals( userId +"-" + userName )) 
                                        || (!createdBy.isEmpty() && createdBy.equals(userId +"-" + userName)))
                                    lstTasks.add( taskMap );
                            }
                            
                            //filter with user
                            //"task-actual-owner": "wbadmin",
                            //"task-created-by": "wbadmin",
                            
                            //Window.alert("Task id = "+taskMap.get("task-id")[1].toString() +", Created on=" + (long)taskMap.get("task-created-on")[1]);
                        } catch (Exception ex) {
                            Window.alert(ex.getMessage());
                            Logger.getLogger(TasksListView.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                    pager.setLimit( 20 );
                    dataSource = new ListDataSource<>();
                    dataSource.add(0, lstTasks );
                    pager.setTable(table);
                    pager.setDataSource(dataSource); 
                    table.add(pager); //pager.updateRowsPerPage(20);
                    
                    
                }
            }

            @Override
            public void fail(String message, int stage) {
              Window.alert( message );
              MaterialLoader.loading( false );
            }
        });
        String arrStatus[] = new String[cbostatus.getSelectedValues().size()];
        for ( int i=0; i < cbostatus.getSelectedValues().size(); i++){
            arrStatus[i] = (String) cbostatus.getSelectedValues().get(i);
            //Window.alert( arrStatus[i] );
        }
        
        
        helper.tasksList( 
                //new String[]{APIHelper.STATUS_READY,APIHelper.STATUS_RESERVED, APIHelper.STATUS_INPROGRESS },
                arrStatus,
                0, 1000, null, null, true);
        //helper.query( Constants.containerId, "355");
    }
    
    private void taskSelected( java.util.Map<String, Object[]> map ){
        String container = map.get("task-container-id")[1].toString();
        String taskId = map.get("task-id")[1].toString();
        String processId = map.get("task-proc-def-id")[1].toString();
        String taskName = map.get("task-name")[1].toString();
        
        
        Window.Location.assign( "?container=" + container 
                + "&taskId=" + taskId 
                + "&process=" + processId 
                + "&taskName=" + taskName 
                + "&display=" + TaskDisplayView.DISPLAY_PROCESS
                + "#taskdisplay" );
            
        /*
        TaskInputDataReader reader = new TaskInputDataReader();
        reader.setListener(new TaskInputDataReaderListener(){
            @Override
            public void result(String taskName, String nodeName, String Skippable, Map<String, Object[]> maps) {
                Window.alert("Task name=" + taskName + ", Node name=" + nodeName+", size=" + maps.size());
            }

            @Override
            public void fail(String message) {
            }
        });
        reader.read(container, taskId);
        */
    }
    
  /*  
    @UiHandler("btnGotoFirstPage")
    void onGotoFirstPage(ClickEvent e) {
        pager.firstPage();
    }
*/
    
    
}
