import { useEffect, useState } from 'react';
import {
  AppConfigDataClient,
  StartConfigurationSessionCommand,
  GetLatestConfigurationCommand
} from '@aws-sdk/client-appconfigdata';

const DEFAULT_FLAG = import.meta.env.VITE_FEATURE_FLAG_DEFAULT === 'true';

function parseConfiguration(buffer, flagKey) {
  if (!buffer) {
    return DEFAULT_FLAG;
  }

  try {
    const text = new TextDecoder('utf-8').decode(buffer);
    if (!text) {
      return DEFAULT_FLAG;
    }

    const configuration = JSON.parse(text);

    if (typeof configuration === 'boolean') {
      return configuration;
    }

    if (configuration && typeof configuration === 'object') {
      const flags = configuration.flags || configuration;
      const flagValue = flags?.[flagKey];

      if (typeof flagValue === 'boolean') {
        return flagValue;
      }
    }
  } catch (error) {
    console.warn('Failed to parse AppConfig configuration', error);
  }

  return DEFAULT_FLAG;
}

export function useFeatureFlag(flagKey) {
  const [enabled, setEnabled] = useState(DEFAULT_FLAG);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let isCancelled = false;

    async function fetchFeatureFlag() {
      setLoading(true);
      setError(null);

      const client = new AppConfigDataClient({
        region: import.meta.env.VITE_AWS_REGION,
        apiVersion: '2021-11-11'
      });

      try {
        const session = new StartConfigurationSessionCommand({
          ApplicationIdentifier: import.meta.env.VITE_APPCONFIG_APPLICATION,
          EnvironmentIdentifier: import.meta.env.VITE_APPCONFIG_ENVIRONMENT,
          ConfigurationProfileIdentifier: import.meta.env.VITE_APPCONFIG_PROFILE
        });

        const { InitialConfigurationToken } = await client.send(session);
        const configurationCommand = new GetLatestConfigurationCommand({
          ConfigurationToken: InitialConfigurationToken
        });

        const { Configuration } = await client.send(configurationCommand);

        if (!isCancelled) {
          const isEnabled = parseConfiguration(Configuration, flagKey);
          setEnabled(isEnabled);
        }
      } catch (err) {
        if (!isCancelled) {
          setError(err);
        }
      } finally {
        if (!isCancelled) {
          setLoading(false);
        }
      }
    }

    fetchFeatureFlag();

    return () => {
      isCancelled = true;
    };
  }, [flagKey]);

  return { enabled, loading, error };
}
