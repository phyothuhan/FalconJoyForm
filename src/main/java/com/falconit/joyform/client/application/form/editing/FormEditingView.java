package com.falconit.joyform.client.application.form.editing;

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


import com.falconit.joyform.client.application.form.util.Field;
import com.falconit.joyform.client.application.form.util.Form;
import com.falconit.joyform.client.application.form.util.FormCRUD;
import com.falconit.joyform.client.application.tasks.display.TaskDisplayView;
import com.falconit.joyform.client.application.util.Constants;
import com.falconit.joyform.client.application.util.CookieHelper;
import com.falconit.joyform.client.application.util.jbpmclient.api.ContainerManager;
import com.falconit.joyform.client.application.util.jbpmclient.api.process.ProcessVariables;
import com.falconit.joyform.client.application.util.jbpmclient.api.process.ProcessesManager;
import com.falconit.joyform.client.application.util.jbpmclient.api.process.ProcessesVariablesMapping;
import com.falconit.joyform.client.place.NameTokens;
import com.falconit.joyform.shared.jsonconvert.ObjectConverter;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import gwt.material.design.addins.client.combobox.MaterialComboBox;
import gwt.material.design.addins.client.stepper.MaterialStepper;
import gwt.material.design.client.constants.ButtonSize;
import gwt.material.design.client.constants.ButtonType;
import gwt.material.design.client.constants.CheckBoxType;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.DialogType;
import gwt.material.design.client.constants.Display;
import gwt.material.design.client.constants.IconPosition;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.*;
import gwt.material.design.client.ui.animate.MaterialAnimation;
import gwt.material.design.client.ui.animate.Transition;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;


public class FormEditingView extends ViewImpl implements FormEditingPresenter.MyView {
    interface Binder extends UiBinder<Widget, FormEditingView> {
    }

    private Form myForm = new Form( );
    //private Long id = null;
    //private java.util.List<Field> lstItem = new java.util.ArrayList<>();
    
    private java.util.List<Object[]> lstProjects, lstProcesses, lstTasks;
    private java.util.Map<String, java.util.List<MaterialTextBox[]>> choice = new java.util.HashMap<>();
    private java.util.Map<String, MaterialRow> mapRemove = new java.util.HashMap<>();
    private Map<String, Object[]> variableMaps = new java.util.HashMap<>();
    
    @UiField
    MaterialStepper stepper;
        
    @UiField
    MaterialPanel holder, source;
    
    @UiField
    MaterialRow fieldholder;
    
    @UiField
    MaterialLabel lblproname;
    
    @UiField
    MaterialTextBox txtformName;
    
    @UiField
    MaterialTextArea txtshare, txtembedded;
    
    @UiField 
    MaterialComboBox cbocontainer, cboprocess, cboformtype, cboaddfield, cboproperty;

    @UiField
    MaterialAnchorButton btnaddField;
    
    @UiField
    MaterialTitle txtTitle;
    
    @UiField
    MaterialButton btnContinue4;    

    private MaterialDialog dialog;
    //private MaterialDialogFooter footer;
    
    Field tmpField;
    String userId = "1";
    String paramContainer, paramProcess, paramTaskId;
    
    @Inject
    FormEditingView(Binder uiBinder) {
        
        initWidget( uiBinder.createAndBindUi( this ) );
        
        if( CookieHelper.getMyCookie( Constants.COOKIE_USER_ID ) == null ){
            History.newItem( NameTokens.login );
        }
        
        userId =CookieHelper.getMyCookie( Constants.COOKIE_USER_ID );
        
        String value = com.google.gwt.user.client.Window.Location.getParameter("container");
        if( value != null ){
            paramContainer = value;
        }
        String pro = com.google.gwt.user.client.Window.Location.getParameter("process");
        if( pro != null ){
            paramProcess = pro;
        }
        String id = com.google.gwt.user.client.Window.Location.getParameter("taskId");
        if( id != null ){
            paramTaskId = id;
        }
        
        myForm.setMode( Form.DISPLAY_MODE_DESIGNER );
        myForm.setMouseOverShadow(true);
        myForm.setOwner(Long.parseLong(userId));
        myForm.setItemListener(new Form.FormItemListener(){
            @Override
            public void onClick(Field field, int index) {
                //MaterialToast.fireToast("Click on " + field.getName()+", index=" + index );
            }

            @Override
            public void mouseEnter(Field field, int index) {
                //MaterialToast.fireToast("Mouse enter " + field.getName()+", index=" + index );
            }

            @Override
            public void mouseExit(Field field, int index) {
                //MaterialToast.fireToast("Mouse exit " + field.getName()+", index=" + index );
            }

            @Override
            public void onEditClick(Field field, int index) {
                tempPro( field );
            }

            @Override
            public void onDeleteClick(Field field, int index) {
                myForm.getChild().remove(index);
                myForm.render(holder);
                //MaterialToast.fireToast("Click on Delete button " + field.getName()+", index=" + index );
            }

            @Override
            public void onUpClick(Field field, int index) {
                if( index > 0 ){
                    Field current = myForm.getChild().remove(index);
                    myForm.getChild().add( index - 1, current);
                    myForm.render(holder);
                }
            }

            @Override
            public void onDownClick(Field field, int index) {
                if( index < myForm.getChild().size() - 1 ){
                    Field current = myForm.getChild().remove(index);
                    myForm.getChild().add( index + 1, current);
                    myForm.render(holder);
                }
            }
        });
        //myForm.setChild( lstItem );
        myForm.setId(null);
        
        fieldholder.setDisplay( Display.BLOCK );
        //holder.setDisplay( Display.FLEX );
        loadProjects( );
        registercboProcessListener( );
        registercboFormListener();
        
        createDialog( );
                    
        cboproperty.addItem("Select", "Select");
        cboproperty.addItem("profile", "profile");
        cboproperty.addItem("contact", "contact");
        cboproperty.addItem("documents", "documents");

        cboproperty.addItem("travel info", "travel info");
        cboproperty.addItem("work & education", "work & education");
        cboproperty.addItem("bio-matric", "bio-matric");

        cboproperty.addItem("family & relationships", "family & relationships");
        cboproperty.addItem("others", "others");
        cboproperty.addItem("health-care", "health-care");
        cboproperty.addItem("places", "places");
            
        cboproperty.addValueChangeHandler(handler ->{
            tmpField.setCategory( cboproperty.getSingleValue().toString());
        });
        //MaterialPushpin.apply( source, 300.0, 64.0 );
        source.setVisible(false);
    }
    
