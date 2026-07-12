import { render, screen } from "@testing-library/react";
import App from "./App";

beforeEach(() => {
    localStorage.setItem("accessToken", "test-token");

    global.fetch = jest.fn((url) => {
        if (url.toString().includes("/unread-count")) {
            return Promise.resolve({
                ok: true,
                json: () => Promise.resolve({ unreadCount: 0 }),
            });
        }

        return Promise.resolve({
            ok: true,
            json: () => Promise.resolve([]),
        });
    });
});

afterEach(() => {
    localStorage.clear();
    jest.restoreAllMocks();
});

test("renders agent dashboard navigation", async () => {
    render(<App />);

    const navigationButton = screen.getByText(/agent dashboard/i);

    expect(navigationButton).toBeInTheDocument();
    expect(await screen.findByText(/0 din 0 tichete active/i))
            .toBeInTheDocument();
});
