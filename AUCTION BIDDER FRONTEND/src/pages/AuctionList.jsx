import React, { useEffect, useState } from 'react';
import { Container, Typography, Grid, CircularProgress, Box, TextField, InputAdornment } from '@mui/material';
import AuctionCard from '../components/AuctionCard';
import { getAllAuctions } from '../api/auctionService';
import SearchIcon from '@mui/icons-material/Search';

export default function AuctionList() {
    const [auctions, setAuctions] = useState([]);
    const [loading, setLoading] = useState(true);
    const [searchTerm, setSearchTerm] = useState('');

    useEffect(() => {
        const fetchAuctions = async () => {
            try {
                const data = await getAllAuctions();
                setAuctions(data);
            } catch (error) {
                console.error("Failed to fetch auctions", error);
            } finally {
                setLoading(false);
            }
        };
        fetchAuctions();
    }, []);

    const filteredAuctions = auctions.filter(auction =>
        (auction.title && auction.title.toLowerCase().includes(searchTerm.toLowerCase())) ||
        (auction.description && auction.description.toLowerCase().includes(searchTerm.toLowerCase()))
    );

    if (loading) return <Box sx={{ display: 'flex', justifyContent: 'center', mt: 10 }}><CircularProgress /></Box>;

    return (
        <Container maxWidth="lg">
            <Box sx={{ mb: 6, textAlign: 'center' }}>
                <Typography variant="h3" fontWeight="bold" gutterBottom sx={{
                    background: 'linear-gradient(45deg, #8b5cf6, #ec4899)',
                    backgroundClip: 'text',
                    textFillColor: 'transparent',
                    WebkitBackgroundClip: 'text',
                    WebkitTextFillColor: 'transparent',
                    mb: 2
                }}>
                    Explore Auctions
                </Typography>
                <Typography variant="h6" sx={{ mb: 4, color: 'rgba(255,255,255,0.9)' }}>
                    Discover unique items and place your bids today.
                </Typography>

                <TextField
                    placeholder="Search auctions..."
                    variant="outlined"
                    sx={{ width: '100%', maxWidth: 500 }}
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    InputProps={{
                        startAdornment: (
                            <InputAdornment position="start">
                                <SearchIcon color="action" />
                            </InputAdornment>
                        ),
                        sx: { bgcolor: 'rgba(30, 41, 59, 0.5)', backdropFilter: 'blur(5px)' }
                    }}
                />
            </Box>

            <Grid container spacing={4}>
                {filteredAuctions.length > 0 ? (
                    filteredAuctions.map((auction) => (
                        <Grid item xs={12} sm={6} md={4} key={auction.auctionId}>
                            <AuctionCard auction={auction} />
                        </Grid>
                    ))
                ) : (
                    <Grid item xs={12}>
                        <Box sx={{ textAlign: 'center', py: 8 }}>
                            <Typography variant="h6" color="text.secondary">
                                No auctions found matching your search.
                            </Typography>
                        </Box>
                    </Grid>
                )}
            </Grid>
        </Container>
    );
}
