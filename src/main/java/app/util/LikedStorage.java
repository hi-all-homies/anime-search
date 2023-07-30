package app.util;

import app.config.ContainerHolder;
import app.model.anime.Anime;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class LikedStorage {

    private static final String DIR = "anime-search";
    private static final String FILE_NAME = "liked.ser";
    private static final String USER_HOME = "user.home";

    public void storeLiked() {
        var likedAnime = ContainerHolder.INSTANCE.getContainer().getLikedAnime();
        File likedFile = this.getLikedFile();

        try (
                FileOutputStream outStream = new FileOutputStream(likedFile);
                ObjectOutputStream writer = new ObjectOutputStream(outStream)) {

            writer.writeObject(likedAnime);

            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }

    }

    @SuppressWarnings("unchecked")
    public void loadLiked(){
        File likedFile = this.getLikedFile();
        var likedAnime = ContainerHolder.INSTANCE.getContainer().getLikedAnime();

        try (
                FileInputStream input = new FileInputStream(likedFile);
                ObjectInputStream reader = new ObjectInputStream(input)){

            var result = reader.readObject();
            if (result != null){
                likedAnime.putAll((Map<Integer, Anime>) result);
            }
        }
        catch (IOException | ClassNotFoundException e){
            if (!(e instanceof EOFException)){
                var userHome = System.getProperty(USER_HOME);
                var filePath = Path.of(userHome, DIR, FILE_NAME);
                new File(filePath.toUri()).delete();

                try {
                    Files.createFile(filePath);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    private File getLikedFile() {
        try {
            var userHome = System.getProperty(USER_HOME);

            var filePath = Path.of(userHome, DIR, FILE_NAME);
            var file = new File(filePath.toUri());

            var dirPath = Path.of(userHome, DIR);
            var dir = new File(dirPath.toUri());

            if (!dir.isDirectory()) {
                var path = Files.createDirectory(dirPath);
                filePath = Files.createFile(Path.of(path.toString(), FILE_NAME));
                file = new File(filePath.toUri());
            }
            if (!file.isFile()) {
                var path = Files.createFile(filePath);
                file = new File(path.toUri());
            }
            return file;

        } catch (IOException e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
