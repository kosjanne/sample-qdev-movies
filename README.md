# 🏴‍☠️ Movie Service - Pirate-Themed Spring Boot Demo Application

Ahoy matey! Welcome to our treasure chest of movies! A pirate-themed movie catalog web application built with Spring Boot, demonstrating Java application development best practices with a swashbuckling twist.

## ⚓ Features

- **🎬 Movie Catalog**: Browse 12 classic movies with detailed information
- **🔍 Pirate Movie Search**: Hunt for treasure with our advanced search functionality!
  - Search by movie name (partial matching, case-insensitive)
  - Search by treasure map ID (exact movie ID)
  - Search by adventure type (genre filtering)
  - Combined search criteria support
- **📋 Movie Details**: View comprehensive information including captain (director), year of adventure, quest type (genre), journey length (duration), and description
- **⭐ Customer Reviews**: Each movie includes authentic customer reviews with ratings and avatars
- **🌊 Responsive Design**: Mobile-first design that works on all devices like a proper ship
- **🎨 Modern Pirate UI**: Dark theme with treasure-inspired gradients and smooth animations
- **🗺️ REST API**: JSON endpoints for other ships (applications) to access our movie treasure

## 🛠️ Technology Stack

- **Java 8**
- **Spring Boot 2.7.18**
- **Maven** for dependency management
- **Thymeleaf** for HTML templating
- **Log4j 2** for logging
- **JUnit 5.8.2** for testing
- **JSON** for data processing

## 🚀 Quick Start

### Prerequisites

- Java 8 or higher
- Maven 3.6+

### Run the Application

```bash
git clone https://github.com/<youruser>/sample-qdev-movies.git
cd sample-qdev-movies
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access the Application

- **🏴‍☠️ Movie Treasure Chest**: http://localhost:8080/movies
- **🔍 Search for Movies**: http://localhost:8080/movies?name=prison&genre=drama
- **⚓ Movie Details**: http://localhost:8080/movies/{id}/details (where {id} is 1-12)
- **🗺️ REST API Search**: http://localhost:8080/movies/search?name=action&genre=crime

## 🔍 Search Functionality - Chart Yer Course!

### HTML Search Interface

Navigate to `/movies` and use our pirate-themed search form:

- **🎬 Movie Name**: Search by movie title (e.g., "Prison", "Family", "Hero")
- **🗺️ Treasure Map ID**: Enter specific movie ID (1-12)
- **⚔️ Adventure Type**: Filter by genre (e.g., "Drama", "Action", "Crime")

**Example Searches:**
- Find all drama movies: `/movies?genre=drama`
- Search for movies with "the" in the name: `/movies?name=the`
- Find specific movie by ID: `/movies?id=5`
- Combined search: `/movies?name=family&genre=crime`

### REST API Search Endpoints

#### Search Movies (JSON Response)
```
GET /movies/search
```

**Query Parameters:**
- `name` (optional): Movie name to search for (case-insensitive, partial matching)
- `id` (optional): Specific movie ID to find (takes precedence over other parameters)
- `genre` (optional): Genre to filter by (case-insensitive, partial matching)

**Example Requests:**
```bash
# Search by name
curl "http://localhost:8080/movies/search?name=prison"

# Search by genre
curl "http://localhost:8080/movies/search?genre=action"

# Search by ID
curl "http://localhost:8080/movies/search?id=1"

