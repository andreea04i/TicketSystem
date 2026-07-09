import { useState } from "react";
import NotificationCenter from "./components/notifications/NotificationCenter";
import SlaConfigPage from "./features/admin/sla/SlaConfigPage";
import AgentDashboard from "./pages/AgentDashboard";
import TicketDetailsPage from "./pages/TicketDetailsPage";
import AdminReportsPage from "./features/admin/reports/AdminReportsPage";

import "./App.css";

function App() {
    const [currentPage, setCurrentPage] = useState("agent");
    const [selectedTicketId, setSelectedTicketId] = useState(null);

        function showAgentDashboard() {
            setSelectedTicketId(null);
            setCurrentPage("agent");
        }

        function showTicketDetails(ticketId) {
            setSelectedTicketId(ticketId);
            setCurrentPage("ticket-details");
        }

        function showSlaConfig() {
            setSelectedTicketId(null);
            setCurrentPage("sla");
        }

        function showAdminReports() {
            setSelectedTicketId(null);
            setCurrentPage("reports");
        }

    return (
        <div>
            <nav className="app-navbar">
                <div className="app-navbar-links">
                    <button
                        type="button"
                        onClick={showAgentDashboard}
                    >
                        Agent Dashboard
                    </button>

                    <button
                        type="button"
                        onClick={showSlaConfig}
                    >
                        Configurare SLA
                    </button>

                    <button
                        type="button"
                        onClick={showAdminReports}
                    >
                        Rapoarte admin
                    </button>
                </div>

                <NotificationCenter
                    onOpenTicket={showTicketDetails}
                />
            </nav>

            {currentPage === "agent" && (
                <AgentDashboard
                    onOpenTicket={showTicketDetails}
                />
            )}

            {currentPage === "ticket-details" &&
                selectedTicketId !== null && (
                    <TicketDetailsPage
                        ticketId={selectedTicketId}
                        onBack={showAgentDashboard}
                    />
                )}

            {currentPage === "sla" && (
                <SlaConfigPage />
            )}

            {currentPage === "reports" && (
                <AdminReportsPage />
            )}
        </div>
    );
}

export default App;