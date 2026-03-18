'use client';

import { useState, useEffect } from 'react';
import LoginStatusContext from './loginStatusContext';

export default function LoginStatusProvider({
  children,
}: {
  children: React.ReactNode;
}) {
  //Global login context
  const [loginStatus, setLoginStatus] = useState<boolean>(() => {
    if (typeof window === 'undefined') return false;

    const stored = window.sessionStorage.getItem('loginStatus');
    if (!stored) return false;

    try {
      return JSON.parse(stored) as boolean;
    } catch {
      return false;
    }
  });

  // Persist login state changes to Session Storage.
  useEffect(() => {
    window.sessionStorage.setItem('loginStatus', JSON.stringify(loginStatus));
  }, [loginStatus]);

  return (
    <LoginStatusContext.Provider value={[loginStatus, setLoginStatus]}>
      {children}
    </LoginStatusContext.Provider>
  );
}
