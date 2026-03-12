'use client';

import { SubmitEvent, useContext, useRef } from 'react';
import { Form, Button, Container, Row, Col, Card } from 'react-bootstrap'
import LoginStatusContext from '../../components/context/loginStatusContext';
import { useRouter } from 'next/navigation';


export default function Register() {

    const [, setLoginStatus] = useContext(LoginStatusContext);
    const usernameRef = useRef<HTMLInputElement>(null);
    const passwordRef = useRef<HTMLInputElement>(null);

    const router = useRouter()

    function onLogin(e: SubmitEvent) {
        e?.preventDefault();

        const username = String(usernameRef?.current?.value ?? "").trim()
        const password = String(passwordRef?.current?.value ?? "")

        console.log(username)

        if (!username || !password?.trim()) {
            alert("You must provide both a username and password!");
            return;
        }

        fetch(`http://localhost:8080/api/register?username=${username}&password=${password}`, {
            method: "POST"
        }).then(res => {
            if (res.status === 200) {
                alert("You have been successfully registered!")
                setLoginStatus(true);
                
                router.push("/")
                router.replace("/")

            } else {
                alert("Something went wrong! :/")
            }
        })
    }

    return <div className="bg-warning-subtle min-vh-100 py-5 px-3">
        <Container>
            <Row className="justify-content-center">
                <Col xs={12} sm={10} md={8} lg={5}>
                    <Card className="shadow-sm border-0">
                        <Card.Body className="p-4 p-md-5">
                            <h1 className="h3 mb-1">Register</h1>
                            <p className="text-muted mb-4">
                                Create a new account.
                            </p>

                            <Form onSubmit={onLogin}>
                                <Form.Group className="mb-3">
                                    <Form.Label htmlFor="usernameInput">Username</Form.Label>
                                    <Form.Control
                                        id="usernameInput"
                                        ref={usernameRef}
                                        placeholder="Enter username"
                                    />
                                </Form.Group>

                                <Form.Group className="mb-4">
                                    <Form.Label htmlFor="passwordInput">Password</Form.Label>
                                    <Form.Control
                                        id="passwordInput"
                                        type="password"
                                        ref={passwordRef}
                                        placeholder="Enter password"
                                    />
                                </Form.Group>

                                <Button
                                    type="submit"
                                    variant="dark"
                                    className="w-100"
                                >
                                    Login
                                </Button>
                            </Form>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    </div>
}
