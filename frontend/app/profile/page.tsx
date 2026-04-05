'use client';

import { useEffect, useState } from 'react';
import { Button, Container, Card, Row, Col, Spinner, Alert } from 'react-bootstrap';
import { useRouter } from 'next/navigation';

type ProfileData = {
    username: string;
    matchesWon: number;
    highScore: number;
};

export default function ProfilePage() {
    const [profile, setProfile] = useState<ProfileData | null>(null);
    const [error, setError] = useState<string | null>(null);
    const router = useRouter();

    useEffect(() => {
        const username = window.sessionStorage.getItem('username');
        if (!username) {
            // not logged in, redirect
            router.replace('/auth/login');
            return;
        }

        fetch(`http://localhost:8080/api/user/profile?username=${username}`)
            .then(res => {
                if (res.status === 401) throw new Error('Not authenticated');
                if (!res.ok) throw new Error('Failed to load profile');
                return res.json();
            })
            .then((data: ProfileData) => setProfile(data))
            .catch(err => setError(err.message));
    }, [router]);

    return (
        <div className="bg-warning-subtle min-vh-100 py-5 px-3">
            <Container>
                <Row className="justify-content-center">
                    <Col xs={12} sm={10} md={8} lg={5}>
                        {error && <Alert variant="danger">{error}</Alert>}
                        {!profile && !error && (
                            <div className="text-center">
                                <Spinner animation="border" />
                            </div>
                        )}
                        {profile && (
                            <Card className="shadow-sm border-0">
                                <Card.Body className="p-4 p-md-5">
                                    <h1 className="h3 mb-4">Profile</h1>
                                    <p><strong>Username:</strong> {profile.username}</p>
                                    <p><strong>Matches Won:</strong> {profile.matchesWon}</p>
                                    <p><strong>High Score:</strong> {profile.highScore}</p>
                                    <Button variant="dark" href="/" className="mt-3 w-100">Back to Menu</Button>
                                </Card.Body>
                            </Card>
                        )}
                    </Col>
                </Row>
            </Container>
        </div>
    );
}