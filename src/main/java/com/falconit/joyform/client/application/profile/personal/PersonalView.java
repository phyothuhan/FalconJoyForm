package com.falconit.joyform.client.application.profile.personal;



import com.falconit.joyform.client.application.form.util.Field;
import com.falconit.joyform.client.application.form.util.Form;
import com.falconit.joyform.client.application.form.util.FormCRUD;
import com.falconit.joyform.client.application.form.util.WidgetGenerator;
import com.falconit.joyform.client.application.util.Constants;
import com.falconit.joyform.client.application.util.CookieHelper;
import com.falconit.joyform.client.place.NameTokens;
import com.falconit.joyform.client.ui.NavigatedView;
import com.falconit.joyform.shared.jsonconvert.ObjectConverter;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.addins.client.webp.MaterialWebpImage;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.IconSize;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.constants.ImageType;
import gwt.material.design.client.constants.WavesType;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialCollapsible;
import gwt.material.design.client.ui.MaterialCollapsibleBody;
import gwt.material.design.client.ui.MaterialCollapsibleHeader;
import gwt.material.design.client.ui.MaterialCollapsibleItem;
import gwt.material.design.client.ui.MaterialColumn;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialLoader;
import gwt.material.design.client.ui.MaterialPanel;
import gwt.material.design.client.ui.MaterialRow;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;


public class PersonalView extends NavigatedView implements PersonalPresenter.MyView {
    interface Binder extends UiBinder<Widget, PersonalView> {
    }
    
    private Map<String, Object[]> personMaps;
    private MaterialCollapsible coll;
    MaterialCollapsibleBody body[]=null;
    
    private Form myForm = new Form( );
    
    @UiField
    MaterialPanel holder;
        
    @Inject
    PersonalView(Binder uiBinder) {
        
        initWidget( uiBinder.createAndBindUi( this ) );
        
                
        if( CookieHelper.getMyCookie( Constants.COOKIE_USER_ID ) == null ){
            History.newItem( NameTokens.login );
        }
        coll = new MaterialCollapsible( );
        coll.setAccordion( true );

        body = new MaterialCollapsibleBody[10];

        for ( int i =0; i < body.length; i++){
            body[i] = new MaterialCollapsibleBody( );

            MaterialCollapsibleItem item = new MaterialCollapsibleItem();
            coll.add(item);

            MaterialCollapsibleHeader header = new MaterialCollapsibleHeader();
            item.add(header);
            MaterialLink lnktitle = new MaterialLink();
            lnktitle.setFontWeight( Style.FontWeight.BOLDER );
            lnktitle.setTextColor(Color.TEAL);
            lnktitle.setText( Constants.PROFILE_CATEGORIES[i] );
            header.add(lnktitle);//<m:MaterialIcon iconType="EDIT" waves="DEFAULT" float="LEFT" circle="true" iconSize="LARGE" />

            MaterialLink lnkedit = new MaterialLink( );
            lnkedit.setIconType(IconType.EDIT);
            lnkedit.setWaves(WavesType.DEFAULT);
            lnkedit.setIconColor(Color.TEAL);
            lnkedit.setFloat(Style.Float.LEFT);
            lnkedit.setCircle(true);
            lnkedit.setIconSize(IconSize.LARGE);
            lnkedit.setId(""+ i );
            header.add( lnkedit );
            lnkedit.addClickHandler(handler ->{
                edit( lnkedit.getId() );
            });
            
            item.add( body[i] );
        }

        coll.setActive(1);
        coll.setShadow(0);
        holder.add( coll );
        
        if( CookieHelper.getMyCookie(Constants.COOKIE_USER_ID) == null ){
            History.newItem( NameTokens.login );
        }else{
            getPerson( Long.parseLong( CookieHelper.getMyCookie(Constants.COOKIE_USER_PERSON_ID) ) );
            
        }
    }
    
