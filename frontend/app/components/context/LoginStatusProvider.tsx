'use client';

import { useState, useEffect } from 'react';
import LoginStatusContext from './loginStatusContext';

export default function LoginStatusProvider({
  children,
}: {
  children: React.ReactNode;
}) {
  //Global login context
  const [loginStatus, setLoginStatus] = useState<boolean>(false);
  // Prevent writing default state before we finish reading from sessionStorage.
  const [storageReady, setStorageReady] = useState(false);

  //After refresh, load loginStatus
  useEffect(() => {
    const stored = window.sessionStorage.getItem('loginStatus');
    let parsedStatus = false;

    if (stored) {
      try {
        parsedStatus = JSON.parse(stored) as boolean;
      } catch {
        parsedStatus = false;
      }
    }

    setLoginStatus(parsedStatus);
    setStorageReady(true);
  }, []);

  // Persist state changes only after we have gotten loginStatus from Session Storage
  useEffect(() => {
    if (!storageReady) return;
    window.sessionStorage.setItem('loginStatus', JSON.stringify(loginStatus));
  }, [loginStatus, storageReady]);

  return (
    <LoginStatusContext.Provider value={[loginStatus, setLoginStatus]}>
      {children}
    </LoginStatusContext.Provider>
  );
}
