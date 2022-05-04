package academy.devdojo.springboot2.client;

import academy.devdojo.springboot2.domain.Anime;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Log4j2
public class SpringClient {
    public static void main(String[] args) {

        ResponseEntity<Anime> entity = new RestTemplate().getForEntity(
                "http://localhost:8080/animes/{id}", Anime.class, 8);
        log.info(entity);

        Anime object = new RestTemplate().getForObject(
                "http://localhost:8080/animes/{id}", Anime.class, 30);
        log.info(object);

        Anime[] animeArray = new RestTemplate().getForObject(
                "http://localhost:8080/animes/all", Anime[].class);
        log.info(Arrays.toString(animeArray));

        ResponseEntity<List<Anime>> animeList = new RestTemplate().exchange(
                "http://localhost:8080/animes/all",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        log.info(animeList.getBody());

        Anime kingdom = Anime.builder().name("kingdom").build();
        Anime kingdomSaved = new RestTemplate().postForObject(
                "http://localhost:8080/animes",
                kingdom, Anime.class);
        log.info("saved anime {}", kingdomSaved);

        Anime samuraiShamploo = Anime.builder().name("samurai shamploo").build();
        ResponseEntity<Anime> samuraiShamplooSaved = new RestTemplate().exchange("http://localhost:8080/animes",
                HttpMethod.POST, new HttpEntity<>(samuraiShamploo, createJsonHeader()), Anime.class);
        log.info("saved anime {}", samuraiShamplooSaved);

        Anime animeToBeUpdate = samuraiShamplooSaved.getBody();
        animeToBeUpdate.setName("Shougueki no soma");
        ResponseEntity<Void> animeUpdated = new RestTemplate().exchange(
                "http://localhost:8080/animes",
                HttpMethod.PUT,
                new HttpEntity<>(animeToBeUpdate, createJsonHeader()),
                Void.class
        );
        log.info(animeUpdated);

        ResponseEntity<Void> samuraiShamplooDeleted = new RestTemplate().exchange(
                "http://localhost:8080/animes/{id}", HttpMethod.DELETE,
                null,
                Void.class,
                animeToBeUpdate.getId()
        );
        log.info(samuraiShamplooDeleted);
    }

    private static HttpHeaders createJsonHeader(){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }
}
