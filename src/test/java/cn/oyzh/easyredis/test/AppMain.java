package cn.oyzh.easyredis.test;

// import com.gluonhq.attach.util.Platform;
// import com.gluonhq.attach.util.Services;
// import com.gluonhq.maps.MapLayer;
// import com.gluonhq.maps.MapPoint;
// import com.gluonhq.maps.MapView;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;


/**
 * @author oyzh
 * @since 2022/5/18
 */
public class AppMain extends Application {

    private EventType type = new EventType(Event.ANY, "test11");

    public static void main(String[] args) {
        launch(AppMain.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // test1(stage);
        // test2(stage);
        // test3(stage);
    }

    private void test1(Stage stage) {
        String content1 = "m 284.96875,120.11459 v -1.71094 l 7.92187,-9.09375 q -1.34765,0.0703 -2.3789,0.0703 h -5.07422 v -1.71094 h 10.17187 v 1.39453 l -6.73828,7.89844 -1.30078,1.44141 q 1.41797,-0.10547 2.66016,-0.10547 h 5.7539 v 1.81641 z";
        String content2 = "m 297.23828,116.39974 2.08594,-0.32812 q 0.17578,1.2539 0.97265,1.92187 0.8086,0.66797 2.25,0.66797 1.45313,0 2.15625,-0.58594 0.70313,-0.59765 0.70313,-1.39453 0,-0.71484 -0.62109,-1.125 -0.4336,-0.28125 -2.15625,-0.71484 -2.32032,-0.58594 -3.22266,-1.00781 -0.89063,-0.4336 -1.35938,-1.1836 -0.45703,-0.76172 -0.45703,-1.67578 0,-0.83203 0.375,-1.53516 0.38672,-0.71484 1.04297,-1.18359 0.49219,-0.36328 1.33594,-0.60937 0.85547,-0.25782 1.82812,-0.25782 1.46485,0 2.56641,0.42188 1.11328,0.42187 1.64063,1.14844 0.52734,0.71484 0.72656,1.92187 l -2.0625,0.28125 q -0.14063,-0.96094 -0.82031,-1.5 -0.66797,-0.53906 -1.89844,-0.53906 -1.45313,0 -2.07422,0.48047 -0.62109,0.48047 -0.62109,1.125 0,0.41015 0.25781,0.73828 0.25781,0.33984 0.80859,0.5625 0.31641,0.11719 1.86328,0.53906 2.23828,0.59766 3.11719,0.98438 0.89063,0.375 1.39453,1.10156 0.50391,0.72656 0.50391,1.80469 0,1.05468 -0.6211,1.99218 -0.60937,0.92578 -1.76953,1.44141 -1.16015,0.50391 -2.625,0.50391 -2.42578,0 -3.70312,-1.00782 -1.26563,-1.00781 -1.61719,-2.98828 z";
        SVGPath svgPath1 = new SVGPath();
        svgPath1.setContent(content1 + " " + content2);

        Region region = new Region();
        region.setShape(svgPath1);
        region.setMaxWidth(100);
        region.setPrefWidth(150);
        region.setStyle("-fx-background-color: black;");

        HBox group = new HBox(region);
        stage.setScene(new Scene(group, 150, 100));
        stage.show();
    }

    // private void test2(Stage stage) {
    //     MapView mapView = new MapView();
    //     mapView.setCenter(30, 30);
    //     mapView.addLayer(new MapLayer());
    //     stage.setScene(new Scene(mapView, 150, 100));
    //     stage.show();
    // }

    // private void test3(Stage stage) {
    //     MapView view = new MapView();
    //     view.addLayer(positionLayer());
    //     view.setZoom(DEFAULT_ZOOM);
    //     Scene scene;
    //     if (Platform.isDesktop()) {
    //         scene = new Scene(view, 600, 700);
    //         stage.setTitle("Gluon Maps Demo");
    //     } else {
    //         BorderPane bp = new BorderPane();
    //         bp.setCenter(view);
    //         final Label label = new Label("Gluon Maps Demo");
    //         label.setAlignment(Pos.CENTER);
    //         label.setMaxWidth(Double.MAX_VALUE);
    //         label.setStyle("-fx-background-color: dimgrey; -fx-text-fill: white;");
    //         bp.setTop(label);
    //         Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
    //         scene = new Scene(bp, bounds.getWidth(), bounds.getHeight());
    //
    //         view.flyTo(1., mapPoint, 2.);
    //     }
    //
    //     stage.setScene(scene);
    //     stage.show();
    // }
    //
    //
    // private static final int DEFAULT_ZOOM = 3;
    // private static final double DEFAULT_LAT = 50.0;
    // private static final double DEFAULT_LONG = 4.0;
    //
    // private MapPoint mapPoint;
    //
    // private MapLayer positionLayer() {
    //     return Services.get(PositionService.class)
    //             .map(positionService -> {
    //                 positionService.start();
    //
    //                 ReadOnlyObjectProperty<Position> positionProperty = positionService.positionProperty();
    //                 Position position = positionProperty.get();
    //                 if (position == null) {
    //                     position = new Position() {
    //                         @Override
    //                         public int getOffset() {
    //                             return 0;
    //                         }
    //                     };
    //                 }
    //                 mapPoint = new MapPoint(30, 30);
    //
    //                 PoiLayer answer = new PoiLayer();
    //                 answer.addPoint(mapPoint, new Circle(7, Color.RED));
    //
    //                 positionProperty.addListener(e -> {
    //                     Position pos = positionProperty.get();
    //                     mapPoint.update(30, 30);
    //                 });
    //                 return answer;
    //             })
    //             .orElseGet(() -> {
    //                 PoiLayer answer = new PoiLayer();
    //                 mapPoint = new MapPoint(DEFAULT_LAT, DEFAULT_LONG);
    //                 answer.addPoint(mapPoint, new Circle(7, Color.RED));
    //                 return answer;
    //             });
    // }

}