    private void tempPro( Field field ){
        tmpField = field;
        lblproname.setText(field.getName() + " , " + field.getCategory());
        if( field.getCategory() != null && !field.getCategory().isEmpty())
            cboproperty.setSingleValue(field.getCategory());
    }
    
    @UiHandler({"btnContinue1", "btnContinue2", "btnContinue3", "btnContinue4"})
    void onNextStep(ClickEvent e){
        stepper.nextStep();
    }

    @UiHandler({"btnPrev1", "btnPrev2", "btnPrev3","btnPrev4" })
    void onPrevStep(ClickEvent e){
        stepper.prevStep();
    }
    
    
    @UiHandler({ "btnContinue4"})
    void onComplete(ClickEvent e){
        try {
            myForm.setName( txtformName.getText() );
            myForm.setContainer( cbocontainer.getSelectedValue().get(0).toString() );
            myForm.setProcess( cboprocess.getSelectedValue().get(0).toString() );

            myForm.setTask( cboformtype.getSelectedValue().get(0).toString() );
            if( myForm.getId() == null)
                myForm.setCreated(new java.util.Date());
            myForm.setUpdated(new java.util.Date());
            myForm.setActors(new String[]{"everyone"});
            myForm.setOwner( Long.parseLong(userId) );
            myForm.setGroups(new String[]{});
            myForm.setStatus( 1 );
            
            saveForm();
        } catch (Exception ex) {
            Window.alert("Error "+ex.getMessage());
        }
    }
    
    @UiHandler({ "btnContinue2"})
    void onFormDesign(ClickEvent e){
        try {
            createForm( );
            String shareLink = 
                "?container=" + cbocontainer.getSingleValue().toString()
                + "&process=" + cboprocess.getSingleValue().toString()
                + "&title=" + cboprocess.getSingleValue().toString()
                + "&taskName=" + Form.TASK_NAME_START_UP
                + "&display=" + TaskDisplayView.DISPLAY_START_UP
                + "&ownerId=" + userId
                + "&owner=" + CookieHelper.getMyCookie( Constants.COOKIE_USER_NAME )
                + "#taskdisplay";
            
            txtshare.setText( GWT.getHostPageBaseURL() + shareLink );
            txtembedded.setText("<iframe src=\""+ GWT.getHostPageBaseURL() + shareLink +"\"></iframe>");//<iframe src="URL"></iframe>
            
        } catch (Exception ex) {
            Window.alert("Error form creating " + ex.getMessage());
        }
    }
    
        
    @UiHandler({ "btnaddField"})
    void onAddField(ClickEvent e){
        try {
            fieldholder.add( mapRemove.remove( cboaddfield.getSelectedValue().get(0).toString()) );
            
            if( mapRemove.isEmpty()){
                cboaddfield.setVisible(false);
                btnaddField.setVisible(false);
            }else{
                cboaddfield.clear();
                for( java.util.Map.Entry<String, MaterialRow> entry : mapRemove.entrySet() ){
                    cboaddfield.addItem( entry.getKey(), entry.getKey() );
                }
            }
        } catch (Exception ex) {
            Window.alert("Error "+ex.getMessage());
        }
    }
    
    private void registercbocontainerListener(){
        cbocontainer.addValueChangeHandler(handler ->{
            loadProcesses( );
        });
    }
    
        
    private void registercboProcessListener(){
        cboprocess.addValueChangeHandler(handler ->{
            try{
                if( cbocontainer.getSelectedValue() != null && cboprocess.getSelectedValue() != null)
                    processMappings( cbocontainer.getSelectedValue().get(0).toString(), cboprocess.getSelectedValue().get(0).toString() );
            }catch(Exception ex){}
        });
    }
        
    private void registercboFormListener(){
        cboformtype.addValueChangeHandler(handler ->{
            try{
                if( cbocontainer.getSelectedValue() != null 
                        && cboprocess.getSelectedValue() != null
                        && cboformtype.getSelectedIndex() > 0 ){
                    
                    if( cboformtype.getSelectedValue().get(0).toString().equals(Form.TASK_NAME_START_UP))
                        processVariables( cbocontainer.getSelectedValue().get(0).toString(), cboprocess.getSelectedValue().get(0).toString() );
                    else
                        processVariablesMapping( cbocontainer.getSelectedValue().get(0).toString(), cboprocess.getSelectedValue().get(0).toString() );
                }
            }catch(Exception ex){}
        });
    }
        
    private void loadProjects( ){
        try{
            ContainerManager manager = new ContainerManager();
            manager.setListener(new ContainerManager.ContainerManagerListener(){
                @Override
                public void success(Map<String, Object[]> maps) {
                    
                    for( java.util.Map.Entry<String, Object[]> entry : maps.entrySet() ){
                        cbocontainer.addItem("Select one");
                        lstProjects = (java.util.List<Object[]>) entry.getValue()[1];
                        for( Object[] o : lstProjects ){
                            java.util.Map<String, Object[]> map = (java.util.Map<String, Object[]>) o[1];
                            String cid = map.get("container-id")[1].toString();
                            if( !Constants.containerFilter.contains( cid )){
                                cbocontainer.addItem( cid.substring( 0, cid.indexOf("_")), cid);
                            }
                        }
                    }
                    registercbocontainerListener();
                    
                    if( paramContainer != null ){
                        cbocontainer.setSingleValue( paramContainer, true);
                    }
                }

                @Override
                public void fail(String message, int stage) {
                }
            });
            
            manager.list();
        }catch(Exception ex){Window.alert(ex.getMessage());}
    }
        
