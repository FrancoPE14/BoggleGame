'use client';

import {useContext, useEffect} from 'react';
import LoginStatusContext from '../../components/context/loginStatusContext';


export default function Logout() {

    const [loginStatus, setLoginStatus] = useContext(LoginStatusContext);


    useEffect(() => {
        // First flip in-memory auth state; on the next effect pass persist the logged-out state.
        if (loginStatus) {
            setLoginStatus(false);
            return;
        }

        window.sessionStorage.setItem('loginStatus', JSON.stringify(false));
        window.sessionStorage.removeItem("username");
    }, [loginStatus, setLoginStatus]);

    return <>
        <h1>Logout</h1>
        <p>You have been successfully logged out.</p>
    </>
}
