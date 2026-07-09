const AGENT_TICKETS_URL =
    "http://localhost:8080/api/agent/tickets";

function getAccessToken() {
    const token = localStorage.getItem("accessToken");

    if (!token) {
        throw new Error(
            "Nu exista un token de autentificare."
        );
    }

    return token;
}

function createHeaders(hasJsonBody = false) {
    const headers = {
        Authorization: `Bearer ${getAccessToken()}`,
    };

    if (hasJsonBody) {
        headers["Content-Type"] = "application/json";
    }

    return headers;
}

async function handleResponse(response) {
    if (!response.ok) {
        let message = `Cererea a esuat. Status ${response.status}`;

        try {
            const errorData = await response.json();

            message =
                errorData.detail ||
                errorData.message ||
                errorData.error ||
                message;
        } catch {
            // Raspunsul nu contine JSON.
        }

        throw new Error(message);
    }

    return response.json();
}

export async function getAgentMessages(ticketId) {
    const response = await fetch(
        `${AGENT_TICKETS_URL}/${ticketId}/messages`,
        {
            headers: createHeaders(),
        }
    );

    return handleResponse(response);
}

export async function addAgentMessage(
    ticketId,
    content,
    internal
) {
    const response = await fetch(
        `${AGENT_TICKETS_URL}/${ticketId}/messages`,
        {
            method: "POST",
            headers: createHeaders(true),
            body: JSON.stringify({
                content,
                internal,
            }),
        }
    );

    return handleResponse(response);
}