    private void loadProcesses( ){
        try{
            MaterialLoader.loading( true );
            
            ProcessesManager manager = new ProcessesManager();
            manager.setListener(new ProcessesManager.ProcessesManagerListener(){
                @Override
                public void success(Map<String, Object[]> maps) {
                    MaterialLoader.loading( false );
                    cboprocess.clear();
                    cboformtype.clear();
                    cboprocess.addItem("Select one");
                    for( java.util.Map.Entry<String, Object[]> entry : maps.entrySet() ){
                        lstProcesses = (java.util.List<Object[]>) entry.getValue()[1];
                                                
                        for( Object[] o : lstProcesses ){//container-id
                            java.util.Map<String, Object[]> map = (java.util.Map<String, Object[]>) o[1];
                            if( map.get("container-id")[1].toString().equals(cbocontainer.getSelectedValue().get(0).toString()))
                                cboprocess.addItem( map.get("process-name")[1].toString(), map.get("process-id")[1].toString());
                        }
                    }
                    
                    if( paramProcess != null ){
                        cboprocess.setSingleValue( paramProcess, true);
                    }
                }

                @Override
                public void fail(String message, int stage) {
                    MaterialLoader.loading( false );
                }
            });
            
            manager.list( null, null );
        }catch(Exception ex){Window.alert(ex.getMessage());}
    }
        
    private void processMappings( String container, String processId ){
        try{
            MaterialLoader.loading( true );
            ProcessesVariablesMapping manager = new ProcessesVariablesMapping();
            manager.setListener(new ProcessesVariablesMapping.ProcessesVariablesMappingListener(){
                @Override
                public void success(Map<String, Object[]> maps) {
                    cboformtype.clear();
                    cboformtype.addItem("Select one");
                    
                    if( !maps.isEmpty()){
                        if( processId.equals("Personal-Information" ))
                            cboformtype.addItem( "Personal Information", Form.TASK_NAME_START_UP);
                    }else{
                        if( processId.equals("Personal-Information" ))
                            cboformtype.addItem( "Personal Information", Form.TASK_NAME_START_UP);
                    }
                    
                    for( java.util.Map.Entry<String, Object[]> entry : maps.entrySet() ){
                        lstTasks = (java.util.List<Object[]>) entry.getValue()[1];
                                                
                        for( Object[] o : lstTasks ){
                            java.util.Map<String, Object[]> map = (java.util.Map<String, Object[]>) o[1];
                            cboformtype.addItem( map.get("task-name")[1].toString(), map.get("task-name")[1].toString());
                        }
                    }
                    
                    if( paramTaskId != null ){
                        cboformtype.setSingleValue( paramTaskId, true);
                    }
                    MaterialLoader.loading( false );
                }

                @Override
                public void fail(String message, int stage) {
                    MaterialLoader.loading( true );
                }
            });
            
            manager.list( container, processId );
        }catch(Exception ex){Window.alert(ex.getMessage());}
    }
    
    private void processVariablesMapping( String container, String processId ){
        try{
            MaterialLoader.loading( true );
            ProcessesVariablesMapping manager = new ProcessesVariablesMapping();
            manager.setListener(new ProcessesVariablesMapping.ProcessesVariablesMappingListener(){
                @Override
                public void success(Map<String, Object[]> maps) {

                    for( java.util.Map.Entry<String, Object[]> entry : maps.entrySet() ){
                        java.util.List<Object[]> lstVariables = (java.util.List<Object[]>) entry.getValue()[1];
                                                
                        for( Object[] o : lstVariables ){
                            java.util.Map<String, Object[]> map = (java.util.Map<String, Object[]>) o[1];
                            if( cboformtype.getSelectedValue().get(0).toString().equals( map.get("task-name")[1].toString() )){
                                java.util.Map<String, Object[]> mapChild = (java.util.Map<String, Object[]>) map.get("taskOutputMappings")[1];
                                
                                //Window.alert("Variables size = " + mapChild.size());
                                if( mapChild != null && !mapChild.isEmpty() )
                                    preprocess( mapChild );
                                else
                                    Window.alert( "No out put form field= " + mapChild.size());
                            }
                        }
                    }
                    
                    MaterialLoader.loading( false );
                }

                @Override
                public void fail(String message, int stage) {
                    MaterialLoader.loading( true );
                }
            });
            
            manager.list( container, processId );
        }catch(Exception ex){Window.alert(ex.getMessage());}
    }

    private void processVariables( String container, String processId ){
        try{
            ProcessVariables manager = new ProcessVariables();
            manager.setListener(new ProcessVariables.ProcessVariablesListener(){
                @Override
                public void success(Map<String, Object[]> maps) {
                    preprocess( maps );
                }

                @Override
                public void fail(String message, int stage) {
                }
            });
            
            manager.list( container, processId );
        }catch(Exception ex){Window.alert(ex.getMessage());}
    }
    
    private void preprocess( Map<String, Object[]> maps ){
   
        for (String skip : Constants.skipFields ){
            if( maps.containsKey(skip)){
                maps.remove( skip );
            }
        }
        
        boolean found = false;
        for( java.util.Map.Entry<String, Object[]> entry : maps.entrySet() ){
            if (entry.getValue()[1]!= null 
                    && entry.getValue()[1].toString().startsWith(Constants.FQDN_KEY_START) ){
                String fqdn = entry.getValue()[1].toString();
                
                solveFQDN ( fqdn );
                myForm.setFqdn(fqdn);
                myForm.setObjectName( entry.getKey() );
                found = true; break;
            }
        }
        
        variableMaps = maps;
        
        if( !found )
            createFields( variableMaps );
    }
         
    private void createFields( Form form, Map<String, Object[]> maps ){
        
        if( form != null && form.getFqdn().equalsIgnoreCase("com.falconit.automation.entity.Customer") ){
            myForm.setItemDisplay( Form.ITEMS_DISPLAY_CATEGORIZE );
        }else{
            myForm.setItemDisplay( Form.ITEMS_DISPLAY_UP_DOWN );
        }
        
        //myForm.setItemDisplay( Form.ITEMS_DISPLAY_CATEGORIZE );
        
        
        fieldholder.clear( );
        fieldholder.add( createHeader( ) );
        
        for( Field field : form.getChild() ){
            Object[] values = maps.remove(field.getName());
            createFields( field.getName(), values, field, false ); 
        }
        
        for( java.util.Map.Entry<String, Object[]> entry : maps.entrySet() ){
            createFields( entry.getKey(), entry.getValue(), null, true );
        }
    }
    
