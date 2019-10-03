/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.falconit.joyform.client.application.form.customwidget;


import gwt.material.design.addins.client.signature.MaterialSignaturePad;
import gwt.material.design.addins.client.window.MaterialWindow;
import gwt.material.design.client.constants.ButtonType;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.DialogType;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialColumn;
import gwt.material.design.client.ui.MaterialDialog;
import gwt.material.design.client.ui.MaterialDialogFooter;
import gwt.material.design.client.ui.MaterialImage;
import gwt.material.design.client.ui.MaterialRow;

/**
 *
 * @author User
 */
public class SignatureWidget extends MaterialRow {
    
    private MaterialWindow dialog;
    private MaterialSignaturePad signaturePad;
    private MaterialImage imageData;
    //private MaterialButton btnok, btnclear;
    
    public SignatureWidget( ){
        
        MaterialColumn colPath = new MaterialColumn( );
        colPath.setGrid("l12 m12 s12");
        add( colPath );
        
        imageData = new MaterialImage();
        imageData.setWidth("100px");
        imageData.setHeight("100px");
        //imageData.setMarginLeft(20);
        colPath.add( imageData );
        
        MaterialColumn colupload = new MaterialColumn( );
        add( colupload );
        
        MaterialButton btnupload = new MaterialButton( );
        btnupload.setIconType( IconType.EDIT );
        btnupload.setTooltip("Your signature here");
        btnupload.setText("Sign");
        colupload.add( btnupload );
        btnupload.addClickHandler(handler -> {
            dialog.open( );
        });
        
        createDialog();
    }
    
        
        
    public void setValue( Object value ){
        if( value != null ){
            imageData.setUrl( value.toString() );
        }
    }
    
    public Object getValue(){
        return signaturePad.toDataUrl();
    }
    
    private void createDialog( ){
        
        dialog = new MaterialWindow();
        dialog.setWidth("50%");
        dialog.setTitle( "Signature pad" );
        dialog.setToolbarColor(Color.GREY_DARKEN_3);
        /*
        dialog.setType( DialogType.DEFAULT );
        dialog.setDismissible( true );
        dialog.setInDuration(200);
        dialog.setOutDuration(200);
        */
        dialog.setPadding( 10 );
        
        signaturePad = new MaterialSignaturePad();
        signaturePad.setDotSize(12);//grid="s9" height="300px" penColor="black" border="1px solid #e9e9e9" />
        signaturePad.setGrid("s9");
        signaturePad.setHeight("300px");
        signaturePad.setPenColor("black");
        signaturePad.setBorder("1px solid #e9e9e9");
        dialog.add( signaturePad );
        
        MaterialDialogFooter footer = new MaterialDialogFooter();
        MaterialButton btnclear = new MaterialButton();
        btnclear.setText("Clear");
        btnclear.setType( ButtonType.FLAT );
        btnclear.addClickHandler(handler ->{
           signaturePad.clear();
        });
        footer.add(btnclear);
        
        MaterialButton btnok = new MaterialButton();
        btnok.setText("OK");
        btnok.setType( ButtonType.FLAT );
        btnok.addClickHandler(handler ->{
            imageData.setUrl(signaturePad.toDataUrl() );
            dialog.close();
        });
        footer.add( btnok );
        
        dialog.add(footer);
        add( dialog );
        
    }
    
}
