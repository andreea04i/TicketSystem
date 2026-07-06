import {
    useCallback,
    useEffect,
    useState,
} from "react";

import {
    assignTicket,
    changeTicketStatus,
    escalateTicket,
    getAgentTicketDetails,
} from "../api/agentTicketsApi";

import "./TicketDetailsPage.css";

function formatDate(dateValue) {
    if (!dateValue) {
        return "—";
    }

    const date = new Date(dateValue);

    if (Number.isNaN(date.getTime())) {
        return dateValue;
    }

    return new Intl.DateTimeFormat("ro-RO", {
        dateStyle: "medium",
        timeStyle: "short",
    }).format(date);
}

function TicketDetailsPage({ ticketId, onBack }) {
    const [ticket, setTicket] = useState(null);
    const [loading, setLoading] = useState(true);
    const [actionLoading, setActionLoading] = useState(false);
    const [error, setError] = useState("");

    const currentAgentId = Number(
        localStorage.getItem("userId")
    );

    const loadTicket = useCallback(
        async (showPageLoader = true) => {
            try {
                if (showPageLoader) {
                    setLoading(true);
                }

                setError("");

                const data =
                    await getAgentTicketDetails(ticketId);

                setTicket(data);
            } catch (requestError) {
                setError(requestError.message);
            } finally {
                if (showPageLoader) {
                    setLoading(false);
                }
            }
        },
        [ticketId]
    );

    useEffect(() => {
        loadTicket();
    }, [loadTicket]);

    async function handleAssign() {
        if (!currentAgentId) {
            setError(
                "ID-ul agentului autentificat nu este disponibil."
            );
            return;
        }

        try {
            setActionLoading(true);
            setError("");

            await assignTicket(
                ticketId,
                currentAgentId
            );

            await loadTicket(false);
        } catch (requestError) {
            setError(requestError.message);
        } finally {
            setActionLoading(false);
        }
    }

    async function handleStatusChange(newStatus) {
        if (newStatus === ticket.status) {
            return;
        }

        try {
            setActionLoading(true);
            setError("");

            await changeTicketStatus(
                ticketId,
                newStatus
            );

            await loadTicket(false);
        } catch (requestError) {
            setError(requestError.message);
        } finally {
            setActionLoading(false);
        }
    }

    async function handleEscalate() {
        const reason = window.prompt(
            "Scrie motivul escaladării:"
        );

        if (reason === null) {
            return;
        }

        if (!reason.trim()) {
            setError(
                "Motivul escaladării este obligatoriu."
            );
            return;
        }

        try {
            setActionLoading(true);
            setError("");

            await escalateTicket(
                ticketId,
                reason.trim()
            );

            await loadTicket(false);
        } catch (requestError) {
            setError(requestError.message);
        } finally {
            setActionLoading(false);
        }
    }

    if (loading) {
        return (
            <main className="ticket-details-page">
                <button
                    type="button"
                    className="back-button"
                    onClick={onBack}
                >
                    ← Înapoi la dashboard
                </button>

                <p>
                    Se încarcă detaliile tichetului...
                </p>
            </main>
        );
    }

    if (!ticket) {
        return (
            <main className="ticket-details-page">
                <button
                    type="button"
                    className="back-button"
                    onClick={onBack}
                >
                    ← Înapoi la dashboard
                </button>

                {error ? (
                    <p className="ticket-details-error">
                        {error}
                    </p>
                ) : (
                    <p>Tichetul nu a fost găsit.</p>
                )}
            </main>
        );
    }

    return (
        <main className="ticket-details-page">
            <button
                type="button"
                className="back-button"
                onClick={onBack}
            >
                ← Înapoi la dashboard
            </button>

            <section className="ticket-details-header">
                <div>
                    <p className="ticket-number">
                        {ticket.ticketNumber}
                    </p>

                    <h1>{ticket.title}</h1>
                </div>

                <div className="ticket-details-badges">
                    <span
                        className={
                            `details-priority ` +
                            `priority-${ticket.priority.toLowerCase()}`
                        }
                    >
                        {ticket.priority}
                    </span>

                    <span className="details-status">
                        {ticket.status}
                    </span>

                    <span
                        className={
                            ticket.slaBreached
                                ? "details-sla details-sla-breached"
                                : "details-sla details-sla-ok"
                        }
                    >
                        {ticket.slaBreached
                            ? "SLA depășit"
                            : "SLA în termen"}
                    </span>
                </div>
            </section>

            {error && (
                <p className="ticket-details-error">
                    {error}
                </p>
            )}

            <section className="ticket-details-card">
                <h2>Descriere</h2>

                <p className="ticket-description">
                    {ticket.description ||
                        "Nu există descriere."}
                </p>
            </section>

            <section className="ticket-details-card ticket-details-actions-card">
                <h2>Acțiuni agent</h2>

                <div className="ticket-details-actions">
                    <button
                        type="button"
                        className="details-assign-button"
                        onClick={handleAssign}
                        disabled={
                            ticket.assignedAgentId !== null ||
                            actionLoading
                        }
                    >
                        {ticket.assignedAgentId
                            ? "Tichet preluat"
                            : "Preia tichetul"}
                    </button>

                    <label>
                        Schimbă statusul

                        <select
                            value={ticket.status}
                            disabled={actionLoading}
                            onChange={(event) =>
                                handleStatusChange(
                                    event.target.value
                                )
                            }
                        >
                            <option value="OPEN">
                                Open
                            </option>

                            <option value="IN_PROGRESS">
                                In progress
                            </option>

                            <option
                                value="ESCALATED"
                                disabled
                            >
                                Escalated
                            </option>

                            <option value="RESOLVED">
                                Resolved
                            </option>

                            <option value="CLOSED">
                                Closed
                            </option>
                        </select>
                    </label>

                    <button
                        type="button"
                        className="details-escalate-button"
                        onClick={handleEscalate}
                        disabled={
                            ticket.status === "ESCALATED" ||
                            actionLoading
                        }
                    >
                        Escaladează
                    </button>
                </div>

                {actionLoading && (
                    <p className="ticket-action-loading">
                        Se procesează acțiunea...
                    </p>
                )}
            </section>

            <section className="ticket-details-grid">
                <article className="ticket-details-card">
                    <h2>Informații tichet</h2>

                    <dl>
                        <div>
                            <dt>Categorie</dt>
                            <dd>{ticket.category}</dd>
                        </div>

                        <div>
                            <dt>Prioritate</dt>
                            <dd>{ticket.priority}</dd>
                        </div>

                        <div>
                            <dt>Status</dt>
                            <dd>{ticket.status}</dd>
                        </div>
                    </dl>
                </article>

                <article className="ticket-details-card">
                    <h2>Creat de</h2>

                    <dl>
                        <div>
                            <dt>Nume</dt>
                            <dd>{ticket.createdByName}</dd>
                        </div>

                        <div>
                            <dt>Email</dt>
                            <dd>{ticket.createdByEmail}</dd>
                        </div>

                        <div>
                            <dt>ID utilizator</dt>
                            <dd>{ticket.createdById}</dd>
                        </div>
                    </dl>
                </article>

                <article className="ticket-details-card">
                    <h2>Agent atribuit</h2>

                    {ticket.assignedAgentId ? (
                        <dl>
                            <div>
                                <dt>Nume</dt>
                                <dd>
                                    {ticket.assignedAgentName}
                                </dd>
                            </div>

                            <div>
                                <dt>Email</dt>
                                <dd>
                                    {ticket.assignedAgentEmail}
                                </dd>
                            </div>

                            <div>
                                <dt>ID agent</dt>
                                <dd>
                                    {ticket.assignedAgentId}
                                </dd>
                            </div>
                        </dl>
                    ) : (
                        <p>
                            Tichetul nu este încă atribuit.
                        </p>
                    )}
                </article>

                <article className="ticket-details-card">
                    <h2>Istoric temporal</h2>

                    <dl>
                        <div>
                            <dt>Creat</dt>
                            <dd>
                                {formatDate(ticket.createdAt)}
                            </dd>
                        </div>

                        <div>
                            <dt>Actualizat</dt>
                            <dd>
                                {formatDate(ticket.updatedAt)}
                            </dd>
                        </div>

                        <div>
                            <dt>Rezolvat</dt>
                            <dd>
                                {formatDate(ticket.resolvedAt)}
                            </dd>
                        </div>

                        <div>
                            <dt>Închis</dt>
                            <dd>
                                {formatDate(ticket.closedAt)}
                            </dd>
                        </div>
                    </dl>
                </article>
            </section>

            {ticket.escalationReason && (
                <section className="ticket-details-card escalation-card">
                    <h2>Motivul escaladării</h2>

                    <p>{ticket.escalationReason}</p>
                </section>
            )}
        </main>
    );
}

export default TicketDetailsPage;