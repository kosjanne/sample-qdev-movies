package com.amazonaws.samples.qdevmovies.movies;

import com.amazonaws.samples.qdevmovies.utils.MovieIconUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class MoviesController {
    private static final Logger logger = LogManager.getLogger(MoviesController.class);

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/movies")
    public String getMovies(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre,
            org.springframework.ui.Model model) {
        
        logger.info("Ahoy! Fetching movies with search parameters - name: {}, id: {}, genre: {}", name, id, genre);
        
        List<Movie> movies;
        boolean isSearchRequest = (name != null && !name.trim().isEmpty()) || 
                                 (id != null && id > 0) || 
                                 (genre != null && !genre.trim().isEmpty());
        
        if (isSearchRequest) {
            try {
                movieService.validateSearchParameters(name, id, genre);
                movies = movieService.searchMovies(name, id, genre);
                
                // Add search context to model
                model.addAttribute("searchPerformed", true);
                model.addAttribute("searchName", name);
                model.addAttribute("searchId", id);
                model.addAttribute("searchGenre", genre);
                model.addAttribute("searchResultCount", movies.size());
                
                if (movies.isEmpty()) {
                    model.addAttribute("searchMessage", "Arrr! No treasure found with those search terms, matey! Try different keywords to find yer movies.");
                } else {
                    model.addAttribute("searchMessage", String.format("Ahoy! Found %d movie%s matching yer search, ye savvy sailor!", 
                            movies.size(), movies.size() == 1 ? "" : "s"));
                }
                
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid search parameters: {}", e.getMessage());
                movies = movieService.getAllMovies();
                model.addAttribute("searchError", e.getMessage());
                model.addAttribute("searchPerformed", true);
            }
        } else {
            movies = movieService.getAllMovies();
            model.addAttribute("searchPerformed", false);
        }
        
        model.addAttribute("movies", movies);
        model.addAttribute("allGenres", movieService.getAllGenres());
        return "movies";
    }

    /**
     * REST API endpoint for movie search - returns JSON response
     * Arrr! This be the treasure map for other ships (applications) to find our movies!
     */
    @GetMapping("/movies/search")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> searchMoviesApi(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre) {
        
        logger.info("Ahoy! API search request with name: {}, id: {}, genre: {}", name, id, genre);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            movieService.validateSearchParameters(name, id, genre);
            List<Movie> movies = movieService.searchMovies(name, id, genre);
            
            response.put("success", true);
            response.put("message", String.format("Ahoy matey! Found %d movie%s in our treasure chest!", 
                    movies.size(), movies.size() == 1 ? "" : "s"));
            response.put("count", movies.size());
            response.put("movies", movies);
            response.put("searchCriteria", Map.of(
                    "name", name != null ? name : "",
                    "id", id != null ? id : "",
                    "genre", genre != null ? genre : ""
            ));
            
            if (movies.isEmpty()) {
                response.put("message", "Arrr! No treasure found with those search terms, matey! Try different keywords to find yer movies.");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid search parameters for API: {}", e.getMessage());
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("count", 0);
            response.put("movies", List.of());
            
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("Unexpected error during movie search: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Blimey! Something went wrong on our ship. Please try again later, matey!");
            response.put("count", 0);
            response.put("movies", List.of());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/movies/{id}/details")
    public String getMovieDetails(@PathVariable("id") Long movieId, org.springframework.ui.Model model) {
        logger.info("Fetching details for movie ID: {}", movieId);
        
        Optional<Movie> movieOpt = movieService.getMovieById(movieId);
        if (!movieOpt.isPresent()) {
            logger.warn("Movie with ID {} not found", movieId);
            model.addAttribute("title", "Movie Not Found - Arrr!");
            model.addAttribute("message", "Shiver me timbers! Movie with ID " + movieId + " was not found in our treasure chest, matey!");
            return "error";
        }
        
        Movie movie = movieOpt.get();
        model.addAttribute("movie", movie);
        model.addAttribute("movieIcon", MovieIconUtils.getMovieIcon(movie.getMovieName()));
        model.addAttribute("allReviews", reviewService.getReviewsForMovie(movie.getId()));
        
        return "movie-details";
    }
}