package com.falconit.joyform.client.application.tasks.display;



import com.falconit.joyform.client.application.form.util.Field;
import com.falconit.joyform.client.application.form.util.Form;
import com.falconit.joyform.client.application.form.util.FormCRUD;
import com.falconit.joyform.client.application.profile.personal.PersonCRUD;
import com.falconit.joyform.client.application.util.Constants;
import com.falconit.joyform.client.application.util.CookieHelper;
import com.falconit.joyform.client.application.util.jbpmclient.api.process.ProcessesVariablesMapping;
import com.falconit.joyform.client.application.util.jbpmclient.api.task.TaskComplete;
import com.falconit.joyform.client.application.util.jbpmclient.api.task.TaskInputDataReader;
import com.falconit.joyform.client.application.util.jbpmclient.api.task.TaskInputDataReader.TaskInputDataReaderListener;
import com.falconit.joyform.client.application.util.jbpmclient.api.task.TaskInputDataRenderring;
import com.falconit.joyform.client.application.util.jbpmclient.api.task.TaskStarter;
import com.falconit.joyform.client.ui.NavigatedView;
import com.falconit.joyform.shared.jsonconvert.ObjectConverter;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialLoader;
import gwt.material.design.client.ui.MaterialPanel;
import gwt.material.design.client.ui.MaterialRow;
import gwt.material.design.client.ui.MaterialTab;
import gwt.material.design.client.ui.MaterialTabItem;
import gwt.material.design.client.ui.MaterialTitle;
import gwt.material.design.client.ui.animate.MaterialAnimation;
import gwt.material.design.client.ui.animate.Transition;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskDisplayView extends NavigatedView implements TaskDisplayPresenter.MyView {
    interface Binder extends UiBinder<Widget, TaskDisplayView> {
    }

    public Map<String, Object[]> inputMaps;
    public static final String DISPLAY_START_UP = "startup";
    public static final String DISPLAY_PROCESS = "process";
    public static final String DISPLAY = "display";
    
   private String container = "";
   private String taskId = "";
   private String taskName = "";
   private String process = "";
   private String display = "";
   private String title = "";
   private Form form;
   private String owner = "sysadmin@falconbreeze.com";
   private String ownerId = "1";
   private String auth = "true";
   
    @UiField
    MaterialRow comments, forwards, processing, startup;
    @UiField
    MaterialTab tab;
    @UiField
    MaterialTitle txtTitle, txtTitleReply;
    @UiField
    MaterialPanel holder, formHolder, fillup;
    
    //@UiField
    //MaterialTextArea txtbrief;

    @UiField
    MaterialButton btnsend, btnreply, btnreceivedOK;
    
    @UiField
    MaterialTabItem in, out, comment, foward;
    

    @Inject
    TaskDisplayView( Binder uiBinder ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        
        String value = com.google.gwt.user.client.Window.Location.getParameter( "container" );
        if( value != null ){
            container = value;
        }
        String pro = com.google.gwt.user.client.Window.Location.getParameter( "process" );
        if( pro != null ){
            process = pro;
        }
        String id = com.google.gwt.user.client.Window.Location.getParameter( "taskId" );
        if( id != null ){
            taskId = id;
        }
        String n = com.google.gwt.user.client.Window.Location.getParameter( "taskName" );
        if( n != null ){
            taskName = n;
        }
        String t = com.google.gwt.user.client.Window.Location.getParameter( "title" );
        if( t != null ){
            title = t;
        }
        String d = com.google.gwt.user.client.Window.Location.getParameter( "display" );
        if( d != null ){
            display = d;
        }
        String o = com.google.gwt.user.client.Window.Location.getParameter( "owner" );
        if( o != null ){
            owner = o;
        }
        String oID = com.google.gwt.user.client.Window.Location.getParameter( "ownerId" );
        if( oID != null ){
            ownerId = oID;
        }
        String au = com.google.gwt.user.client.Window.Location.getParameter( "auth" );
        if( au != null ){
            auth = au;
        }
        
        //tab.selectTab("out");
        
        if( display.equals(DISPLAY_PROCESS) ){
            taskSelected( );
            loadForm( );
            //new Comments( comments, container, taskId );
        }
        else
            startUp( );//loadOutput( );
    }


    private void startUp( ){
        String userName = CookieHelper.getMyCookie( Constants.COOKIE_USER_NAME );
        String userId =CookieHelper.getMyCookie( Constants.COOKIE_USER_ID );
        
        if( (userId == null || userName == null) && auth.equals("true") ){
            
            Window.Location.assign(
                "?container=" + container
                + "&process=" + process
                + "&title=" + title
                + "&taskName=" + title
                + "&owner=" + owner
                + "&ownerId=" + ownerId
                + "&display=" + TaskDisplayView.DISPLAY_START_UP
                + "#login" );
            return;
        }else if( (userId == null || userName == null) && auth.equals("false") ){
            
            userId = "0";
            userName = "anonymous";
        }
        
        MaterialLoader.loading( true );
        try {
            JSONObject initiate = new JSONObject( );
            initiate.put( "formowner", new JSONString( ownerId + "-" + owner ));
            initiate.put( "formuser", new JSONString( userId + "-" + userName ));
            
            String body = initiate.toString();
            
            TaskStarter start = new TaskStarter( );
            start.setListener(new TaskStarter.TaskStarterListener(){
                @Override
                public void success(String taskId) {
                    String taskInfo[] = taskId.split(":");
                    taskSelected( taskInfo[0], taskInfo[1] );
                }

                @Override
                public void fail(String message, int stage) {
                    MaterialLoader.loading( false );
                    Window.alert(message);
                }
            });
            
            start.startInstances( Constants.url, container, process, body, false );
        } catch (Exception ex) {
            MaterialLoader.loading( false );
            Logger.getLogger(TaskDisplayView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    private void taskSelected( String newTaskId, String newTaskName ){
        
        Window.Location.assign( "?container=" + container 
                + "&taskId=" + newTaskId 
                + "&process=" + process 
                + "&taskName=" + newTaskName 
                + "&display=" + TaskDisplayView.DISPLAY_PROCESS
                + "&owner=" + owner
                + "&ownerId=" + ownerId
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
    
    private void loadOutput( ){
        btnreply.setEnabled(false);
        MaterialLoader.loading( true );
        processing.setLayoutPosition(Style.Position.ABSOLUTE);
        processing.setVisibility(Style.Visibility.HIDDEN);
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
                //Window.alert("Success ");
                if( !result.isEmpty() ){
                    //Window.alert("Size");
                    form = result.get(0);
                    form.setDraggable( false );
                    
                    txtTitle.setTitle( form.getName() );
                    form.render( formHolder );
                    btnreply.setEnabled( true );
                }else{
                    Window.alert("You have no permission to use this Apps");
                    Window.Location.assign( "#privateapps" );
                }
                MaterialLoader.loading( false );
            }

            @Override
            public void fail(String message) {
                Window.alert("You have no permission to use this Apps");
                Window.Location.assign( "#privateapps" );
                MaterialLoader.loading( false );
            }

            @Override
            public void fqdn(Map<String, Object[]> maps) { }
        });
        
        crud.getBy( container, process, taskName, ownerId );
    }
    
    private void taskSelected( ){
        
        MaterialLoader.loading( true );
        startup.setLayoutPosition(Style.Position.ABSOLUTE);
        startup.setVisibility(Style.Visibility.HIDDEN);
        
        TaskInputDataReader reader = new TaskInputDataReader( );
        reader.setListener(new TaskInputDataReaderListener( ){
            @Override
            public void result(String taskName, String nodeName, String Skippable, String actor, Map<String, Object[]> maps) {
                //Window.alert("Task name=" + taskName + ", Node name=" + nodeName+", size=" + maps.size());
                if( !maps.isEmpty() ){
                    boolean found = false;
                    if( maps.containsKey(Constants.PARENT_TASK)){
                        String task = maps.remove( Constants.PARENT_TASK )[1].toString();
                        Window.alert("Refer task from variables="+ task );
                        String userId =CookieHelper.getMyCookie( Constants.COOKIE_USER_ID );
                        found = true;
                        
                        for( java.util.Map.Entry<String, Object[]> entry : maps.entrySet() ){
                            if( entry.getValue()[0].toString( ).equals( ObjectConverter.TYPE_OBJECT ) ){
                                for( java.util.Map.Entry<String, Object[]> entry1 : ((java.util.Map<String, Object[]>) entry.getValue()[1]).entrySet() ){
                                    inputMaps = (java.util.Map<String, Object[]>) entry1.getValue()[1];
                                }
                                if( inputMaps == null )
                                    inputMaps = maps;
                                
                                break;
                            }
                        }
                        
                        getForm( container, process, task, userId );
                    }else {
                        for( java.util.Map.Entry<String, Object[]> entry : maps.entrySet() ){
                            if( entry.getValue()[0].toString( ).equals( ObjectConverter.TYPE_OBJECT ) ){
                                String fqdn = entry.getKey();
                                outputTaskMappings(  container,  process );
                                found = true;
                                for( java.util.Map.Entry<String, Object[]> entry1 : ((java.util.Map<String, Object[]>) entry.getValue()[1]).entrySet() ){
                                    inputMaps = (java.util.Map<String, Object[]>) entry1.getValue()[1];
                                }
                                if( inputMaps == null )
                                    inputMaps = maps;
                                
                                break;
                            }
                        }
                    }
                    
                    
                    if( !found )
                        holder.add( new TaskInputDataRenderring().render( maps, "") );
                    
                }else{
                    //Window.alert("No input data");
                    //in.setVisible( false ); // auto commit here
                    tab.remove( in );
                    tab.setTabIndex( 0 );
                }
                MaterialLoader.loading( false );
                
            }

            @Override
            public void fail(String message) {
                MaterialLoader.loading( false );
            }
        });
        reader.read( container, taskId );
    }
       
    private void outputTaskMappings( String container, String processId ){
        try{
            MaterialLoader.loading( true );
            ProcessesVariablesMapping manager = new ProcessesVariablesMapping();
            manager.setListener(new ProcessesVariablesMapping.ProcessesVariablesMappingListener(){
                @Override
                public void success( Map<String, Object[]> maps ) {
                    String referTask = "";
                    
                    if( !maps.isEmpty()){
                        outer:for( java.util.Map.Entry<String, Object[]> entry : maps.entrySet() ){
                            java.util.List<Object[]> lstTasks = (java.util.List<Object[]>) entry.getValue()[1];

                            for( Object[] o : lstTasks ){
                                java.util.Map<String, Object[]> map = (java.util.Map<String, Object[]>) o[1];
                                String currentTask = map.get("task-name")[1].toString();
                                if( currentTask.equalsIgnoreCase(taskName)){
                                    break outer;
                                }
                                referTask = currentTask;
                            }
                        }
                    }
                    
                    MaterialLoader.loading( false );
                    Window.alert( "Refer task from processes=" + referTask );
                    if ( !referTask.isEmpty() ){
                        getForm( container, processId, referTask, ownerId );
                    }else{
                        holder.add( new TaskInputDataRenderring().render( inputMaps, "" ) );
                    }
                }

                @Override
                public void fail(String message, int stage) {
                    MaterialLoader.loading( true );
                }
            });
            
            manager.list( container, processId );
        }catch(Exception ex){Window.alert(ex.getMessage());}
    }
        
    private void getForm( String container, String process, String task, String oID ){
        
        MaterialLoader.loading( true );
        
        FormCRUD crud = new FormCRUD( );
        crud.setListener(new FormCRUD.CRUDListener( ){
            @Override
            public void success( String result ) { }

            @Override
            public void success( Form result ) { }

            @Override
            public void success(List<Form> result) {
                if( !result.isEmpty() ){
                    MaterialLoader.loading( false );
                    Form referForm = result.get( 0 );
                    referForm.setMode( Form.DISPLAY_MODE_READ_ONLY );
                    referForm.setMouseOverShadow( false );
                    try {
                        java.util.Map<String, Object[]> maps = new java.util.HashMap<>();
                        maps.put( referForm.getName(), new Object[] { ObjectConverter.TYPE_STRING, Constants.OBJECT_TITLE });
                        StringBuilder sb = new StringBuilder();
                        
                        for( Field field : referForm.getChild()){
                            try{
                            
                            if( inputMaps.containsKey(field.getName())){
                                Object[] values = inputMaps.remove(field.getName());
                                
                                if( field.getName().equals("title") 
                                        || field.getName().equals("firstname") 
                                        || field.getName().equals("middlename") 
                                        || field.getName().equals("lastname")){
                                    sb.append( values[1] + " ");
                                    
                                    if( field.getName().equals("lastname") ){
                                        maps.put( "Name", 
                                                new Object[] { ObjectConverter.TYPE_STRING, 
                                                    sb.toString() });
                                    }
                                }else {
                                    Object[] newValues = new Object[]{values[0], values[1], field.getName()};
                                    maps.put( field.getLabel().get(Constants.LANGUAGE), newValues );
                                }
                            }
                            }catch(Exception ex){Window.alert(ex.getMessage());}
                        }
                        holder.add( new TaskInputDataRenderring().render( maps, "") );
                        //referForm.bindWithTaskData( inputMaps );
                    } catch (Exception ex) {
                        Window.alert( ex.getMessage() );
                    }
                    //referForm.render(holder);
        
                }else{
                    holder.add( new TaskInputDataRenderring().render( inputMaps, "") );
                    MaterialLoader.loading( false );
                    //Window.alert("No existing record");
                }
            }

            @Override
            public void fail(String message) {
                Window.alert( "Result = " + message );
            }

            @Override
            public void fqdn(Map<String, Object[]> maps) {}
        });
        
        crud.getBy( container, process, task, oID );
    }

    private void loadForm( ){
        
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
                    form = result.get(0);
                    form.setDraggable( false );
                    
                    txtTitleReply.setTitle(form.getName());
                    if( CookieHelper.getMyCookie( Constants.COOKIE_USER_PERSON_ID ) != null ){
                        long customerId = Long.parseLong(CookieHelper.getMyCookie(Constants.COOKIE_USER_PERSON_ID));
                        getPerson( customerId );
                    }else{
                        form.render( fillup );
                    }
                    
                    solveFQDN( form.getFqdn() );
                }else{
                    tab.remove( out );
                    tab.setTabIndex(0);
                    btnreceivedOK.setVisible(true);
                    //out.setVisible( false ); // auto commit here
                }
            }

            @Override
            public void fail(String message) {
            }

            @Override
            public void fqdn(Map<String, Object[]> maps) { }
        });
        
        crud.getBy( container, process, taskName, ownerId );
    }
    
          
    private void getPerson( long customerId ){
        MaterialLoader.loading( true );
        PersonCRUD crud = new PersonCRUD();
        crud.setListener(new PersonCRUD.CRUDListener(){
            @Override
            public void success(String result) {
                //Window.alert( "Result = " + result );
            }

            @Override
            public void fail(String message) {
                MaterialLoader.loading( false );
                Window.alert( "Result = " + message );
            }

            @Override
            public void fqdn(Map<String, Object[]> maps) {}

            @Override
            public void success(Map<String, Object[]> result) {
                MaterialLoader.loading( false );
                try {
                    form.bindWithTaskData( result );
                } catch (Exception ex) {  }
                form.render( fillup );
            }

            @Override
            public void success(List<Map<String, Object[]>> result) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        
        crud.get( customerId );
    }
    
    @UiHandler("btnsend")
    void onSend(ClickEvent e) {
        MaterialLoader.loading( true );
        try {
            String body = form.getOutputDataForTask().toString();
            //txtbrief.setText( body );
            //Window.alert( form.getOutputDataForTask().toString() );
            TaskStarter start = new TaskStarter();
            start.setListener(new TaskStarter.TaskStarterListener(){
                @Override
                public void success(String processInstanceId) {
                    MaterialLoader.loading( false );
                    btnsend.setBackgroundColor( Color.GREEN );
                    animate( btnsend );
                    resetColor( btnsend );
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
    
    //@UiHandler("btnreply")
    @UiHandler({"btnreply", "btnreceivedOK"})
    void onReply(ClickEvent e) {
        
        
        MaterialLoader.loading( true );
        try {
            String body = "{}";
            if( form != null ){
                body = form.getOutputDataForTask().toString();
                //txtbrief.setText( body );
            }
            //Window.alert( form.getOutputDataForTask().toString() );
            TaskComplete complete = new TaskComplete();
            complete.setListener(new TaskComplete.TaskCompleteListener(){
                @Override
                public void success(String processInstanceId) {
                    MaterialLoader.loading( false );
                    btnreply.setBackgroundColor( Color.GREEN );
                    animate( btnreply );
                    resetColor( btnreply );
                }

                @Override
                public void fail(String message, int stage) {
                    MaterialLoader.loading( false );
                    Window.alert(message);
                }
            });
            
            complete.released( Constants.url, container, taskId, body );
        } catch (Exception ex) {
            MaterialLoader.loading( false );
            Logger.getLogger(TaskDisplayView.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }    
         
    private void solveFQDN( String fqdn ){
        MaterialLoader.loading( true );
        FormCRUD crud = new FormCRUD();
        crud.setListener(new FormCRUD.CRUDListener(){
            @Override
            public void success(String result) { }

            @Override
            public void success(Form result) { }

            @Override
            public void success(List<Form> result) { }

            @Override
            public void fail(String message) {
                MaterialLoader.loading( false );
                Window.alert( "Result = " + message );
            }

            @Override
            public void fqdn(Map<String, Object[]> maps) {
                MaterialLoader.loading( false );
                
                preprocess( maps );
            }
        });
        
        String query ="from " + fqdn.substring( fqdn.lastIndexOf(".") + 1) +" f where f.id=1"; 
        
        crud.solveFQDN( query, fqdn );
    }
   
    private void preprocess( Map<String, Object[]> maps ){
   
        for( java.util.Map.Entry<String, Object[]> entry : maps.entrySet() ){
            if( form.contain( entry.getKey()) == null ){
                
                Field f = new Field( entry.getKey(), null, entry.getKey() );
                f.setHide(true);
                f.setWidgetType("hide");
                Field child = new Field( entry.getKey(), null, entry.getKey() );
                child.setHide(true);
                child.setWidgetType("hide");
                f.getChildren().add( child );
                form.getChild().add(f);
            }
        }
    }
    
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
