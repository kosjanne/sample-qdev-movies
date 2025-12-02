# 🏴‍☠️ Movie Search API Documentation

Ahoy matey! This be the complete treasure map for navigating our Movie Search API. Use this guide to integrate with our pirate-themed movie service like a proper sea captain!

## 🗺️ Base URL

```
http://localhost:8080
```

## 🔍 Search Endpoints

### 1. HTML Movie Search
**Endpoint:** `GET /movies`

**Description:** Returns an HTML page with movie results and search form. Perfect for web browsers and human pirates!

**Query Parameters:**
| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| `name` | String | No | Movie name to search (case-insensitive, partial match) | `prison`, `family`, `the` |
| `id` | Long | No | Specific movie ID (1-12, takes precedence) | `1`, `5`, `12` |
| `genre` | String | No | Genre to filter by (case-insensitive, partial match) | `drama`, `action`, `crime` |

**Example Requests:**
```bash
# Search by name
GET /movies?name=prison

# Search by genre  
GET /movies?genre=drama

# Search by ID
GET /movies?id=1

# Combined search
GET /movies?name=family&genre=crime

# Show all movies
GET /movies
```

**Response:** HTML page with search form and movie results

---

### 2. JSON Movie Search API
**Endpoint:** `GET /movies/search`

**Description:** Returns JSON response with search results. Perfect for API integrations and other applications!

**Query Parameters:**
| Parameter | Type | Required | Description | Validation |
|-----------|------|----------|-------------|------------|
| `name` | String | No* | Movie name to search | Max 100 characters |
| `id` | Long | No* | Specific movie ID | Must be > 0 |
| `genre` | String | No* | Genre to filter by | Max 50 characters |

*At least one parameter is required

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Ahoy matey! Found 2 movies in our treasure chest!",
  "count": 2,
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

**Error Response (400 Bad Request):**
```json
{
  "success": false,
  "error": "Arrr! That ID be not a valid treasure map number, matey!",
  "count": 0,
  "movies": []
}
```

**Error Response (500 Internal Server Error):**
```json
{
  "success": false,
  "error": "Blimey! Something went wrong on our ship. Please try again later, matey!",
  "count": 0,
  "movies": []
}
```

## 🎬 Movie Details Endpoint

### Get Movie Details
**Endpoint:** `GET /movies/{id}/details`

**Description:** Returns detailed HTML page for a specific movie with reviews.

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `id` | Long | Yes | Movie ID (1-12) |

**Example Request:**
```bash
GET /movies/1/details
```

**Response:** HTML page with movie details and customer reviews

## 📊 Data Models

### Movie Object
```json
{
  "id": 1,
  "movieName": "The Prison Escape",
  "director": "John Director",
  "year": 1994,
  "genre": "Drama", 
  "description": "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
  "duration": 142,
  "imdbRating": 5.0,
  "icon": "🎬"
}
```

### Search Response Object
```json
{
  "success": boolean,
  "message": "string",
  "count": number,
  "movies": [Movie],
  "searchCriteria": {
    "name": "string",
    "id": "string", 
    "genre": "string"
  }
}
```

## 🚨 Error Handling

### Validation Errors (400 Bad Request)

| Error Condition | Pirate Error Message |
|-----------------|---------------------|
| No search parameters | "Shiver me timbers! Ye need to provide at least one search parameter to find yer treasure!" |
| Invalid ID (≤ 0) | "Arrr! That ID be not a valid treasure map number, matey!" |
| Name too long (> 100 chars) | "Arrr! That movie name be longer than a pirate's tale, matey!" |
| Genre too long (> 50 chars) | "Blimey! That genre name be longer than a ship's anchor chain!" |

### Server Errors (500 Internal Server Error)
- Generic server error: "Blimey! Something went wrong on our ship. Please try again later, matey!"

## 🧪 Testing Examples

### cURL Examples

```bash
# Basic search by name
curl "http://localhost:8080/movies/search?name=prison"

# Search by genre
curl "http://localhost:8080/movies/search?genre=action"

# Search by ID
curl "http://localhost:8080/movies/search?id=1"

# Combined search
curl "http://localhost:8080/movies/search?name=the&genre=drama"

# Error case - no parameters
curl "http://localhost:8080/movies/search"

# Error case - invalid ID
curl "http://localhost:8080/movies/search?id=-1"
```

### JavaScript/Fetch Examples

```javascript
// Search by name
fetch('/movies/search?name=prison')
  .then(response => response.json())
  .then(data => {
    console.log('Found movies:', data.movies);
    console.log('Message:', data.message);
  });

// Combined search with error handling
async function searchMovies(name, genre) {
  try {
    const params = new URLSearchParams();
    if (name) params.append('name', name);
    if (genre) params.append('genre', genre);
    
    const response = await fetch(`/movies/search?${params}`);
    const data = await response.json();
    
    if (data.success) {
      return data.movies;
    } else {
      throw new Error(data.error);
    }
  } catch (error) {
    console.error('Search failed:', error.message);
    return [];
  }
}
```

## 🎯 Available Data

### Movie IDs (1-12)
1. The Prison Escape
2. The Family Boss  
3. The Masked Hero
4. Urban Stories
5. Life Journey
6. Dream Heist
7. The Virtual World
8. The Wise Guys
9. The Quest for the Ring
10. Space Wars: The Beginning
11. The Factory Owner
12. Underground Club

### Available Genres
- Action/Crime
- Action/Sci-Fi
- Adventure/Fantasy
- Adventure/Sci-Fi
- Crime/Drama
- Drama
- Drama/History
- Drama/Romance
- Drama/Thriller

## 🔧 Integration Tips

1. **Parameter Precedence:** ID search takes precedence over name/genre filters
2. **Case Sensitivity:** All text searches are case-insensitive
3. **Partial Matching:** Name and genre support partial string matching
4. **Rate Limiting:** No rate limiting currently implemented
5. **Caching:** Consider caching results for better performance
6. **Error Handling:** Always check the `success` field in JSON responses

## 🏴‍☠️ Pirate Language Guide

Our API uses pirate-themed language in messages and errors:
- "Ahoy!" = Hello/Success greeting
- "Arrr!" = Emphasis/Frustration
- "Matey" = Friend/User
- "Treasure chest" = Movie database
- "Treasure map" = Movie ID
- "Adventure type" = Genre
- "Shiver me timbers!" = Surprise/Error
- "Blimey!" = Surprise/Concern

---

**May the winds be at yer back and yer API calls be swift! 🏴‍☠️⚓**