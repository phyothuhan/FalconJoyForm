/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.falconit.joyform.client.application.form.customwidget;


//import com.google.gwt.user.client.Window;
import com.google.gwt.core.client.GWT;
import gwt.material.design.addins.client.cropper.constants.Type;
import gwt.material.design.addins.client.cropper.MaterialImageCropper;
import gwt.material.design.addins.client.fileuploader.MaterialFileUploader;
import gwt.material.design.addins.client.fileuploader.MaterialUploadLabel;
import gwt.material.design.addins.client.fileuploader.base.UploadFile;
import gwt.material.design.addins.client.fileuploader.events.CompleteEvent;
import gwt.material.design.addins.client.fileuploader.events.SuccessEvent;
import gwt.material.design.addins.client.fileuploader.events.TotalUploadProgressEvent;
import gwt.material.design.addins.client.window.MaterialWindow;
import gwt.material.design.client.constants.ButtonSize;
import gwt.material.design.client.constants.ButtonType;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.MaterialAnchorButton;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialColumn;
import gwt.material.design.client.ui.MaterialDialogFooter;
import gwt.material.design.client.ui.MaterialImage;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialProgress;
import gwt.material.design.client.ui.MaterialRow;
import gwt.material.design.client.ui.MaterialTextBox;

/**
 *
 * @author User
 */
public class MediaUploading extends MaterialRow{
    
    public static final String BASED_URL = GWT.getHostPageBaseURL() + "/";//"http://theburmapeople.com:8090/autofacecentral/";//"http://localhost:8090/autofacecentral/";
    public static final String UPLOAD_FILE_PATH = "form-service/";
    public static final String FILE_TYPE_DOC = "doc";
    public static final String FILE_TYPE_IMAGE = "img";
    public static final String FILE_TYPE_FACIAL = "facial";
    private MaterialTextBox txtpath;
    private MaterialWindow dialog;
    private MaterialWindow dialogPreview;
    private MaterialWindow dialogCropper;
    private MaterialImage imgPreview;
    private MaterialImageCropper cropper;
    private String type;
    private long unitval = System.currentTimeMillis();
    private String value;
    
    public MediaUploading( String type ){
        this.type = type;
        MaterialColumn colPath = new MaterialColumn( );
        colPath.setGrid("l6 m6 s8");
        add( colPath );
        
        txtpath = new MaterialTextBox( );
        txtpath.setLabel("Resource URL");
        txtpath.setEnabled(false);
        colPath.add( txtpath );
                
        MaterialColumn colupload = new MaterialColumn( );
        //colupload.setGrid("l2 m2 s8");
        add( colupload );
        
        MaterialAnchorButton btnupload = new MaterialAnchorButton( );
        btnupload.setType( ButtonType.FLOATING );
        btnupload.setBackgroundColor( Color.TEAL );
        btnupload.setIconType( IconType.CLOUD_UPLOAD );
        btnupload.setSize( ButtonSize.MEDIUM );
        btnupload.setTooltip("Upload");
        colupload.add( btnupload );
        btnupload.addClickHandler(handler -> {
            dialog.open( );
        });
        
                
        MaterialColumn coledit = new MaterialColumn( );
        //coledit.setGrid("l2 m2 s8");
        add( coledit );

        MaterialAnchorButton btnpreview = new MaterialAnchorButton( );
        btnpreview.setType( ButtonType.FLOATING );
        btnpreview.setBackgroundColor( Color.TEAL );
        btnpreview.setIconType( IconType.PHOTO_ALBUM );
        btnpreview.setSize( ButtonSize.MEDIUM );
        btnpreview.setTooltip("Preview");
        coledit.add( btnpreview );
        btnpreview.addClickHandler(handler ->{
            if( !type.equals(FILE_TYPE_DOC)){
                imgPreview.setUrl( txtpath.getText() );
                dialogPreview.open();
            }
        });
        
        
        createDialog( );
        previewDialog();
        if( !type.equals(FILE_TYPE_DOC) ){
            previewCropper();
        }
    }
    
        
    private void createDialog( ){
        
        dialog = new MaterialWindow( );
        dialog.setWidth("50%");
        dialog.setTitle( "Upload your photo/doc" );
        dialog.setToolbarColor(Color.GREY_DARKEN_3);
        
        //dialog.setType( DialogType.DEFAULT );
        //dialog.setDismissible( true );
        //dialog.setInDuration(200);
        //dialog.setOutDuration(200);
        dialog.setPadding( 10 );
        
        MaterialLabel lbl = new MaterialLabel( );
        lbl.setTextColor(Color.RED_DARKEN_2);
        dialog.add(lbl);
        
        
        
        MaterialFileUploader fileLoader = new MaterialFileUploader( );
        fileLoader.setAcceptedFiles( "image/*,application/pdf,.doc,.docx,.xls,.xlsx" );
        fileLoader.setUrl( BASED_URL  + "fileupload?unitval=" + unitval + "&type=" + type );
        fileLoader.setMaxFileSize( 49 );
        dialog.add(fileLoader);
        
        MaterialUploadLabel uploadLabel = new MaterialUploadLabel();
        uploadLabel.setTitle( "Upload your photo/doc" );
        uploadLabel.setDescription( "Press upload or drap and drop here" );
        fileLoader.add( uploadLabel );
        
        MaterialDialogFooter footer = new MaterialDialogFooter();
        MaterialButton btnclose = new MaterialButton();
        btnclose.setText("Close");
        btnclose.setType( ButtonType.FLAT );
        btnclose.addClickHandler(handler ->{
            dialog.close( );
        });
        footer.add(btnclose);
        dialog.add(footer);
        add( dialog );
   
        uploadFile( fileLoader, null, null, lbl );
    }
    