    private void createFields( Map<String, Object[]> maps ){
        MaterialLoader.loading( true );
        // check the form is already exist or not
        getForm(cbocontainer.getSelectedValue().get(0).toString(), 
                cboprocess.getSelectedValue().get(0).toString(), 
                cboformtype.getSelectedValue().get(0).toString() );
        fieldholder.clear( );
        fieldholder.add( createHeader( ) );
        
        for( java.util.Map.Entry<String, Object[]> entry : maps.entrySet() ){
             createFields( entry.getKey(), entry.getValue(), null, false );        
        }
    }
    
    private void changeCheckValue( boolean value ){
        for ( Widget w : fieldholder.getChildrenList() ){
            MaterialRow row =  (MaterialRow) w;
            MaterialColumn colName =  (MaterialColumn) row.getChildrenList().get(0);
            MaterialCheckBox txtName = (MaterialCheckBox) colName.getChildrenList().get(0);
            txtName.setValue(value);
        }
    }
    
    private MaterialRow createHeader(){
        
        MaterialRow row = new MaterialRow( );
        row.setGrid("l12 m12 s12");
        row.setMarginTop(15);
                
        MaterialColumn colName = new MaterialColumn();
        colName.setGrid("l2 m2 s12");
        row.add(colName);
        
        MaterialCheckBox chk = new MaterialCheckBox();
        chk.setType(CheckBoxType.FILLED);
        chk.setText("Allow blank");
        colName.add( chk );
        chk.addValueChangeHandler(handler -> {
            changeCheckValue( handler.getValue() );
        });

        
        MaterialColumn colLabel = new MaterialColumn();
        colLabel.setGrid("l4 m4 s12");
        row.add(colLabel);

        MaterialLabel txtLabel = new MaterialLabel();
        txtLabel.setText("Label");
        colLabel.add( txtLabel );
        
        
        MaterialColumn coltype = new MaterialColumn();
        coltype.setGrid("l2 m2 s8");
        row.add( coltype );

        MaterialLabel cbotype = new MaterialLabel();
        cbotype.setText("Type");
        coltype.add( cbotype );

        
        MaterialColumn colgroup = new MaterialColumn();
        colgroup.setGrid("l2 m2 s8");
        row.add(colgroup);

        MaterialLabel cbogroup = new MaterialLabel();
        cbogroup.setText("Widget");
        colgroup.add( cbogroup );
        
        return row;
    }
    
