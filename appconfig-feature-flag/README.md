# AppConfig Feature Flag Demo

This React application demonstrates how to read a feature flag from [AWS AppConfig](https://aws.amazon.com/systems-manager/features/appconfig/). It uses the AWS SDK for JavaScript (v3) to establish a configuration session and retrieve the latest flag value. Depending on the state of the flag, the UI renders either **"Feature is enabled!"** or **"Feature is disabled!"**.

> **Note:** The app expects the configuration payload in AppConfig to either be a boolean or a JSON object with a `flags` map (e.g. `{ "flags": { "enableNewFeature": true } }`).

## Getting started

1. Install dependencies:

   ```bash
   npm install
   ```

2. Provide your AppConfig identifiers and defaults by creating an `.env` file in the project root:

   ```bash
   VITE_AWS_REGION=us-east-1
   VITE_APPCONFIG_APPLICATION=your-app-id
   VITE_APPCONFIG_ENVIRONMENT=your-env-id
   VITE_APPCONFIG_PROFILE=your-profile-id
   VITE_FEATURE_FLAG_KEY=enableNewFeature
   VITE_FEATURE_FLAG_DEFAULT=false
   ```

   The `VITE_FEATURE_FLAG_DEFAULT` value is used as a fallback before AppConfig responds or if parsing fails.

3. Run the development server:

   ```bash
   npm run dev
   ```

4. Open the printed URL (defaults to [http://localhost:5173](http://localhost:5173)) to view the application.

## How it works

- `src/hooks/useFeatureFlag.js` handles the AppConfig calls. It starts a configuration session, retrieves the latest configuration, and parses the response to determine the flag state.
- `src/App.jsx` consumes the hook and displays contextual messaging for loading, success, and error states.

## Troubleshooting

- Ensure that the IAM identity used in the browser has permission to call `StartConfigurationSession` and `GetLatestConfiguration` for the specified resources.
- If the configuration cannot be parsed, the hook falls back to the default value (`VITE_FEATURE_FLAG_DEFAULT`).
- The AppConfig Data API requires HTTPS; when hosting locally behind Vite the AWS SDK handles the request using the browser's `fetch` implementation.
