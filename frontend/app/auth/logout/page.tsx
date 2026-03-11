'use client';

import {useContext, useEffect} from 'react';
import LoginStatusContext from '../../components/context/loginStatusContext';


export default function Logout() {

    const [loginStatus, setLoginStatus] = useContext(LoginStatusContext);


    useEffect(() => {
        if (loginStatus) {
            setLoginStatus(false);
            return;
        }

        window.sessionStorage.setItem('loginStatus', JSON.stringify(false));
    }, [loginStatus, setLoginStatus]);

    return <>
        <h1>Logout</h1>
        <p>You have been successfully logged out.</p>
    </>
}
