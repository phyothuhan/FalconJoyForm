/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.falconit.joyform.client.application.form.util;

import com.falconit.joyform.client.application.util.Constants;
import com.falconit.joyform.client.application.util.jbpmclient.HumanTaskHelper;
import com.falconit.joyform.shared.jsonconvert.ObjectConverter;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Window;

/**
 *
 * @author User
 */
public class FormCRUD {
    /**
     * 
     * @param form created form object to save or update
     * @param isSave true for saving (don't add Id allow it null)
     */
    public void saveUpdate( Form form, boolean isSave ){
        try{
            
            if( isSave )
                form.setId( null );
            java.util.Map<String, Object[]> maps = fillData( form );
            
            JSONObject json = new ObjectConverter().toJSON( maps );
            
            HumanTaskHelper helper = new HumanTaskHelper();
            helper.setListener(new HumanTaskHelper.HumanTaskHelperListener() {
                @Override
                public void success(String result) {

                    JSONObject jsonOnlineUser = JSONParser.parseStrict( result ).isObject();

                    JSONObject obj = jsonOnlineUser.get("object").isObject();
                    JSONObject objForm = obj.get("com.falconit.automation.entity.Form").isObject();
                    try {
                        if( objForm != null ){
                            listener.success("Success");
                            if( isSave ){
                                java.util.Map<String, Object[]> maps = new ObjectConverter().fromJSON( objForm, false, false );
                                    form.setId( getFormBack( maps ).getId() );
                            }
                        }else{
                            listener.fail( "Failed to save" );
                        }
                    } catch (Exception ex) {
                        listener.fail( ex.getMessage() );
                    }
                }

                @Override
                public void fail(String message, int stage) {
                    listener.fail( message );
                }
            });

            JSONObject objForm = new JSONObject();
            objForm.put("Form", json);

            JSONObject obj = new JSONObject();
            if( isSave ){
                obj.put("action", new JSONString( "create" ));
            } else{
                obj.put("action", new JSONString( "update" ));
            }
            
            obj.put("object", objForm);
          
            helper.startInstances( Constants.formProcessId, obj.toString( ) );
        }catch(Exception ex){ex.printStackTrace();}
    }

    /**
     * Extract with container name
     * @param contaner  container name
     */
    public void getBy(String contaner ){
        getBy( contaner, null, null, -1, 0, null );
    }
    
        
    public void getBy( long ownerId ){
        getBy( null, null, null, -1, 0, ownerId+"" );
    }
    /**
     * Extract with container name and process name
     * @param contaner container name
     * @param process process name
     */
    public void getBy(String contaner, String process){
        getBy( contaner, process, null, -1, 0, null );
    }
    
    /**
     * Extract with container name, process name and task name
     * @param contaner container name
     * @param process process name
     * @param task task name
     */
   
    public void getBy(String contaner, String process, String task ){
        getBy( contaner, process, task, -1, 0, null );
    }
    
        
    public void getBy(String contaner, String process, String task, String ownerId ){
        getBy( contaner, process, task, -1, 0, ownerId );
    }
    
