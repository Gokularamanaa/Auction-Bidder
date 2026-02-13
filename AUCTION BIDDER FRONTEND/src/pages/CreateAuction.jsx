import React, { useState } from 'react';
import { Container, Typography, Paper, TextField, Button, Box, Grid, Alert, useTheme } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { createAuction } from '../api/auctionService';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';

export default function CreateAuction() {
    const navigate = useNavigate();
    const theme = useTheme();
    const [formData, setFormData] = useState({
        title: '',
        description: '',
        startingPrice: '',
        startTime: '',
        endTime: ''
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        try {
            // Basic validation
            if (new Date(formData.endTime) <= new Date(formData.startTime)) {
                throw new Error("End time must be after start time");
            }

            // Ensure date format matches what backend likely expects (ISO)
            // or let the input type="datetime-local" handle it, usually sends 'YYYY-MM-DDTHH:mm'
            // We might need to append ':00' seconds if backend is strict

            const payload = {
                ...formData,
                startingPrice: parseFloat(formData.startingPrice),
                // Ensure dates are compatible with LocalDateTime
                // Input gives "2024-02-01T12:00", Java wants "2024-02-01T12:00:00" usually
                startTime: formData.startTime.length === 16 ? formData.startTime + ':00' : formData.startTime,
                endTime: formData.endTime.length === 16 ? formData.endTime + ':00' : formData.endTime
            };

            await createAuction(payload);
            navigate('/auctions');
        } catch (err) {
            console.error("Failed to create auction", err);
            setError(err.response?.data?.message || err.message || "Failed to create auction");
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container maxWidth="md" sx={{ mt: 6, mb: 6 }}>
            <Paper elevation={3} sx={{ p: 5, borderRadius: 4 }}>
                <Typography variant="h4" fontWeight="bold" gutterBottom sx={{ mb: 4, color: 'primary.main' }}>
                    Create New Auction
                </Typography>

                {error && (
                    <Alert severity="error" sx={{ mb: 3 }}>
                        {error}
                    </Alert>
                )}

                <Box component="form" onSubmit={handleSubmit}>
                    <Grid container spacing={3}>
                        <Grid item xs={12}>
                            <TextField
                                required
                                fullWidth
                                label="Auction Title"
                                name="title"
                                value={formData.title}
                                onChange={handleChange}
                            />
                        </Grid>

                        <Grid item xs={12}>
                            <TextField
                                required
                                fullWidth
                                multiline
                                rows={4}
                                label="Description"
                                name="description"
                                value={formData.description}
                                onChange={handleChange}
                            />
                        </Grid>

                        <Grid item xs={12} sm={6}>
                            <TextField
                                required
                                fullWidth
                                label="Starting Price (₹)"
                                type="number"
                                name="startingPrice"
                                value={formData.startingPrice}
                                onChange={handleChange}
                                InputProps={{ inputProps: { min: 0, step: "0.01" } }}
                            />
                        </Grid>



                        <Grid item xs={12} sm={6}>
                            <TextField
                                required
                                fullWidth
                                label="Start Time"
                                type="datetime-local"
                                name="startTime"
                                value={formData.startTime}
                                onChange={handleChange}
                                InputLabelProps={{ shrink: true }}
                            />
                        </Grid>

                        <Grid item xs={12} sm={6}>
                            <TextField
                                required
                                fullWidth
                                label="End Time"
                                type="datetime-local"
                                name="endTime"
                                value={formData.endTime}
                                onChange={handleChange}
                                InputLabelProps={{ shrink: true }}
                            />
                        </Grid>



                        <Grid item xs={12}>
                            <Button
                                type="submit"
                                variant="contained"
                                size="large"
                                fullWidth
                                disabled={loading}
                                sx={{
                                    mt: 2,
                                    py: 1.5,
                                    fontWeight: 'bold',
                                    background: `linear-gradient(45deg, ${theme.palette.primary.main}, ${theme.palette.secondary.main})`
                                }}
                            >
                                {loading ? 'Creating...' : 'Create Auction'}
                            </Button>
                        </Grid>
                    </Grid>
                </Box>
            </Paper>
        </Container>
    );
}