    private void createFields( String key, Object[] value, Field oldField, boolean referenceOnly ){
        
            MaterialRow row = new MaterialRow( );
            row.setGrid("l12 m12 s12");
            row.setId( key );
            fieldholder.add(row);

            MaterialColumn colName = new MaterialColumn();
            colName.setGrid("l2 m2 s12");
            colName.setPaddingTop(15);
            row.add(colName);
            
                        
            MaterialCheckBox chk = new MaterialCheckBox();
            chk.setType(CheckBoxType.FILLED);
            chk.setText( key );
            colName.add( chk );
            
            
            MaterialColumn colLabel = new MaterialColumn();
            colLabel.setGrid("l4 m4 s12");
            row.add(colLabel);
            
            //MaterialTextBox txtLabel = new MaterialTextBox();
            //txtLabel.setText( key );
            //txtLabel.setFieldType(FieldType.OUTLINED);
            //txtLabel.setGrid("l10 m10 s10");
            //colLabel.add( txtLabel );
            MaterialComboBox cboLabel = new MaterialComboBox();
            cboLabel.setTags(true);
            cboLabel.setMultiple(true);
            cboLabel.setCloseOnSelect(true);
            cboLabel.setHelperText("en:EN label, my:MM label, etc.");
            cboLabel.setTooltip("en:English label, my:ျမန္မာအညႊန္း, jp:日本語, zh:中文, kr:한국어, th:ไทย, hi:हिंदी, etc");
            colLabel.add( cboLabel );
            /*            
            MaterialIcon lang = new MaterialIcon();
            lang.setIconType(IconType.LANGUAGE );
            lang.setCircle(true);
            //lang.setType( ButtonType.FLOATING );
            lang.setLayoutPosition(Style.Position.ABSOLUTE);
            lang.setDisplay( Display.FLEX );
            lang.setTop(5);
            //lang.setRight(5);
            //lang.setFloat(Style.Float.RIGHT);
            colLabel.add( lang );
            */
            
            MaterialColumn coltype = new MaterialColumn();
            coltype.setGrid("l2 m2 s8");
            row.add( coltype );
            
            MaterialComboBox cboWidgetType = new MaterialComboBox();
            cboWidgetType.setPlaceholder("Input type");
            coltype.add(cboWidgetType);
            
            cboWidgetType.addItem("Text", "Text");
            cboWidgetType.addItem("Number", "Number");
            cboWidgetType.addItem("Choice", "Choice");
            cboWidgetType.addItem("Yes/No", "Yes/No");
            cboWidgetType.addItem("Date", "Date");
            cboWidgetType.addItem("Document upload", "Document upload");
            cboWidgetType.addItem("Myanmar NRC", "Myanmar NRC");
            cboWidgetType.addItem( "Signature pad", "Signature pad" );
            cboWidgetType.setSelectedIndex(0);
            
            MaterialColumn colgroup = new MaterialColumn();
            colgroup.setGrid("l2 m2 s8");
            row.add(colgroup);
            
            MaterialComboBox cbogroup = new MaterialComboBox();
            cbogroup.setPlaceholder("More rich");
            //cbogroup.setGrid("l2 m2 s8");
            colgroup.add(cbogroup);
            
            MaterialAnchorButton btnedit = new MaterialAnchorButton( );
            btnedit.setType( ButtonType.FLOATING );
            btnedit.setBackgroundColor( Color.TEAL );
            btnedit.setIconType( IconType.ADD_CIRCLE );
            btnedit.setSize( ButtonSize.MEDIUM );
            //btnedit.setFloat(Style.Float.RIGHT);
            btnedit.setTooltip("Add / remove items");
            //btnedit.setMarginLeft(3 );
            MaterialColumn coledit = new MaterialColumn( );
            //coledit.setGrid("l2 m2 s8");
            row.add( coledit );
            coledit.add( btnedit );
            btnedit.setVisible(false);
            btnedit.addClickHandler(handler ->{
                try{
                    if( cboWidgetType.getSelectedValue().get(0).toString().equalsIgnoreCase("Choice") ){
                        
                    if( choice.get( key ) == null ){
                        
                        java.util.List<MaterialTextBox[]> item = new java.util.ArrayList<>();
                        choice.put( key, item );
                        items( item );
                    }else{
                        
                        java.util.List<MaterialTextBox[]> item = choice.get( key );
                        items( item );
                    }
                }
                }catch(Exception ex){Window.alert(ex.getMessage());}
            });
            
            
            if( value[1] != null ){
                String dType = value[1].toString();
                //Window.alert("Type=" + dType);
                if( dType.trim().equals("String")){
                    cboWidgetType.setSelectedIndex(0);
                    //Window.alert("Type selection=" + dType);
                    //f = addTestItem( Field.WIDGET_TEXT_BOX, entry.getKey(), entry.getKey(), txtLabel.getText(), txtLabel.getText() );
                }
                else if( dType.trim().equals("Integer") || dType.trim().equals("Float")){
                    cboWidgetType.setSelectedIndex(1);
                    //f = addTestItem( Field.WIDGET_TEXT_BOX_NUMBER, entry.getKey(), entry.getKey(), txtLabel.getText(), txtLabel.getText() );
                }
                else if( dType.trim().equals("Boolean")){
                    cboWidgetType.setSelectedIndex(3);
                    //f = addTestItem( Field.WIDGET_CHECK_BOX, entry.getKey(), entry.getKey(), txtLabel.getText(), txtLabel.getText() );
                }
                else if( dType.trim().equals( ObjectConverter.JSON_INPUT_FIELD_TIMESTAMP )
                        || dType.trim().equals( ObjectConverter.JSON_INPUT_FIELD_DATETIME ) ){
                    cboWidgetType.setSelectedIndex(4);
                    //f = addTestItem( Field.WIDGET_DATE_TIME, entry.getKey(), entry.getKey(), txtLabel.getText(), txtLabel.getText() );
                }else{
                    //Window.alert( "Unknown data type" );
                }
            }
            
            cboWidgetType.addValueChangeHandler( handler ->{
                setInputType( cboWidgetType.getSelectedValue().get(0).toString(), cbogroup );
                if( cboWidgetType.getSelectedValue().get(0).toString().equalsIgnoreCase("Choice"))
                    btnedit.setVisible(true);
                else
                    btnedit.setVisible(false);
            });
            
            MaterialAnchorButton btnremove = new MaterialAnchorButton();
            btnremove.setType( ButtonType.FLOATING );
            btnremove.setBackgroundColor( Color.RED );
            btnremove.setIconType( IconType.DELETE );
            btnremove.setSize( ButtonSize.MEDIUM );
            btnremove.setFloat(Style.Float.RIGHT);
            btnremove.setTooltip( "Remove" );
            btnremove.setMarginRight(5);
            row.add( btnremove );
            btnremove.addClickHandler(handler ->{
                fieldholder.remove( row );
                mapRemove.put( key, row );
                cboaddfield.addItem( key, key );
                cboaddfield.setVisible( true );
                btnaddField.setVisible( true );
            });
            
            //Window.alert("Row creating OK " + key +" selected=" +(cboWidgetType.getSelectedValue()==null)+", val=" 
                    //+ cboWidgetType.getSelectedValue().toString() +", index="+ cboWidgetType.getSelectedIndex() );
            
            
            
            setInputType( cboWidgetType.getSelectedValue().get(0).toString(), cbogroup );
            //Window.alert("Selection creating OK " + cboWidgetType.getSelectedValue().get(0).toString());
            
            if( oldField != null ){
                Field oldChild = oldField.getChildren().get(0);
                
                java.util.List<String> selectedItems = new java.util.ArrayList<>();
                for( java.util.Map.Entry<String, String> entry : oldChild.getLabel().entrySet() ){
                    cboLabel.addItem( entry.getKey() + ":" + entry.getValue() );
                    selectedItems.add( entry.getKey() + ":" + entry.getValue() );
                }
                cboLabel.setValues(selectedItems, false);
                
                //txtLabel.setText(oldChild.getLabel().get(Constants.LANGUAGE));// this is hard code for test only
                
                chk.setValue(oldChild.isAllowBlank());
                if( oldChild.getWidgetType().equals(Field.WIDGET_COMBO_BOX)
                    ||oldChild.getWidgetType().equals(Field.WIDGET_RADIO_GROUP) ){
                    
                    cboWidgetType.setSelectedIndex( 2 );
                    setInputType( cboWidgetType.getSelectedValue().get(0).toString(), cbogroup );
                    
                    java.util.List<MaterialTextBox[]> item = new java.util.ArrayList<>( );
                    choice.put( key, item );
                    btnedit.setVisible( true );
                    
                    for( Field children : oldChild.getChildren()){

                        MaterialTextBox[] txtg = addNewItem();
                        txtg[0].setText(children.getLabel().get(Constants.LANGUAGE));
                        txtg[1].setText(children.getValue().toString());
                        item.add( txtg );
                    }
                }else if ( oldChild.getWidgetType().equals(Field.WIDGET_NRC) ){
                    cboWidgetType.setSelectedIndex( 6 );
                    setInputType( cboWidgetType.getSelectedValue().get(0).toString(), cbogroup );
                    
                }else if ( oldChild.getWidgetType().equals(Field.WIDGET_SIGNATURE_PAD) ){
                    cboWidgetType.setSelectedIndex( 7 );
                    setInputType( cboWidgetType.getSelectedValue().get(0).toString(), cbogroup );
                    
                }else if ( oldChild.getWidgetType().equals(Field.WIDGET_DATE_TIME) ){
                    cboWidgetType.setSelectedIndex( 4 );
                    setInputType( cboWidgetType.getSelectedValue().get(0).toString(), cbogroup );
                    
                }else if( oldChild.getWidgetType().equals(Field.WIDGET_FILE_UPLOADER)
                    ||oldChild.getWidgetType().equals(Field.WIDGET_MEDIA)
                    ||oldChild.getWidgetType().equals(Field.WIDGET_FACIAL_PHOTO)){
                    cboWidgetType.setSelectedIndex( 5 );
                    setInputType( cboWidgetType.getSelectedValue().get(0).toString(), cbogroup );
                    
                }else if( oldChild.getWidgetType().equals(Field.WIDGET_TEXT_BOX_NUMBER) ){
                    cboWidgetType.setSelectedIndex( 1 );
                    setInputType( cboWidgetType.getSelectedValue().get(0).toString(), cbogroup );
                }
                
                cbogroup.setSingleValue(oldChild.getWidgetType());
                
            }else{
                java.util.List<String> selectedItems = new java.util.ArrayList<>();
                cboLabel.addItem( "en:" + key );
                selectedItems.add( "en:" + key );
                cboLabel.setValues(selectedItems, false);
            }

            if( referenceOnly ){
                fieldholder.remove( row );
                mapRemove.put( key, row );
                cboaddfield.addItem( key, key );
                cboaddfield.setVisible( true );
                btnaddField.setVisible( true );
            }
    }
    
