'use client'

import { Navbar, Nav, Container, NavDropdown, Form, Button } from "react-bootstrap";
import { useState } from "react";

const Navigation = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const handleLogin = (e: React.SubmitEvent) => {
    e.preventDefault();

    /**
     * Password requirements:
        At least 8 characters
        At least 1 uppercase
        At least 1 lowercase
        At least 1 number
     **/
    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/;
    if (!passwordRegex.test(password)) {
        setError("Password must be 8+ characters, include uppercase, lowercase, and a number.");
    }
    else {
        setError("");
        console.log("Login:", username, password);
    }
  };

    return (
        <Navbar bg="dark" variant="dark" expand="lg">
            <Container>
                <Navbar.Brand href="/">Boggle UW</Navbar.Brand>
                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="ms-auto">
                    <Nav.Link href="/leaderboard">Leaderboard</Nav.Link>
                    <NavDropdown
                        title="Login"
                        id="login-dropdown"
                        align="end"
                        className="login-dropdown"
                    >
                        <Form className="px-4 py-3" onSubmit={handleLogin}>
                            <Form.Group className="mb-3">
                                <Form.Label>Username</Form.Label>
                                <Form.Control
                                    type="text"
                                    placeholder="Username"
                                    value={username}
                                    onChange= {(e) => setUsername(e.target.value)}
                                />

                            </Form.Group>

                            <Form.Group className="mb-3">
                                <Form.Label>Password</Form.Label>
                                <Form.Control
                                    type="password"
                                    placeholder="Password"
                                    value={password}
                                    onChange = {(e) => setPassword(e.target.value)}
                                    isInvalid={!!error}
                                />
                                <Form.Control.Feedback type="invalid">
                                    {error}
                                </Form.Control.Feedback>
                            </Form.Group>

                            <Button type="submit" variant="primary" className="w-100">
                                Login
                            </Button>
                        </Form>
                        <NavDropdown.Divider />
                        <NavDropdown.Item href="/register">
                            New here? Register
                        </NavDropdown.Item>
                    </NavDropdown>
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}

export default Navigation;