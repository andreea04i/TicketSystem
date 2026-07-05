const AGENT_TICKETS_URL =
    "http://localhost:8080/api/agent/tickets";

async function handleResponse(response) {
    if (!response.ok) {
        let message = `Crearea a esuat. Status ${response.status}`;

        try {
            const errorData = await response.json();

            message =
                errorData.detail ||
                errorData.message ||
                message;
        } catch {
            // Raspunsul nu contine JSON
        }

        throw new Error(message);
    }

    return response.json();
}

export async function getAgentTickets() {
    const response = await fetch(AGENT_TICKETS_URL);

    return handleResponse(response);
}

export async function assignTicket(ticketId, agentId) {
    const response = await fetch (
        `${AGENT_TICKETS_URL}/${ticketId}/assign`,
        {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
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
            headers: {
                "Content-Type": "application/json",
            },
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
            headers: {
                "Content-Type": "application/json", 
            },
            body: JSON.stringify({
                reason,
            }),
        }
    );

    return handleResponse(response);
}