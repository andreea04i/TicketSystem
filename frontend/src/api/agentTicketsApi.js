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
            // Raspunsul nu contine JSON
        }

        throw new Error(message);
    }

    return response.json();
}

export async function getAgentTickets() {
    const response = await fetch(AGENT_TICKETS_URL, { headers: createHeaders(),});

    return handleResponse(response);
}

export async function getAgentTicketDetails(ticketId) {
    const response = await fetch(
        `${AGENT_TICKETS_URL}/${ticketId}`,
        { headers: createHeaders(),}
    );

    return handleResponse(response);
}

export async function assignTicket(ticketId, agentId) {
    const response = await fetch (
        `${AGENT_TICKETS_URL}/${ticketId}/assign`,
        {
            method: "PUT",
            headers: createHeaders(true),
            body: JSON.stringify({
                agentId,
            }),
        }
    );

    return handleResponse(response);
}

export async function changeTicketStatus(ticketId, status) {
    const response = await fetch(
        `${AGENT_TICKETS_URL}/${ticketId}/status`,
        {
            method: "PUT",
            headers: createHeaders(true),
            body: JSON.stringify({
                status,
            }),
        }
    );

    return handleResponse(response);
}

export async function escalateTicket(ticketId, reason) {
    const response = await fetch(
        `${AGENT_TICKETS_URL}/${ticketId}/escalate`,
        {
            method: "PUT",
            headers: createHeaders(true),
            body: JSON.stringify({
                reason,
            }),
        }
    );

    return handleResponse(response);
}