    /**
     * Extract with container name, process name and task with limit and offset
     * @param contaner container name or null for all forms
     * @param process process name, use null when container name was null
     * @param task task name, use null when container name and process name null
     * @param first offset value
     * @param max limit value
     */
    public void getBy(String contaner, String process, String task, int first, int max, String ownerId  ){
        java.util.List<Form> lst = new java.util.ArrayList<>( );
        
        try{
            HumanTaskHelper helper = new HumanTaskHelper();
            helper.setListener(new HumanTaskHelper.HumanTaskHelperListener() {
                @Override
                public void success(String result) {

                    JSONObject jsonOnlineUser = JSONParser.parseStrict( result ).isObject();
                    JSONArray groups = jsonOnlineUser.get("results").isArray();
                    
                    if( groups != null && groups.size() > 0 ){
                                            
                        for( int i=0; i < groups.size(); i++){
                            
                            JSONObject obj = groups.get(i).isObject();
                            JSONObject objForm = obj.get("com.falconit.automation.entity.Form").isObject();
                            try {
                                if( objForm != null ){
                                    listener.success("Success");
                                    
                                    //Window.alert(objForm.toString());///////////////
                                    java.util.Map<String, Object[]> maps = new ObjectConverter().fromJSON( objForm, false, false );
                                    //Window.alert("Map size=" + maps.size());
                                    lst.add( getFormBack( maps ) );
                                }else{
                                    listener.fail( "Failed to get" );
                                }
                            } catch (Exception ex) {
                                listener.fail( ex.getMessage() );
                            }
                        }
                    }
                        
                    listener.success( lst );
                }

                @Override
                public void fail(String message, int stage) {
                    listener.fail( message );
                }
            });

            JSONObject obj = new JSONObject();
            obj.put("action", new JSONString( "query" ));
            
            StringBuffer sb = new StringBuffer();
            sb.append("from Form f");
            if( contaner!=null && !contaner.isEmpty()){
                sb.append(" where f.container='" + contaner + "'");
            }
            
            if( process != null && !process.isEmpty() ){
                sb.append(" AND f.process='" + process + "'");
            }
            
            if( task != null && !task.isEmpty() ){
                sb.append( " AND f.task='" + task + "'" );
            }
            
            if( ownerId != null && !ownerId.isEmpty() ){
                if( sb.toString().contains("where"))
                    sb.append( " AND f.owner=" + ownerId + "" );
                else
                    sb.append( " where f.owner=" + ownerId + "" );
            }
            
                if( sb.toString().contains("where"))
                    sb.append( " AND f.status=1" );
                else
                    sb.append( " where f.status=1" );
            //sb.append( " WHERE f.status=1" );
            obj.put("query", new JSONString( sb.toString() ));
            
            if( max > 0)
                obj.put("limit", new JSONNumber( max ) );
            if( first > -1)
                obj.put("offset", new JSONNumber( first ) );

            //Window.alert( "Request=" + obj.toString() );
            helper.startInstances( Constants.formProcessId, obj.toString( ) );
        }catch(Exception ex){ex.printStackTrace();}
    }

    public void list( ){
        list( -1, 0 );
    }
    
