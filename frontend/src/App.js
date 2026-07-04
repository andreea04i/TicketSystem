import { useState } from "react";
import SlaConfigPage from "./features/admin/sla/SlaConfigPage";
import AgentDashboard from "./pages/AgentDashboard";

function App() {
    const [currentPage, setCurrentPage] = useState("agent");

    return (
        <div>
            <nav>
                <button onClick={() => setCurrentPage("agent")}>
                    Agent Dashboard
                </button>

                <button onClick={() => setCurrentPage("sla")}>
                    Configurare SLA
                </button>
            </nav>

            {currentPage === "agent"
                ? <AgentDashboard />
                : <SlaConfigPage />}
        </div>
    );
}

export default App;