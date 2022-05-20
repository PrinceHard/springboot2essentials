package academy.devdojo.springboot2.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import academy.devdojo.springboot2.Exception.BadRequestException;
import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.repository.AnimeRepository;
import academy.devdojo.springboot2.util.AnimeCreator;
import academy.devdojo.springboot2.util.AnimePutRequestBodyCreator;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for Anime Service")
public class AnimeServiceTest {

    @InjectMocks // Utiliza quando a classe a ser testada é testada por completo
    AnimeService animeService;

    @Mock // Utilizado para as classes dentro do controller
    AnimeRepository animeRepository;

    @BeforeEach
    void setUp() {
        PageImpl<Anime> animePage = new PageImpl<>(List.of(AnimeCreator.createValidAnime()));
        BDDMockito.when(animeRepository.findAll(ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(animePage);

        BDDMockito.when(animeRepository.findAll())
                .thenReturn(List.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeRepository.findByName(ArgumentMatchers.anyString()))
                .thenReturn(List.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeRepository.save(ArgumentMatchers.any(Anime.class)))
                .thenReturn(AnimeCreator.createValidAnime());

        BDDMockito.doNothing().when(animeRepository).delete(ArgumentMatchers.any(Anime.class));

    }

    @Test
    @DisplayName("ListAll returns list of anime inside page object when successful")
    void listAll_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {
        String expectedName = AnimeCreator.createValidAnime().getName();
        Page<Anime> animePage = animeService.listAll(PageRequest.of(1, 1));

        Assertions.assertThat(animePage).isNotNull();

        Assertions.assertThat(animePage.toList())
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);

    }

    @Test
    @DisplayName("ListAll returns list of animes when successful")
    void listAll_returnsListOfAnimes_WhenSuccessful() {
        String expectedName = AnimeCreator.createValidAnime().getName();
        List<Anime> animeList = animeService.listAll();

        Assertions.assertThat(animeList)
                .isNotEmpty()
                .isNotNull();
        Assertions.assertThat(animeList).contains(AnimeCreator.createValidAnime());
        Assertions.assertThat(animeList.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("FindByIdOrThrowBadRequestException returns anime when successful")
    void findByIdOrThrowBadRequestException_returnsAnime_WhenSuccessful() {
        Long expectedId = AnimeCreator.createValidAnime().getId();
        Anime anime = animeService.findByIdOrThrowBadRequestException(1);

        Assertions.assertThat(anime).isNotNull();
        Assertions.assertThat(anime).isEqualTo(AnimeCreator.createValidAnime());
        Assertions.assertThat(anime.getId())
                .isNotNull()
                .isEqualTo(expectedId);
    }

    @Test
    @DisplayName("FindByIdOrThrowBadRequestException throws BadRequestException when anime is not found")
    void findByIdOrThrowBadRequestException_ThrowsBadRequestException_WhenAnimeIsNotFound() {
        BDDMockito.when(animeRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Assertions.assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> this.animeService.findByIdOrThrowBadRequestException(1));
    }

    @Test
    @DisplayName("FindByName returns list of animes when successful")
    void findByName_returnsListOfAnimes_WhenSuccessful() {
        String expectedName = AnimeCreator.createValidAnime().getName();
        List<Anime> animeList = animeService.findByName("");

        Assertions.assertThat(animeList)
                .isNotEmpty()
                .isNotNull();
        Assertions.assertThat(animeList).contains(AnimeCreator.createValidAnime());
        Assertions.assertThat(animeList.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("FindByName returns list of animes when successful")
    void findByName_returnsEmptyListOfAnimes_WhenAnimeNameIsNotFound() {
        BDDMockito.when(animeRepository.findByName(ArgumentMatchers.anyString()))
                .thenReturn(Collections.emptyList());

        List<Anime> animeList = animeService.findByName("");

        Assertions.assertThat(animeList)
                .isNotNull()
                .isEmpty();

    }

    @Test
    @DisplayName("Save returns anime when successful")
    void save_returnsAnime_WhenSuccessful() {
        Anime anime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        Assertions.assertThat(anime)
                .isNotNull()
                .isEqualTo(AnimeCreator.createValidAnime());
    }

    @Test
    @DisplayName("Replace updates anime when successful")
    void replace_updatesAnime_WhenSuccessful() {

        Assertions.assertThatCode(() -> animeService.replace(
                AnimePutRequestBodyCreator.createAnimePutRequestBody())).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Delete removes anime when successful")
    void delete_removesAnime_WhenSuccessful() {

        Assertions.assertThatCode(() -> animeService.delete(1))
                .doesNotThrowAnyException();
    }
}
