'use client';

import { useEffect, useState } from 'react';
import { Button, Container, Card, Row, Col, Spinner, Alert } from 'react-bootstrap';
import { useRouter } from 'next/navigation';

type ProfileData = {
    username: string;
    matchesWon: number;
    highScore: number;
    profilePicture: string;
};

export default function ProfilePage() {
    const [profile, setProfile] = useState<ProfileData | null>(null);
    const [error, setError] = useState<string | null>(null);
    const [uploadError, setUploadError] = useState<string | null>(null);
    const [uploadSuccess, setUploadSuccess] = useState(false);
    // holds base64 of selected-but-not-yet-saved image for preview
    const [previewPic, setPreviewPic] = useState<string | null>(null);
    const router = useRouter();

    useEffect(() => {
        const username = window.sessionStorage.getItem('username');
        if (!username) {
            // no session means unauthenticated — send to login
            router.replace('/auth/login');
            return;
        }

        // fetch profile data from backend on mount
        fetch(`http://localhost:8080/api/user/profile?username=${username}`)
            .then(res => {
                if (res.status === 401) throw new Error('Not authenticated');
                if (!res.ok) throw new Error('Failed to load profile');
                return res.json();
            })
            .then((data: ProfileData) => setProfile(data))
            .catch(err => setError(err.message));
    }, [router]);

    // read selected file as base64 and show preview without uploading yet
    function handleFileSelect(e: React.ChangeEvent<HTMLInputElement>) {
        const file = e.target.files?.[0];
        if (!file) return;
        const reader = new FileReader();
        reader.onload = () => setPreviewPic(reader.result as string);
        reader.readAsDataURL(file);
    }

    // upload the previewed picture to the backend when Save is clicked
    function handleSavePicture() {
        if (!previewPic) return;
        const username = window.sessionStorage.getItem('username');
        setUploadError(null);
        setUploadSuccess(false);

        fetch(`http://localhost:8080/api/user/profile/picture?username=${username}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ imageData: previewPic })
        }).then(res => {
            if (!res.ok) throw new Error('Upload failed');
            // update displayed picture locally and clear the preview
            setProfile(prev => prev ? { ...prev, profilePicture: previewPic } : prev);
            setPreviewPic(null);
            setUploadSuccess(true);
        }).catch(() => setUploadError('Failed to update profile picture. Please try again.'));
    }

    return (
        <div className="bg-warning-subtle min-vh-100 py-5 px-3">
            <Container>
                <Row className="justify-content-center">
                    <Col xs={12} sm={10} md={8} lg={5}>
                        {error && <Alert variant="danger">{error}</Alert>}
                        {/* show spinner while profile is loading */}
                        {!profile && !error && (
                            <div className="text-center">
                                <Spinner animation="border" />
                            </div>
                        )}
                        {profile && (
                            <Card className="shadow-sm border-0">
                                <Card.Body className="p-4 p-md-5">
                                    <h1 className="h3 mb-4">Profile</h1>

                                    {/* show preview if a file is selected, otherwise saved pic or default avatar */}
                                    <div className="text-center mb-4">
                                        <img
                                            src={previewPic || profile.profilePicture || '/default-avatar.png'}
                                            alt="Profile"
                                            style={{ width: 120, height: 120, borderRadius: '50%', objectFit: 'cover', border: '3px solid #dee2e6' }}
                                        />
                                    </div>

                                    {/* file input — Save button only appears after a file is selected */}
                                    <div className="mb-3">
                                        <label htmlFor="picInput" className="form-label"><strong>Change Profile Picture</strong></label>
                                        <input
                                            id="picInput"
                                            type="file"
                                            accept="image/*"
                                            className="form-control mb-2"
                                            onChange={handleFileSelect}
                                        />
                                        {previewPic && (
                                            <Button variant="dark" className="w-100" onClick={handleSavePicture}>
                                                Save Picture
                                            </Button>
                                        )}
                                    </div>

                                    {uploadError && <Alert variant="danger">{uploadError}</Alert>}
                                    {uploadSuccess && <Alert variant="success">Profile picture updated!</Alert>}

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