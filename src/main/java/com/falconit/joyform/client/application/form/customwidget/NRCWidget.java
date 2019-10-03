/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.falconit.joyform.client.application.form.customwidget;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import gwt.material.design.addins.client.combobox.MaterialComboBox;
import gwt.material.design.client.constants.InputType;
import gwt.material.design.client.constants.TextAlign;
import gwt.material.design.client.ui.MaterialColumn;
import gwt.material.design.client.ui.MaterialRow;
import gwt.material.design.client.ui.MaterialTextBox;

/**
 *
 * @author User
 */
public class NRCWidget extends MaterialRow{
    

    private MaterialComboBox<String> cboNo;
    private MaterialComboBox<String> cboTSP;
    private MaterialComboBox<String> cboType;
    private MaterialTextBox txtNumber;
    
    public NRCWidget( ){
        MaterialColumn colNo = new MaterialColumn();
        colNo.setGrid("l2 m2 s3");
        cboNo = new MaterialComboBox<>( );
        colNo.add( cboNo );
        add(colNo);
        for( int i=1; i < 15; i++)
            cboNo.addItem("" + i, "" + i);
        cboNo.setSelectedIndex(0);
        
        MaterialColumn collbl = new MaterialColumn();
        collbl.setGrid("l1 m1 s1");
        collbl.setTextAlign(TextAlign.CENTER);
        MaterialTextBox lbl = new MaterialTextBox();
        lbl.setText("/");
        lbl.setTextAlign(TextAlign.CENTER);
        lbl.setEnabled(false);
        
        collbl.add(lbl);
        add(collbl);
        
        MaterialColumn colTSP = new MaterialColumn();
        colTSP.setGrid( "l2 m2 s4" );
        cboTSP = new MaterialComboBox<>( );
        colTSP.add( cboTSP );
        add( colTSP );
        for( String t : a1 )
            cboTSP.addItem( t, t );
        cboTSP.setSelectedIndex(0);
        
        MaterialColumn colType = new MaterialColumn( );
        colType.setGrid("l1 m1 s4");
        add( colType );
        cboType = new MaterialComboBox<>( );
        colType.add(cboType);
        cboType.addItem("နိုင်", "နိုင်");
        cboType.addItem("ဧည့်", "ဧည့်");
        cboType.addItem("ပြု", "ပြု");
        
        
        MaterialColumn colNum = new MaterialColumn();
        colNum.setGrid("l3 m3 s3");
        add( colNum );
        txtNumber = new MaterialTextBox();
        txtNumber.setType(InputType.NUMBER );
        txtNumber.setMaxLength( 6 );
        colNum.add(txtNumber);

        cboNo.addValueChangeHandler( handler -> {
            String tsp[] = mapTSP.get(cboNo.getSelectedValue().get(0));
            cboTSP.clear();
            for( String t : tsp )
                cboTSP.addItem( t, t );
            cboTSP.setSelectedIndex(0);
        });
        
                // third action
        txtNumber.addKeyUpHandler(new KeyUpHandler(){
            @Override
            public void onKeyUp(KeyUpEvent event) {
                if( txtNumber.getText().trim().length() < 6 ){
                    //txtNumber.setErrorText("6 digits required");
                }
            }
        });
        
       mapTSP.put("1", a1);
       mapTSP.put("2", b2);
       mapTSP.put("3", c3);
       mapTSP.put("4", d4);
       mapTSP.put("5", e5);
       mapTSP.put("6", f6);
       mapTSP.put("7", g7);
       mapTSP.put("8", h8);
       mapTSP.put("9", i9);
       mapTSP.put("10", j10);
       mapTSP.put("11", k11);
       mapTSP.put("12", l12);
       mapTSP.put("13", m13);
       mapTSP.put("14", n14);
    }
    
