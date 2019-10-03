package com.falconit.joyform.client.application.menu;

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


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import gwt.material.design.addins.client.combobox.MaterialComboBox;
import gwt.material.design.client.base.SearchObject;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.MaterialHeader;
import gwt.material.design.client.ui.MaterialNavBar;
import gwt.material.design.client.ui.MaterialSearch;
import gwt.material.design.client.ui.MaterialSideNavPush;
import com.falconit.joyform.client.ThemeManager;
import com.falconit.joyform.client.application.tasks.list.TasksListView;
import com.falconit.joyform.client.application.util.Constants;
import com.falconit.joyform.client.application.util.CookieHelper;
import com.falconit.joyform.client.application.util.jbpmclient.APIHelper;
import com.falconit.joyform.client.place.NameTokens;
import com.falconit.joyform.shared.jsonconvert.ObjectConverter;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import gwt.material.design.client.ui.MaterialBadge;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialLoader;
import gwt.material.design.incubator.client.language.Language;
import gwt.material.design.incubator.client.language.LanguageSelector;
import gwt.material.design.themes.amber.ThemeAmber;
import gwt.material.design.themes.blue.ThemeBlue;
import gwt.material.design.themes.brown.ThemeBrown;
import gwt.material.design.themes.client.ThemeLoader;
import gwt.material.design.themes.green.ThemeGreen;
import gwt.material.design.themes.grey.ThemeGrey;
import gwt.material.design.themes.orange.ThemeOrange;
import gwt.material.design.themes.pink.ThemePink;
import gwt.material.design.themes.purple.ThemePurple;
import gwt.material.design.themes.red.ThemeRed;
import gwt.material.design.themes.yellow.ThemeYellow;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

class MenuView extends ViewWithUiHandlers<MenuUiHandlers> implements MenuPresenter.MyView {

    interface Binder extends UiBinder<Widget, MenuView> {
    }


    private List<SearchObject> listSearches = new ArrayList<>();

    @UiField MaterialHeader header;
    @UiField MaterialNavBar navBar, navBarSearch;
    @UiField MaterialSideNavPush sideNav;
    @UiField MaterialSearch txtSearch;
    @UiField MaterialComboBox<ThemeLoader.ThemeBundle> comboThemes;
    @UiField MaterialBadge badgeinbox;
    @UiField MaterialLink account, logout;    
    @UiField LanguageSelector languageSelector;

    @Inject
    MenuView(Binder uiBinder) {
        initWidget(uiBinder.createAndBindUi(this));
        languageSelector.setLanguages(getLanguages());
    }

            
    @UiHandler("languageSelector")
    void selection(ValueChangeEvent<Language> e) {
        String lang = e.getValue().getValue();
        //asWidget().getElement().getStyle().setProperty("font-family", "myan3");
        //Window.alert(lang);
        /*
        if( e.getValue().getValue().equalsIgnoreCase("my")){
            asWidget().getElement().getStyle().setProperty("font-family", "myan3");
        }else{
            asWidget().getElement().getStyle().setProperty("font-family", "sans-serif");
        }
        */
    }
    static List<Language> getLanguages() {
        List<Language> languages = new ArrayList<>( );
        Language my = new Language("မြန်မာ", "my", "images/flag-3d-round-250.png");
        Language en = new Language("English", "en", "images/United_Kingdom_96354.png");
        Language hi = new Language("हिंदी", "hi", "images/India_96286.png");
        Language jp = new Language("日本語", "jp", "images/Japan_96239.png");
        Language kr = new Language("한국어", "kr", "images/Korea_96165.png");
        Language th = new Language("ไทย", "th", "images/Thailand_96170.png");
        Language zh = new Language("中文", "zh", "images/China_96342.png");
        languages.add( my );
        languages.add( en );
        languages.add( hi );
        languages.add( jp );
        languages.add( kr );
        languages.add( th );
        languages.add( zh );
        return languages;
    }
    
    @Override
    protected void onAttach() {
        super.onAttach();
        
        // search close event
        txtSearch.addCloseHandler(event -> {
            navBar.setVisible(true);
            navBarSearch.setVisible(false);
        });

        // search open event
        txtSearch.addOpenHandler(openEvent -> {
            navBarSearch.setVisible(true);
            navBar.setVisible(false);
        });

        sideNav.addOpenedHandler(event -> {
            getUiHandlers().setContentPush( );
            /*
            String un = CookieHelper.getMyCookie( "un" );
            if( un != null )
                Window.alert(un);
            else
                Window.alert("cookie null");
            */
        });
        sideNav.addClosedHandler(event -> {getUiHandlers().setContentPush();});

        initThemes( );
        initSearches( );
        if( CookieHelper.getMyCookie( Constants.COOKIE_USER_ID ) != null ){
            account.setVisible( true );
            loadNoti( );
        }
    }