    private void setInputType(String type, MaterialComboBox colgroup ){
        
        colgroup.clear( );
        
        if( type.equals("Text") ){
            colgroup.addItem("Single line", Field.WIDGET_TEXT_BOX);
            colgroup.addItem("Multi lines", Field.WIDGET_TEXT_AREA);
            colgroup.addItem("Rich text area", Field.WIDGET_TEXT_BOX_RICH);
            
        }else if( type.equals("Number") ){
            colgroup.addItem("Number", Field.WIDGET_TEXT_BOX_NUMBER);
            colgroup.addItem("Rating", Field.WIDGET_RATING);
            
        }else if( type.equals("Choice")){
            colgroup.addItem("Dropdown choice", Field.WIDGET_COMBO_BOX);
            colgroup.addItem("Radio choice", Field.WIDGET_RADIO_GROUP);
            
        }else if( type.equals("Yes/No") ){
            colgroup.addItem("Check box", Field.WIDGET_CHECK_BOX);
            colgroup.addItem("On/Off switch", Field.WIDGET_SWITCH);
            
        }else if( type.equals("Date") ){
            colgroup.addItem("Date picker", Field.WIDGET_DATE_TIME);
            
        }else if( type.equals( "Document upload" ) ){
            colgroup.addItem( "Facial photo", Field.WIDGET_FACIAL_PHOTO );//Myanmar NRC
            colgroup.addItem( "Image", Field.WIDGET_FILE_UPLOADER );//Myanmar NRC
            colgroup.addItem( "File", Field.WIDGET_MEDIA );
            
        }else if( type.equals( "Myanmar NRC" ) ){
            colgroup.addItem( "NRC", Field.WIDGET_NRC );
            
        }else if( type.equals( "Signature pad" ) ){
            colgroup.addItem( "Signature", Field.WIDGET_SIGNATURE_PAD );
        }
        
        colgroup.setSelectedIndex( 0 );
    }

    private void createForm( ){
        
        boolean isFirst = true;
        
        txtTitle.setTitle( txtformName.getText() );
        
        java.util.List<Field> lstChild = new java.util.ArrayList<>();
        
        for ( Widget w : fieldholder.getChildrenList() ){
            if( isFirst ){
                isFirst = false;
                continue;
            }
            MaterialRow row =  (MaterialRow) w;
            
            MaterialColumn colName =  (MaterialColumn) row.getChildrenList().get(0);
             MaterialCheckBox txtName = (MaterialCheckBox) colName.getChildrenList().get(0);
            
             MaterialColumn colLabel =  (MaterialColumn) row.getChildrenList().get(1);
             MaterialComboBox txtLabel = (MaterialComboBox) colLabel.getChildrenList().get(0);
             
             MaterialColumn coltype =  (MaterialColumn) row.getChildrenList().get(3);
             MaterialComboBox cbotype = (MaterialComboBox) coltype.getChildrenList().get(0);
             

             lstChild.add( addItem( cbotype.getSelectedValue().get(0).toString(), txtName.getText(), 
            txtName.getText(), txtLabel.getValue(), txtName.getValue() ));
        }
        myForm.getChild( ).clear( );
        myForm.setChild( lstChild );
        myForm.render( holder );
    }
    
    private Field addItem( String type, String id, 
            String name, java.util.List<String> lstlabels ,boolean blank ){
        
        Field f = myForm.contain( name );
        Field child = null;
        
        java.util.Map<String,String> labelLang = new java.util.HashMap<>( );
        for( String lbl : lstlabels){
            if( lbl.contains(":"))
                labelLang.put( lbl.split(":")[0], lbl.split(":")[1] );
            else
                labelLang.put( Constants.LANGUAGE, lbl );
        }
        
        if( f == null ){
            f = new Field( id, labelLang, name );
            child = new Field( id, labelLang, name );
            f.getChildren().add( child );
        }
        f.setId(id);
        f.setName(name);
        f.setLabel(labelLang);

        child = f.getChildren().get(0);
        child.setId(id);
        child.setName(name);
        child.setLabel(labelLang);
        child.setPlaceHolder( labelLang.get(Constants.LANGUAGE) );
        child.setAllowBlank( blank );
        child.setWidgetType( type );

        
        if( type.equals(Field.WIDGET_COMBO_BOX) 
                || type.equals(Field.WIDGET_RADIO_GROUP)){    
            // Items
            if( choice.get( name ) == null ){
                return f;
            }else{
                
                child.getChildren().clear();
                for( MaterialTextBox[] s : choice.get( name ) ){
                    if( s[0].getText() != null && !s[0].getText().trim().isEmpty()){
                        Field comboItem = new Field( );
                        
                        java.util.Map<String,String> labelItems = new java.util.HashMap<>();
                        labelItems.put( Constants.LANGUAGE, s[0].getText() );
                        comboItem.setLabel( labelItems );
                        comboItem.setValue( s[1].getText() );
                        comboItem.setAllowBlank( blank );
                        child.getChildren().add( comboItem );
                    }
                }
            }
        }
        
        return f;
    }
       
