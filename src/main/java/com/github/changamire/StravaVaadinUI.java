package com.github.changamire;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javastrava.api.v3.auth.AuthorisationService;
import javastrava.api.v3.auth.impl.retrofit.AuthorisationServiceImpl;
import javastrava.api.v3.auth.model.Token;
import javastrava.api.v3.model.StravaAthlete;
import javastrava.api.v3.model.StravaStream;
import javastrava.api.v3.service.Strava;

import org.geojson.geometry.LineString;
import org.geojson.object.Feature;
import org.geojson.object.FeatureCollection;
import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LPolyline;
import org.vaadin.addon.leaflet.LTileLayer;
import org.vaadin.addon.leaflet.shared.Point;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;
import org.vaadin.addons.lazyquerycontainer.LazyQueryDefinition;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.DetailsGenerator;
import com.vaadin.ui.Grid.RowReference;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.ClickableRenderer.RendererClickEvent;
import com.vaadin.ui.renderers.ClickableRenderer.RendererClickListener;
import com.vaadin.ui.renderers.ImageRenderer;
import com.vaadin.ui.themes.ValoTheme;

@Theme("strava")
public class StravaVaadinUI extends UI {

    private int athleteId;
    private Grid activeGrid;

    @Override
    protected void init(VaadinRequest request) {
        int clientId = 0;
        try {
            clientId = Integer.parseInt(System.getProperty("strava.clientid"));
        } catch (NumberFormatException nfe) {
            Notification.show("Client id is not a valid number", 
                    Notification.Type.ERROR_MESSAGE);
            return;
        }
        String clientSecret = System.getProperty("strava.secret");
        String mapKey = System.getProperty("map.key");

        if (clientId == 0 || clientSecret == null) {
            Notification.show("Please provide a valid client id and secret as " +
                    "system parameters (-Dstrava.clientid and -Dstrava.secret", 
                     Notification.Type.ERROR_MESSAGE);
            return;
        }

        VerticalLayout base = new VerticalLayout();
        base.setSizeFull();
        final String code = request.getParameter("code");
        if (code == null || code.trim().length() == 0) {
            Notification.show("Please provide a valid OAuth code as a request " +
                    "parameter named code", Notification.Type.ERROR_MESSAGE);
            return;
        }

        final AuthorisationService authorisationService = new AuthorisationServiceImpl();

        Token token = authorisationService.tokenExchange(clientId, clientSecret, code);
        final Strava strava = new Strava(token);
        StravaAthlete me = strava.getAuthenticatedAthlete();
        athleteId = me.getId();

        CssLayout header = new CssLayout();
        header.addStyleName("v-component-group");

        Button showOnMapButton = new Button("Show on Map", FontAwesome.MAP_MARKER);
        showOnMapButton.addStyleName("first");
        header.addComponent(showOnMapButton);

        final Button exportToGeoJSONButton = new Button("Export to GeoJSON", 
                FontAwesome.ARROW_DOWN);
        exportToGeoJSONButton.addStyleName("last");
        header.addComponent(exportToGeoJSONButton);

        HorizontalSplitPanel mainSplit = new HorizontalSplitPanel();
        final StravaQueryFactory stravaQueryFactory = new StravaQueryFactory(strava, athleteId);
        final LazyQueryContainer activityContainer = new LazyQueryContainer(
                new LazyQueryDefinition(true, 20, null), stravaQueryFactory);

        final PagedGrid myActivityGrid = new PagedGrid(activityContainer);
        myActivityGrid.setCaption("My Activites");
        myActivityGrid.setIcon(FontAwesome.USER);
        myActivityGrid.setSizeFull();
        myActivityGrid.setSelectionMode(SelectionMode.MULTI);

        final GeneratedPropertyContainer wrapperCont = new GeneratedPropertyContainer(activityContainer);
        myActivityGrid.setContainerDataSource(wrapperCont);

        wrapperCont.addGeneratedProperty("details", new PropertyValueGenerator<Resource>() {
            @Override
            public Resource getValue(Item item, Object itemId, Object propertyId) {
                return new ThemeResource("img/add.png");
            }
            @Override
            public Class<Resource> getType() {
                return Resource.class;
            }
        });

        activityContainer.addContainerProperty("type", String.class, 0, true, false);
        activityContainer.addContainerProperty("distance", Float.class, 0, true, false);
        activityContainer.addContainerProperty("averageHeartrate", Float.class, 0, true, false);

        myActivityGrid.getColumn("details").setRenderer(new ImageRenderer(new RendererClickListener() {
            @Override
            public void click(RendererClickEvent event) {
                for (Object item : myActivityGrid.getContainerDataSource().getItemIds()) {
                    myActivityGrid.setDetailsVisible(item, false);
                }
                Object itemId = event.getItemId();
                myActivityGrid.setDetailsVisible(itemId, !myActivityGrid.isDetailsVisible(itemId));
            }
        }));

        myActivityGrid.addItemClickListener(new ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                for (Object item : myActivityGrid.getContainerDataSource().getItemIds()) {
                    myActivityGrid.setDetailsVisible(item, false);
                }
                Object itemId = event.getItemId();
                myActivityGrid.setDetailsVisible(itemId, !myActivityGrid.isDetailsVisible(itemId));

            }
        });

        myActivityGrid.setDetailsGenerator(new DetailsGenerator() {
            @Override
            public Component getDetails(RowReference rowReference) {
                BeanItem<StravaActivityDTO> beanItem = (BeanItem<StravaActivityDTO>)wrapperCont.getWrappedContainer().getItem(rowReference.getItemId());
                List<StravaStream> streams = strava.getActivityStreams(beanItem.getBean().getId());
                StravaStream heartRate = streams.get(4);
                SparklineChart heartRateChart = new SparklineChart("Heartrate", SolidColor.RED, heartRate.getData(), 0);
                VerticalLayout layout = new VerticalLayout(heartRateChart);
                layout.setSpacing(true);
                layout.setMargin(true);
                return layout;
            }
        });

        myActivityGrid.getColumn("details").setExpandRatio(1);
        myActivityGrid.getColumn("type").setExpandRatio(2);
        myActivityGrid.getColumn("distance").setExpandRatio(2);
        myActivityGrid.getColumn("averageHeartrate").setExpandRatio(2);

        activeGrid = myActivityGrid;

        final StravaFriendActivityQueryFactory stravaFriendActivityQueryFactory = new StravaFriendActivityQueryFactory(strava);
        final LazyQueryContainer friendActivityContainer = new LazyQueryContainer(
                new LazyQueryDefinition(true, 20, null), stravaFriendActivityQueryFactory);
        friendActivityContainer.addContainerProperty("name", String.class, 0, true, false);
        friendActivityContainer.addContainerProperty("type", String.class, 0, true, false);
        friendActivityContainer.addContainerProperty("distance", Float.class, 0, true, false);
        friendActivityContainer.addContainerProperty("averageHeartrate", Float.class, 0, true, false);

        final PagedGrid friendActivityGrid = new PagedGrid(friendActivityContainer);
        friendActivityGrid.setCaption("Friends Activites");
        friendActivityGrid.setIcon(FontAwesome.USERS);
        friendActivityGrid.setSizeFull();
        friendActivityGrid.setSelectionMode(SelectionMode.MULTI);

        TabSheet baseTabSheet = new TabSheet();
        baseTabSheet.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
        baseTabSheet.addStyleName(ValoTheme.TABSHEET_ICONS_ON_TOP);
        baseTabSheet.setSizeFull();

        baseTabSheet.addComponent(myActivityGrid);
        baseTabSheet.addComponent(friendActivityGrid);
        baseTabSheet.addSelectedTabChangeListener(new SelectedTabChangeListener(){

            @Override
            public void selectedTabChange(SelectedTabChangeEvent event) {
                if (event.getTabSheet().getSelectedTab().equals(myActivityGrid)) {
                    activeGrid = myActivityGrid;
                } else {
                    activeGrid = friendActivityGrid;
                }

            }});

        mainSplit.setFirstComponent(baseTabSheet);

        final LMap leafletMap = new LMap();
        leafletMap.setSizeFull();
        leafletMap.setCenter(-31.9522, 115.8589);
        leafletMap.setZoomLevel(12);

        final LTileLayer mapBoxTiles = new LTileLayer(mapKey);
        mapBoxTiles.setDetectRetina(true);

        leafletMap.addLayer(mapBoxTiles);

        mainSplit.setSecondComponent(leafletMap);

        showOnMapButton.addClickListener(new ClickListener(){
            @Override
            public void buttonClick(ClickEvent event) {
                leafletMap.removeAllComponents();
                leafletMap.addLayer(mapBoxTiles);
                Collection<Object> selectedRows = getActiveGrid().getSelectionModel().getSelectedRows();
                for (Iterator<Object> iterator = selectedRows.iterator(); iterator.hasNext();) {
                    BeanItem<StravaActivityDTO> beanItem;
                    if (activeGrid.equals(myActivityGrid)) {
                        beanItem = (BeanItem<StravaActivityDTO>)wrapperCont.getWrappedContainer().getItem(iterator.next());
                    } else {
                        beanItem = (BeanItem<StravaActivityDTO>)getActiveGrid().getContainerDataSource().getItem(iterator.next());
                    }
                    String polyline;

                    polyline = beanItem.getBean().getSummaryPolyline();

                    LPolyline leafletPolyline = new LPolyline();
                    if (polyline != null) {
                        List<Point> points = PolyLineDecoder.decodePoly(polyline);
                        leafletPolyline.setPoints(points);
                    }
                    leafletPolyline.setColor("#000099");
                    leafletPolyline.setClickable(false);
                    leafletPolyline.setWeight(3);
                    leafletMap.addComponent(leafletPolyline);
                }
            }
        });

        StreamResource myResource = createGeoJSONDownloadResource();
        FileDownloader fileDownloader = new FileDownloader(myResource);
        fileDownloader.extend(exportToGeoJSONButton);

        base.addComponent(header);
        base.addComponent(mainSplit);
        base.setExpandRatio(mainSplit, 1);

        setContent(base);
    }


    @VaadinServletConfiguration(productionMode = false, ui = StravaVaadinUI.class, widgetset = "github.changamire.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    private StreamResource createGeoJSONDownloadResource() {
        return new StreamResource(new StreamSource() {
            @Override
            public InputStream getStream() {
                Collection<Object> selectedRows = getActiveGrid().getSelectionModel().getSelectedRows();
                List<Feature> features = new ArrayList<>();
                for (Iterator<Object> iterator = selectedRows.iterator(); iterator.hasNext();) {
                    LazyQueryContainer container = (LazyQueryContainer)((GeneratedPropertyContainer)getActiveGrid().getContainerDataSource()).getWrappedContainer();
                    BeanItem<StravaActivityDTO> beanItem = (BeanItem<StravaActivityDTO>)container.getItem(iterator.next());
                    if (beanItem.getBean().getSummaryPolyline() != null) {
                        List<Point> points = PolyLineDecoder.decodePoly(beanItem.getBean().getSummaryPolyline());
                        List<org.geojson.geometry.Point> coords = new ArrayList<org.geojson.geometry.Point>();
                        for (Point p : points) {
                            coords.add(new org.geojson.geometry.Point(p.getLon(), p.getLat()));
                        }
                        LineString lineString = new LineString(coords);
                        Feature feature = new Feature(lineString);
                        features.add(feature);
                    }
                }
                FeatureCollection featureCollection = new FeatureCollection(features);
                ObjectMapper serializer = new ObjectMapper();
                try {
                    return new ByteArrayInputStream(serializer.writeValueAsBytes(featureCollection));
                } catch (Exception e) {
                    return null;
                }
            }
        }, "strava_geojson.json");
    }

    private Grid getActiveGrid() {
        return activeGrid;
    }
}
