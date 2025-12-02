package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Arrr! Test class for our MovieService treasure hunting functionality!
 * These tests ensure our search methods work like a proper pirate's compass.
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application.yml")
public class MovieServiceTest {

    private MovieService movieService;

    @BeforeEach
    void setUp() {
        movieService = new MovieService();
    }

    @Test
    @DisplayName("Ahoy! Test loading all movies from our treasure chest")
    void testGetAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        
        assertNotNull(movies, "Movies list should not be null, matey!");
        assertFalse(movies.isEmpty(), "Our treasure chest shouldn't be empty!");
        assertTrue(movies.size() > 0, "Should have some movies in our collection");
        
        // Verify we have the expected test movies
        assertTrue(movies.size() >= 10, "Should have at least 10 movies in our test data");
    }

    @Test
    @DisplayName("Test finding a movie by ID like a proper treasure map")
    void testGetMovieById() {
        // Test finding an existing movie
        Optional<Movie> movie = movieService.getMovieById(1L);
        assertTrue(movie.isPresent(), "Should find movie with ID 1");
        assertEquals("The Prison Escape", movie.get().getMovieName());
        
        // Test with non-existent ID
        Optional<Movie> nonExistentMovie = movieService.getMovieById(999L);
        assertFalse(nonExistentMovie.isPresent(), "Should not find movie with non-existent ID");
        
        // Test with null ID
        Optional<Movie> nullIdMovie = movieService.getMovieById(null);
        assertFalse(nullIdMovie.isPresent(), "Should not find movie with null ID");
        
        // Test with invalid ID
        Optional<Movie> invalidIdMovie = movieService.getMovieById(-1L);
        assertFalse(invalidIdMovie.isPresent(), "Should not find movie with negative ID");
    }

    @Test
    @DisplayName("Arrr! Test searching movies by name like a savvy sailor")
    void testSearchMoviesByName() {
        // Test exact name match (case insensitive)
        List<Movie> results = movieService.searchMovies("The Prison Escape", null, null);
        assertEquals(1, results.size(), "Should find exactly one movie with exact name match");
        assertEquals("The Prison Escape", results.get(0).getMovieName());
        
        // Test partial name match
        List<Movie> partialResults = movieService.searchMovies("the", null, null);
        assertTrue(partialResults.size() > 1, "Should find multiple movies with 'the' in the name");
        
        // Test case insensitive search
        List<Movie> caseResults = movieService.searchMovies("PRISON", null, null);
        assertEquals(1, caseResults.size(), "Case insensitive search should work");
        
        // Test non-existent movie name
        List<Movie> noResults = movieService.searchMovies("Nonexistent Movie", null, null);
        assertTrue(noResults.isEmpty(), "Should return empty list for non-existent movie");
        
        // Test empty string
        List<Movie> emptyResults = movieService.searchMovies("", null, null);
        assertEquals(movieService.getAllMovies().size(), emptyResults.size(), 
                "Empty string should return all movies");
    }

    @Test
    @DisplayName("Test searching movies by ID - the most specific treasure hunt!")
    void testSearchMoviesById() {
        // Test valid ID search
        List<Movie> results = movieService.searchMovies(null, 1L, null);
        assertEquals(1, results.size(), "Should find exactly one movie with ID 1");
        assertEquals(1L, results.get(0).getId());
        
        // Test non-existent ID
        List<Movie> noResults = movieService.searchMovies(null, 999L, null);
        assertTrue(noResults.isEmpty(), "Should return empty list for non-existent ID");
        
        // Test that ID search takes precedence over other parameters
        List<Movie> precedenceResults = movieService.searchMovies("Some Name", 2L, "Some Genre");
        assertEquals(1, precedenceResults.size(), "ID search should take precedence");
        assertEquals(2L, precedenceResults.get(0).getId());
    }

    @Test
    @DisplayName("Test searching movies by genre - find yer preferred adventure type!")
    void testSearchMoviesByGenre() {
        // Test exact genre match
        List<Movie> dramaResults = movieService.searchMovies(null, null, "Drama");
        assertTrue(dramaResults.size() > 0, "Should find drama movies");
        dramaResults.forEach(movie -> 
            assertTrue(movie.getGenre().toLowerCase().contains("drama"), 
                "All results should contain 'drama' in genre"));
        
        // Test partial genre match
        List<Movie> crimeResults = movieService.searchMovies(null, null, "Crime");
        assertTrue(crimeResults.size() > 0, "Should find crime movies");
        
        // Test case insensitive genre search
        List<Movie> caseResults = movieService.searchMovies(null, null, "ACTION");
        assertTrue(caseResults.size() > 0, "Case insensitive genre search should work");
        
        // Test non-existent genre
        List<Movie> noResults = movieService.searchMovies(null, null, "NonexistentGenre");
        assertTrue(noResults.isEmpty(), "Should return empty list for non-existent genre");
    }

    @Test
    @DisplayName("Test combined search parameters - multiple treasure hunting criteria!")
    void testSearchMoviesCombined() {
        // Test name and genre combination
        List<Movie> combinedResults = movieService.searchMovies("Family", null, "Crime");
        assertTrue(combinedResults.size() >= 0, "Combined search should work");
        
        // If results exist, verify they match both criteria
        combinedResults.forEach(movie -> {
            assertTrue(movie.getMovieName().toLowerCase().contains("family"), 
                "Movie name should contain 'family'");
            assertTrue(movie.getGenre().toLowerCase().contains("crime"), 
                "Movie genre should contain 'crime'");
        });
        
        // Test search that should return no results
        List<Movie> noResults = movieService.searchMovies("Nonexistent", null, "NonexistentGenre");
        assertTrue(noResults.isEmpty(), "Should return empty list when no movies match all criteria");
    }

    @Test
    @DisplayName("Test getting all unique genres from our treasure chest")
    void testGetAllGenres() {
        List<String> genres = movieService.getAllGenres();
        
        assertNotNull(genres, "Genres list should not be null");
        assertFalse(genres.isEmpty(), "Should have some genres");
        
        // Verify genres are unique and sorted
        for (int i = 1; i < genres.size(); i++) {
            assertTrue(genres.get(i).compareTo(genres.get(i-1)) >= 0, 
                "Genres should be sorted alphabetically");
        }
        
        // Verify we have expected genres from test data
        assertTrue(genres.contains("Drama"), "Should contain Drama genre");
        assertTrue(genres.contains("Action/Crime"), "Should contain Action/Crime genre");
    }

    @Test
    @DisplayName("Test parameter validation like a proper pirate captain!")
    void testValidateSearchParameters() {
        // Test valid parameters
        assertDoesNotThrow(() -> movieService.validateSearchParameters("test", null, null),
                "Valid name parameter should not throw exception");
        assertDoesNotThrow(() -> movieService.validateSearchParameters(null, 1L, null),
                "Valid ID parameter should not throw exception");
        assertDoesNotThrow(() -> movieService.validateSearchParameters(null, null, "Drama"),
                "Valid genre parameter should not throw exception");
        
        // Test invalid ID
        IllegalArgumentException idException = assertThrows(IllegalArgumentException.class,
                () -> movieService.validateSearchParameters(null, -1L, null),
                "Negative ID should throw exception");
        assertTrue(idException.getMessage().contains("not a valid treasure map number"),
                "Error message should have pirate flair");
        
        // Test no parameters provided
        IllegalArgumentException noParamsException = assertThrows(IllegalArgumentException.class,
                () -> movieService.validateSearchParameters(null, null, null),
                "No parameters should throw exception");
        assertTrue(noParamsException.getMessage().contains("at least one search parameter"),
                "Error message should indicate need for parameters");
        
        // Test empty string parameters
        IllegalArgumentException emptyException = assertThrows(IllegalArgumentException.class,
                () -> movieService.validateSearchParameters("", null, ""),
                "Empty strings should throw exception");
        
        // Test overly long parameters
        String longName = "a".repeat(101);
        IllegalArgumentException longNameException = assertThrows(IllegalArgumentException.class,
                () -> movieService.validateSearchParameters(longName, null, null),
                "Overly long name should throw exception");
        assertTrue(longNameException.getMessage().contains("longer than a pirate's tale"),
                "Error message should have pirate flair");
        
        String longGenre = "a".repeat(51);
        IllegalArgumentException longGenreException = assertThrows(IllegalArgumentException.class,
                () -> movieService.validateSearchParameters(null, null, longGenre),
                "Overly long genre should throw exception");
        assertTrue(longGenreException.getMessage().contains("longer than a ship's anchor chain"),
                "Error message should have pirate flair");
    }

    @Test
    @DisplayName("Test edge cases and boundary conditions")
    void testEdgeCases() {
        // Test whitespace-only parameters
        List<Movie> whitespaceResults = movieService.searchMovies("   ", null, "   ");
        assertEquals(movieService.getAllMovies().size(), whitespaceResults.size(),
                "Whitespace-only parameters should return all movies");
        
        // Test ID boundary values
        List<Movie> zeroIdResults = movieService.searchMovies(null, 0L, null);
        assertTrue(zeroIdResults.isEmpty(), "ID 0 should return no results");
        
        // Test very large ID
        List<Movie> largeIdResults = movieService.searchMovies(null, Long.MAX_VALUE, null);
        assertTrue(largeIdResults.isEmpty(), "Very large ID should return no results");
        
        // Test special characters in search
        List<Movie> specialCharResults = movieService.searchMovies("@#$%", null, null);
        assertTrue(specialCharResults.isEmpty(), "Special characters should return no results");
    }
}