package app.controllers;

import app.model.DialogData;
import app.model.anime.Anime;
import app.model.anime.enums.Genre;
import app.model.anime.GenreEntity;
import app.model.request.SearchRequest;
import app.service.injector.ViewInjector;
import app.util.DataTransfer;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import java.util.*;

public class FavoritesController {

    public Pagination pagination;
    private final Map<Integer, Anime> likedAnime;

    public Tab genreTab;
    public PieChart genresPie;
    public ChoiceBox<String> yearChoice;
    public Label percents;
    public Slider slider;
    public Label indicator;
    private DoubleBinding genresTotal;

    private final Subject<SearchRequest> reqPublisher;
    private final DataTransfer dataService;
    private final ViewInjector viewInjector;


    public FavoritesController(Map<Integer, Anime> likedAnime, Subject<SearchRequest> reqPublisher, DataTransfer dataService, ViewInjector viewInjector) {
        this.likedAnime = likedAnime;
        this.reqPublisher = reqPublisher;
        this.dataService = dataService;
        this.viewInjector = viewInjector;
    }

    public void initialize() {
        this.initLikedList();

        this.slider.valueProperty()
                .addListener(addSliderListener);

        this.genreTab.selectedProperty().addListener((obs, oldVal, nVal) -> {
            if (nVal) {
                this.fillChoiceBox();
                if (this.yearChoice.getItems().size() > 0)
                    this.yearChoice.setValue(this.yearChoice.getItems().get(0));
            }
        });
    }

    private final ChangeListener<Number> addSliderListener = (obs, oldVal, nVal) -> {
            double position = nVal.doubleValue();
            String year = this.yearChoice.getValue();

            int index = "unknown".equals(year) ? -1 : year != null ? Integer.parseInt(year) : 0;

            if (position == 1d && index != 0) {
                this.indicator.setText("themes by a year");
                this.initPieChart(index);
            } else if (position == 0d && index != 0) {
                this.indicator.setText("genres by a year");
                this.initPieChart(index);
            }
    };


    private final int itemsPerPage = 13;

    private void initLikedList() {
        int evenOdd = this.likedAnime.size() % itemsPerPage;
        int count = this.likedAnime.size() / itemsPerPage;
        this.pagination.setPageCount(evenOdd == 0 ? count : count + 1);

        this.pagination.setPageFactory(this::getPageFactory);
    }

    private Node getPageFactory(Integer index){
        final List<Anime> pages = new ArrayList<>(this.likedAnime.size());
        pages.addAll(this.likedAnime.values());

        int from = index*itemsPerPage;
        int to = Math.min(from + itemsPerPage, pages.size());
        var onePage = pages.subList(from, to);
        var items = FXCollections.observableArrayList(onePage);

        ListView<Anime> list = new ListView<>();
        list.setItems(items);
        list.setCellFactory(elements -> new LikedCell());
        return list;
    }


    private void fillChoiceBox(){
        this.yearChoice.getItems().clear();

        this.likedAnime.values()
                .stream()
                .map(Anime::year)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .forEach(year ->
                    this.yearChoice.getItems().add(year > 0 ? String.valueOf(year) : "unknown"));

        this.yearChoice.valueProperty().addListener((obs, oldVal, nVal) -> {
            int choice;
            if (nVal != null)
                choice = nVal.equals("unknown") ? -1 : Integer.parseInt(nVal);
            else choice = oldVal.equals("unknown") ? -1 : Integer.parseInt(oldVal);
            this.initPieChart(choice);
        });
    }

    private void initPieChart(int choice) {
        this.percents.setText("");
        int lent = Genre.values().length;
        final Map<String, Integer> genreStats = new HashMap<>(lent);

        this.likedAnime.values()
                .stream()
                .filter(liked -> liked.year() == choice)
                .forEach(liked -> this.fillStatsMap(genreStats,
                        this.slider.getValue() == 0d ? liked.genres() : liked.themes()));

        this.genresPie.setData(this.createPieData(genreStats));

        this.genresTotal = Bindings.createDoubleBinding(() -> genresPie.getData().stream().mapToDouble(PieChart.Data::getPieValue).sum(),
                genresPie.getData());

        this.genresPie.getData()
                .forEach(this::addClickHandlers);
    }