    private void initSearches() {
        // About
        listSearches.add(new SearchObject(IconType.INFO_OUTLINE, "About", "#" + NameTokens.about));

        // Getting Started
        listSearches.add(new SearchObject(IconType.CLOUD_DOWNLOAD, "Getting Started", "#" + NameTokens.gettingstarted));

        // Components
        listSearches.add(new SearchObject(IconType.POLYMER, "PWA Getting Started", "#" + NameTokens.pwagettingstarted));
        listSearches.add(new SearchObject(IconType.POLYMER, "PWA Installable", "#" + NameTokens.installable));
        listSearches.add(new SearchObject(IconType.POLYMER, "PWA Service Worker", "#" + NameTokens.serviceworker));
        listSearches.add(new SearchObject(IconType.POLYMER, "PWA PushNotification", "#" + NameTokens.notification));

        // Components
        listSearches.add(new SearchObject(IconType.POLYMER, "Badges", "#" + NameTokens.badges));
        listSearches.add(new SearchObject(IconType.POLYMER, "Buttons", "#" + NameTokens.buttons));
        listSearches.add(new SearchObject(IconType.POLYMER, "Cards", "#" + NameTokens.cards));
        listSearches.add(new SearchObject(IconType.POLYMER, "CheckBox", "#" + NameTokens.checkbox));
        listSearches.add(new SearchObject(IconType.POLYMER, "Chips", "#" + NameTokens.chips));
        listSearches.add(new SearchObject(IconType.POLYMER, "Collapsible", "#" + NameTokens.collapsible));
        listSearches.add(new SearchObject(IconType.POLYMER, "Collections", "#" + NameTokens.collections));
        listSearches.add(new SearchObject(IconType.POLYMER, "Date Picker", "#" + NameTokens.pickers));
        listSearches.add(new SearchObject(IconType.POLYMER, "Dialogs", "#" + NameTokens.dialogs));
        listSearches.add(new SearchObject(IconType.POLYMER, "Dropdown", "#" + NameTokens.dropdown));
        listSearches.add(new SearchObject(IconType.POLYMER, "Errors", "#" + NameTokens.errors));
        listSearches.add(new SearchObject(IconType.POLYMER, "FAB", "#" + NameTokens.fabs));
        listSearches.add(new SearchObject(IconType.POLYMER, "Footer", "#" + NameTokens.footer));
        listSearches.add(new SearchObject(IconType.POLYMER, "Loaders", "#" + NameTokens.loader));
        listSearches.add(new SearchObject(IconType.POLYMER, "ListBox", "#" + NameTokens.listbox));
        listSearches.add(new SearchObject(IconType.POLYMER, "Media", "#" + NameTokens.media));
        listSearches.add(new SearchObject(IconType.POLYMER, "NavBar", "#" + NameTokens.navbar));
        listSearches.add(new SearchObject(IconType.POLYMER, "PushPin", "#" + NameTokens.pushPin));
        listSearches.add(new SearchObject(IconType.POLYMER, "Radio Button", "#" + NameTokens.radiobutton));
        listSearches.add(new SearchObject(IconType.POLYMER, "Range", "#" + NameTokens.range));
        listSearches.add(new SearchObject(IconType.POLYMER, "Search", "#" + NameTokens.search));
        listSearches.add(new SearchObject(IconType.POLYMER, "SideNavs", "#" + NameTokens.sidenavs));
        listSearches.add(new SearchObject(IconType.POLYMER, "Scrollspy", "#" + NameTokens.scrollspy));
        listSearches.add(new SearchObject(IconType.POLYMER, "Switches", "#" + NameTokens.switches));
        listSearches.add(new SearchObject(IconType.POLYMER, "Tabs", "#" + NameTokens.tabs));
        listSearches.add(new SearchObject(IconType.POLYMER, "TextFields", "#" + NameTokens.textfields));

        // Animations
        listSearches.add(new SearchObject(IconType.PLAY_CIRCLE_OUTLINE, "Core Animations", "#" + NameTokens.coreAnimations));
        listSearches.add(new SearchObject(IconType.PLAY_CIRCLE_OUTLINE, "Meaningful", "#" + NameTokens.meaningful));

        // DATA TABLE
        listSearches.add(new SearchObject(IconType.VIEW_LIST, "Standard DataTable", "#" + NameTokens.standardDataTable));
        listSearches.add(new SearchObject(IconType.VIEW_LIST, "Infinite DataTable", "#" + NameTokens.infiniteDataTable));
        listSearches.add(new SearchObject(IconType.VIEW_LIST, "Paged DataTable", "#" + NameTokens.pagedDataTable));
        listSearches.add(new SearchObject(IconType.VIEW_LIST, "Frozen DataTable", "#" + NameTokens.frozenDataTable));
        listSearches.add(new SearchObject(IconType.VIEW_LIST, "Custom DataTable", "#" + NameTokens.customDataTable));
        listSearches.add(new SearchObject(IconType.VIEW_LIST, "Context Menu DataTable", "#" + NameTokens.contextMenuDataTable));

        // Addins
        listSearches.add(new SearchObject(IconType.EXTENSION, "Autocomplete", "#" + NameTokens.autocomplete));
        listSearches.add(new SearchObject(IconType.EXTENSION, "Avatar", "#" + NameTokens.avatar));
        listSearches.add(new SearchObject(IconType.EXTENSION, "Bubble", "#" + NameTokens.bubble));
        listSearches.add(new SearchObject(IconType.EXTENSION, "Breadcrumb", "#" + NameTokens.breadcrumbs));
        listSearches.add(new SearchObject(IconType.EXTENSION, "Camera", "#" + NameTokens.camera));
        listSearches.add(new SearchObject(IconType.EXTENSION, "Carousel", "#" + NameTokens.carousel));
        listSearches.add(new SearchObject(IconType.EXTENSION, "Circular Progress", "#" + NameTokens.circularProgress));
        listSearches.add(new SearchObject(IconType.EXTENSION, "ComboBox", "#" + NameTokens.combobox));
        listSearches.add(new SearchObject(IconType.EXTENSION, "Count Up", "#" + NameTokens.countUp));
        listSearches.add(new SearchObject(IconType.EXTENSION, "CutOut", "#" + NameTokens.cutouts));
        listSearches.add(new SearchObject(IconType.EXTENSION, "Doc Viewer", "#" + NameTokens.docviewer));
        listSearches.add(new SearchObject(IconType.EXTENSION, "Drag and Drop", "#" + NameTokens.dnd));
        listSearches.add(new SearchObject(IconType.EXTENSION, "Empty States", "#" + NameTokens.emptystates));
        listSearches.add(new SearchObject(IconType.EXTENSION, "File Uploader", "#" + NameTokens.fileuploader));
        listSearches.add(new SearchObject(IconType.EXTENSION, "IconMorph", "#" + NameTokens.iconMorph));
        listSearches.add(new SearchObject(IconType.EXTENSION, "Image Cropper", "#" + NameTokens.imageCropper));
        listSearches.add(new SearchObject(IconType.EXTENSION, "Input Mask", "#" + NameTokens.inputMask));
        listSearches.add(new SearchObject(IconType.EXTENSION, "Live Stamp", "#" + NameTokens.liveStamp));
        listSearches.add(new SearchObject(IconType.EXTENSION, "Masonry", "#" + NameTokens.masonry));
        listSearches.add(new SearchObject(IconType.EXTENSION, "MenuBar", "#" + NameTokens.menubar));
        listSearches.add(new SearchObject(IconType.EXTENSION, "Overlay", "#" + NameTokens.overlay));
        listSearches.add(new SearchObject(IconType.EXTENSION, "PathAnimator", "#" + NameTokens.pathAnimator));
        listSearches.add(new SearchObject(IconType.EXTENSION, "Rating", "#" + NameTokens.rating));
        listSearches.add(new SearchObject(IconType.EXTENSION, "Rich Editor", "#" + NameTokens.richeditor));
        listSearches.add(new SearchObject(IconType.EXTENSION, "ScrollFire", "#" + NameTokens.scrollfire));
        listSearches.add(new SearchObject(IconType.EXTENSION, "Split Panel", "#" + NameTokens.splitpanel));
        listSearches.add(new SearchObject(IconType.EXTENSION, "Signature Pad", "#" + NameTokens.signaturePad));
        listSearches.add(new SearchObject(IconType.EXTENSION, "Subheader", "#" + NameTokens.subheaders));
        listSearches.add(new SearchObject(IconType.EXTENSION, "Steppers", "#" + NameTokens.steppers));
        listSearches.add(new SearchObject(IconType.EXTENSION, "Swipeable", "#" + NameTokens.swipeable));
        listSearches.add(new SearchObject(IconType.EXTENSION, "Time Picker", "#" + NameTokens.timepickers));
        listSearches.add(new SearchObject(IconType.EXTENSION, "Tree View", "#" + NameTokens.tree));
        listSearches.add(new SearchObject(IconType.EXTENSION, "Waterfall", "#" + NameTokens.waterfall));
        listSearches.add(new SearchObject(IconType.EXTENSION, "WebP", "#" + NameTokens.webpImage));
        listSearches.add(new SearchObject(IconType.EXTENSION, "Window", "#" + NameTokens.window));

        // Incubator
        listSearches.add(new SearchObject(IconType.ARCHIVE, "Alert", "#" + NameTokens.alert));
        listSearches.add(new SearchObject(IconType.ARCHIVE, "Async", "#" + NameTokens.async));
        listSearches.add(new SearchObject(IconType.ARCHIVE, "Language", "#" + NameTokens.languageSelector));
        listSearches.add(new SearchObject(IconType.ARCHIVE, "LoadingState", "#" + NameTokens.loadingState));
        listSearches.add(new SearchObject(IconType.ARCHIVE, "Progress Line Bar", "#" + NameTokens.progressLineBar));
        listSearches.add(new SearchObject(IconType.ARCHIVE, "(Google) Recaptcha", "#" + NameTokens.recaptcha));
        listSearches.add(new SearchObject(IconType.ARCHIVE, "(Google) AddressLookup", "#" + NameTokens.addressLookup));
        listSearches.add(new SearchObject(IconType.ARCHIVE, "Infinite Scroll", "#" + NameTokens.infiniteScroll));
        listSearches.add(new SearchObject(IconType.ARCHIVE, "Inline Search", "#" + NameTokens.inlineSearch));
        listSearches.add(new SearchObject(IconType.ARCHIVE, "Timer", "#" + NameTokens.timer));
        listSearches.add(new SearchObject(IconType.ARCHIVE, "Toggle", "#" + NameTokens.toggle));

        // Style And Layout
        listSearches.add(new SearchObject(IconType.STYLE, "Colors", "#" + NameTokens.colors));
        listSearches.add(new SearchObject(IconType.STYLE, "Grid", "#" + NameTokens.grid));
        listSearches.add(new SearchObject(IconType.STYLE, "Helper", "#" + NameTokens.helper));
        listSearches.add(new SearchObject(IconType.STYLE, "Icons", "#" + NameTokens.icons));
        listSearches.add(new SearchObject(IconType.STYLE, "Shadow", "#" + NameTokens.shadow));
        listSearches.add(new SearchObject(IconType.STYLE, "Theming", "#" + NameTokens.themes));

        // Roadmap
        listSearches.add(new SearchObject(IconType.TIMELINE, "Roadmap", "#" + NameTokens.roadmap));

        // Showcase
        listSearches.add(new SearchObject(IconType.WEB, "Showcase", "#" + NameTokens.showcase));

        // Apps
        listSearches.add(new SearchObject(IconType.WEB, "Apps", "#" + NameTokens.apps));

        // Template
        listSearches.add(new SearchObject(IconType.DASHBOARD, "Template", "#" + NameTokens.templates));

        // Charts
        listSearches.add(new SearchObject(IconType.INSERT_CHART, "Charts", "#" + NameTokens.charts));

        txtSearch.setListSearches(listSearches);

    }