    public void setValue( Object value ){
        if( value == null ) return;
        try{
            String v = (String) value;
            
            if( v.length() < 10) return;
            
            String no = v.substring( 0, v.indexOf("/"));
            String tsp = v.substring( v.indexOf("/") + 1, v.indexOf("("));
            String type = v.substring( v.indexOf("(") + 1, v.indexOf(")"));
            String number = v.substring( v.indexOf(")") + 1, v.length() );
//Window.alert("Value=" + v +", NO=" + no+", tsp=" + tsp +", type=" + type+", num=" + number);

            
            txtNumber.setText( number );

            java.util.List<String> noItems = new java.util.ArrayList<>();
            noItems.add( no );
            cboNo.setSingleValue(no, true);
            //cboNo.setValues( noItems, true );
            
            java.util.List<String> tspItems = new java.util.ArrayList<>();
            tspItems.add( tsp );
            cboTSP.setSingleValue(tsp, true);
            //cboTSP.setValues( tspItems, false );
            
            java.util.List<String> typeItems = new java.util.ArrayList<>();
            typeItems.add( type );
            cboType.setSingleValue( type, true );
            //cboType.setValues( typeItems, false );
            
        }catch(Exception ex){}
    }
    
    public Object getValue(){
        Object object = null;
        
        String no = cboNo.getSelectedValue().get(0);
        String tsp = cboTSP.getSelectedValue().get(0);
        String type = cboType.getSelectedValue().get(0);
        String number = txtNumber.getText();
        String value = no + "/" + tsp + "(" + type + ")" + number;
        object = value;
        
        return object;
    }

    private java.util.Map<String, String[]> mapTSP = new java.util.HashMap<>();
    private String a1[] = new String[]{"ကတန","ကမတ","ကမန","ခတဒ","ခဖန","ခဘဒ","ဆဒန","တနန","နမန","ပကတ","ပညန","ပတအ","ပဘတ","ဗမန",
"မကက","မကတ","မကထ","မကန","မကရ","မခဘ","မဂဒ","မစန","မညန","မထန","မဘန","မဘပ","မမန","မအန","ရကန",
"ရနက","လခန","လဂန","လမန","ဝမန","သတန","ဟခန","ဟပန","အခက","အဂယ","အပန","ဥမန"};
    
    private String b2[] = new String[]{"ကမန","ကလန","ကလဝ","ကဝန","ဂလန","ဒမဆ","ဒမသ","ပတက","ဖဆန","ဖရဆ","ဘလခ","မဂတ","မဂဒ","မရန",
"ရကန","ရတန","လကန","လမတ","အလန","ဥကလ"};
    
    private String c3[] = new String[]{"ကကန","ကကရ","ကခန","ကစက","ကဆက","ကမန","ကလန","တကန","တဆက","တတန","တယန","တအန","နတလ",
"ပကန","ပအန","ဖပန","ဖအန","ဗအန","ဘကဆ","ဘလန","ဘသဆ","ဘအန","မဝတ","လစန","လဘန","လသန","သကန",
"သတန","သထန","အအန"};
    
    private String d4[] = new String[]{"ကကန","ကပလ","ကဝလ","တဇန","တတန","တတလ","ထတလ","ဓမန","ပလန","ပလဝ","ဖပန","ဖလန","မတန","မတပ",
"မမက","မလန","ရဇန","ဟခန"};
    
    private String e5[] = new String[]{"ကဆန","ကတလ","ကနန","ကတလ","ကလတ","ကလထ","ကလန","ကလဝ","ကလသ","ကသန","ခတန","ခပန",
"ခမန","ခဥက","ခဥတ","ခဥန","ငဇန","စကန","စကရ","စသန","ဆကပ","ဆလန","ဆလက","ညနန","တငန","တဆန",
"တမန","တသန","ထခန","ထဥတ","ဒပယ","ဓကန","ပဖန","ပရန","ပလန","ပလတ","ပလသ","ဖပန","ဗမန","ဘတလ",
"ဘအန","မကန","မမက","မမတ","မမန","မမရ","မယန","မရက","မရန","မလန","ယမပ","ရတန","ရတရ","ရထန","ရဘန",
"ရသန","ရသယ","ရအန","ရဥန","လမန","လရန","လဝန","ဝလန","ဝသန","သကန","သကလ","ဟမလ","အတန","အရတ",
"အရသ"};
    
