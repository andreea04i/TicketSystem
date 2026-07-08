import { useEffect, useState } from "react";

import { 
    getNotifications,
    getUnreadNotificationsCount,
    markAllNotificationsAsRead,
    markNotificationAsRead
} from "../../api/notificationsApi";

import "./NotificationCenter.css";

function formatDate(dateValue) {
    if (!dateValue) {
        return "-";
    }

    const date = new Date(dateValue);

    if (Number.isNaN(date.getTime())) {
        return dateValue;
    }

    return new Intl.DateTimeFormat("ro-RO", {
        dateStyle: "short",
        timeStyle: "short",
    }).format(date);
}

function NotificationCenter({ onOpenTicket}) {
    const [open, setOpen] = useState(false);
    const [notifications, setNotifications] = useState([]);
    const [unreadCount, setUnreadCount] = useState(0);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    async function loadNotifications() {
        try {
            setLoading(true);
            setError("");

            const [notificationsData, unreadData] = 
                await Promise.all([
                    getNotifications(),
                    getUnreadNotificationsCount(),
                ]);
            setNotifications(notificationsData);
            setUnreadCount(unreadData.unreadCount);
        } catch (requestError) {
            setError(requestError.message);
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        loadNotifications();
    }, []);

    async function handleToggle() {
        const nextOpen = !open;

        setOpen(nextOpen);

        if (nextOpen) {
            await loadNotifications();
        }
    }

    async function handleOpenNotification(notification) {
        try {
            setError("");

            if (!notification.read) {
                const updatedNotification = await markNotificationAsRead(notification.id);

                setNotifications((currentNotifications) => 
                    currentNotifications.map((item) =>
                        item.id === notification.id ? updatedNotification : item
                    )
                );
            }

            setOpen(false);

            if (onOpenTicket) {
                onOpenTicket(notification.ticketId);
            }
        } catch (requestError) {
            setError(requestError.message);
        }
    }

    async function handleMarkAllAsRead() {
        try {
            setError("");

            await markAllNotificationsAsRead();

            setNotifications((currentNotifications) =>
                currentNotifications.map((notification) => ({
                    ...notification,
                    read: true,
                }))
            );

            setUnreadCount(0);
        } catch (requestError) {
            setError(requestError.message);
        }
    }

    return (
        <div className="notification-center">
            <button
                type="button"
                className="notification-button"
                onClick={handleToggle}
            >
                Notificări

                {unreadCount > 0 && (
                    <span className="notification-badge">
                        {unreadCount}
                    </span>
                )}
            </button>

            {open && (
                <section className="notification-panel">
                    <div className="notification-panel-header">
                        <div>
                            <h2>Notificări</h2>

                            <p>
                                {unreadCount} necitite
                            </p>
                        </div>

                        <button
                            type="button"
                            className="mark-all-read-button"
                            onClick={handleMarkAllAsRead}
                            disabled={unreadCount === 0}
                        >
                            Marchează toate
                        </button>
                    </div>

                    {loading && (
                        <p className="notification-muted">
                            Se încarcă notificările...
                        </p>
                    )}

                    {error && (
                        <p className="notification-error">
                            {error}
                        </p>
                    )}

                    {!loading &&
                        notifications.length === 0 && (
                            <p className="notification-muted">
                                Nu ai notificări momentan.
                            </p>
                        )}

                    <div className="notification-list">
                        {notifications.map((notification) => (
                            <button
                                key={notification.id}
                                type="button"
                                className={
                                    notification.read
                                        ? "notification-item"
                                        : "notification-item notification-item-unread"
                                }
                                onClick={() =>
                                    handleOpenNotification(
                                        notification
                                    )
                                }
                            >
                                <span className="notification-title">
                                    {notification.title}
                                </span>

                                <span className="notification-message">
                                    {notification.message}
                                </span>

                                <span className="notification-meta">
                                    {notification.ticketNumber} ·{" "}
                                    {formatDate(
                                        notification.createdAt
                                    )}
                                </span>
                            </button>
                        ))}
                    </div>
                </section>
            )}
        </div>
    );
}

export default NotificationCenter;
