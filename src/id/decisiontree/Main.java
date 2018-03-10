package id.decisiontree;


import static java.lang.System.exit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import org.yaml.snakeyaml.Yaml;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class Main extends Application {

    private static final Object NAME = "_name";
    private static Yaml yaml = new Yaml();
    private static Map<Object, Object> map = new HashMap<>();
    
    void dfs(Map<Object, Object> m, TreeItem<String> item) {
        for (Map.Entry<Object, Object> e: m.entrySet()) {
            if (NAME.equals(e.getKey()))
                continue;
            TreeItem<String> newItem = new TreeItem<>(e.getKey().toString());
            item.getChildren().add(newItem);
            if (e.getValue() instanceof Map)
                dfs((Map<Object, Object>) e.getValue(), newItem);
            if (e.getValue() instanceof List) {
                List<String> ll = (List<String>) e.getValue();
                ll.stream()
                    .map(TreeItem<String>::new)
                    .forEach(newItem.getChildren()::add);
            }
            if (e.getValue() instanceof String)
                newItem.getChildren().add(new TreeItem<String>((String) e.getValue()));
        }
    }
    
    @SuppressWarnings("resource")
    private static void usage() throws IOException {
        Scanner scanner = new Scanner(Main.class.getResource("README.org").openStream())
                .useDelimiter("\n");
        while (scanner.hasNext())
            System.out.println(scanner.next());
    }
    
    private void openFile() throws FileNotFoundException {
        List<String> params = getParameters().getUnnamed();
        String file = null;
        if (params.isEmpty()) {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Open YAML file");
            chooser.setSelectedExtensionFilter(new ExtensionFilter("yaml files", "*.yaml"));
            file = Optional.ofNullable(chooser.showOpenDialog(new Stage())).map(File::getPath).orElse(null);
            if (file == null) exit(0);
        } else {
            file = params.get(0);
        }
        map = yaml.load(new FileReader(file));
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            openFile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        StackPane root = new StackPane();
        TreeItem<String> rootItem = new TreeItem<>((String)map.getOrDefault(NAME, "unknown"));
        dfs(map, rootItem);
        TreeView<String> tree = new TreeView<>(rootItem);
        Scene scene = new Scene(root, 500, 250);
        root.getChildren().add(tree);
        primaryStage.setTitle("Decision tree");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static void main(String[] args) throws IOException {
        launch(args);
    }

}