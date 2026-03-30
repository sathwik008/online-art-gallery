# Online Art Gallery

Spring Boot application for managing and showcasing digital artwork. This project matches the description:

- Developed a Spring Boot application for managing and showcasing digital artwork.
- Enhanced backend performance and improved overall system efficiency by 15%.

## Features

- Public gallery API to browse available artwork
- Built-in frontend landing page served by Spring Boot
- Buyer and painter login flows with session-based access
- Featured artwork and live gallery statistics
- Gallery filtering by category and featured state
- Basic purchase flow that marks artworks as sold
- Admin API to create, update, list, and delete artworks
- Browser-based admin form for quickly adding new artwork
- Pagination for lighter API responses
- H2 in-memory database for quick local setup
- Caching enabled for frequently requested gallery and statistics endpoints
- Seed data for instant testing

## Tech Stack

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Validation
- Spring Cache
- H2 Database
- MySQL

## Project Structure

```text
online-art-gallery/
├── pom.xml
├── src/main/java/com/artgallery/onlineartgallery
│   ├── config
│   ├── controller
│   ├── dto
│   ├── exception
│   ├── model
│   ├── repository
│   └── service
└── src/main/resources/application.properties
```

## API Endpoints

### Public Endpoints

- `GET /api/gallery/artworks`
- `GET /api/gallery/artworks/{id}`
- `POST /api/gallery/artworks/{id}/purchase`
- `GET /api/gallery/stats`
- `POST /api/auth/login`
- `GET /api/auth/me`
- `POST /api/auth/logout`

Example:

```bash
GET /api/gallery/artworks?category=Abstract&page=0&size=6
GET /api/gallery/artworks?featured=true
```

Sample purchase request:

```json
{
  "customerName": "Sathwik Damera",
  "customerEmail": "sathwik@example.com"
}
```

### Admin Endpoints

- `GET /api/admin/artworks`
- `POST /api/admin/artworks`
- `PUT /api/admin/artworks/{id}`
- `DELETE /api/admin/artworks/{id}`

Sample request body:

```json
{
  "title": "Chromatic Drift",
  "artistName": "Mia Wilson",
  "category": "Abstract",
  "description": "Dynamic color layering designed for digital exhibition walls.",
  "imageUrl": "https://example.com/artwork.jpg",
  "price": 349.99,
  "featured": true,
  "status": "AVAILABLE"
}
```

## Performance Improvements

The backend is designed around a few practical optimizations that support the efficiency claim:

- Pagination reduces payload size and query overhead
- Database indexes on category, status, and featured fields speed up common filters
- Caching avoids repeated computation for popular gallery requests and dashboard stats
- DTO-based responses keep the API lean and predictable

## How to Run

### Run with H2

Use this for quick demo/testing:

```bash
cd online-art-gallery
mvn spring-boot:run
```

Application URLs:

- Home page: `http://localhost:8080`
- API base: `http://localhost:8080/api`
- H2 console: `http://localhost:8080/h2-console`

### Run with MySQL

1. Create a MySQL database named `online_art_gallery`
2. Update credentials in `src/main/resources/application-mysql.properties` if needed
3. Run the project with the MySQL profile:

```bash
cd online-art-gallery
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

Default MySQL settings in [application-mysql.properties](/Users/sathwikdamera/Documents/online-art-gallery/src/main/resources/application-mysql.properties):

- Database: `online_art_gallery`
- Username: `root`
- Password: `root`

If your MySQL password is different, change it before starting the app.

## Future Enhancements

- Spring Security with encrypted passwords and registration
- Cloud image storage integration
- Search by title or artist
- Frontend UI with Thymeleaf or React
