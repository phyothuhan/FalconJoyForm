package com.falconit.joyform.client.application.apps.privateapps;



import com.falconit.joyform.client.application.form.util.Form;
import com.falconit.joyform.client.application.form.util.FormCRUD;
import com.falconit.joyform.client.application.tasks.display.TaskDisplayView;
import com.falconit.joyform.client.application.util.Constants;
import com.falconit.joyform.client.application.util.CookieHelper;
import com.falconit.joyform.client.place.NameTokens;
import com.falconit.joyform.client.ui.NavigatedView;
import com.google.gwt.core.client.GWT;
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
import gwt.material.design.client.ui.MaterialSwitch;
import gwt.material.design.client.ui.MaterialTextArea;
import gwt.material.design.client.ui.MaterialToast;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PrivateAppsView extends NavigatedView implements PrivateAppsPresenter.MyView {
    interface Binder extends UiBinder<Widget, PrivateAppsView> {
    }

    @UiField
    MaterialRow appsholder;

    
    private List<java.util.Map<String, Object[]>> lstTasks = new ArrayList<>();
            
    @Inject
    PrivateAppsView(Binder uiBinder) {
        initWidget( uiBinder.createAndBindUi(this) );
        //load();
                
        if( CookieHelper.getMyCookie( Constants.COOKIE_USER_ID ) == null ){
            History.newItem( NameTokens.login );
            Window.Location.reload( );
        }else
            loadForms();
    }

        
    private void loadForms( ){
         MaterialLoader.loading( true );
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
                    
                    for( Form form : result ){
                        createProcess ( form );
                    }
                    
                    MaterialLoader.loading( false );
                }else{
                    MaterialLoader.loading( false );
                }
            }

            @Override
            public void fail(String message) {
                MaterialLoader.loading( false );
            }

            @Override
            public void fqdn( Map<String, Object[]> maps ) { }
        });
        String userId = CookieHelper.getMyCookie( Constants.COOKIE_USER_ID );
        crud.getBy( Long.parseLong( userId ) );
    }
    
        
    private java.util.Set<String> names = new java.util.HashSet<>();
    
    private void createProcess( Form form ){
        try{
            if( form.getTask().equalsIgnoreCase("start up"))
                return;
            
            if( names.contains( form.getProcess() )){
                return;
            }else{
                names.add( form.getProcess() );
            }
            
            MaterialColumn col = new MaterialColumn();
            col.setGrid("l4 m4 s12");
            appsholder.add(col);
            
            MaterialCard card = new MaterialCard( );
            card.setBackgroundColor( Color.GREY_LIGHTEN_3 );
            card.setShadow(0);
            col.add( card );
            
            MaterialCardContent content = new MaterialCardContent();
            card.add( content );
            //content.setTextColor(Color.WHITE);
            
            MaterialCardTitle title = new MaterialCardTitle();
            content.add( title );
            title.setIconType( IconType.APPS );
            title.setIconPosition( IconPosition.RIGHT );
            title.setText( form.getProcess().replace("-", " ") );
            
            try{
                MaterialLabel label = new MaterialLabel( );
                content.add( label );
                String version = form.getContainer().substring( form.getContainer().indexOf("_") + 1, 
                        form.getContainer().contains("-") ? 
                                form.getContainer().lastIndexOf("-") : form.getContainer().length() -1 );
                label.setText( "Version: " + version );
            }catch(Exception ex){}
            
                        
            MaterialSwitch onOff = new MaterialSwitch();
            onOff.setTextColor( Color.TEAL );
            onOff.setOnLabel("Share");
            onOff.setOffLabel("Only me");
            onOff.setMarginTop(15);
            onOff.setValue( (form.getStatus() == 1) );
            content.add( onOff );
            
            String shareLink = 
                "?container=" + form.getContainer()
                + "&process=" + form.getProcess()
                + "&title=" + form.getProcess()
                + "&taskName=" + Form.TASK_NAME_START_UP
                + "&display=" + TaskDisplayView.DISPLAY_START_UP
                + "&ownerId=" + form.getOwner()
                + "&owner=" + CookieHelper.getMyCookie( Constants.COOKIE_USER_NAME )
                + "#taskdisplay";
            
            MaterialTextArea txtlink = new MaterialTextArea();
            txtlink.setText( GWT.getHostPageBaseURL() + shareLink );
            //txtlink.setReadOnly( true );
            txtlink.setResizeRule(MaterialTextArea.ResizeRule.FOCUS);
            txtlink.setSelectionRange(0, txtlink.getText().length());
            txtlink.setVisible( false );
            content.add( txtlink );
             
            
            MaterialCardAction actions = new MaterialCardAction();
            card.add(actions);
            
            MaterialLink link = new MaterialLink( );
            actions.add( link );
            link.setText( "Form link" );
            link.setTooltip("Copy form link");
            link.setIconType( IconType.ATTACHMENT );
            link.setTextColor(Color.TEAL );
            //link.setHref( shareLink );
            link.addClickHandler(handler ->{
                txtlink.setVisible( !txtlink.isVisible() );
                MaterialToast.fireToast("Copied to clipboard", "rounded");
                copy( shareLink);
            });
            
            
            MaterialLink lnkuse = new MaterialLink( );
            actions.add( lnkuse );
            lnkuse.setText( "Edit" );
            lnkuse.setTextColor( Color.TEAL );
            lnkuse.setIconType( IconType.EDIT );
            
            lnkuse.setHref(
                "?container=" + form.getContainer()
                + "&process=" + form.getProcess()
                + "&taskId=" + form.getTask()
                + "#formediting" );
           
            
        }catch(Exception ex){}
    }

    private native void copy ( String s ) /*-{
        $wnd.window.clipboardData.setData("Text", s); 
     }-*/;
    
}
