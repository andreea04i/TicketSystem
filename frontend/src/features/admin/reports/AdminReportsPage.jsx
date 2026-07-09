import { useEffect, useMemo, useState } from "react";

import {
    getAverageResolutionTime,
    getSlaBreaches,
    getTicketsByCategory,
} from "./adminReportsApi";

import "./AdminReportsPage.css";

function AdminReportsPage() {
    const [ticketsByCategory, setTicketsByCategory] = useState([]);
    const [averageResolution, setAverageResolution] = useState(null);
    const [slaBreaches, setSlaBreaches] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        async function loadReports() {
            try {
                setLoading(true);
                setError("");

                const [categoryData, averageData, slaData] =
                    await Promise.all([
                        getTicketsByCategory(),
                        getAverageResolutionTime(),
                        getSlaBreaches(),
                    ]);

                setTicketsByCategory(categoryData);
                setAverageResolution(averageData);
                setSlaBreaches(slaData);
            } catch (requestError) {
                setError(requestError.message);
            } finally {
                setLoading(false);
            }
        }

        loadReports();
    }, []);

    const totalTickets = useMemo(
        () =>
            ticketsByCategory.reduce(
                (sum, item) => sum + item.ticketCount,
                0
            ),
        [ticketsByCategory]
    );

    const totalBreachedTickets = useMemo(
        () =>
            slaBreaches.reduce(
                (sum, item) => sum + item.breachedTickets,
                0
            ),
        [slaBreaches]
    );

    if (loading) {
        return (
            <main className="admin-reports-page">
                <p>Se încarcă rapoartele...</p>
            </main>
        );
    }

    return (
        <main className="admin-reports-page">
            <section className="admin-reports-container">
                <div className="admin-reports-header">
                    <p className="admin-reports-eyebrow">
                        Panou administrator
                    </p>

                    <h1>Rapoarte tichete</h1>

                    <p>
                        Vezi rapid distribuția tichetelor, timpul mediu de
                        rezolvare și depășirile SLA.
                    </p>
                </div>

                {error && (
                    <p className="admin-reports-error" role="alert">
                        {error}
                    </p>
                )}

                <section className="report-cards-grid">
                    <article className="report-card">
                        <span>Total tichete</span>
                        <strong>{totalTickets}</strong>
                    </article>

                    <article className="report-card">
                        <span>Tichete rezolvate</span>
                        <strong>
                            {averageResolution?.resolvedTickets ?? 0}
                        </strong>
                    </article>

                    <article className="report-card">
                        <span>Timp mediu rezolvare</span>
                        <strong>
                            {averageResolution?.averageResolutionHours ?? 0}h
                        </strong>
                    </article>

                    <article className="report-card report-card-warning">
                        <span>Depășiri SLA</span>
                        <strong>{totalBreachedTickets}</strong>
                    </article>
                </section>

                <section className="report-section">
                    <h2>Tichete pe categorie</h2>

                    <div className="report-table-wrapper">
                        <table className="report-table">
                            <thead>
                                <tr>
                                    <th>Categorie</th>
                                    <th>Număr tichete</th>
                                </tr>
                            </thead>

                            <tbody>
                                {ticketsByCategory.map((item) => (
                                    <tr key={item.category}>
                                        <td>{item.category}</td>
                                        <td>{item.ticketCount}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </section>

                <section className="report-section">
                    <h2>Depășiri SLA pe prioritate</h2>

                    <div className="report-table-wrapper">
                        <table className="report-table">
                            <thead>
                                <tr>
                                    <th>Prioritate</th>
                                    <th>Total</th>
                                    <th>Depășite</th>
                                    <th>Rată</th>
                                </tr>
                            </thead>

                            <tbody>
                                {slaBreaches.map((item) => (
                                    <tr key={item.priority}>
                                        <td>{item.priority}</td>
                                        <td>{item.totalTickets}</td>
                                        <td>{item.breachedTickets}</td>
                                        <td>
                                            {item.breachRatePercent}%
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </section>
            </section>
        </main>
    );
}

export default AdminReportsPage;