    protected void initThemes() {
        ThemeManager.initialize();
        ThemeManager.register(sideNav );
        ThemeManager.register(navBar, ThemeManager.DARKER_SHADE);
        buildThemeList();
        ThemeManager.loadTheme( ThemeGrey.INSTANCE );
        
        
        comboThemes.setSingleValue(ThemeManager.getBundle());
        comboThemes.addValueChangeHandler(event -> {
            ThemeManager.loadTheme(comboThemes.getSingleValue());
        });
        
    }

    protected void buildThemeList() {
        comboThemes.addItem("Amber", ThemeAmber.INSTANCE);
        comboThemes.addItem("Blue", ThemeBlue.INSTANCE);
        comboThemes.addItem("Brown", ThemeBrown.INSTANCE);
        comboThemes.addItem("Green", ThemeGreen.INSTANCE);
        comboThemes.addItem("Grey", ThemeGrey.INSTANCE);
        comboThemes.addItem("Orange", ThemeOrange.INSTANCE);
        comboThemes.addItem("Pink", ThemePink.INSTANCE);
        comboThemes.addItem("Purple", ThemePurple.INSTANCE);
        comboThemes.addItem("Red", ThemeRed.INSTANCE);
        comboThemes.addItem("Yellow", ThemeYellow.INSTANCE);
    }