    private void edit( String id ){
        
        int intID = Integer.parseInt( id );
        
        String group = "profile";
        switch (intID) {
            case 0:
                group = "profile";
                break;
            case 1:
                group = "contact";
                break;
            case 2:
                group = "places";
                break;
            case 3:
                group = "work & education";
                break;
            case 4:
                group = "documents";
                break;
            case 5:
                group = "travel info";
                break;
            case 6:
                group = "family & relationships";
                break;
            case 7:
                group = "bio-matric";
                break;
            case 8:
                group = "health-care";
                break;
            case 9:
                group = "others";
                break;
            default:
                break;
        }
        
        body[ intID ].clear();
        //WidgetGenerator generator = new WidgetGenerator();
        
        MaterialPanel panel = new MaterialPanel();
        panel.setMarginTop(20);
        panel.setShadow(1);
        panel.setPaddingTop(20);
        panel.setPaddingBottom(20);
        holder.clear();
        holder.add( panel );
        
        myForm.setItemDisplay(Form.ITEMS_DISPLAY_CATEGORIZE);
        myForm.render( panel );
        /*
        for( Field parent : myForm.getChild( ) ){
            if( parent.getCategory( ).equals( group )){
                try {
                    MaterialRow row = generator.getWidget( parent, 0 );
                    for( Field field : parent.getChildren() ){
                        int colSize = 12 / parent.getChildren( ).size( );
                        MaterialColumn child = new MaterialColumn( );
                        child.setId( field.getId( ) );
                        child.setTitle( field.getLabel().get(Constants.LANGUAGE) );
                        child.setGrid("l" + colSize+" m" + colSize+" s12");
                        row.add( child );
                        generator.generate( child, field, 0, Form.DISPLAY_MODE_FILL_UP );
                    }
                    panel.add( row );
                    
                } catch (Exception ex) {
                    Window.alert( ex.getMessage() );
                }
            }
        }
        */
        
        MaterialButton btnupdate = new MaterialButton();
        btnupdate.setText("Update");
        btnupdate.setIconType(IconType.UPDATE);
        btnupdate.setBackgroundColor(Color.TEAL);
        btnupdate.setTextColor(Color.WHITE);
        panel.add(btnupdate);
        btnupdate.setMargin(20);
        btnupdate.addClickHandler(handler ->{
                        
            try{
               updatePerson( );
            }catch(Exception ex){ Window.alert(ex.getMessage());}
            display( );
        });
        
        MaterialButton btnclose = new MaterialButton();
        btnclose.setText("Cancel");
        btnclose.setIconType(IconType.CANCEL);
        btnclose.setBackgroundColor(Color.TEAL);
        btnclose.setTextColor(Color.WHITE);
        panel.add( btnclose );
        btnupdate.setMargin(20);
        btnclose.addClickHandler(handler ->{
            display( );
        });
        
        
        coll.open( intID + 1 );
    }
    