    public void list( int first, int max  ){
        java.util.List<Form> lst = new java.util.ArrayList<>();
        
        try{
            HumanTaskHelper helper = new HumanTaskHelper();
            helper.setListener(new HumanTaskHelper.HumanTaskHelperListener() {
                @Override
                public void success(String result) {

                    JSONObject jsonOnlineUser = JSONParser.parseStrict( result ).isObject();
                    JSONArray groups = jsonOnlineUser.get("results").isArray();
                    
                    if( groups != null && groups.size() > 0 ){
                                            
                        for( int i=0; i < groups.size(); i++){
                            
                            JSONObject obj = groups.get(i).isObject();
                            JSONObject objForm = obj.get("com.falconit.automation.entity.Form").isObject();
                            try {
                                if( objForm != null ){
                                    listener.success("Success");
                                    java.util.Map<String, Object[]> maps = new ObjectConverter().fromJSON( objForm, false, false );
                                    lst.add( getFormBack( maps ) );
                                }else{
                                    listener.fail( "Failed to get" );
                                }
                            } catch (Exception ex) {
                                listener.fail( ex.getMessage() );
                            }
                        }
                        
                        listener.success( lst );
                    }
                }

                @Override
                public void fail(String message, int stage) {
                    listener.fail( message );
                }
            });

            JSONObject obj = new JSONObject();
            obj.put("action", new JSONString( "list" ));
            
            if( max > 0)
                obj.put("limit", new JSONNumber( max ) );
            if( first > -1)
                obj.put("offset", new JSONNumber( first ) );

            helper.startInstances( Constants.formProcessId, obj.toString( ) );
        }catch(Exception ex){ex.printStackTrace();}
    }
    
    
    public void get( long formId ){
        try{
            
            JSONObject json = new JSONObject();
            json.put( Form.JSON_FORM_ID, new JSONNumber(formId) );
            
            HumanTaskHelper helper = new HumanTaskHelper();
            helper.setListener(new HumanTaskHelper.HumanTaskHelperListener() {
                @Override
                public void success(String result) {

                    JSONObject jsonOnlineUser = JSONParser.parseStrict( result ).isObject();

                    JSONObject obj = jsonOnlineUser.get("object").isObject();
                    JSONObject objForm = obj.get("com.falconit.automation.entity.Form").isObject();
                    try {
                        if( objForm != null ){
                            listener.success("Success");
                            java.util.Map<String, Object[]> maps = new ObjectConverter().fromJSON( objForm, false, false );
                            listener.success( getFormBack( maps ) );
                        }else{
                            listener.fail( "Failed to get" );
                        }
                    } catch (Exception ex) {
                        listener.fail( ex.getMessage() );
                    }
                }

                @Override
                public void fail(String message, int stage) {
                    listener.fail( message );
                }
            });

            JSONObject objForm = new JSONObject( );
            objForm.put("Form", json);

            JSONObject obj = new JSONObject();
            obj.put("action", new JSONString( "get" ));
            obj.put("object", objForm);

            helper.startInstances( Constants.formProcessId, obj.toString( ) );
        }catch(Exception ex){
            Window.alert(ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private java.util.Map<String, Object[]> fillData( Form form ){
        java.util.Map<String, Object[]> maps = new java.util.HashMap<>();
        
        try{
            if( form.getId() != null ){
                maps.put( Form.JSON_FORM_ID, new Object[] { ObjectConverter.TYPE_NUMBER, form.getId()} );
            }

            maps.put( Form.JSON_FORM_NAME, new Object[] { ObjectConverter.TYPE_STRING, form.getName()} );

            maps.put( Form.JSON_FORM_CONTAINER_ID, new Object[] { ObjectConverter.TYPE_STRING, form.getContainer()} );
            maps.put( Form.JSON_PROCESS_ID, new Object[] { ObjectConverter.TYPE_STRING, form.getProcess()} );
            maps.put( Form.JSON_TASK_ID, new Object[] { ObjectConverter.TYPE_STRING, form.getTask()} );
            maps.put( Form.JSON_FORM_DATA, new Object[] { ObjectConverter.TYPE_STRING, form.toJSON().toString()} );
            
            StringBuffer sb = new StringBuffer();
            for( String actor : form.getActors() ){
                sb.append(sb.toString().isEmpty() ? actor : "," + actor);
            }
            maps.put( Form.JSON_FORM_PERMISSION_ACTORS, new Object[] { ObjectConverter.TYPE_STRING, sb.toString()} );
            
            sb = new StringBuffer();
            for( String group : form.getGroups() ){
                sb.append( sb.toString().isEmpty() ? group : "," + group );
            }
            maps.put( Form.JSON_FORM_PERMISSION_GROUPS, new Object[] { ObjectConverter.TYPE_STRING, sb.toString()} );
            
            maps.put( Form.JSON_FORM_CREATED, new Object[] { ObjectConverter.TYPE_TIMESTAMP, 
                form.getCreated() == null ? new java.util.Date().getTime() : form.getCreated().getTime()} );
            
            maps.put( Form.JSON_FORM_UPDATED, new Object[] { ObjectConverter.TYPE_TIMESTAMP, 
                form.getUpdated() == null ? new java.util.Date().getTime() : form.getUpdated().getTime()} );
            
            maps.put( Form.JSON_FORM_STATUS, new Object[] { ObjectConverter.TYPE_NUMBER, form.getStatus()} );
            
            maps.put( Form.JSON_FORM_OBJECT_NAME, new Object[] { ObjectConverter.TYPE_STRING, form.getObjectName()} );
            
            maps.put( Form.JSON_FORM_FQDN, new Object[] { ObjectConverter.TYPE_STRING, form.getFqdn()} );
            
            maps.put( Form.JSON_FORM_OWNER, new Object[] { ObjectConverter.TYPE_NUMBER, form.getOwner()} );
        
        }catch(Exception ex){}
        
        return maps;
    }

    private Form getFormBack( java.util.Map<String, Object[]> maps ){
        Form form = new Form();
        
        try{
            form.setId( (long) maps.get( Form.JSON_FORM_ID )[1] );
            //Window.alert( maps.get( Form.JSON_FORM_ID )[1].toString() );
            form.setName( (String) maps.get( Form.JSON_FORM_NAME )[1] );
            //Window.alert( maps.get( Form.JSON_FORM_NAME )[1].toString() );
            form.setContainer( (String) maps.get( Form.JSON_FORM_CONTAINER_ID )[1] );
            //Window.alert( maps.get( Form.JSON_FORM_CONTAINER_ID )[1].toString() );
            form.setProcess( (String) maps.get( Form.JSON_PROCESS_ID )[1] );
            //Window.alert( maps.get( Form.JSON_PROCESS_ID )[1].toString() );
            form.setTask( (String) maps.get( Form.JSON_TASK_ID )[1] );
            //Window.alert( maps.get( Form.JSON_TASK_ID )[1].toString() );
            //form.setContainer( (String) maps.get( Form.JSON_FORM_DATA )[1] );
            
            form.setFqdn( (String) maps.get( Form.JSON_FORM_FQDN )[1] );
            //Window.alert( maps.get( Form.JSON_FORM_FQDN )[1].toString() );
            form.setObjectName( (String) maps.get( Form.JSON_FORM_OBJECT_NAME )[1] );
            //Window.alert( maps.get( Form.JSON_FORM_OBJECT_NAME )[1].toString() );
            form.setCreated( new java.util.Date( (long) maps.get( Form.JSON_FORM_CREATED )[1]) );
            //Window.alert( maps.get( Form.JSON_FORM_CREATED )[1].toString() );
            form.setUpdated(new java.util.Date( (long) maps.get( Form.JSON_FORM_UPDATED )[1] ));
            //Window.alert( maps.get( Form.JSON_FORM_UPDATED )[1].toString() );
            
            form.setActors( ((String) maps.get( Form.JSON_FORM_PERMISSION_ACTORS )[1]).split(",") );
            form.setGroups( ((String) maps.get( Form.JSON_FORM_PERMISSION_GROUPS )[1]).split(",") );
            form.setStatus( Integer.parseInt( maps.get( Form.JSON_FORM_STATUS )[1].toString()) );
            form.setOwner( Long.parseLong( maps.get( Form.JSON_FORM_OWNER)[1].toString()));
            
//Window.alert( maps.get( Form.JSON_FORM_STATUS )[1].toString() );
            
            //Window.alert("Attributes OK");
            form.fromJSON( JSONParser.parseStrict( (String) maps.get( Form.JSON_FORM_DATA )[1] ).isObject() );
             //Window.alert("Pass back OK");
            
        }catch(Exception ex){}
        
        return form;
    }
    
    
        
    public void solveFQDN( String query, String fqdn  ){
        
        try{
            HumanTaskHelper helper = new HumanTaskHelper();
            helper.setListener(new HumanTaskHelper.HumanTaskHelperListener() {
                @Override
                public void success(String result) {

                    JSONObject jsonOnlineUser = JSONParser.parseStrict( result ).isObject();
                    JSONArray groups = jsonOnlineUser.get("results").isArray();
                    
                    if( groups != null && groups.size() > 0 ){
                                            
                        for( int i=0; i < groups.size(); i++){
                            
                            JSONObject obj = groups.get(i).isObject();
                            JSONObject objForm = obj.get( fqdn ).isObject();
                            try {
                                if( objForm != null ){
                                    listener.success("Success");
                                    java.util.Map<String, Object[]> maps = new ObjectConverter().fromJSON( objForm, false, false );
                                    listener.fqdn( maps );
                                }else{
                                    listener.fail( "Failed to get" );
                                }
                            } catch (Exception ex) {
                                listener.fail( ex.getMessage() );
                            }
                        }
                        
                    }
                }

                @Override
                public void fail(String message, int stage) {
                    listener.fail( message );
                }
            });

            JSONObject obj = new JSONObject();
            obj.put("action", new JSONString( "query" ));
            obj.put("query", new JSONString( query ));

            helper.startInstances( Constants.formProcessId, obj.toString( ) );
        }catch(Exception ex){ex.printStackTrace();}
    }
     
    
    public interface CRUDListener{
        public void success(String result);
        public void success(Form result);
        public void success(java.util.List<Form> result);
        public void fail( String message);
        public void fqdn( java.util.Map<String, Object[]> maps );
    }
    
    private CRUDListener listener;
    public void setListener( CRUDListener listener ){
        this.listener = listener;
    }
}
