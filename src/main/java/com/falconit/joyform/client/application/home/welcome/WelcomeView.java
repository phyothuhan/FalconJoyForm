package com.falconit.joyform.client.application.home.welcome;


import com.falconit.joyform.client.application.profile.personal.PersonCRUD;
import com.falconit.joyform.client.application.util.Constants;
import com.falconit.joyform.client.application.util.CookieHelper;
import com.falconit.joyform.client.place.NameTokens;
import com.falconit.joyform.client.ui.NavigatedView;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.addins.client.webp.MaterialWebpImage;
import gwt.material.design.client.ui.MaterialLoader;
import gwt.material.design.client.ui.MaterialTitle;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

public class WelcomeView extends NavigatedView implements WelcomePresenter.MyView {
    interface Binder extends UiBinder<Widget, WelcomeView> {
    }

    @UiField MaterialWebpImage photo;
    @UiField MaterialTitle txtname;
    
    @Inject
    WelcomeView(Binder uiBinder) {
        initWidget( uiBinder.createAndBindUi(this) );
                
        if( CookieHelper.getMyCookie( Constants.COOKIE_USER_ID ) == null ){
            History.newItem( NameTokens.login );
        }
        
        long customerId = Long.parseLong(CookieHelper.getMyCookie(Constants.COOKIE_USER_PERSON_ID));
        getPerson( customerId );
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
                Object[] first = result.get("firstname");
                Object[] middle = result.get("middlename");
                Object[] last = result.get("lastname");
                
                StringBuilder sb = new StringBuilder();
                sb.append( first[1]!=null ? first[1].toString() : "" );
                sb.append(middle[1]!=null ? " " + middle[1].toString() : "" );
                sb.append(last[1]!=null ? " " + last[1].toString() : "" );
                txtname.setTitle("Welcome "+sb.toString());
                
                Object[] photoValue = result.get("photo");
                if( photoValue[1] != null && !photoValue[1].toString().isEmpty()){
                    photo.setUrl( photoValue[1].toString() );
                }
            }

            @Override
            public void success(List<Map<String, Object[]>> result) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        
        crud.get( customerId );
    }
    
}