    private void getForm( ){
        
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
                MaterialLoader.loading( false );
                //Window.alert("form load size="+result.size());
                if( !result.isEmpty() ){
                    myForm = result.get( 0 );
                    
                    try{
                        myForm.bindWithTaskData(personMaps);
                    }catch(Exception ex){Window.alert(ex.getMessage());}
                    
                    display();
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
        
        crud.getBy( Constants.DEFAULT_CONTAINER, Constants.DEFAULT_PROCESS, Constants.DEFAULT_TASK);
    }

    private void display(){
        
        holder.clear();
        holder.add( coll );
        
        for ( int i =0; i < body.length; i++){
            body[i].clear();
        }
        
        for( Field field : myForm.getChild( ) ){
                        
            MaterialRow row = new MaterialRow();
            MaterialLabel label = new MaterialLabel( );
            label.setGrid( "l4 m4 s12" );
            label.setFontSize("0.8em");
            label.setText( field.getChildren().get(0).getLabel().get( Constants.LANGUAGE ) );
            row.add( label );

            MaterialLabel value = new MaterialLabel();
            value.setGrid( "l6 m6 s12" );
            value.setFontWeight(Style.FontWeight.BOLD);
            row.add( value );

            if( field.getName().equals("photo")){
                
                MaterialWebpImage photo = new MaterialWebpImage();
                row.add(photo);
                photo.setGrid("l2 m2 s4");
                photo.setFloat(Style.Float.LEFT);
                photo.setFallbackExtension("png");
                photo.setMaxWidth("150px");
                photo.setMaxHeight("150px");
                photo.setType(ImageType.CIRCLE);
                if( field.getChildren().get(0).getValue() != null 
                        && !field.getChildren().get(0).getValue().toString().isEmpty()){
                    photo.setUrl( field.getChildren().get(0).getValue().toString() );
                }else
                    photo.setUrl("https://p7.hiclipart.com/preview/535/466/472/google-account-microsoft-account-login-email-gmail-email.jpg");

                value.setText( "A photo helps personalize your account" );
            }else{
                //Object[] values = personMaps.get(field.getName());
                //if( values[1] != null )
                if( field.getChildren().get(0).getValue() != null )
                    value.setText( field.getChildren().get(0).getValue().toString() ); //values[1].toString()
                else
                    value.setText( "" );
                row.setBorderBottom("1px dotted #b2dfdb");
            }

            if( field.getCategory( ).equals("profile")){
            if( field.getName().equals("photo") )
                body[0].insert(row, 0);
            else
                body[0].add(row);

            }else if( field.getCategory().equals("contact")){
                body[1].add(row);
            }else if( field.getCategory().equals("places")){
                body[2].add(row);
            }else if( field.getCategory().equals("work & education")){
                body[3].add(row);
            }else if( field.getCategory().equals("documents")){
                body[4].add(row);
            }else if( field.getCategory().equals("travel info")){
                body[5].add(row);
            }else if( field.getCategory().equals("family & relationships")){
                body[6].add(row);
            }else if( field.getCategory().equals("bio-matric")){
                body[7].add(row);
            }else if( field.getCategory().equals("health-care")){
                body[8].add(row);
            }else if( field.getCategory().equals("others")){
                body[9].add(row);
            }
        }// end of for
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
                personMaps = result;
                getForm();
            }

            @Override
            public void success(List<Map<String, Object[]>> result) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        
        crud.get( customerId );
    }
    
        
    private void updatePerson( ){
        if( CookieHelper.getMyCookie(Constants.COOKIE_USER_PERSON_ID) == null ){
            History.newItem( NameTokens.login );
            return;
        }
        try {
            MaterialLoader.loading( true );
            PersonCRUD crud = new PersonCRUD();
            crud.setListener(new PersonCRUD.CRUDListener(){
                @Override
                public void success(String result) {
                    MaterialLoader.loading( false );
                    try {
                        myForm.bindWithTaskData(personMaps);
                    } catch (Exception ex) {
                        Logger.getLogger(PersonalView.class.getName()).log(Level.SEVERE, null, ex);
                    }
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
                public void success(Map<String, Object[]> result) { }
                
                @Override
                public void success(List<Map<String, Object[]>> result) { }
            });
            
            personMaps = myForm.getOutputDataForPerson();
            personMaps.put( "id",  new Object[] { ObjectConverter.TYPE_NUMBER, CookieHelper.getMyCookie(Constants.COOKIE_USER_PERSON_ID) } );
            personMaps.put( "status",  new Object[] { ObjectConverter.TYPE_NUMBER, 1 } );
            //CookieHelper.setMyCookie( Constants.COOKIE_USER_PERSON_ID, cid );//customerId, new Object[] { ObjectConverter.TYPE_NUMBER, value };
            crud.saveUpdate( personMaps, false );
        } catch (Exception ex) {
            Window.alert(ex.getMessage());
        }
    }

}