# Combined search
curl "http://localhost:8080/movies/search?name=family&genre=crime"
```

**Example Response:**
```json
{
  "success": true,
  "message": "Ahoy matey! Found 1 movie in our treasure chest!",
  "count": 1,
  "movies": [
    {
      "id": 1,
      "movieName": "The Prison Escape",
      "director": "John Director",
      "year": 1994,
      "genre": "Drama",
      "description": "Two imprisoned men bond over a number of years...",
      "duration": 142,
      "imdbRating": 5.0,
      "icon": "🎬"
    }
  ],
  "searchCriteria": {
    "name": "prison",
    "id": "",
    "genre": ""
  }
}
```

**Error Response:**
```json
{
  "success": false,
  "error": "Arrr! That ID be not a valid treasure map number, matey!",
  "count": 0,
  "movies": []
}
```

## 🏗️ Building for Production

```bash
mvn clean package
java -jar target/sample-qdev-movies-0.1.0.jar
```

## 📁 Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/amazonaws/samples/qdevmovies/
│   │       ├── movies/
│   │       │   ├── MoviesApplication.java     # Main Spring Boot application
│   │       │   ├── MoviesController.java     # REST controller with search endpoints
│   │       │   ├── MovieService.java         # Business logic with search functionality
│   │       │   ├── Movie.java                # Movie data model
│   │       │   ├── Review.java               # Review data model
│   │       │   └── ReviewService.java        # Review business logic
│   │       └── utils/
│   │           ├── MovieIconUtils.java       # Movie icon utilities
│   │           └── MovieUtils.java           # Movie validation utilities
│   └── resources/
│       ├── application.yml                   # Application configuration
│       ├── movies.json                       # Movie treasure data
│       ├── mock-reviews.json                 # Mock review data
│       ├── log4j2.xml                        # Logging configuration
│       ├── static/css/
│       │   └── movies.css                    # Pirate-themed styling
│       └── templates/
│           ├── movies.html                   # Main movie list with search form
│           └── movie-details.html            # Movie details page
└── test/                                     # Comprehensive unit tests
    └── java/
        └── com/amazonaws/samples/qdevmovies/movies/
            ├── MovieServiceTest.java         # Service layer tests
            ├── MoviesControllerTest.java     # Controller tests
            └── MovieTest.java                # Model tests
```

## 🗺️ API Endpoints

### Get All Movies (with optional search)
```
GET /movies
```
Returns an HTML page displaying movies. Supports search parameters:
- `?name=searchterm` - Search by movie name
- `?id=123` - Search by movie ID  
- `?genre=drama` - Filter by genre
- Multiple parameters can be combined

### Search Movies (JSON API)
```
GET /movies/search
```
Returns JSON response with search results. Same parameters as above.

### Get Movie Details
```
GET /movies/{id}/details
```
Returns an HTML page with detailed movie information and customer reviews.

**Parameters:**
- `id` (path parameter): Movie ID (1-12)

**Example:**
```
http://localhost:8080/movies/1/details
```

## 🧪 Testing

Run the comprehensive test suite:

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=MovieServiceTest

# Run with coverage
mvn test jacoco:report
```

**Test Coverage:**
- ✅ MovieService search functionality
- ✅ MoviesController HTML and REST endpoints
- ✅ Parameter validation with pirate error messages
- ✅ Edge cases and error handling
- ✅ Integration tests

## 🐛 Troubleshooting

### Port 8080 already in use

Run on a different port:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Build failures

Clean and rebuild:
```bash
mvn clean compile
```

### Search not working

1. Verify at least one search parameter is provided
2. Check that movie IDs are between 1-12
3. Ensure search terms are not excessively long
4. Check application logs for detailed error messages

## 🎯 Available Movie Data

Our treasure chest contains 12 movies with the following genres:
- **Action/Crime**: The Masked Hero, Dream Heist, The Virtual World
- **Action/Sci-Fi**: Dream Heist, The Virtual World, Space Wars: The Beginning
- **Adventure/Fantasy**: The Quest for the Ring
- **Adventure/Sci-Fi**: Space Wars: The Beginning
- **Crime/Drama**: The Family Boss, Urban Stories, The Wise Guys
- **Drama**: The Prison Escape
- **Drama/History**: The Factory Owner
- **Drama/Romance**: Life Journey
- **Drama/Thriller**: Underground Club

## 🤝 Contributing

This project demonstrates modern Spring Boot development with search functionality. Feel free to:
- Add more movies to the treasure chest
- Enhance the pirate-themed UI/UX
- Add new search features (rating filters, year ranges, etc.)
- Improve the responsive design
- Add more comprehensive error handling
- Implement caching for better performance

## 📜 License

This sample code is licensed under the MIT-0 License. See the LICENSE file.

---

**Arrr! May fair winds fill yer sails as ye explore our movie treasure! 🏴‍☠️⚓**
