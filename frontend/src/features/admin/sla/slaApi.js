const API_BASE_URL =
    process.env.REACT_APP_API_BASE_URL ||
    'http://localhost:8080';

function createHeaders() {
    const headers = { 'Content-Type': 'application/json' };

    const accessToken = localStorage.getItem('accessToken');
    if (accessToken) {
        headers.Authorization = `Bearer ${accessToken}`;
    }

    return headers;
}

async function handleResponse(response) {
    if (!response.ok) {
        let message = `Crearea a esuat cu statusul ${response.status}`;
        try {
            const errorBody = await response.json();
            message = errorBody.detail || errorBody.message || message;
        } catch (error) {
            console.error('Eroare la parsarea răspunsului de eroare:', error);
        }

        throw new Error(message);
    }

    return response.json();
}

export async function getSlaConfig() {
    const response = await fetch(`${API_BASE_URL}/api/admin/sla`, {
        headers: createHeaders(),
    });

    return handleResponse(response);
}

export async function updateSlaConfig(priority, resolutionHours) {
    const response = await fetch(`${API_BASE_URL}/api/admin/sla/${priority}`, {
        method: 'PUT',
        headers: createHeaders(),
        body: JSON.stringify({ resolutionHours,
            }),
        }
    );
    
    return handleResponse(response);
}
