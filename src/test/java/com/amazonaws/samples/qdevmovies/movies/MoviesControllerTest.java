package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Arrr! Test class for our MoviesController - testing both HTML and REST endpoints!
 * These tests ensure our controller handles requests like a proper pirate captain.
 */
public class MoviesControllerTest {

    private MoviesController moviesController;
    private Model model;
    private MovieService mockMovieService;
    private ReviewService mockReviewService;

    @BeforeEach
    public void setUp() {
        moviesController = new MoviesController();
        model = new ExtendedModelMap();
        
        // Create mock services with enhanced functionality
        mockMovieService = new MovieService() {
            @Override
            public List<Movie> getAllMovies() {
                return Arrays.asList(
                    new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5),
                    new Movie(2L, "Action Movie", "Action Director", 2022, "Action", "Action description", 110, 4.0),
                    new Movie(3L, "Comedy Film", "Comedy Director", 2021, "Comedy", "Comedy description", 95, 3.5)
                );
            }
            
            @Override
            public Optional<Movie> getMovieById(Long id) {
                if (id == 1L) {
                    return Optional.of(new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5));
                } else if (id == 2L) {
                    return Optional.of(new Movie(2L, "Action Movie", "Action Director", 2022, "Action", "Action description", 110, 4.0));
                }
                return Optional.empty();
            }
            
            @Override
            public List<Movie> searchMovies(String name, Long id, String genre) {
                List<Movie> allMovies = getAllMovies();
                List<Movie> results = new ArrayList<>();
                
                // Simple mock search logic
                if (id != null && id > 0) {
                    Optional<Movie> movie = getMovieById(id);
                    if (movie.isPresent()) {
                        results.add(movie.get());
                    }
                    return results;
                }
                
                for (Movie movie : allMovies) {
                    boolean matches = true;
                    
                    if (name != null && !name.trim().isEmpty()) {
                        matches = matches && movie.getMovieName().toLowerCase().contains(name.toLowerCase());
                    }
                    
                    if (genre != null && !genre.trim().isEmpty()) {
                        matches = matches && movie.getGenre().toLowerCase().contains(genre.toLowerCase());
                    }
                    
                    if (matches) {
                        results.add(movie);
                    }
                }
                
                return results;
            }
            
            @Override
            public List<String> getAllGenres() {
                return Arrays.asList("Action", "Comedy", "Drama");
            }
            
            @Override
            public void validateSearchParameters(String name, Long id, String genre) {
                if (id != null && id <= 0) {
                    throw new IllegalArgumentException("Arrr! That ID be not a valid treasure map number, matey!");
                }
                
                boolean hasName = name != null && !name.trim().isEmpty();
                boolean hasId = id != null && id > 0;
                boolean hasGenre = genre != null && !genre.trim().isEmpty();
                
                if (!hasName && !hasId && !hasGenre) {
                    throw new IllegalArgumentException("Shiver me timbers! Ye need to provide at least one search parameter to find yer treasure!");
                }
            }
        };
        
        mockReviewService = new ReviewService() {
            @Override
            public List<Review> getReviewsForMovie(long movieId) {
                return new ArrayList<>();
            }
        };
        
        // Inject mocks using reflection
        try {
            java.lang.reflect.Field movieServiceField = MoviesController.class.getDeclaredField("movieService");
            movieServiceField.setAccessible(true);
            movieServiceField.set(moviesController, mockMovieService);
            
            java.lang.reflect.Field reviewServiceField = MoviesController.class.getDeclaredField("reviewService");
            reviewServiceField.setAccessible(true);
            reviewServiceField.set(moviesController, mockReviewService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock services", e);
        }
    }

    // Original tests updated for new method signature
    @Test
    @DisplayName("Test getting all movies without search parameters")
    public void testGetMovies() {
        String result = moviesController.getMovies(null, null, null, model);
        assertNotNull(result);
        assertEquals("movies", result);
        
        // Verify model attributes
        assertFalse((Boolean) model.getAttribute("searchPerformed"));
        assertNotNull(model.getAttribute("movies"));
        assertNotNull(model.getAttribute("allGenres"));
    }

    @Test
    @DisplayName("Test getting movie details")
    public void testGetMovieDetails() {
        String result = moviesController.getMovieDetails(1L, model);
        assertNotNull(result);
        assertEquals("movie-details", result);
    }

    @Test
    @DisplayName("Test getting movie details for non-existent movie")
    public void testGetMovieDetailsNotFound() {
        String result = moviesController.getMovieDetails(999L, model);
        assertNotNull(result);
        assertEquals("error", result);
        
        // Verify pirate-themed error message
        String errorMessage = (String) model.getAttribute("message");
        assertTrue(errorMessage.contains("Shiver me timbers"));
        assertTrue(errorMessage.contains("treasure chest"));
    }

    @Test
    @DisplayName("Test movie service integration")
    public void testMovieServiceIntegration() {
        List<Movie> movies = mockMovieService.getAllMovies();
        assertEquals(3, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
    }

    // New search functionality tests
    @Test
    @DisplayName("Ahoy! Test searching movies by name through HTML endpoint")
    public void testSearchMoviesByName() {
        String result = moviesController.getMovies("Test", null, null, model);
        assertEquals("movies", result);
        
        // Verify search was performed
        assertTrue((Boolean) model.getAttribute("searchPerformed"));
        assertEquals("Test", model.getAttribute("searchName"));
        
        // Verify search results
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
        
        // Verify search message
        String searchMessage = (String) model.getAttribute("searchMessage");
        assertTrue(searchMessage.contains("Ahoy!"));
        assertTrue(searchMessage.contains("1 movie matching"));
    }

    @Test
    @DisplayName("Test REST API search endpoint with valid parameters")
    public void testSearchMoviesApiSuccess() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMoviesApi("Test", null, null);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        assertEquals(1, body.get("count"));
        
        String message = (String) body.get("message");
        assertTrue(message.contains("Ahoy matey!"));
    }
}
