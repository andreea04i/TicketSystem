import { useEffect, useState } from 'react';
import { getSlaConfig, updateSlaConfig } from './slaApi';
import './SlaConfigPage.css';

const priorityLabels = {
  LOW: 'Low',
  MEDIUM: 'Medium',
  HIGH: 'High',
  CRITICAL: 'Critical',
};

function SlaConfigPage() {
  const [configs, setConfigs] = useState([]);
  const [hours, setHours] = useState({});
  const [loading, setLoading] = useState(true);
  const [savingPriority, setSavingPriority] = useState(null);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    async function loadConfigs() {
      try {
        setLoading(true);
        setError('');

        const data = await getSlaConfig();
        setConfigs(data);

        const initialHours = Object.fromEntries(
          data.map((config) => [config.priority, config.resolutionHours])
        );
        setHours(initialHours);
      } catch (requestError) {
        setError(requestError.message);
      } finally {
        setLoading(false);
      }
    }

    loadConfigs();
  }, []);

  function handleHoursChange(priority, value) {
    setHours((currentHours) => ({
      ...currentHours,
      [priority]: value,
    }));
  }

  async function handleSave(priority) {
    const resolutionHours = Number(hours[priority]);

    if (!Number.isInteger(resolutionHours) || resolutionHours <= 0) {
      setError(
        'Numărul de ore trebuie să fie un număr întreg mai mare decât zero.'
      );
      setSuccess('');
      return;
    }

    try {
      setSavingPriority(priority);
      setError('');
      setSuccess('');

      const updatedConfig = await updateSlaConfig(priority, resolutionHours);

      setConfigs((currentConfigs) =>
        currentConfigs.map((config) =>
          config.priority === priority ? updatedConfig : config
        )
      );

      setHours((currentHours) => ({
        ...currentHours,
        [priority]: updatedConfig.resolutionHours,
      }));

      setSuccess(
        `Configurația pentru prioritatea ${priorityLabels[priority]} a fost actualizată cu succes.`
      );
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setSavingPriority(null);
    }
  }

  if (loading) {
    return (
      <main className="sla-page">
        <p>Se încarcă configurările SLA...</p>
      </main>
    );
  }

  return (
    <main className="sla-page">
      <section className="sla-container">
        <div className="sla-header">
          <p className="sla-eyebrow">Panou administrator</p>
          <h1>Configurare SLA</h1>
          <p>Stabilește timpul maxim de rezolvare pentru fiecare prioritate.</p>
        </div>

        {error && (
          <div className="sla-message sla-error" role="alert">
            {error}
          </div>
        )}

        {success && (
          <div className="sla-message sla-success" role="status">
            {success}
          </div>
        )}

        <div className="sla-grid">
          {configs.map((config) => (
            <article className="sla-card" key={config.priority}>
              <div>
                <span
                  className={`sla-priority sla-priority-${config.priority.toLowerCase()}`}
                >
                  {priorityLabels[config.priority]}
                </span>

                <p className="sla-current-value">
                  Valoare actuală: <strong>{config.resolutionHours} ore</strong>
                </p>
              </div>

              <label htmlFor={`hours-${config.priority}`}>
                Timp maxim de rezolvare
              </label>
              <div className="sla-input-row">
                <input
                  id={`hours-${config.priority}`}
                  type="number"
                  min="1"
                  step="1"
                  value={hours[config.priority] ?? ''}
                  onChange={(event) =>
                    handleHoursChange(config.priority, event.target.value)
                  }
                />
                <span>ore</span>
              </div>

              <button
                type="button"
                disabled={savingPriority === config.priority}
                onClick={() => handleSave(config.priority)}
              >
                {savingPriority === config.priority
                  ? 'Se salvează...'
                  : 'Salvează'}
              </button>
            </article>
          ))}
        </div>
      </section>
    </main>
  );
}

export default SlaConfigPage;