    @UiHandler("btnSearch")
    void onSearch(ClickEvent e){
        txtSearch.open();
    }
    
        
    @UiHandler("logout")
    void onLogout( ClickEvent e ){
        CookieHelper.setMyCookie( Constants.COOKIE_USER_NAME, null );
        CookieHelper.setMyCookie( Constants.COOKIE_USER_ID, null );
        CookieHelper.setMyCookie( Constants.COOKIE_USER_PERSON_ID, null );
        CookieHelper.setMyCookie( Constants.COOKIE_USER_ROLES, null );
        CookieHelper.setMyCookie( Constants.COOKIE_USER_CREDENTIAL, null );
        History.newItem( NameTokens.login );
        Window.Location.reload( );
    }
    
        
    private void loadNoti(){
            
        MaterialLoader.loading( true );
        APIHelper helper = new APIHelper();
        helper.setListener(new APIHelper.APIHelperListener() {
            @Override
            public void success(String result) {
                
                MaterialLoader.loading( false );
                
                JSONObject jsonOnlineUser = JSONParser.parseStrict( result ).isObject();
                JSONArray tasks = jsonOnlineUser.get("task-summary").isArray();
                if( tasks == null || tasks.size() == 0 ){
                    
                }else{
                    int notiCount = 0;
                    for( int i=0; i < tasks.size(); i++){
                        JSONObject task = tasks.get(i).isObject();
                        
                        try {
                            java.util.Map<String, Object[]> taskMap = new ObjectConverter().fromJSON( task, false, false );
                            String cid = taskMap.get("task-container-id")[1].toString();
                            if( !Constants.containerFilter.contains( cid )){
                                String userName = CookieHelper.getMyCookie( Constants.COOKIE_USER_NAME );
                                String userId =CookieHelper.getMyCookie( Constants.COOKIE_USER_ID );
                                
                                
                                String actualOwner = taskMap.get("task-actual-owner")[1] != null ? taskMap.get("task-actual-owner")[1].toString() : "";
                                String createdBy = taskMap.get("task-created-by")[1] !=null ? taskMap.get("task-created-by")[1].toString() : "";
                                if( ( !actualOwner.isEmpty() && actualOwner.equals( userId +"-" + userName )) 
                                        || (!createdBy.isEmpty() && createdBy.equals(userId +"-" + userName))){
                                    
                                    String status = taskMap.get("task-status")[1].toString();
                                    if( status.equalsIgnoreCase(APIHelper.STATUS_CREATED) 
                                            || status.equalsIgnoreCase(APIHelper.STATUS_READY)
                                            || status.equalsIgnoreCase(APIHelper.STATUS_INPROGRESS)
                                            || status.equalsIgnoreCase(APIHelper.STATUS_RESERVED)){
                                        notiCount++;
                                    }
                                }
                            }
                            badgeinbox.setText(notiCount +" active" +( notiCount>1 ? "s" : "" ) );
                            //filter with user
                            //"task-actual-owner": "wbadmin",
                            //"task-created-by": "wbadmin",
                            
                            //Window.alert("Task id = "+taskMap.get("task-id")[1].toString() +", Created on=" + (long)taskMap.get("task-created-on")[1]);
                        } catch (Exception ex) {
                            Window.alert(ex.getMessage());
                            Logger.getLogger(TasksListView.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                }
            }

            @Override
            public void fail(String message, int stage) {
              Window.alert( message );
              MaterialLoader.loading( false );
            }
        });
        String arrStatus[] = new String[]{ APIHelper.STATUS_CREATED, APIHelper.STATUS_READY, APIHelper.STATUS_INPROGRESS, APIHelper.STATUS_RESERVED };

        
        helper.tasksList( 
                //new String[]{APIHelper.STATUS_READY,APIHelper.STATUS_RESERVED, APIHelper.STATUS_INPROGRESS },
                arrStatus,
                0, 1000, null, null, true);
        //helper.query( Constants.containerId, "355");
    }
    
}