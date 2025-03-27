# Project Title: Integrating LangChain4j with Local Ollama to Provide SSE Services

## Project Overview

This project aims to integrate [LangChain4j](https://github.com/langchain4j/langchain4j), a Java adaptation of the LangChain framework, with a locally hosted [Ollama](https://docs.langchain4j.dev/integrations/language-models/ollama/) language model. The integration facilitates real-time AI responses through Server-Sent Events (SSE), enabling efficient and seamless client-server communication.

## Project Structure

The project is organized into the following key components:

- **src/main/java/com/example/ai/**: Contains the core Java classes implementing the AI functionalities.
  - **OllamaClient.java**: Manages HTTP interactions with the Ollama service.
  - **AIController.java**: Defines RESTful endpoints to handle client requests and stream AI responses via SSE.
  - **CorsConfig.java**: Configures Cross-Origin Resource Sharing (CORS) settings to allow cross-origin requests.
- **src/main/resources/**: Holds configuration files and resources.
- **pom.xml**: Maven configuration file managing project dependencies, including LangChain4j and Spring Boot.

## Module Descriptions

### 1. OllamaClient Class

**Functionality**: Encapsulates HTTP interactions with the Ollama service, facilitating streaming of AI-generated responses.

**Implementation Details**:

- Utilizes `OkHttpClient` to send POST requests to the Ollama API endpoint (`http://localhost:11434/api/generate`).
- Implements a callback mechanism (`ResponseBodyCallback`) to process Ollama's streaming responses in real-time by reading JSON data line by line.
- Supports asynchronous, non-blocking operations using the `enqueue` method to prevent blocking the main execution thread.

### 2. AIController Class

**Functionality**: Provides an SSE endpoint to clients, streaming AI-generated responses from Ollama.

**Implementation Details**:

- Defines an SSE endpoint (`/ai/generate-stream`) using the `@GetMapping` annotation, accepting a `prompt` parameter from clients.
- Creates an `SseEmitter` object with a 60-second timeout to manage the SSE connection lifecycle.
- Invokes `OllamaClient.generateStream`, utilizing the callback mechanism to send chunks of Ollama's responses to clients in real-time.
- Parses JSON responses from Ollama to extract the `response` field using regular expressions for streamlined data processing.

### 3. CORS Configuration (CorsConfig Class)

**Functionality**: Configures CORS settings to permit cross-origin GET requests to the `/ai/**` endpoints.

**Implementation Details**:

- Implements the `WebMvcConfigurer` interface and overrides the `addCorsMappings` method.
- Sets up CORS mappings to allow all origins (`allowedOrigins("*")`) and GET methods for paths matching `/ai/**`.
- This configuration ensures that web applications hosted on different domains can access the AI services without encountering cross-origin restrictions.

## Getting Started

To set up and run the project locally, follow these steps:

1. **Clone the Repository**:

```bash
git clone https://github.com/yourusername/your-repository.git
```

2. **Navigate to the Project Directory**:

```bash
cd your-repository
```

3. **Install Dependencies**:

Ensure you have Maven installed. Then, execute:

```bash
mvn clean install
```

4. **Run the Application**:

```bash
mvn spring-boot:run
```

5. **Access the SSE Endpoint**:

Open a browser or use a tool like `curl` to access:

```
http://localhost:8080/ai/generate-stream?prompt=Your+AI+Prompt
```

## Prerequisites

- **Java Development Kit (JDK)**: Ensure JDK 11 or higher is installed.
- **Maven**: Required for managing project dependencies and building the application.
- **Ollama**: Install and run the Ollama service locally. Refer to the [Ollama documentation](https://docs.langchain4j.dev/integrations/language-models/ollama/) for setup instructions.

## References

- [LangChain4j GitHub Repository](https://github.com/langchain4j/langchain4j)
- [Integrating Java with Ollama Using LangChain4j](https://tpbabparn.medium.com/java-ollama-unlock-capability-of-generative-ai-to-java-developer-with-langchain4j-model-on-c814f97d9676)

This README provides a comprehensive overview of the project's objectives, structure, and module functionalities, serving as a guide for understanding and deploying the integrated AI service.