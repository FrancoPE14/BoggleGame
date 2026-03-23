'use client';

import { useState, useEffect } from 'react';
import LoginStatusContext from './loginStatusContext';

export default function LoginStatusProvider({
  children,
}: {
  children: React.ReactNode;
}) {
  // Use a deterministic initial value so server and client first render match.
  const [loginStatus, setLoginStatus] = useState<boolean>(false);
  const [hasHydratedStorage, setHasHydratedStorage] = useState(false);

  // Pull persisted auth state after mount.
  useEffect(() => {
    try {
      const stored = window.sessionStorage.getItem('loginStatus');
      if (stored) {
        setLoginStatus(JSON.parse(stored) as boolean);
      }
    } catch {
      setLoginStatus(false);
    } finally {
      setHasHydratedStorage(true);
    }
  }, []);

  // Persist login state changes to Session Storage.
  useEffect(() => {
    if (!hasHydratedStorage) return;

    try {
      window.sessionStorage.setItem('loginStatus', JSON.stringify(loginStatus));
    } catch {
      // Ignore storage write failures.
    }
  }, [hasHydratedStorage, loginStatus]);

  return (
    <LoginStatusContext.Provider value={[loginStatus, setLoginStatus]}>
      {children}
    </LoginStatusContext.Provider>
  );
}
