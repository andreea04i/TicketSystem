import { useState } from "react";
import SlaConfigPage from "./features/admin/sla/SlaConfigPage";
import AgentDashboard from "./pages/AgentDashboard";
import TicketDetailsPage from "./pages/TicketDetailsPage";

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

    return (
        <div>
            <nav>
                <button onClick={showAgentDashboard}>
                    Agent Dashboard
                </button>

                <button onClick={showSlaConfig}>
                    Configurare SLA
                </button>
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
        </div>
    );
}

export default App;