    private void addClickHandlers (PieChart.Data data){
        var dataNode = data.getNode();

        dataNode.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {

            if ((!event.isControlDown()) && event.getButton() == MouseButton.PRIMARY) {
                String percentVal = data.getName() + " - " +
                        String.format("%.1f", 100 * data.getPieValue() / genresTotal.get()) + "%";
                this.percents.setText(percentVal);

                var bounds = dataNode.getBoundsInLocal();

                double newX = bounds.getWidth() / 2 + bounds.getMinX();
                double newY = bounds.getHeight() / 2 + bounds.getMinY();

                dataNode.setTranslateX(0);
                dataNode.setTranslateY(0);

                var transition = new TranslateTransition(Duration.seconds(1d), dataNode);
                transition.setByX(newX);
                transition.setByY(newY);
                transition.setAutoReverse(true);
                transition.setCycleCount(2);
                transition.play();
            }

            else if (event.isControlDown() && event.getButton() == MouseButton.PRIMARY) {
                var genreName = data.getName();
                int year = this.yearChoice.getValue().equals("unknown") ? -1 :
                        Integer.parseInt(this.yearChoice.getValue());

                var filtered = this.likedAnime.values()
                        .stream()
                        .filter(anime -> anime.year().equals(year))
                        .filter(anime -> {
                            if (this.slider.getValue() == 0)
                                return this.findMatch(anime.genres(), genreName);
                            else
                                return this.findMatch(anime.themes(), genreName);
                        })
                        .toList();

                this.dataService.setDialogData(new DialogData(genreName, year, filtered));

                var dialogPane = (DialogPane) this.viewInjector.load("/views/anime-by-year-and-genre.fxml");
                var dialog = new Dialog<>();
                dialog.setTitle("Statistics");
                dialog.setDialogPane(dialogPane);
                var dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
                dialogStage.getIcons().add(new Image("/assets/icon.png"));
                dialog.show();
            }
        });
        var toolTip = new Tooltip("Ctrl+click to see which anime included");
        Tooltip.install(dataNode, toolTip);
    }

    private boolean findMatch(Collection<GenreEntity> genres, String namePattern){
        return genres.stream().map(GenreEntity::name)
                .anyMatch(name -> name.equals(namePattern));
    }

    private void fillStatsMap(final Map<String, Integer> stats, Collection<GenreEntity> genres){
        genres.forEach(genre -> {
            stats.computeIfAbsent(genre.name(), name -> 1);
            stats.computeIfPresent(genre.name(),(name, count) -> count + 1);
        });
    }

    private ObservableList<PieChart.Data> createPieData(final Map<String, Integer> stats){
        var chartData =  FXCollections.<PieChart.Data>observableArrayList();

        stats.forEach((name, count) -> {
            PieChart.Data data = new PieChart.Data(name, count);
            chartData.add(data);
        });

        this.genresPie.setLegendVisible(chartData.size() <= 13);
        return chartData;
    }


    private class LikedCell extends ListCell<Anime> {
        private final ContextMenu menu;

        private Stage imageStage;

        public LikedCell(){
            this.menu = new ContextMenu();
            this.menu.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
                if (event.getButton() == MouseButton.SECONDARY)
                    event.consume();
            });
        }

        @Override
        protected void updateItem(Anime item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                if (this.menu.getItems().isEmpty()){
                    addMenu(item);
                    addContent(item);
                }
            }
        }

        private void addContent(Anime item) {
            setText(null);
            var hBox = new HBox();
            hBox.setSpacing(5d);
            var title = new Text(item.title());
            var filler = new Pane();
            var year = new Label(item.year() != -1 ? String.valueOf(item.year()) : "unknown");
            var type = new Label(item.type().name);
            HBox.setHgrow(filler, Priority.SOMETIMES);
            hBox.getChildren().addAll(title, filler, year, type);
            setGraphic(hBox);

            title.setOnMouseEntered(event -> this.showImage(item));
            title.setOnMouseExited(event -> this.imageStage.close());
        }

        private void showImage(Anime item) {
            var node = viewInjector.load("/views/image-holder.fxml");
            var imgPlace = (ImageView)node.lookup("#image-holder");
            imgPlace.setImage(new Image(item.images().jpg().imageUrl()));
            var scene = new Scene(node);
            scene.getStylesheets().add("/main.css");
            this.imageStage = new Stage();
            this.imageStage.setScene(scene);
            this.imageStage.initOwner(percents.getScene().getWindow());
            this.imageStage.initStyle(StageStyle.UNDECORATED);
            this.imageStage.setX(getListView().getBoundsInLocal().getCenterX() + 250);
            this.imageStage.setY(getListView().getBoundsInLocal().getCenterY());
            this.imageStage.show();
        }

        private void addMenu(Anime item){
            var delete = new MenuItem("delete from faves");
            delete.setOnAction(event -> {
                likedAnime.remove(item.id());
                initLikedList();
            });

            var go = new MenuItem("go to the anime");
            go.setOnAction(event -> {
                dataService.setRequestType(dataService.getReqHistory());
                reqPublisher.onNext(new SearchRequest(item.id()));
            });

            this.menu.getItems().addAll(go, delete);
            setContextMenu(this.menu);
        }
    }
}