import { useEffect, useState } from "react";
import { getAgentTickets } from "../api/agentTicketsApi";
import "./AgentDashboard.css";

function AgentDashboard()
{
    const [tickets, setTickets] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const [priorityFilter, setPriorityFilter] = useState("ALL");
    const [statusFilter, setStatusFilter] = useState("ALL");
    const [categoryFilter, setCategoryFilter] = useState("ALL");

    useEffect(() => {
        async function loadTickets(){
            try {
                const data = await getAgentTickets();
                setTickets(data);
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        }
        loadTickets();
    }, [])

    const filteredTickets = tickets.filter((ticket) => {
        const matchesPriority = 
            priorityFilter === "ALL" || ticket.priority === priorityFilter;

        const matchesStatus = 
            statusFilter === "ALL" || ticket.status === statusFilter;

        const matchesCategory =
            categoryFilter === "ALL" || ticket.category === categoryFilter;

        return matchesPriority && matchesStatus && matchesCategory;
    });
    if (loading) {
        return (
            <main className="agent-dashboard">
                <p>Se încarcă tichetele...</p>
            </main>
        );
    }

    if (error) {
        return (
            <main className="agent-dashboard">
                <p>{error}</p>
            </main>
        );
    }

    return (
        <main className="agent-dashboard">
            <h1>Agent Dashboard</h1>

            <p className="agent-dashboard-subtitle">
                {filteredTickets.length} din {tickets.length} tichete active
            </p>

            <div className="agent-filters">
                <label>
                    Prioritate
                    <select
                        value={priorityFilter}
                        onChange={(event) =>
                            setPriorityFilter(event.target.value)
                        }
                    >
                        <option value="ALL">Toate</option>
                        <option value="CRITICAL">Critical</option>
                        <option value="HIGH">High</option>
                        <option value="MEDIUM">Medium</option>
                        <option value="LOW">Low</option>
                    </select>
                </label>

                <label>
                    Status
                    <select
                        value={statusFilter}
                        onChange={(event) =>
                            setStatusFilter(event.target.value)
                        }
                    >
                        <option value="ALL">Toate</option>
                        <option value="OPEN">Open</option>
                        <option value="IN_PROGRESS">In progress</option>
                        <option value="ESCALATED">Escalated</option>
                    </select>
                </label>

                <label>
                    Categorie
                    <select
                        value={categoryFilter}
                        onChange={(event) =>
                            setCategoryFilter(event.target.value)
                        }
                    >
                        <option value="ALL">Toate</option>
                        <option value="IT">IT</option>
                        <option value="HR">HR</option>
                        <option value="FINANCIAL">Financial</option>
                        <option value="OTHER">Other</option>
                    </select>
                </label>
            </div>

            <div className="tickets-table-wrapper">
                <table className="tickets-table">
                    <thead>
                        <tr>
                            <th>Număr</th>
                            <th>Titlu</th>
                            <th>Categorie</th>
                            <th>Prioritate</th>
                            <th>Status</th>
                            <th>SLA</th>
                        </tr>
                    </thead>

                    <tbody>
                        {filteredTickets.map((ticket) => (
                            <tr key={ticket.id}>
                                <td>{ticket.ticketNumber}</td>

                                <td>{ticket.title}</td>

                                <td>{ticket.category}</td>

                                <td>
                                    <span
                                        className={`ticket-priority priority-${ticket.priority.toLowerCase()}`}
                                    >
                                        {ticket.priority}
                                    </span>
                                </td>

                                <td>
                                    <span className="ticket-status">
                                        {ticket.status}
                                    </span>
                                </td>

                                <td>
                                    <span
                                        className={
                                            ticket.slaBreached
                                                ? "ticket-sla sla-breached"
                                                : "ticket-sla sla-ok"
                                        }
                                    >
                                        {ticket.slaBreached
                                            ? "Depășit"
                                            : "În termen"}
                                    </span>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>

                {filteredTickets.length === 0 && (
                    <p className="empty-message">
                        Nu există tichete pentru filtrele selectate.
                    </p>
                )}
            </div>
        </main>
    );
}

export default AgentDashboard;
