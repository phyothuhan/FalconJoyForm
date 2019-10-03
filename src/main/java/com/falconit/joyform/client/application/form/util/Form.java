/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.falconit.joyform.client.application.form.util;


import com.falconit.joyform.client.application.form.util.WidgetGenerator.WidgetGeneratorButtonClickListener;
import com.falconit.joyform.client.application.form.util.WidgetGenerator.WidgetGeneratorMouseListener;
import com.falconit.joyform.client.application.util.Constants;
import com.falconit.joyform.shared.jsonconvert.ObjectConverter;
import com.google.gwt.dom.client.Style;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.addins.client.dnd.MaterialDnd;
import gwt.material.design.addins.client.dnd.js.JsDragOptions;
import gwt.material.design.client.base.MaterialWidget;
import gwt.material.design.client.constants.Axis;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.ui.MaterialCollapsible;
import gwt.material.design.client.ui.MaterialCollapsibleBody;
import gwt.material.design.client.ui.MaterialCollapsibleHeader;
import gwt.material.design.client.ui.MaterialCollapsibleItem;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialPanel;
import gwt.material.design.client.ui.MaterialRow;
import gwt.material.design.client.ui.MaterialToast;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


/**
 *
 * @author User
 */
public class Form implements java.io.Serializable{
    
        public static final String TASK_NAME_START_UP = "Start up";
        public static final String JSON_FORM_CHILD = "child";
        public static final String JSON_FORM_ID = "id";
        public static final String JSON_FORM_NAME = "name";
        public static final String JSON_FORM_CONTAINER_ID = "container";
        public static final String JSON_PROCESS_ID = "process";
        public static final String JSON_TASK_ID = "task";
        public static final String JSON_FORM_DATA = "formdata";
        public static final String JSON_FORM_PERMISSION_ACTORS = "actors";
        public static final String JSON_FORM_PERMISSION_GROUPS = "groups";
        public static final String JSON_FORM_CREATED = "created";
        public static final String JSON_FORM_UPDATED = "updated";
        public static final String JSON_FORM_STATUS = "status";
        public static final String JSON_FORM_OWNER = "owner";
        public static final String JSON_FORM_OBJECT_NAME = "objectName";
        public static final String JSON_FORM_FQDN = "fqdn";
        public static final String JSON_FORM_ITEMS_DISPLAY = "itemsDisplay";
        public static final String JSON_FORM_TIMER = "useTimer";
        public static final String JSON_FORM_TIMER_DURATION = "timerDuration";
        public static final String JSON_FORM_BACKGROUND = "background";
        
        
        public static final String ITEMS_DISPLAY_ONE_AFTER_ANOTHER = "oneAfterAnother";
        public static final String ITEMS_DISPLAY_UP_DOWN = "upDown";
        public static final String ITEMS_DISPLAY_CATEGORIZE = "categories";
        
        public static final String DISPLAY_MODE_READ_ONLY = "readonly";
        public static final String DISPLAY_MODE_FILL_UP = "fillup";
        public static final String DISPLAY_MODE_NO_FRAME_FILL_UP = "noframe";
        public static final String DISPLAY_MODE_DESIGNER = "designer";
        
        private Long id;
        private String name;
        private String container;
        private String process;
        private String task;
        private long owner=1;
        private String mode = DISPLAY_MODE_FILL_UP;
        private boolean mouseOverShadow = false;
        private java.util.Date created;
        private java.util.Date updated;
        private String[] actors;
        private String[] groups;
        private int status = 1;
        private String objectName="";
        private String fqdn="";
        
        
        private String itemDisplay= ITEMS_DISPLAY_UP_DOWN;
        private boolean useTimer = false;
        private long timerDuration = 0;
        private String background = "";
        
        private boolean draggable = true;
        private java.util.List<Field> child = new java.util.ArrayList<>();
        
    public interface FormItemListener{
        public void onClick( Field field, int index );
        public void mouseEnter( Field field, int index );
        public void mouseExit( Field field, int index );
        public void onEditClick( Field field, int index );
        public void onDeleteClick( Field field, int index );
        public void onUpClick( Field field, int index );
        public void onDownClick( Field field, int index );
    }
    
