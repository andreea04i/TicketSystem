const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || "http://localhost:8080";

function getAccessToken() {
    const token = localStorage.getItem("accessToken");

    if (!token) {
        throw new Error("Nu exista un token de autentificare");
    }

    return token;
}

function createHeaders(hasJsonBody = false) {
    const headers = { Authorization: `Bearer ${getAccessToken()}`,};

    if (hasJsonBody) {
        headers["Content-Type"] = "application/json";
    }

    return headers;
}

async function handleResponse(response) {
    if (response.status === 204) {
        return null;
    }

    if (!response.ok) {
        let message = `Crearea a esuat. Status ${response.status}`;

        try {
            const errorData = await response.json();

            message = errorData.detail || errorData.message || errorData.error || message;
        } catch {

        }

        throw new Error(message);
    }

    return response.json();
}

export async function getNotifications() {
    const response = await fetch(
        `${API_BASE_URL}/api/notifications`,
        {
            headers: createHeaders(),
        }
    );

    return handleResponse(response);
}

export async function getUnreadNotificationsCount() {
    const response = await fetch(
        `${API_BASE_URL}/api/notifications/unread-count`,
        {
            headers: createHeaders(),
        }
    );

    return handleResponse(response);
}

export async function markNotificationAsRead(notificationId) {
    const response = await fetch(
        `${API_BASE_URL}/api/notifications/${notificationId}/read`,
        {
            method: "PATCH",
            headers: createHeaders(),
        }
    );

    return handleResponse(response);
}

export async function markAllNotificationsAsRead() {
    const response = await fetch(
        `${API_BASE_URL}/api/notifications/read-all`,
        {
            method: "PATCH",
            headers: createHeaders(),
        }
    );

    return handleResponse(response);
}