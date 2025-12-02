package com.amazonaws.samples.qdevmovies.movies;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

@Service
public class MovieService {
    private static final Logger logger = LogManager.getLogger(MovieService.class);
    private final List<Movie> movies;
    private final Map<Long, Movie> movieMap;

    public MovieService() {
        this.movies = loadMoviesFromJson();
        this.movieMap = new HashMap<>();
        for (Movie movie : movies) {
            movieMap.put(movie.getId(), movie);
        }
    }

    private List<Movie> loadMoviesFromJson() {
        List<Movie> movieList = new ArrayList<>();
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("movies.json");
            if (inputStream != null) {
                Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name());
                String jsonContent = scanner.useDelimiter("\\A").next();
                scanner.close();
                
                JSONArray moviesArray = new JSONArray(jsonContent);
                for (int i = 0; i < moviesArray.length(); i++) {
                    JSONObject movieObj = moviesArray.getJSONObject(i);
                    movieList.add(new Movie(
                        movieObj.getLong("id"),
                        movieObj.getString("movieName"),
                        movieObj.getString("director"),
                        movieObj.getInt("year"),
                        movieObj.getString("genre"),
                        movieObj.getString("description"),
                        movieObj.getInt("duration"),
                        movieObj.getDouble("imdbRating")
                    ));
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load movies from JSON: {}", e.getMessage());
        }
        return movieList;
    }

    public List<Movie> getAllMovies() {
        return movies;
    }

    public Optional<Movie> getMovieById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(movieMap.get(id));
    }

    /**
     * Search for movies based on multiple criteria with pirate flair!
     * Arrr! This method helps ye find yer treasure in our movie chest!
     * 
     * @param name Movie name to search for (case-insensitive partial match)
     * @param id Specific movie ID to find
     * @param genre Genre to filter by (case-insensitive partial match)
     * @return List of movies matching the search criteria
     */
    public List<Movie> searchMovies(String name, Long id, String genre) {
        logger.info("Ahoy! Searching for movies with name: {}, id: {}, genre: {}", name, id, genre);
        
        List<Movie> results = new ArrayList<>(movies);
        
        // Filter by ID if provided - this be the most specific search, matey!
        if (id != null && id > 0) {
            Optional<Movie> movieById = getMovieById(id);
            return movieById.map(movie -> List.of(movie)).orElse(new ArrayList<>());
        }
        
        // Filter by name if provided - search through our treasure map!
        if (name != null && !name.trim().isEmpty()) {
            String searchName = name.trim().toLowerCase();
            results = results.stream()
                    .filter(movie -> movie.getMovieName().toLowerCase().contains(searchName))
                    .collect(ArrayList::new, (list, movie) -> list.add(movie), ArrayList::addAll);
        }
        
        // Filter by genre if provided - find movies of yer preferred adventure type!
        if (genre != null && !genre.trim().isEmpty()) {
            String searchGenre = genre.trim().toLowerCase();
            results = results.stream()
                    .filter(movie -> movie.getGenre().toLowerCase().contains(searchGenre))
                    .collect(ArrayList::new, (list, movie) -> list.add(movie), ArrayList::addAll);
        }
        
        logger.info("Arrr! Found {} movies matching yer search criteria", results.size());
        return results;
    }

    /**
     * Get all unique genres from our movie treasure chest
     * @return List of all genres available in our collection
     */
    public List<String> getAllGenres() {
        return movies.stream()
                .map(Movie::getGenre)
                .distinct()
                .sorted()
                .collect(ArrayList::new, (list, genre) -> list.add(genre), ArrayList::addAll);
    }

    /**
     * Validate search parameters like a proper pirate captain!
     * @param name Movie name parameter
     * @param id Movie ID parameter  
     * @param genre Genre parameter
     * @throws IllegalArgumentException if parameters are invalid
     */
    public void validateSearchParameters(String name, Long id, String genre) {
        // Check if ID is valid when provided
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("Arrr! That ID be not a valid treasure map number, matey!");
        }
        
        // Check if at least one search parameter is provided
        boolean hasName = name != null && !name.trim().isEmpty();
        boolean hasId = id != null && id > 0;
        boolean hasGenre = genre != null && !genre.trim().isEmpty();
        
        if (!hasName && !hasId && !hasGenre) {
            throw new IllegalArgumentException("Shiver me timbers! Ye need to provide at least one search parameter to find yer treasure!");
        }
        
        // Validate string parameters aren't too long (prevent potential issues)
        if (hasName && name.trim().length() > 100) {
            throw new IllegalArgumentException("Arrr! That movie name be longer than a pirate's tale, matey!");
        }
        
        if (hasGenre && genre.trim().length() > 50) {
            throw new IllegalArgumentException("Blimey! That genre name be longer than a ship's anchor chain!");
        }
    }
}
