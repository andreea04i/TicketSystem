const API_BASE_URL =
    process.env.REACT_APP_API_BASE_URL ||
    "http://localhost:8080";

function getAccessToken() {
    const token = localStorage.getItem("accessToken");

    if (!token) {
        throw new Error("Nu există un token de autentificare.");
    }

    return token;
}

function createHeaders() {
    return {
        Authorization: `Bearer ${getAccessToken()}`,
    };
}

async function handleResponse(response) {
    if (!response.ok) {
        let message = `Cererea a eșuat. Status ${response.status}`;

        try {
            const errorData = await response.json();

            message =
                errorData.detail ||
                errorData.message ||
                errorData.error ||
                message;
        } catch {
            // Răspunsul nu conține JSON.
        }

        throw new Error(message);
    }

    return response.json();
}

export async function getTicketsByCategory() {
    const response = await fetch(
        `${API_BASE_URL}/api/admin/reports/tickets-by-category`,
        {
            headers: createHeaders(),
        }
    );

    return handleResponse(response);
}

export async function getAverageResolutionTime() {
    const response = await fetch(
        `${API_BASE_URL}/api/admin/reports/average-resolution-time`,
        {
            headers: createHeaders(),
        }
    );

    return handleResponse(response);
}

export async function getSlaBreaches() {
    const response = await fetch(
        `${API_BASE_URL}/api/admin/reports/sla-breaches`,
        {
            headers: createHeaders(),
        }
    );

    return handleResponse(response);
}