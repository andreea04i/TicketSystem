const AGENT_TICKETS_URL =
    "http://localhost:8080/api/agent/tickets";

export async function getAgentTickets() {
    const response = await fetch(AGENT_TICKETS_URL);

    if (!response.ok) {
        throw new Error(
            `Nu s-au putut încărca tichetele. Status: ${response.status}`
        );
    }

    return response.json();
}