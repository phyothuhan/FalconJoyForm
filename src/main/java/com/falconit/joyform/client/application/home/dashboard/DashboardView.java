package com.falconit.joyform.client.application.home.dashboard;



import com.falconit.joyform.client.application.form.util.Form;
import com.falconit.joyform.client.application.form.util.FormCRUD;
import com.falconit.joyform.client.application.tasks.display.TaskDisplayView;
import com.falconit.joyform.client.application.tasks.list.TasksListView;
import com.falconit.joyform.client.application.util.Constants;
import com.falconit.joyform.client.application.util.CookieHelper;
import com.falconit.joyform.client.application.util.jbpmclient.APIHelper;
import com.falconit.joyform.client.application.util.jbpmclient.api.ContainerManager;
import com.falconit.joyform.client.application.util.jbpmclient.api.ContainerManager.ContainerManagerListener;
import com.falconit.joyform.client.application.util.jbpmclient.api.process.ProcessByContainer;
import com.falconit.joyform.client.application.util.jbpmclient.api.process.ProcessVariables;
import com.falconit.joyform.client.application.util.jbpmclient.api.process.ProcessVariables.ProcessVariablesListener;
import com.falconit.joyform.client.application.util.jbpmclient.api.process.ProcessesManager;
import com.falconit.joyform.client.application.util.jbpmclient.api.process.ProcessesManager.ProcessesManagerListener;
import com.falconit.joyform.client.application.util.jbpmclient.api.process.ProcessesVariablesMapping;
import com.falconit.joyform.client.application.util.jbpmclient.api.process.ProcessesVariablesMapping.ProcessesVariablesMappingListener;
import com.falconit.joyform.client.place.NameTokens;
import com.falconit.joyform.client.ui.NavigatedView;
import com.falconit.joyform.shared.jsonconvert.ObjectConverter;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.IconPosition;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.MaterialCard;
import gwt.material.design.client.ui.MaterialCardAction;
import gwt.material.design.client.ui.MaterialCardContent;
import gwt.material.design.client.ui.MaterialCardTitle;
import gwt.material.design.client.ui.MaterialColumn;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialLoader;
import gwt.material.design.client.ui.MaterialRow;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DashboardView extends NavigatedView implements DashboardPresenter.MyView {
    interface Binder extends UiBinder<Widget, DashboardView> {
    }

    //@UiField
    //MaterialRow appsholder;
    
    @UiField
    MaterialLabel txtapps, txtworkflow, txtform, txtprogress, txtcompleted;
    
    private java.util.List<Object[]> lstProjects, lstProcesses;
    private List<Form> lstForms;
    private List<java.util.Map<String, Object[]>> lstTasks = new ArrayList<>();
            
    @Inject
    DashboardView(Binder uiBinder) {
        initWidget( uiBinder.createAndBindUi(this) );
        
                
        if( CookieHelper.getMyCookie( Constants.COOKIE_USER_ID ) == null ){
            History.newItem( NameTokens.login );
        }
        
        loadProjects();
        loadProcesses();
        loadForms();
        loadNoti();
        //processMappings();
        //processVariables();
    }

    private void loadProjects( ){
        try{
            ContainerManager manager = new ContainerManager();
            manager.setListener(new ContainerManagerListener(){
                @Override
                public void success(Map<String, Object[]> maps) {
                    //Window.alert("Project size = " + maps.size());
                    for( java.util.Map.Entry<String, Object[]> entry : maps.entrySet() ){
                        
                        lstProjects = (java.util.List<Object[]>) entry.getValue()[1];
                        txtapps.setText( lstProjects.size() +" active Apps");
                    }
                }

                @Override
                public void fail(String message, int stage) {
                }
            });
            
            manager.list();
        }catch(Exception ex){Window.alert(ex.getMessage());}
    }
    
        
    private void loadProcesses(){
        try{
            ProcessesManager manager = new ProcessesManager();
            manager.setListener(new ProcessesManagerListener(){
                @Override
                public void success(Map<String, Object[]> maps) {
                    
                    for( java.util.Map.Entry<String, Object[]> entry : maps.entrySet() ){
                        
                        lstProcesses = (java.util.List<Object[]>) entry.getValue()[1];
                        txtworkflow.setText( lstProcesses.size() +" active workflows");
                    }
                }

                @Override
                public void fail(String message, int stage) {
                }
            });
            
            manager.list( null, null );
        }catch(Exception ex){Window.alert(ex.getMessage());}
    }
    
        
    private void loadForms( ){
        
        FormCRUD crud = new FormCRUD();
        crud.setListener( new FormCRUD.CRUDListener(){
            @Override
            public void success(String result) {
            }

            @Override
            public void success(Form result) {
                
            }

            @Override
            public void success( List<Form> result ) {
                if( !result.isEmpty( ) ){
                    lstForms = result;
                    txtform.setText( lstForms.size( ) + " active forms" );
                }
            }

            @Override
            public void fail(String message) {
            }

            @Override
            public void fqdn( Map<String, Object[]> maps ) { }
        });
        String userId = CookieHelper.getMyCookie( Constants.COOKIE_USER_ID );
        crud.getBy( Long.parseLong( userId ) );
    }
    
        
    private void processMappings(){
        try{
            ProcessesVariablesMapping manager = new ProcessesVariablesMapping();
            manager.setListener(new ProcessesVariablesMappingListener(){
                @Override
                public void success(Map<String, Object[]> maps) {
                    Window.alert("Map size = " + maps.size());
                    for( java.util.Map.Entry<String, Object[]> entry : maps.entrySet() ){
                        Window.alert("List size = " + ((java.util.List<Object[]>) entry.getValue()[1]).size() );
                    }
                }

                @Override
                public void fail(String message, int stage) {
                }
            });
            
            manager.list( "DevTest_1.0.0-SNAPSHOT", "Leave-Request" );
        }catch(Exception ex){Window.alert(ex.getMessage());}
    }
    
        
    private void processVariables(){
        try{
            ProcessVariables manager = new ProcessVariables();
            manager.setListener(new ProcessVariablesListener(){
                @Override
                public void success(Map<String, Object[]> maps) {
                    Window.alert("Variables size = " + maps.size());
                }

                @Override
                public void fail(String message, int stage) {
                }
            });
            
            manager.list( "DevTest_1.0.0-SNAPSHOT", "Leave-Request" );
        }catch(Exception ex){Window.alert(ex.getMessage());}
    }
    
    
    private void load(){
        lstTasks.clear();
        MaterialLoader.loading( true );
        
        ProcessByContainer helper = new ProcessByContainer();
        helper.setListener(new ProcessByContainer.ProcessByContainerListener() {
            @Override
            public void success(String result) {
                
                MaterialLoader.loading( false );
                
                JSONObject jsonOnlineUser = JSONParser.parseStrict( result ).isObject();
                JSONArray tasks = jsonOnlineUser.get("processes").isArray();
                if( tasks == null || tasks.size() == 0 ){
                    
                }else{
                    for( int i=0; i < tasks.size(); i++){
                        JSONObject task = tasks.get(i).isObject();
                        
                        try {
                            java.util.Map<String, Object[]> taskMap = new ObjectConverter().fromJSON( task, false, false );
                            createProcess( taskMap );
                            lstTasks.add( taskMap );
                        } catch (Exception ex) {
                            Window.alert(ex.getMessage());
                        }
                    }
                }
            }

            @Override
            public void fail(String message, int stage) {
              Window.alert( message );
              MaterialLoader.loading( false );
            }
        });

        helper.processesList( "DevTest_1.0.0-SNAPSHOT", 0, 200, true );
    }
    
    private void createProcess( java.util.Map<String, Object[]> taskMap ){
        try{
            MaterialColumn col = new MaterialColumn();
            col.setGrid("l4 m4 s12");
            //appsholder.add(col);
            
            MaterialCard card = new MaterialCard();
            card.setBackgroundColor(Color.BLUE_GREY_DARKEN_3);
            col.add(card);
            
            MaterialCardContent content = new MaterialCardContent();
            card.add(content);
            content.setTextColor(Color.WHITE);
            
            MaterialCardTitle title = new MaterialCardTitle();
            content.add(title);
            title.setIconType(IconType.APPS);
            title.setIconPosition(IconPosition.RIGHT);
            title.setText( taskMap.get("process-name")[1].toString() );
            
            MaterialLabel label = new MaterialLabel();
            content.add(label);
            label.setText("Version: "+taskMap.get("process-version")[1].toString());
            
            MaterialCardAction actions = new MaterialCardAction();
            card.add(actions);
            
            MaterialLink link = new MaterialLink();
            actions.add(link);
            link.setText("Open");
            link.setIconType(IconType.OPEN_IN_NEW);
            link.setHref(
                "?container=" + taskMap.get("container-id")[1].toString()
                + "&process=" + taskMap.get("process-id")[1].toString()
                + "&title=" + taskMap.get("process-name")[1].toString()
                + "&taskName=Start up"
                + "&display=" + TaskDisplayView.DISPLAY_START_UP
                + "#taskdisplay" );
            
        }catch(Exception ex){}
    }
    
    private void taskSelected( java.util.Map<String, Object[]> map ){
        String container = map.get("task-container-id")[1].toString();
        String taskId = map.get("task-id")[1].toString();
        String processId = map.get("task-proc-def-id")[1].toString();
        String taskName = map.get("task-name")[1].toString();
        
        
        Window.Location.assign( "?container=" + container + "&taskId=" + taskId +"&process" + processId +"&taskName=" + taskName + "#taskdisplay" );
            
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
   
        private void loadNoti(){
            
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
                    int progressCount = 0;
                    int completedCount = 0;
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
                                        || (!createdBy.isEmpty() && createdBy.equals(userId +"-" + userName))){
                                    
                                    String status = taskMap.get("task-status")[1].toString();
                                    if( status.equalsIgnoreCase(APIHelper.STATUS_CREATED) 
                                            || status.equalsIgnoreCase(APIHelper.STATUS_READY)
                                            || status.equalsIgnoreCase(APIHelper.STATUS_INPROGRESS)
                                            || status.equalsIgnoreCase(APIHelper.STATUS_RESERVED)){
                                        progressCount++;
                                    }else if( status.equalsIgnoreCase(APIHelper.STATUS_COMPLETED) ){
                                        completedCount++;
                                    }
                                }
                            }
                            //12 tasks in progress, 108 tasks completed
                            txtprogress.setText( progressCount +" task" +( progressCount>1 ? "s in progress" : " in progress" ) );
                            txtcompleted.setText( completedCount +" task" +( completedCount>1 ? "s completed" : " completed" ) );
                            
                            //Window.alert("Task id = "+taskMap.get("task-id")[1].toString() +", Created on=" + (long)taskMap.get("task-created-on")[1]);
                        } catch (Exception ex) {
                            Window.alert(ex.getMessage());
                            Logger.getLogger(TasksListView.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                }
            }

            @Override
            public void fail(String message, int stage) {
              Window.alert( message );
              MaterialLoader.loading( false );
            }
        });
        String arrStatus[] = new String[]{ APIHelper.STATUS_CREATED, APIHelper.STATUS_READY, 
            APIHelper.STATUS_INPROGRESS, APIHelper.STATUS_RESERVED, APIHelper.STATUS_COMPLETED };

        
        helper.tasksList( 
                //new String[]{APIHelper.STATUS_READY,APIHelper.STATUS_RESERVED, APIHelper.STATUS_INPROGRESS },
                arrStatus,
                0, 1000, null, null, true);
        //helper.query( Constants.containerId, "355");
    }
    
  /*  
    @UiHandler("btnGotoFirstPage")
    void onGotoFirstPage(ClickEvent e) {
        pager.firstPage();
    }
*/
    
    
}