    private void saveForm( ){
        
        MaterialLoader.loading( true );
        
        FormCRUD crud = new FormCRUD();
        crud.setListener(new FormCRUD.CRUDListener(){
            @Override
            public void success(String result) {
                MaterialLoader.loading( false );
                btnContinue4.setBackgroundColor( Color.GREEN );
                animate( btnContinue4 );
                resetColor( btnContinue4 );
            }

            @Override
            public void success(Form result) {
                
            }

            @Override
            public void success(List<Form> result) {
            }

            @Override
            public void fail(String message) {
                MaterialLoader.loading( false );
                Window.alert( "Result = " + message );
            }

            @Override
            public void fqdn(Map<String, Object[]> maps) {}
        });
        
        if( myForm.getId() == null )
            crud.saveUpdate( myForm, true );
        else{
            crud.saveUpdate( myForm, false );
        }
    }
   
    private void getForm( String container, String process, String task ){
        //id = null;
        FormCRUD crud = new FormCRUD();
        crud.setListener(new FormCRUD.CRUDListener(){
            @Override
            public void success(String result) {
                //Window.alert( "Result = " + result );
            }

            @Override
            public void success(Form result) {
                
            }

            @Override
            public void success(List<Form> result) {
                if( !result.isEmpty() ){
                    MaterialLoader.loading( false );
                    //myForm = result.get( 0 );
                    myForm.setId(result.get( 0 ).getId() );
                    myForm.setName(result.get( 0 ).getName());
                    myForm.setContainer(result.get( 0 ).getContainer());
                    myForm.setProcess(result.get( 0 ).getProcess());
                    myForm.setTask(result.get( 0 ).getTask());
                    myForm.setCreated(result.get( 0 ).getCreated());
                    myForm.setUpdated(result.get( 0 ).getUpdated());
                    myForm.setActors(result.get( 0 ).getActors());
                    myForm.setGroups(result.get( 0 ).getGroups());
                    myForm.setStatus(result.get( 0 ).getStatus());
                    myForm.setItemDisplay(result.get( 0 ).getItemDisplay());
                    myForm.setUseTimer(result.get( 0 ).isUseTimer());
                    myForm.setTimerDuration(result.get( 0 ).getTimerDuration());
                    myForm.setBackground(result.get( 0 ).getBackground());
                    txtformName.setText(myForm.getName());
                    myForm.setChild( result.get( 0 ).getChild() );
                    
                    createFields( myForm, variableMaps );
                }else{
                    MaterialLoader.loading( false );
                    Window.alert("No existing record");
                    if( myForm.getFqdn().equals(Constants.DEFAULT_FQDN)){
                        Window.alert("FQDN=" + myForm.getFqdn());
                        copyTemplateForm( container, process, task );
                    }
                }
            }

            @Override
            public void fail(String message) {
                Window.alert( "Result = " + message );
            }

            @Override
            public void fqdn(Map<String, Object[]> maps) {}
        });
        
        crud.getBy( container, process, task, userId );
    }
    
        
    private void copyTemplateForm( String container, String process, String task ){
        
        Window.alert("Copying from template form=" + container +"," + process +"," + task );
        
        FormCRUD crud = new FormCRUD();
        crud.setListener(new FormCRUD.CRUDListener(){
            @Override
            public void success(String result) {
                //Window.alert( "Result = " + result );
            }

            @Override
            public void success(Form result) {
                
            }

            @Override
            public void success(List<Form> result) {
                if( !result.isEmpty() ){
                    MaterialLoader.loading( false );
                    //myForm = result.get( 0 );
                    myForm.setId( null );
                    myForm.setName(result.get( 0 ).getName());
                    myForm.setContainer(result.get( 0 ).getContainer());
                    myForm.setProcess(result.get( 0 ).getProcess());
                    myForm.setTask(result.get( 0 ).getTask());
                    myForm.setCreated(result.get( 0 ).getCreated());
                    myForm.setUpdated(result.get( 0 ).getUpdated());
                    myForm.setActors(result.get( 0 ).getActors());
                    myForm.setGroups(result.get( 0 ).getGroups());
                    myForm.setStatus(result.get( 0 ).getStatus());
                    myForm.setItemDisplay(result.get( 0 ).getItemDisplay());
                    myForm.setUseTimer(result.get( 0 ).isUseTimer());
                    myForm.setTimerDuration(result.get( 0 ).getTimerDuration());
                    myForm.setBackground(result.get( 0 ).getBackground());
                    myForm.setOwner( Long.parseLong(userId) );
                    txtformName.setText(myForm.getName());
                    myForm.setChild( result.get( 0 ).getChild() );
                    
                    createFields( myForm, variableMaps );
                }else{
                    MaterialLoader.loading( false );
                    Window.alert("No template found");
                    if( myForm.getFqdn().equals(Constants.DEFAULT_FQDN)){
                        Window.alert("FQDN=" + myForm.getFqdn());
                        getCopyMasterForm(   );
                    }
                }
            }

            @Override
            public void fail(String message) {
                Window.alert( "Result = " + message );
            }

            @Override
            public void fqdn(Map<String, Object[]> maps) {}
        });
        
        crud.getBy( container, process, task, "1" );
    }
    
