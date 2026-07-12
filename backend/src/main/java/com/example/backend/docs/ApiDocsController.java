package com.example.backend.docs;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiDocsController {

    @GetMapping(
            value = "/api/docs/swagger",
            produces = MediaType.TEXT_HTML_VALUE
    )
    public String swaggerUi() {
        return """
                <!doctype html>
                <html lang="ro">
                <head>
                    <meta charset="utf-8">
                    <title>TicketSystem API</title>
                    <link rel="stylesheet" href="https://unpkg.com/swagger-ui-dist@5/swagger-ui.css">
                </head>
                <body>
                    <div id="swagger-ui"></div>
                    <script src="https://unpkg.com/swagger-ui-dist@5/swagger-ui-bundle.js"></script>
                    <script>
                        window.onload = () => {
                            window.ui = SwaggerUIBundle({
                                url: "/api/docs/openapi.json",
                                dom_id: "#swagger-ui"
                            });
                        };
                    </script>
                </body>
                </html>
                """;
    }

    @GetMapping(
            value = "/api/docs/openapi.json",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Map<String, Object> openApi() {
        Map<String, Object> spec = new LinkedHashMap<>();

        spec.put("openapi", "3.0.3");
        spec.put(
                "info",
                Map.of(
                        "title", "TicketSystem API",
                        "version", "1.0.0"
                )
        );
        spec.put("servers", List.of(Map.of("url", "http://localhost:8080")));
        spec.put("tags", tags());
        spec.put("paths", paths());
        spec.put("components", components());

        return spec;
    }

    private List<Map<String, String>> tags() {
        return List.of(
                Map.of("name", "Auth"),
                Map.of("name", "Employee Tickets"),
                Map.of("name", "Employee Messages"),
                Map.of("name", "Agent Tickets"),
                Map.of("name", "Agent Messages"),
                Map.of("name", "Admin SLA"),
                Map.of("name", "Admin Reports"),
                Map.of("name", "Notifications")
        );
    }

    private Map<String, Object> paths() {
        Map<String, Object> paths = new LinkedHashMap<>();

        paths.put("/api/auth/register", path("post", "Auth", "Register"));
        paths.put("/api/auth/login", path("post", "Auth", "Login"));
        paths.put("/api/users/me", path("get", "Auth", "Current user"));

        paths.put("/api/employee/tickets", path(
                Map.of(
                        "get", operation("Employee Tickets", "List own tickets"),
                        "post", operation("Employee Tickets", "Create ticket")
                )
        ));
        paths.put("/api/employee/tickets/{ticketId}", path(
                "get",
                "Employee Tickets",
                "Get own ticket"
        ));
        paths.put("/api/employee/tickets/{ticketId}/close", path(
                "patch",
                "Employee Tickets",
                "Close resolved ticket"
        ));
        paths.put("/api/employee/tickets/{ticketId}/messages", path(
                Map.of(
                        "get", operation("Employee Messages", "List public messages"),
                        "post", operation("Employee Messages", "Add public message")
                )
        ));

        paths.put("/api/agent/tickets", path(
                "get",
                "Agent Tickets",
                "List and filter tickets"
        ));
        paths.put("/api/agent/tickets/{ticketId}", path(
                "get",
                "Agent Tickets",
                "Get ticket details"
        ));
        paths.put("/api/agent/tickets/{ticketId}/claim", path(
                "patch",
                "Agent Tickets",
                "Claim ticket"
        ));
        paths.put("/api/agent/tickets/{ticketId}/assign", path(
                "patch",
                "Agent Tickets",
                "Assign ticket to agent"
        ));
        paths.put("/api/agent/tickets/{ticketId}/status", path(
                "patch",
                "Agent Tickets",
                "Change ticket status"
        ));
        paths.put("/api/agent/tickets/{ticketId}/escalate", path(
                "post",
                "Agent Tickets",
                "Escalate ticket"
        ));
        paths.put("/api/agent/tickets/{ticketId}/messages", path(
                Map.of(
                        "get", operation("Agent Messages", "List all messages"),
                        "post", operation("Agent Messages", "Add public or internal message")
                )
        ));

        paths.put("/api/admin/sla", path(
                Map.of(
                        "get", operation("Admin SLA", "List SLA configuration"),
                        "put", operation("Admin SLA", "Update SLA hours")
                )
        ));
        paths.put("/api/admin/reports/tickets-by-category", path(
                "get",
                "Admin Reports",
                "Tickets by category"
        ));
        paths.put("/api/admin/reports/average-resolution-time", path(
                "get",
                "Admin Reports",
                "Average resolution time"
        ));
        paths.put("/api/admin/reports/sla-breaches", path(
                "get",
                "Admin Reports",
                "SLA breaches"
        ));
        paths.put("/api/notifications", path(
                "get",
                "Notifications",
                "List current user notifications"
        ));
        paths.put("/api/notifications/unread-count", path(
                "get",
                "Notifications",
                "Unread notifications count"
        ));

        return paths;
    }

    private Map<String, Object> components() {
        return Map.of(
                "securitySchemes",
                Map.of(
                        "bearerAuth",
                        Map.of(
                                "type", "http",
                                "scheme", "bearer",
                                "bearerFormat", "JWT"
                        )
                )
        );
    }

    private Map<String, Object> path(
            String method,
            String tag,
            String summary
    ) {
        return path(Map.of(method, operation(tag, summary)));
    }

    private Map<String, Object> path(
            Map<String, Object> operations
    ) {
        return new LinkedHashMap<>(operations);
    }

    private Map<String, Object> operation(
            String tag,
            String summary
    ) {
        Map<String, Object> operation = new LinkedHashMap<>();

        operation.put("tags", List.of(tag));
        operation.put("summary", summary);
        operation.put(
                "security",
                List.of(Map.of("bearerAuth", List.of()))
        );
        operation.put(
                "responses",
                Map.of(
                        "200",
                        Map.of("description", "Succes"),
                        "401",
                        Map.of("description", "Neautentificat"),
                        "403",
                        Map.of("description", "Acces interzis")
                )
        );

        return operation;
    }
}