    private void previewDialog( ){
        
        dialogPreview = new MaterialWindow();
        dialogPreview.setWidth("50%");
        dialogPreview.setTitle( "Preview" );
        dialogPreview.setToolbarColor(Color.GREY_DARKEN_3);
        dialogPreview.setPadding( 10 );
        
        /*
        dialogPreview.setType( DialogType.DEFAULT );
        dialogPreview.setDismissible( true );
        dialogPreview.setInDuration(200);
        dialogPreview.setOutDuration(200);
        */
        
        imgPreview = new MaterialImage();
        imgPreview.setMinHeight("250px");
        imgPreview.setMinWidth("250px");
        dialogPreview.add( imgPreview );
        
        MaterialDialogFooter footer = new MaterialDialogFooter();
        MaterialButton btnclose = new MaterialButton();
        btnclose.setText("Close");
        btnclose.setType( ButtonType.FLAT );
        btnclose.addClickHandler(handler ->{
            dialogPreview.close( );
        });
        footer.add(btnclose);
        dialogPreview.add(footer);
        add( dialogPreview );
        
    }
    
        
    private void previewCropper( ){
        
        dialogCropper = new MaterialWindow();
        dialogCropper.setWidth("50%");
        dialogCropper.setTitle( "Image cropper" );
        dialogCropper.setToolbarColor(Color.GREY_DARKEN_3);
        dialogCropper.setPadding( 10 );
        
        
        cropper = new MaterialImageCropper();
        //imgPreview.setMinHeight("350px");
        //imgPreview.setMinWidth("350px");
        dialogCropper.add( cropper );
        
        MaterialDialogFooter footer = new MaterialDialogFooter();
        MaterialButton btnclear = new MaterialButton();
        btnclear.setText("Crop");
        btnclear.setType( ButtonType.FLAT );
        btnclear.addClickHandler(handler ->{
           cropper.crop(Type.BASE64);
        });
        footer.add(btnclear);
                
        cropper.addCropHandler(valueChangeEvent -> {
            txtpath.setText( valueChangeEvent.getResult() );
            cropper.setUrl( txtpath.getText() );
            cropper.reload( );
            //imgPreview.setUrl(valueChangeEvent.getResult());
        });
        
        MaterialButton btnclose = new MaterialButton();
        btnclose.setText("OK");
        btnclose.setType( ButtonType.FLAT );
        btnclose.addClickHandler(handler ->{
            dialogCropper.close( );
        });
        footer.add(btnclose);
        dialogCropper.add(footer);
        add( dialogCropper );
        
    }
        
    private void uploadFile( MaterialFileUploader uploader, MaterialProgress progress, MaterialImage imgPreview, MaterialLabel lbl ){
         uploader.addTotalUploadProgressHandler(new TotalUploadProgressEvent.TotalUploadProgressHandler() {
            @Override
            public void onTotalUploadProgress(TotalUploadProgressEvent event) {
                if( progress != null )
                    progress.setPercent(event.getProgress());
            }
       });
         
        uploader.addCompleteHandler(new CompleteEvent.CompleteHandler<UploadFile>() {
            @Override
            public void onComplete(CompleteEvent<UploadFile> event) {
                //Window.alert( "Body="+ event.getResponse().getBody() );
                //MaterialToast.fireToast( "Event : Complete (" + event.getTarget().getName() + ")" );
            }
        });
        
        uploader.addSuccessHandler(new SuccessEvent.SuccessHandler<UploadFile>() {
            @Override
            public void onSuccess(SuccessEvent<UploadFile> event) {
                //Window.alert( "Body="+ event.getResponse().getBody() );
                if( type.equals(FILE_TYPE_FACIAL) ) {
                   if( event.getResponse().getBody().equals("OK")){

                      String uploadedFileName = event.getTarget().getName( );
                      txtpath.setText( BASED_URL + UPLOAD_FILE_PATH + unitval + "-" + uploadedFileName );
                      txtpath.clearErrorText();
                      txtpath.clearStatusText();
                      lbl.setText("");
                      
                      if( imgPreview != null )
                         imgPreview.setUrl(  BASED_URL + UPLOAD_FILE_PATH + unitval + "-" + uploadedFileName );
                   }else{
                       lbl.setText("* Invalid photo, photo must have face *");
                        txtpath.setErrorText("* Photo must have your face *");
                        txtpath.setText( "" );
                        if( imgPreview != null )
                            imgPreview.setUrl( "" );
                   }
                }else{
                   String uploadedFileName = event.getTarget().getName( );
                   txtpath.setText( BASED_URL + UPLOAD_FILE_PATH + unitval + "-" + uploadedFileName );
                   if( imgPreview != null && !type.equals(FILE_TYPE_DOC))
                      imgPreview.setUrl(  BASED_URL + UPLOAD_FILE_PATH + unitval + "-" + uploadedFileName );
                    dialog.close( );
                }
                
                if( !type.equals(FILE_TYPE_DOC) && txtpath.getText().length() > 10 ){
                    dialog.close( );
                    cropper.setUrl( txtpath.getText() );
                    cropper.reload();
                    dialogCropper.open();
                }
            }
           });
    }
    
    public void setValue( Object value ){
        if( value != null ){
            txtpath.setText(value.toString());
        }
    }
    
    public Object getValue(){
        return txtpath.getText( );
    }
}