    private String f6[] = new String[]{"ကစန","ကပန","ကယယ","ကရန","ကလအ","ကသတ","ကသန","ခမက","တဂန","တဓန","တပန","တဝန","တသန","တသရ",
"ထဝန","ပကမ","ပတန","ပနလ","ပလတ","ပလန","ပသန","ဖမန","ဗအရ","ဘပန","ဘလန","မကန","မဂတ","မတရ","မမန",
"မလန","မလလ","မအန","မအရ","ရပန","ရဖန","လကန","လလန","ဝကန","ဝထန","သရခ"};
    
    private String g7[] = new String[]{"ကကခ","ကကန","ကကပ","ကကရ","ကငန","ကစန","ကတခ","ကတန","ကထခ","ကပက","ကပတ","ကပန","ကမက","က၀န",
"ကသန","စခန","စမန","ဇကန","ညလပ","တငန","တဆန","တတခ","တမန","တ၀န","ထတပ","ဒဥန","ဓမန","ဓဥန","နတလ",
"ပခတ","ပခန","ပစန","ပဇန","ပတက","ပတခ","ပတတ","ပတန","ပဓန","ပဖန","ပမန","ပလန","ပ၀က","ဖခန","ဖတန",
"ဖပန","ဖမန","ဖဥန","ဗတန","ဗမန","မကန","မခန","မစန","မညန","မတန","မလန","ရကတ","ရကန","ရတန","ရတရ",
"ရပတ","ရမတ","ရမန","လကန","လပတ","လပန","လမန","၀ခမ","၀တန","၀မန","သ၀တ","သကန","သနပ","သပန",
"သလပ","သလ၀","သ၀က","သ၀တ","သ၀န","အတန","အပန","အဖင","အဖန","အဘန","အမန"};
    
    private String h8[] = new String[]{"ဝခက","ကတက","ကတတ","ကပန","ကမန","ကလန","ခမန","ဂဂန","ငဖန","စတရ","စလန","ဆတက","ဆပန","ဆပဝ","ဆဖန","ဆမန","ဆလန","တကက","တတက","ထလန","နပန","နမန","နမဥ","နလန","ပကန","ပခက","ပခတ","ပဖန","ပမန","ဘရန","ဘလန","မကန","မခက","မခန","မတန","မထန","မမန","မဘန","မသန","မအရ","ရကန","ရစက","ရတန","ရနခ","ရမက","ရဥမ","သရန","သလန","ဟမန","အမရ","အလန","ဥမန"};
    
    private String i9[] = new String[]{"ကကရ", "ကခတ","ကဆန","ကပတ","ကပဘ","ကဘန","ကမကန","ကမတ","ကမန","ကဝတ", "ကသန","ခမခ","ခမစ","ခမန","ခအဇ","ငဇန","ငသရ","စကတ","စကန","စကရ","စတတ","စတန","စသန",
 "ဇဇန","ဇပသ","ဇဗသ","ဇယသ","ညဥန","ညဥလ","တကတ","တကန","တတက", "တတဥ","တပတ","တမန","တယန","တသန","ဒခသ","ဒဏသ","နတက","နထက", "ပကခ","ပကစ","ပခန","ပတခ","ပတန",
"ပတလ","ပနလ","ပဗသ","ပဘန", "ပမန","ပလန","ပသက","ပဥလ","ဗမန","မကန","မခန","မတန","မတမ","မတရ","မတလ","မထလ","မနက","မနတ","မနမ","မနလ","မပန","မဖန","မဘန","မမတ","မမန",
"မယတ","မယမ","မရက","မရတ","မရန","မရပ","မရဘ","မရမ","မရသ","မလန","မသက","မသန","မသရ","မဟမ","မအဇ","မအန","မအရ","မဥလ","ရနခ","ရဘန","ရမတ","ရမသ","ရဥန","လဆန","လဝန",
"ဝတန","ဝထန","သကမ","သခက","သစန","သတန","သဓန","သပက","သပတ","သရပ","အတန","အဓန","အမဇ","အမရ","ဥတသ"};
    