    private FormItemListener itemListener;
    public void setItemListener(FormItemListener itemListener){
        this.itemListener = itemListener;
    }
    
    public Form() {
    }

        
    public Form(Long id, String name, String container, String process, String task) {
        this.id = id;
        this.name = name;
        this.container = container;
        this.process = process;
        this.task = task;
    }

        
    public Form(String name, String container, String process, String task) {
        this.name = name;
        this.container = container;
        this.process = process;
        this.task = task;
    }

    public Form(String name, String container, String process, String task, List<Field> child) {
        this.name = name;
        this.container = container;
        this.process = process;
        this.task = task;
        this.child = child;
    }

    public Form(Long id, String name, String container, String process, String task, List<Field> child) {
        this.id = id;
        this.name = name;
        this.container = container;
        this.process = process;
        this.task = task;
        this.child = child;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String[] getActors() {
        return actors;
    }

    public void setActors(String[] actors) {
        this.actors = actors;
    }

    public String[] getGroups() {
        return groups;
    }

    public void setGroups(String[] groups) {
        this.groups = groups;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getFqdn() {
        return fqdn;
    }

    public void setFqdn(String fqdn) {
        this.fqdn = fqdn;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public boolean isDraggable() {
        return draggable;
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }

    public boolean isMouseOverShadow() {
        return mouseOverShadow;
    }

    public void setMouseOverShadow(boolean mouseOverShadow) {
        this.mouseOverShadow = mouseOverShadow;
    }

    public List<Field> getChild() {
        return child;
    }

    public void setChild(List<Field> child) {
        this.child = child;
    }

    public String getItemDisplay() {
        return itemDisplay;
    }

    public void setItemDisplay(String itemDisplay) {
        this.itemDisplay = itemDisplay;
    }

    public boolean isUseTimer() {
        return useTimer;
    }

    public void setUseTimer(boolean useTimer) {
        this.useTimer = useTimer;
    }

    public long getTimerDuration() {
        return timerDuration;
    }

    public void setTimerDuration(long timerDuration) {
        this.timerDuration = timerDuration;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public long getOwner() {
        return owner;
    }

    public void setOwner(long owner) {
        this.owner = owner;
    }
    
    /**
     * Render the form with related fields and widgets
     * @param holder container widget to draw the form
     */    
    public void render( MaterialPanel holder ){
        
        MaterialCollapsible coll = null;
        MaterialCollapsibleBody body[]=null;
        
        if( child == null ) return;
        holder.clear( );
        
        if( getItemDisplay().equals(Form.ITEMS_DISPLAY_CATEGORIZE) ){
            coll = new MaterialCollapsible();
            coll.setAccordion(true);
            
            body = new MaterialCollapsibleBody[10];

            for ( int i =0; i < body.length; i++){
                body[i] = new MaterialCollapsibleBody();
                //body[i].setPaddingTop(20);
                MaterialCollapsibleItem item = new MaterialCollapsibleItem();
                coll.add(item);

                MaterialCollapsibleHeader header = new MaterialCollapsibleHeader();
                item.add(header);
                MaterialLink lnktitle = new MaterialLink();
                lnktitle.setFontWeight( Style.FontWeight.BOLDER );
                lnktitle.setTextColor(Color.TEAL);
                lnktitle.setText( Constants.PROFILE_CATEGORIES[i] );
                header.add(lnktitle);//<m:MaterialIcon iconType="EDIT" waves="DEFAULT" float="LEFT" circle="true" iconSize="LARGE" />
                
                item.add( body[i] );
            }
            
            coll.setActive(1);
            coll.setShadow(0);
        }
        
        for( int i=0; i < child.size(); i++ ){
            Field f = child.get(i);
            if( f.getChildren().isEmpty()){
                --i; 
                child.remove(f); continue;
            }
            
            if( f.isHide() || f.getChildren().get(0).isHide() )
                continue;
            
            
            MaterialRow row;
            WidgetGenerator generator = new WidgetGenerator();
            eventRegister( generator );
            
            try {
                row = generator.getWidget( f, i );
                generator.createWidget(f, row, i, getMode(), isMouseOverShadow() );
                            
                if( !getItemDisplay().equals(Form.ITEMS_DISPLAY_CATEGORIZE) ){
                    if( isDraggable()){
                        MaterialDnd.draggable( row, JsDragOptions.create( Axis.VERTICAL ) );
                        row.addDragEndHandler(event -> {
                        MaterialToast.fireToast("Added " );
                        //txtbrief.setText( "" );
                        int count = 0; 
                        for ( Widget w : holder.getChildrenList() ){
                            child.get( count ).setTop( w.getAbsoluteTop() );
                            child.get( count ).setLeft( w.getAbsoluteLeft() );
                            count++;
                        }
                        verticalMove( row, holder );
                       });
                    }             
                    holder.add( row );
                    f.setTop( row.getAbsoluteTop() );
                    f.setLeft( row.getAbsoluteLeft() );
                
                }else{
                    String category = f.getCategory();
                    if( category.equals("profile")){
                        if( body[0].getChildrenList().isEmpty()){
                            row.setMarginTop(35);
                        }
                        body[0].add(row);
                    }else if( category.equals("contact")){
                        if( body[0].getChildrenList().isEmpty()){
                            row.setMarginTop(35);
                        }
                        body[1].add(row);
                    }else if( category.equals("places")){
                        if( body[0].getChildrenList().isEmpty()){
                            row.setMarginTop(35);
                        }
                        body[2].add(row);
                    }else if( category.equals("work & education")){
                        if( body[0].getChildrenList().isEmpty()){
                            row.setMarginTop(35);
                        }
                        body[3].add(row);
                    }else if( category.equals("documents")){
                        if( body[0].getChildrenList().isEmpty()){
                            row.setMarginTop(35);
                        }
                        body[4].add(row);
                    }else if( category.equals("travel info")){
                        if( body[0].getChildrenList().isEmpty()){
                            row.setMarginTop(35);
                        }
                        body[5].add(row);
                    }else if( category.equals("family & relationships")){
                        if( body[0].getChildrenList().isEmpty()){
                            row.setMarginTop(35);
                        }
                        body[6].add(row);
                    }else if( category.equals("bio-matric")){
                        if( body[0].getChildrenList().isEmpty()){
                            row.setMarginTop(35);
                        }
                        body[7].add(row);
                    }else if( category.equals("health-care")){
                        if( body[0].getChildrenList().isEmpty()){
                            row.setMarginTop(35);
                        }
                        body[8].add(row);
                    }else if( category.equals("others")){
                        if( body[0].getChildrenList().isEmpty()){
                            row.setMarginTop(35);
                        }
                        body[9].add(row);
                    }
                }
                
            } catch (Exception ex) {
                Window.alert(ex.getMessage());
                break;
            }
        }
        
        
        if( getItemDisplay().equals(Form.ITEMS_DISPLAY_CATEGORIZE)){
            holder.add( coll );
        }
        
        
    }
    
    private void eventRegister(WidgetGenerator generator){
        if( getMode().equals(Form.DISPLAY_MODE_DESIGNER)){
            
            generator.setClickListener((Field field, int index) -> {
                if( itemListener != null ) itemListener.onClick(field, index);
            });
            generator.setMouseListener(new WidgetGeneratorMouseListener(){
                @Override
                public void mouseEnter(Field field, int index) {
                    if( itemListener != null ) itemListener.mouseEnter(field, index);
                }

                @Override
                public void mouseExit(Field field, int index) {
                    if( itemListener != null ) itemListener.mouseExit(field, index);
                }
            });
            generator.setButtonListener(new WidgetGeneratorButtonClickListener(){
                @Override
                public void onEditClick(Field field, int index) {
                    if( itemListener != null ) itemListener.onEditClick(field, index);
                }

                @Override
                public void onDeleteClick(Field field, int index) {
                    if( itemListener != null ) itemListener.onDeleteClick(field, index);
                }

                @Override
                public void onUpClick(Field field, int index) {
                    if( itemListener != null ) itemListener.onUpClick(field, index);
                }

                @Override
                public void onDownClick(Field field, int index) {
                    if( itemListener != null ) itemListener.onDownClick(field, index);
                }
            });
        }
    }
    
    private Field get( String id ){
        for ( int i=0; i < child.size(); i++)
            if( child.get(i).getId().equals( id ))
                return child.get(i);
        
        return null;
    }  
    
    /**
     * handling drag and drop movement
     * @param widget moving widget
     * @param holder parent widget of form
     */
    private void verticalMove( MaterialWidget widget, MaterialPanel holder ){
        if( child.size() < 2) return;
        
        try{
            Field field = remove( widget.getId());
            int tmpDistance = 100000;
            int position = -1;
            int topDif = 10000;
            int leftDif = 10000;
            for ( int i=0; i < child.size(); i++ ){
                if( child.get(i).getTop() > field.getTop() && child.get(i).getTop() < tmpDistance ){
                    tmpDistance = child.get(i).getTop();
                    position = i;
                    topDif = child.get(i).getTop() - field.getTop();
                    leftDif = child.get(i).getLeft() - field.getLeft();
                }
                
            }
        
            //if( topDif < 15 && leftDif >=7 ){
                //Window.alert("Column movement top=" + topDif +", left=" + leftDif);
            //}
            
            if( position >= 0 ){
                child.add( position, field );
            }else{
                child.add( field );
            }
            
            render( holder );
        }catch(Exception ex){Window.alert(ex.getMessage());}
    }
       
    private Field remove( String id ){
        for ( int i=0; i < child.size(); i++)
            if( child.get(i).getId().equals( id ))
                return child.remove(i);
        
        return null;
    }
    
    /**
     * grouping the components. Before call this method, the group name must be added to related component
     * e.g. lstItem.get(index).getChildren().get(index).setGroup(groupName);
     * @param holder parent container widget of form
     */
    public void addGroup( MaterialPanel holder ){
        for( int i=0; i < child.size(); i++ ){
            Field f = child.get( i );
            if( !f.getChildren().isEmpty() ){
                // pickup the first child column
                Field children = f.getChildren().get( 0 );
                // check the group name
                if( children.getGroup() != null && !children.getGroup().isEmpty()){
                    boolean found = false;
                    // find the related group name
                    for( int j=i +1; j < child.size(); j++ ){
                        Field relative_child = child.get( j ).getChildren().get( 0 );
                        if( relative_child.getGroup() != null 
                                && !relative_child.getGroup().isEmpty()
                                && children.getGroup().trim().equalsIgnoreCase(relative_child.getGroup().trim()) ){
                            Field r = child.remove( j );
                            f.getChildren().addAll( r.getChildren() );
                            j--;
                            found = true;
                        }
                    }
                    // remove from group name for single component
                    if( !found && f.getChildren().size() == 1){
                        children.setGroup("");
                    }
                }
                
                // sorting by index
                Collections.sort( child, new SortByIndex());
            }
        }
        render( holder );
    }
    
    public void removeGroup( int rowIndex, int columnIndex, MaterialPanel holder ){
        
        if( rowIndex < 0 || columnIndex < 0 || rowIndex > child.size() - 1) return;
        
        Field row = child.get( rowIndex );
        if( row.getChildren( ).size() <= 1 || columnIndex > row.getChildren( ).size() - 1) return;
        
        if( columnIndex == row.getChildren().size() - 1){
            rowIndex++;
        }
        Field col = row.getChildren( ).remove( columnIndex );
        
        Field f = new Field( col.getId( ), col.getLabel( ), col.getName( ) );
        f.getChildren( ).add( col );
        child.add( rowIndex, f );
        
        render( holder );
    }
       
    class SortByIndex implements Comparator<Field> {
        @Override
        public int compare(Field a, Field b) {
            return a.getIndex() - b.getIndex(); 
        } 
    } 
    
    /**
     * Form design generating from JSON object
     * @param json object of form design
     * @param holder container type widget to draw the form
     * @throws Exception 
     */
    public void fromJSON( JSONObject json, MaterialPanel holder) throws Exception {
        fromJSON( json );
        render ( holder );
    }
      /**
       * Form design generating from JSON object
       * @param json object of form design
       * @throws Exception 
       */
    public void fromJSON( JSONObject json )throws Exception{
        
          if( json.get(JSON_FORM_ID)!= null && json.get(JSON_FORM_ID).isNumber() != null )
            setId( (long) json.get(JSON_FORM_ID).isNumber().doubleValue());
          
          setName( json.get(JSON_FORM_NAME).isString().stringValue());
          setContainer( json.get(JSON_FORM_CONTAINER_ID).isString().stringValue());
          setProcess( json.get(JSON_PROCESS_ID).isString().stringValue());
          setTask( json.get(JSON_TASK_ID).isString().stringValue());
          //setOwner( (long) json.get(JSON_FORM_OWNER).isNumber().doubleValue());
          
          JSONArray group = json.get(JSON_FORM_CHILD).isArray();
          
            child = new java.util.ArrayList<>();
            for( int i=0; i < group.size(); i++){
                //Window.alert("Index=" + i);
                JSONObject objField = group.get(i).isObject();
                //Window.alert("From parent=" + objField.toString() );
                Field field = new Field();
                field.fromJSON( objField );
                child.add( field );
                //Window.alert("Child added=" + child.size() );
                
            }
            //Window.alert("Child size after reading=" + child.size());
            
      }

    /**
     * Form design to converting JSON object
     * @return JSON object
     * @throws Exception 
     */
    public JSONObject toJSON() throws Exception{
          JSONObject json = new JSONObject( );
          
          if( getId() != null )
            json.put( JSON_FORM_ID, new JSONNumber(getId()) );
          
          json.put( JSON_FORM_NAME, new JSONString(getName()) );
          json.put( JSON_FORM_CONTAINER_ID, new JSONString(getContainer()) );
          json.put( JSON_PROCESS_ID, new JSONString(getProcess()) );
          json.put( JSON_TASK_ID, new JSONString(getTask()) );
          //json.put( JSON_FORM_OWNER, new JSONNumber(getOwner()) );
                 
          if( !child.isEmpty()){
            JSONArray group = new JSONArray();
            int count=0;
            for( Field f : child){
                JSONObject objChild = f.toJSON();
                group.set(count++, objChild);
            }
            json.put( JSON_FORM_CHILD, group );
        }
          
          return json;
      }
      
    /**
     * Picking up form field values and convert to JSON object for data processing in KIE server via API
     * @return JSON object for KIE server for data processing
     * @throws Exception 
     */
    public JSONObject getOutputDataForTask() throws Exception{
          java.util.Map<String, Object[]> maps = new java.util.HashMap<>();
          
          for( Field child : child ){
              for ( Field children : child.getChildren() ){
                  maps.put( children.getName(), children.getBindValue( getFqdn() ) );
              }
          }
          
          JSONObject json = new ObjectConverter().toJSON( maps );
          if( getFqdn() != null && !getFqdn().trim().isEmpty() ){
                JSONObject objFQDN = new JSONObject();
                objFQDN.put( getFqdn(), json );
                JSONObject obj = new JSONObject();
                obj.put( getObjectName(), objFQDN );
                
                return obj;
          }else
            return json;
      }
      
        public java.util.Map<String, Object[]> getOutputDataForPerson() throws Exception{
          java.util.Map<String, Object[]> maps = new java.util.HashMap<>();
          
          for( Field child : child ){
              for ( Field children : child.getChildren() ){
                  maps.put( children.getName(), children.getBindValue( getFqdn() ) );
              }
          }
          
          return maps;
      }
        
    /**
     * Filling data from Task Instance of KIE server
     * @param json Task JSON object from KIE server
     * @throws Exception 
     */
    public void bindWithTaskData( JSONObject json )throws Exception{
        java.util.Map<String, Object[]> maps = new ObjectConverter().fromJSON( json,false,false );
        bindWithTaskData( maps );
    }
        
    public void bindWithTaskData( java.util.Map<String, Object[]> maps )throws Exception{
        for( java.util.Map.Entry<String, Object[]> entry : maps.entrySet() ){
            Outer: for( Field child : child ){
              for ( Field children : child.getChildren()){
                  if( children.getName().equals(entry.getKey())){
                      children.setValue( entry.getValue()[1] );
                      break Outer;
                  }
              }
            }
        }
    }
    
    public Field contain( String field){
        Field exist = null;
        for ( Field f : getChild()){
            if( f.getName().equals(field)){
                exist = f;
                break;
            }
        }
        return exist;
    }
}
