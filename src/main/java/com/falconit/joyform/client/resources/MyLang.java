/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.falconit.joyform.client.resources;

/**
 *
 * @author User
 */

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;


public interface MyLang extends Messages{
    public static final MyLang LANG = GWT.create( MyLang.class);
    
    String login( );
    String emailmobile( );
    String password( );
    String register( );
    String keep_me_logged_in( );
    String msg_login( );
    String msg_login_wait( );
    String msg_login_success( );
    String msg_login_fail_title( );
    String msg_login_fail();
}