    private String j10[] = new String[]{"ကကန","ကတန","ကထန","ကမရ","ကလန","ကသန","ခစန","ခဆန","ခဇန","စတန","ဆခန","ဇလမ","တဖန","တမန","တလန","တဝန","ပခက","ပဒန","ပမန","ဖပရ","ဘလန","မကမ","မဆန",
"မဒန","မဓန","မပန","မမန","မလခ","မလတ","မလန","မလမ","မအန","မဥန","ယပရ","ရမန","လဘန","လမတ","လမန","သကန","သခရ","သတန","သထန","သထလ","သဖရ","သဗရ","သမရ",
"သလန","အမန","အလန"};
    
    private String k11[] = new String[]{"ကတလ","ကဖန","ကမန","ခတန","ခမန","ဂမန","ဂလမ","စတန","စသန","ဉထသ","တကတ","တကန","တစန","တပဝ","တဖန","ဒတဂ","ဓဓန","ပဏက","ပဏတ","ပတန","ပတပ","ပသတ",
"ဖအန","ဘတသ","ဘလန","ဘသတ","မဂန","မတန","မပတ","မပန","မပမ","မလမ","မအတ","မအန","မဥန","ရပန","ရဗန","ရမန","ရသတ","သကန","သတန","အမန"};
    
    private String l12[] = new String[]{"ကကက","ကခက","ကခတ","ကခန","ကခရ","ကစက","ကတက","ကတတ","ကတန","ကတလ","ကထက","ကထထ","ကပတ","ကပန","ကဗတ","ကမက","ကမတ","ကမထ","ကမန",
"ကမရ","ကမလ","ကလထ","ကလန","ကလဝ","ကသရ","ခခန","ခဂတ","ခပန","ခမန","ခရက","ခရန","ခရဟ","ဂဟန","စကန","စခဂ","စခန","စခမ","စဂန","စဇန","စတန","စပန","စပရ",
"ဆကခ","ဆကန","ဆခန","ဆဖန","တကက","တကတ","တကန","တခန","တခလ","တဂတ","တငန","တတက","တတတ","တတထ","တတန","တတပ","တထန","တထပ","တပန","တဖန","တဗတ",
"တမ","တမတ","တမန","တဝန","တသန","ထကန","ထတတ","ထတပ","ထတမ","ထမန","ထသန","ဒကတ","ဒဂဆ","ဒဂတ","ဒဂန","ဒဂမ","ဒဂရ","ဒဒရ","ဒနပ","ဒပန","ဒဖန","ဒရန","ဒလန",
"ဓခန","ဓဓန","ဓနဖ","ဓနမ","ဓလန","နကတ","နကယ","နမန","ပကတ","ပခန","ပဂတ","ပစတ","ပဆတ","ပဇတ","ပဇန","ပတတ","ပတန","ပတအ","ပဇက","ပဘတ","ပဘန","ပမန","ပရတ",
"ပလန","ပသတ","ပသန","ပအတ","ဖပန","ဖဟန","ဗကတ","ဗကလ","ဗဂတ","ဗတက","ဗတတ","ဗတထ","ဗတန","ဗတလ ","ဗထထ","ဗပန","ဗဘတ","ဗမန","ဗယန","ဗရက","ဗလန","ဗလဗ",
"ဗဟတ","ဗဟန","ဘကတ","ဘကလ","ဘတထ","ဘဓန","ဘပတ","ဘလန","မကတ","မကန","မကမ","မခန","မဂက","မဂတ","မဂဒ","မဂဓ","မဂန","မဂလ","မဂဝ","မဆန","မဇတ","မညန",
"မတက","မတည","မတတ","မတထ","မတန","မတလ","မထလ","မဒဂ","မဒန","မဓန","မနက","မနမ","မပန","မဘတ","မဘန","မမက","မမတ","မယတ","မရက","မရတ","မရန","မလတ",
"မလန","မဝတ","မသန","မဟန","မအတ","မအန","မအပ","မအရ","မဥန","ယပသ","ရကတ","ရကန ","ရကမ","ရတန","ရပတ","ရပသ","ရမက","ရယခ","ရလန","ရသပ","လကတ","လကန",
"လကပ","လစန","လတတ","လတန","လတပ","လထန","လပတ","လပန","လဘန","လမက","လမတ","လမန","လမရ","လမသ","လရန","လလန","လဝန","လသန","လသမ","လသယ","လသရ",
"လသသ","လအန","ဝတန","သကက","သကဃ","သကတ","သကန","သကလ","သခက","သခန","သဃက","သဃတ","သဃန","သညက","သတက","သတတ","သတန","သနပ","သပန","သမန",
"သရန","သလက","သလတ","သလန","သလပ","သလရ","ဟခန","ဟသတ","အခက","အခန","အဂတ","အဂန","အစန","အစရ","အဆန","အတန","အထက","အပန","အဖန","အမတ","အမန",
"အလက","အလန","အသန","ဥကက","ဥကတ","ဥကန","ဥကပ","ဥကမ","ဥကလ","ဥတက","ဥတတ","ဥတမ","ဥပတ","ဥသတ"};
    
