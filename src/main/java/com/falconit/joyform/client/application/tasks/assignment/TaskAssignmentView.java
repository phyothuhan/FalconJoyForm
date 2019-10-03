package com.falconit.joyform.client.application.tasks.assignment;



import com.falconit.joyform.client.application.form.util.Form;
import com.falconit.joyform.client.application.form.util.FormCRUD;
import com.falconit.joyform.client.application.tasks.display.TaskDisplayView;
import com.falconit.joyform.client.application.util.Constants;
import com.falconit.joyform.client.application.util.CookieHelper;
import com.falconit.joyform.client.application.util.jbpmclient.api.process.ProcessByContainer;
import com.falconit.joyform.client.application.util.jbpmclient.api.task.TaskStarter;
import com.falconit.joyform.client.place.NameTokens;
import com.falconit.joyform.client.ui.NavigatedView;
import com.falconit.joyform.shared.jsonconvert.ObjectConverter;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.IconPosition;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialCard;
import gwt.material.design.client.ui.MaterialCardAction;
import gwt.material.design.client.ui.MaterialCardContent;
import gwt.material.design.client.ui.MaterialCardTitle;
import gwt.material.design.client.ui.MaterialColumn;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialLoader;
import gwt.material.design.client.ui.MaterialRow;
import gwt.material.design.client.ui.MaterialTextBox;
import gwt.material.design.client.ui.animate.MaterialAnimation;
import gwt.material.design.client.ui.animate.Transition;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskAssignmentView extends NavigatedView implements TaskAssignmentPresenter.MyView {
    interface Binder extends UiBinder<Widget, TaskAssignmentView> {
    }

    private String container = "";
    private String taskId = "";
    private String taskName = "";
    private String process = "";
    private String display = "";
    private String title = "";
   
    @UiField
    MaterialRow appsholder;
    
    @UiField
    MaterialTextBox txtactors, txtgroups;
    
    @UiField
    MaterialButton btnassign, btncancel;
    
    private List<java.util.Map<String, Object[]>> lstTasks = new ArrayList<>();
            
    @Inject
    TaskAssignmentView(Binder uiBinder) {
        initWidget( uiBinder.createAndBindUi(this) );
                
                
        if( CookieHelper.getMyCookie( Constants.COOKIE_USER_ID ) == null ){
            History.newItem( NameTokens.login );
        }
                
        String value = com.google.gwt.user.client.Window.Location.getParameter("container");
        if( value != null ){
            container = value;
        }
        String pro = com.google.gwt.user.client.Window.Location.getParameter("process");
        if( pro != null ){
            process = pro;
        }
        String id = com.google.gwt.user.client.Window.Location.getParameter("taskId");
        if( id != null ){
            taskId = id;
        }
        String n = com.google.gwt.user.client.Window.Location.getParameter("taskName");
        if( n != null ){
            taskName = n;
        }
        String t = com.google.gwt.user.client.Window.Location.getParameter("title");
        if( t != null ){
            title = t;
        }
        String d = com.google.gwt.user.client.Window.Location.getParameter("display");
        if( d != null ){
            display = d;
        }
        //load();
        
        txtactors.setHelperText("Use semi column(;) for multiple recipients");
        txtgroups.setHelperText("Use semi column(;) for multiple groups");
        loadOutput( );        
    }

        
    private void loadOutput(){
        MaterialLoader.loading( true );
        
        FormCRUD crud = new FormCRUD();
        crud.setListener(new FormCRUD.CRUDListener(){
            @Override
            public void success(String result) {
            }

            @Override
            public void success(Form result) {
                
            }

            @Override
            public void success(List<Form> result) {
                if( !result.isEmpty() ){
                    
                    Window.Location.assign(
                "?container=" + container
                + "&process=" + process
                + "&title=" + title
                + "&taskName=" + Form.TASK_NAME_START_UP
                + "&display=" + TaskDisplayView.DISPLAY_START_UP
                + "#taskdisplay" );
                    
                }else{
                    
                }
                MaterialLoader.loading( false );
            }

            @Override
            public void fail(String message) {
                MaterialLoader.loading( false );
            }

            @Override
            public void fqdn(Map<String, Object[]> maps) { }
        });
        
        crud.getBy( container, process, taskName );
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
            appsholder.add(col);
            
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
                + "&taskName=" + Form.TASK_NAME_START_UP
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
    
    @UiHandler({ "btncancel"})
    void onCancel(ClickEvent e){
        Window.Location.assign("#dashboard");
    }
    
        
    @UiHandler({ "btnassign"})
    void onAssign(ClickEvent e){
                
        MaterialLoader.loading( true );
        try {
            String body = "{}";
            //txtbrief.setText( body );
            //Window.alert( form.getOutputDataForTask().toString() );
            TaskStarter start = new TaskStarter();
            start.setListener(new TaskStarter.TaskStarterListener(){
                @Override
                public void success(String processInstanceId) {
                    MaterialLoader.loading( false );
                    btnassign.setBackgroundColor( Color.GREEN );
                    animate( btnassign );
                    resetColor( btnassign );
                }

                @Override
                public void fail(String message, int stage) {
                    MaterialLoader.loading( false );
                    Window.alert(message);
                }
            });
            
            start.startInstances( Constants.url, container, process, body, true );
        } catch (Exception ex) {
            MaterialLoader.loading( false );
            Logger.getLogger(TaskDisplayView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
  /*  
    @UiHandler("btnGotoFirstPage")
    void onGotoFirstPage(ClickEvent e) {
        pager.firstPage();
    }
*/
    
        
    private void animate( Widget widget ){
        MaterialAnimation animation = new MaterialAnimation();
        animation.setTransition(Transition.BOUNCEOUT);
        animation.setDelay( 100 );
        animation.setDuration( 1000 * 2 );
        animation.setInfinite( false );        
        animation.animate( widget );
    }
    
    private void resetColor( MaterialButton widget ){
        new Timer(){
            @Override
            public void run() {
                widget.setBackgroundColor( Color.BLUE );
                Window.Location.assign( "#tasklist" );
            }
        }.schedule( 3500 );
    }
}
