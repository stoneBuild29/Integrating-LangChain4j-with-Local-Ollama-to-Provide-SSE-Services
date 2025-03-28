# Ollama LangChain Integration with SSE Streaming

## Project Overview

This project demonstrates how to integrate [LangChain4j](https://github.com/langchain4j/langchain4j) with a locally hosted [Ollama](https://docs.langchain4j.dev/integrations/language-models/ollama/) model to provide real-time AI-generated responses via Server-Sent Events (SSE). The application is built using Spring Boot and leverages dependency injection for clean bean management.

## Features

- **Ollama Streaming Chat Model**: Uses LangChain4j's abstraction to send streaming requests to a locally deployed Ollama service.
- **SSE Endpoint**: Provides a REST endpoint that streams generated tokens to the client in real time.
- **Spring Dependency Injection**: Ensures clean bean lifecycle management and inter-bean communication.
- **CORS Support**: Allows cross-origin GET requests for the `/ai/**` endpoints.

## Prerequisites

- **Java 11 or higher**
- **Maven** for dependency management and building the project.
- **Ollama Service**: Ensure your local Ollama model is running and accessible at `http://172.168.0.93:11434`.
- **Internet Access**: For downloading dependencies.

## Project Structure

```
.
├── pom.xml
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── ajjtcx
│   │   │           ├── client
│   │   │           │   └── OllamaLangChainClient.java
│   │   │           └── controller
│   │   │               └── AIController.java
│   │   └── resources
│   │       └── application.properties
└── README.md
```

### Module Details

1. **OllamaLangChainClient.java**

  - **Purpose**:  
    Encapsulates the logic for interacting with the Ollama model using LangChain4j. It defines:

    - A **bean** (`streamingChatModel`) configured as an `OllamaStreamingChatModel` that uses a specified base URL, model name, and temperature setting.
    - A **streaming method** (`generateStream`) that sends a prompt to the Ollama service and processes the streaming response via a custom `StreamingResponseHandler`.

  - **Key Points**:
    - Uses `@Configuration` and `@Component` to let Spring manage bean creation.
    - The `generateStream` method creates an `SseEmitter` that streams tokens to the client.
    - The streaming response handler processes tokens with `onNext`, completes the emitter in `onComplete`, and handles errors with `onError`.

2. **AIController.java**

  - **Purpose**:  
    Exposes an SSE endpoint `/ai/generate-stream` to the client. It accepts a prompt parameter and returns an `SseEmitter` that streams the AI-generated response.

  - **Key Points**:
    - Uses constructor injection to obtain an instance of `OllamaLangChainClient`.
    - Annotated with `@RestController` and maps to `/ai`, ensuring the SSE endpoint is properly exposed.

## How to Run

1. **Clone the Repository**:

   ```bash
   git clone https://github.com/yourusername/your-repository.git
   cd your-repository
   ```

2. **Build the Project**:

   Ensure Maven is installed, then run:

   ```bash
   mvn clean install
   ```

3. **Run the Application**:

   Start the Spring Boot application:

   ```bash
   mvn spring-boot:run
   ```

4. **Test the SSE Endpoint**:

   You can test the SSE endpoint using a tool like Apifox, Postman, or directly via your browser. For example, open your browser and navigate to:

   ```
   http://localhost:8080/ai/generate-stream?prompt=Hello+Ollama
   ```

   You should see a stream of tokens as the AI generates the response.

## Fix bugs 
Q: Method annotated with @Bean is called directly. Use dependency injection instead.
```
        streamingChatModel.generate(prompt, new StreamingResponseHandler<>() {
```

A: When a class is not annotated with `@Configuration`, the methods marked with `@Bean` are treated as plain methods, and Spring does not create a CGLIB-based proxy to manage and intercept calls between them. This means that if one `@Bean` method directly calls another within the same class, it bypasses Spring’s container, resulting in direct method invocation rather than returning the managed bean instance.

Adding `@Configuration` tells Spring to enhance the class so that it can manage the lifecycle of the beans and properly handle inter-bean references. Without it, calling one `@Bean` method from another won’t leverage Spring’s dependency injection and may lead to errors or unexpected behavior.

## Additional Notes

- **Dependency Injection and @Configuration**:  
  The class `OllamaLangChainClient` is annotated with both `@Component` and `@Configuration`. This ensures that:
  - The bean `streamingChatModel()` is managed by Spring.
  - Any inter-bean calls (e.g., invoking `streamingChatModel()` within `generateStream()`) are properly handled by Spring’s CGLIB-enhanced proxies. Without the `@Configuration` annotation, such calls would bypass dependency injection, leading to unexpected behavior.

- **Error Handling**:  
  The SSE emitter is configured with a 60-second timeout and includes error handling in the streaming response handler. Adjust the timeout and error responses as needed for your use case.

---

Feel free to customize this README further to suit your project details and guidelines.