# Research Report

## Server-Sent Events (SSE) in Spring

### Summary of Work

I researched Server-Sent Events (SSE) as a mechanism for enabling real-time communication between the backend and frontend. I focused on understanding how SSE works over HTTP, how it differs from polling and WebSockets, and how it can be implemented in a Spring-based backend using `SseEmitter`.

---

### Motivation

Our project currently relies on traditional request-response patterns, where the frontend must repeatedly poll the backend to stay updated. This approach is inefficient and does not scale well for real-time features such as live score updates or game state synchronization.

To address this, I explored SSE as a lightweight solution that allows the backend to push updates directly to the frontend over a persistent connection, without introducing the complexity of WebSockets.

---

### Time Spent

~120 minutes researching SSE concepts, reading documentation, and evaluating how it can be integrated into our existing Spring-based architecture.

---

### Results

SSE is a unidirectional communication protocol that allows a client to maintain a persistent HTTP connection and receive updates from the server as events. Unlike polling, where the client repeatedly sends requests, SSE enables the server to push updates only when new data is available.

In Spring MVC, SSE can be implemented using the `SseEmitter` class. When a client connects to an SSE endpoint, the server creates an `SseEmitter` instance and keeps the connection open. The server can then send events to the client at any time using `emitter.send(...)`.

A basic SSE endpoint in Spring looks like:

```java
@GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public SseEmitter stream() {
    return new SseEmitter();
}
```

The `text/event-stream` media type is required for SSE connections.

Events are sent as structured text messages containing fields such as:
- `event`: event name
- `data`: payload (often JSON)
- `id`: event identifier
- `retry`: reconnection delay

On the frontend, SSE is handled using the `EventSource` API:

```javascript
const es = new EventSource("/stream");

es.addEventListener("message", (event) => {
    console.log(event.data);
});
```

Key observations from the research:

- SSE is simpler than WebSockets and works well for server-to-client updates
- It uses standard HTTP, making it easier to integrate with existing infrastructure
- It is ideal for scenarios where the server needs to continuously push state updates (e.g., game score, accepted words)
- It requires handling connection lifecycle events such as disconnects and reconnections
- Backend should act as the single source of truth, with the frontend reacting to streamed updates

Based on this, SSE was identified as a suitable approach for implementing real-time synchronization in our game system without introducing unnecessary complexity.

---

### Sources

- Baeldung SSE Guide[^1]

[^1]: https://www.baeldung.com/spring-server-sent-events