    private String m13[] = new String[]{"ကကန","ကကရ","ကခန","ကခလ","ကစန","ကတတ","ကတန","ကတရ","ကတလ","ကထန","ကမန","ကမရ","ကရန",
"ကလတ","ကလထ","ကလဒ","ကလန","ကသန","ကသဟ","ကဟန","ခရဟ","ငစန","စကန","ဆဆန","ညကန","ညရန",
"တကန","တခက","တခန","တခလ","တစလ","တတန","တထန","တပန","တမည","တယန","တရန","တလန","တဟန",
"နခတ","နခန","နခမ","နစတ","နစန","နဆန","နတယ","နတရ","နမက","နမတ","ပဆတ","ပဇတ","ပတယ","ပဘတ",
"ပယန","ပလတ","ပလန","ပသရ","ဖခန","ဗဆန","ဘအန","မကတ","မကန","မခန","မငန","မဆက","မဆတ","မဆန",
"မဆလ","မတတ","မတန","မနတ","မနန","မပတ","မပန","မဖတ","မဖန","မဘန","မမက","မမတ","မမန","မယတ",
"မယန","မရတ","မရန","မလတ","မလန","မဟရ","မအန","ရကစ","ရခန","ရငန","ရစန","ရစရ","လကတ","လကန","လကရ",
"လခတ","လခန","လနန","လရန","လလန","လလရ","လသန","သကတ","သနန","သပန","ဟပတ","ဟပန","ဟပမ",
"ဥကမ"};
    
    private String n14[] = new String[]{"ကကတ","ကကထ","ကကန","ကကလ","ကခန","ကတခ","ကပတ","ကပန","ကပရ","ကဖန","ကဘလ","ကမန","ကမရ",
"ကလန","ခလန","ပသတ","ငဆန","ငပတ","ငရက","ငသခ","စခမ","စနဖ","စနမ","ဆကလ","ဇလန","ညတန","ညနတ","တကန",
"တကလ","တခမ","တပတ","တလတ","တသတ","ထကက","ဒဂပ","ဒဂမ","ဒဒရ","ဒနဖ","ဓနဖ","ပစလ","ပတတ","ပတန",
"ပတပ","ပဖန","ပဖမ","ပမန","ပသန","ပသယ","ပသရ","ဖပန","ဖမန","ဗကလ","ဗတထ","ဗသန","ဘကလ","ဘတလ","မကမ",
"မကလ","မဂပ","မဆန","မတန","မပက","မပန","မဘန","မမက","မမတ","မမန","မလမ","မသန","မအန","မအပ","မအဖ",
"မအမ","ရကန","ရသယ","လပတ","လပန","လမတ","လမန","ဝခမ","ဝတန","သကတ","သခမ","သပန","သမန","ဟကက", "ဟသတ","ဟသန","အခမ","အဂဒ","အဂပ","အမတ","အမန" };

}
