'use client';

import { useState, useEffect } from 'react';
import LoginStatusContext from './loginStatusContext';

export default function LoginStatusProvider({
  children,
}: {
  children: React.ReactNode;
}) {
  const [loginStatus, setLoginStatus] = useState<boolean>(false);
  const [storageReady, setStorageReady] = useState(false);

  //On load or refresh, loginStatus will be taken from sessionStorage
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

  //After sessi
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
