package app.service;

import static org.junit.jupiter.api.Assertions.*;

import app.service.anime.AnimeService;
import app.service.anime.DefaultAnimeService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import java.net.http.HttpClient;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DefaultAnimeServiceTest {
    private AnimeService animeService;

    @BeforeAll
    void init(){
        this.animeService = new DefaultAnimeService(HttpClient.newBuilder());
    }


    @Test
    @Order(1)
    void findById() {
        var disp = this.animeService.findById(1)
                        .subscribe(anime -> assertEquals(1, anime.id()));
        while (!disp.isDisposed()){}
    }

    @Test
    @Order(2)
    void findByQuery() throws InterruptedException {
        Thread.sleep(1000);
        var disp = this.animeService.findByQuery("", 1, 10)
                .toList()
                .subscribe(list -> assertEquals(10, list.size()));
        while (!disp.isDisposed()){}
    }

    @Test
    @Order(3)
    void findTop()  {
        var disp = this.animeService.findTop(1, 7)
                .toList()
                .subscribe(list -> assertEquals(7, list.size()));
        while (!disp.isDisposed()){}
    }

    @Test
    @Order(4)
    void findOngoings() throws InterruptedException {
        Thread.sleep(1000);
        var disp = this.animeService.findOngoings( 1, 12)
                .toList()
                .subscribe(list -> assertEquals(12, list.size()));
        while (!disp.isDisposed()){}
    }

    @Test
    @Order(5)
    void findPersonageById() throws InterruptedException {
        Thread.sleep(1000);
        var disp = this.animeService.findPersonageById(1)
                .toList()
                .subscribe(list -> {
                    assertNotEquals(0, list.size());
                    assertNotNull(list.get(0));
                });
        while (!disp.isDisposed()){}
    }
}