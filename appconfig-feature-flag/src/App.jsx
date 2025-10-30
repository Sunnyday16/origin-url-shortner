import { useFeatureFlag } from './hooks/useFeatureFlag.js';

const FLAG_KEY = import.meta.env.VITE_FEATURE_FLAG_KEY || 'enableNewFeature';

export default function App() {
  const { enabled, loading, error } = useFeatureFlag(FLAG_KEY);

  let statusClass = 'status ';
  let statusMessage = '';

  if (loading) {
    statusClass += 'loading';
    statusMessage = 'Checking feature flag…';
  } else if (error) {
    statusClass += 'error';
    statusMessage = 'Feature flag unavailable.';
  } else if (enabled) {
    statusClass += 'enabled';
    statusMessage = 'Feature is enabled!';
  } else {
    statusClass += 'disabled';
    statusMessage = 'Feature is disabled!';
  }

  return (
    <div className="app-shell">
      <h1>AWS AppConfig Feature Flag</h1>
      <p className={statusClass}>{statusMessage}</p>
      <small>
        Configure your AppConfig identifiers in <code>.env</code> and assign an
        <code>enableNewFeature</code> (or custom) flag in your configuration.
      </small>
      {error && (
        <small>
          <strong>Error:</strong> {error.message || 'Unexpected error. Check your configuration.'}
        </small>
      )}
    </div>
  );
}