    private void getCopyMasterForm(  ){
        Window.alert("Copying from master form=" + Constants.DEFAULT_CONTAINER +"," + Constants.DEFAULT_PROCESS +"," + Constants.DEFAULT_TASK );
        MaterialLoader.loading( true );
        FormCRUD crud = new FormCRUD();
        crud.setListener(new FormCRUD.CRUDListener(){
            @Override
            public void success(String result) {
                //Window.alert( "Result = " + result );
            }

            @Override
            public void success(Form result) {
                
            }

            @Override
            public void success(List<Form> result) {
                if( !result.isEmpty() ){
                    
                    //myForm = result.get( 0 );
                    myForm.setId( null );
                    myForm.setChild( result.get( 0 ).getChild() );
                    myForm.setOwner( Long.parseLong(userId) );
                    createFields( myForm, variableMaps );
                    MaterialLoader.loading( false );
                }else{
                    MaterialLoader.loading( false );
                    Window.alert("Default form not found");
                }
            }

            @Override
            public void fail(String message) {
                MaterialLoader.loading( false );
                Window.alert( "Result = " + message );
            }

            @Override
            public void fqdn(Map<String, Object[]> maps) {}
        });
        
        crud.getBy( Constants.DEFAULT_CONTAINER, Constants.DEFAULT_PROCESS, Constants.DEFAULT_TASK, "1" );
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

    
    MaterialRow dialogRow;
    private void createDialog( ){
        
        dialog = new MaterialDialog( );
        dialog.setType( DialogType.DEFAULT );
        dialog.setDismissible( true );
        dialog.setInDuration(200);
        dialog.setOutDuration(200);
        dialog.setPadding( 10 );
        
        dialogRow = new MaterialRow();
        dialog.add( dialogRow );
        
        stepper.add( dialog );
        
        MaterialDialogFooter footer = new MaterialDialogFooter();
        MaterialButton btnclose = new MaterialButton();
        btnclose.setText("Close");
        btnclose.setType( ButtonType.FLAT );
        btnclose.addClickHandler(handler ->{
            dialog.close();
        });
        footer.add(btnclose);
        dialog.add(footer);
   
    }
    
    private void tmpProperty( Field field ){
        
        try{
            
            dialogRow.clear( );
            MaterialRow rowcontents = new MaterialRow( );
            dialogRow.add( rowcontents );
        
            MaterialComboBox cbo = new MaterialComboBox();
            dialogRow.add(cbo);
            
            cbo.addItem("Select", "Select");
            cbo.addItem("profile", "profile");
            cbo.addItem("contact", "contact");
            cbo.addItem("documents", "documents");
            
            cbo.addItem("travel info", "travel info");
            cbo.addItem("work & education", "work & education");
            cbo.addItem("bio-matric", "bio-matric");
            
            cbo.addItem("family & relationships", "family & relationships");
            cbo.addItem("others", "others");
            cbo.addItem("health-care", "health-care");
            cbo.addItem("places", "places");
            
            if( field.getCategory() != null && !field.getCategory().isEmpty())
                cbo.setSingleValue(field.getCategory());
            
            cbo.addValueChangeHandler(handler ->{
                field.setCategory(cbo.getSingleValue().toString());
            });
            dialog.open();
        }catch(Exception ex){}
    }
    
    
    private void items( java.util.List<MaterialTextBox[]> item ){
        dialog.open( );
        dialogRow.clear( );
        
        MaterialRow rowcontents = new MaterialRow( );
        dialogRow.add( rowcontents );

        for( int i=0; i< item.size(); i++ ){
            MaterialTextBox[] txt = item.get(i);
            rowcontents.add( createItem( item, txt, rowcontents ) );
        }
        
        MaterialButton btnadd = new MaterialButton( );
        btnadd.setText( "Add choice item" );
        btnadd.setType( ButtonType.OUTLINED );
        btnadd.setIconType(IconType.ADD);
        btnadd.setIconPosition( IconPosition.LEFT );
        btnadd.setMarginLeft(25);
        dialogRow.add( btnadd );
        
        if( item.isEmpty()){
            MaterialTextBox[] txtg = addNewItem();
            item.add( txtg );
            rowcontents.add( createItem( item, txtg, rowcontents ) );
        }
        
        btnadd.addClickHandler( handler ->{
            /*
            MaterialTextBox txtlabel = new MaterialTextBox( );
            txtlabel.setLabel( "Label" );
            txtlabel.setType(InputType.TIME);
            //txt.setType(InputType.DATETIME);
            txtlabel.setFieldType(FieldType.OUTLINED);
            
            MaterialTextBox txtvalue = new MaterialTextBox( );
            txtvalue.setLabel( "Value" );
            txtvalue.setType(InputType.TIME);
            //txt.setType(InputType.DATETIME);
            txtvalue.setFieldType(FieldType.OUTLINED);
            MaterialTextBox[] txtg = new MaterialTextBox[]{txtlabel,txtvalue };
            */
            MaterialTextBox[] txtg = addNewItem();
            item.add( txtg );
            rowcontents.add( createItem( item, txtg, rowcontents ) );
        });
    }
    
    private MaterialTextBox[] addNewItem( ){
        MaterialTextBox txtlabel = new MaterialTextBox( );
        txtlabel.setLabel( "Label" );
        //txtlabel.setType(InputType.TIME);
        //txt.setType(InputType.DATETIME);
        //txtlabel.setFieldType(FieldType.OUTLINED);

        MaterialTextBox txtvalue = new MaterialTextBox( );
        txtvalue.setLabel( "Value" );
        //txtvalue.setType(InputType.TIME);
        //txt.setType(InputType.DATETIME);
        //txtvalue.setFieldType(FieldType.OUTLINED);
        return new MaterialTextBox[]{txtlabel,txtvalue };
    }
    
    private MaterialRow createItem( 
            java.util.List<MaterialTextBox[]> item, 
            MaterialTextBox[] txt, MaterialRow rowcontents ){
        
        MaterialRow row = new MaterialRow( );
        row.setGrid( "l12 m12 s12" );
        MaterialColumn collabel = new MaterialColumn( );
        row.add( collabel );
        collabel.add( txt[0] );
        collabel.setGrid( "l5 m5 s10" );
        
        MaterialColumn colvalue = new MaterialColumn( );
        row.add( colvalue );
        colvalue.add( txt[1] );
        colvalue.setGrid( "l5 m5 s10" );
        
        MaterialAnchorButton btnremove = new MaterialAnchorButton();
        btnremove.setType( ButtonType.FLOATING );
        btnremove.setBackgroundColor( Color.RED );
        btnremove.setIconType( IconType.DELETE );
        btnremove.setSize( ButtonSize.MEDIUM );
        btnremove.setFloat(Style.Float.RIGHT);
        btnremove.setTooltip("Remove");
        
        //btnremove.setMarginRight(5);
        //btnremove.setGrid("l2 m2 s2");
        row.add( btnremove );
        btnremove.addClickHandler(handler ->{
            //if( txt[0].getValue() != null )
                //Window.alert( "Value=" + txt[0].getValue());
            
            //Window.alert( "Text=" + txt[0].getText());
            item.remove( txt );
            rowcontents.clear();
            
            if( item.isEmpty()){
            MaterialTextBox[] txtg = addNewItem();
            item.add( txtg );
            rowcontents.add( createItem( item, txtg, rowcontents ) );
        }
            
        });
        return row;
    }
}
